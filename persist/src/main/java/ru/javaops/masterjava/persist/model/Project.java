package ru.javaops.masterjava.persist.model;

import lombok.*;

import java.util.List;

/**
 * 22.09.2020
 *
 * @author a.chernyavskiy0n
 */
@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Project extends AbstractNamedEntity {
    private @NonNull String description;
    private @NonNull List<Group> groups;

    public Project(Integer id, String description, String name, List<Group> groups) {
        this(description, groups);
        this.id = id;
        this.setName(name);
    }
}
