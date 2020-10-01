package ru.javaops.masterjava.persist.model;

import com.bertoncelj.jdbi.entitymapper.Column;
import lombok.*;

import java.util.List;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class User extends AbstractNamedEntity {

    private @NonNull String email;
    private @NonNull UserFlag flag;
    private @NonNull City city;
    private @NonNull List<Group> group;

    public User(Integer id, String fullName, String email, UserFlag flag, City city, List<Group> groups) {
        this(email, flag, city, groups);
        this.id=id;
        this.setName(fullName);
    }
}