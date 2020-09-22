package ru.javaops.masterjava.upload;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import one.util.streamex.StreamEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.model.User;
import ru.javaops.masterjava.persist.model.UserFlag;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.JaxbUnmarshaller;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class UserProcessor {
    private static final int NUMBER_THREADS = 4;

    private static final JaxbParser jaxbParser = new JaxbParser(ObjectFactory.class);
    private static UserDao userDao = DBIProvider.getDao(UserDao.class);

    private ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_THREADS);

    @AllArgsConstructor
    public static class FailedChunk {
        public String emailOrRange;
        public String reason;

        @Override
        public String toString() {
            return emailOrRange + " : " + reason;
        }
    }

    /*
     * return failed users chunks
     */
    public List<FailedChunk> process(final InputStream is, int chunkSize) throws XMLStreamException, JAXBException {
        log.info("Start processing with chunkSize=" + chunkSize);

        return new Callable<List<FailedChunk>>() {
            class ChunkFuture {
                String emailRange;
                Future<List<String>> future;

                public ChunkFuture(List<User> chunk, Future<List<String>> future) {
                    this.future = future;
                    this.emailRange = chunk.get(0).getEmail();
                    if (chunk.size() > 1) {
                        this.emailRange += '-' + chunk.get(chunk.size() - 1).getEmail();
                    }
                }
            }

            @Override
            public List<FailedChunk> call() throws XMLStreamException, JAXBException {
                List<ChunkFuture> futures = new ArrayList<>();

                int id = userDao.getSeqAndSkip(chunkSize);
                List<User> chunk = new ArrayList<>(chunkSize);
                val processor = new StaxStreamProcessor(is);
                val unmarshaller = jaxbParser.createUnmarshaller();

                while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
                    ru.javaops.masterjava.xml.schema.User xmlUser = unmarshaller.unmarshal(processor.getReader(), ru.javaops.masterjava.xml.schema.User.class);
                    final User user = new User(id++, xmlUser.getValue(), xmlUser.getEmail(), UserFlag.valueOf(xmlUser.getFlag().value()));
                    chunk.add(user);
                    if (chunk.size() == chunkSize) {
                        futures.add(submit(chunk));
                        chunk = new ArrayList<>(chunkSize);
                        id = userDao.getSeqAndSkip(chunkSize);
                    }
                }

                if (!chunk.isEmpty()) {
                    futures.add(submit(chunk));
                }

                List<FailedChunk> failed = new ArrayList<>();
                futures.forEach(cf -> {
                    try {
                        failed.addAll(StreamEx.of(cf.future.get()).map(email -> new FailedChunk(email, "already present")).toList());
                        log.info(cf.emailRange + " successfully executed");
                    } catch (Exception e) {
                        log.error(cf.emailRange + " failed", e);
                        failed.add(new FailedChunk(cf.emailRange, e.toString()));
                    }
                });
                return failed;
            }

            private ChunkFuture submit(List<User> chunk) {
                ChunkFuture chunkFuture = new ChunkFuture(chunk,
                        executorService.submit(() -> userDao.insertAndGetConflictEmails(chunk))
                );
                log.info("Submit chunk: " + chunkFuture.emailRange);
                return chunkFuture;
            }
        }.call();
    }
}
