package ru.javaops.masterjava;

import com.google.common.io.Resources;
import j2html.tags.ContainerTag;
import one.util.streamex.StreamEx;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.schema.Payload;
import ru.javaops.masterjava.xml.schema.Project;
import ru.javaops.masterjava.xml.schema.User;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.Schemas;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static j2html.TagCreator.*;

/**
 * 17.06.2020
 *
 * @author a.chernyavskiy0n
 */
public class MainXml {
    private static final Comparator<User> USER_COMPARATOR = Comparator.comparing(User::getValue).thenComparing(User::getEmail);

    public static void main(String[] args) throws IOException, JAXBException {
        if (args.length != 2) {
            System.out.println("Format input: projectName, xmlName");
            System.exit(1);
        }
        URL payloadUrl = Resources.getResource(args[1]);
        MainXml main = new MainXml();
        String projectName = args[0];

        Set<User> users = main.parseByJaxb(projectName, payloadUrl);
        String out = outHtml(users, projectName, Paths.get("out/usersJaxb.html"));
        System.out.println(out);
    }

    private static String outHtml(Set<User> users, String projectName, Path path) throws IOException {
        try(Writer writer = Files.newBufferedWriter(path)) {
            final ContainerTag table = table().with(tr().with(th("FullName"), th("email")));
            users.forEach(user -> table().with(tr().with(th(user.getValue()), th(user.getEmail()))));
            table.attr("border", "1");
            table.attr("cellpaddding", "8");
            table.attr("cellspacing", "0");

            String out = html().with(
                    head().with(title(projectName + " users")),
                    body().with(h1(projectName + " users"), table)
            ).render();
            writer.write(out);
            return out;
        }
    }


    private Set<User> parseByJaxb(String projectName, URL payloadUrl) throws IOException, JAXBException {
        JaxbParser parser = new JaxbParser(ObjectFactory.class);
        parser.setSchema(Schemas.ofClasspath("payload.xsd"));

        try (InputStream is = payloadUrl.openStream()) {
            Payload payload = parser.unmarshal(is);
            Project project = StreamEx.of(payload.getProjects().getProject())
                    .filter(p -> p.getName().equals(projectName))
                    .findAny()
                    .orElseThrow(() -> new
                            IllegalArgumentException("Invalid project name " + projectName + '\''));
            Set<Project.Group> groups = new HashSet<>(project.getGroup());
            return StreamEx.of(payload.getUsers().getUser())
                    .filter(u -> StreamEx.of(u.getGroupRefs())
                            .findAny(groups::contains).isPresent())
                    .collect(Collectors.toCollection(() -> new TreeSet<>(USER_COMPARATOR)));
        }
    }
}
