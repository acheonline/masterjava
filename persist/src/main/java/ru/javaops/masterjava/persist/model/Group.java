package ru.javaops.masterjava.persist.model;

import lombok.*;

/**
 * 22.09.2020
 *
 * @author a.chernyavskiy0n
 */
@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Group extends AbstractNamedEntity {

    private @NonNull
    GroupType type;

    public Group(Integer id, String name, GroupType type) {
        this(type);
        this.id = id;
        this.setName(name);
    }
}
