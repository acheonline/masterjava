package ru.javaops.masterjava.persist.model;

import lombok.*;

/**
 * 22.09.2020
 *
 * @author a.chernyavskiy0n
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public abstract class AbstractNamedEntity extends BaseEntity{
    private @NonNull String name;
    
    AbstractNamedEntity(Integer id, String name) {
        this(name);
        this.id = id;
    }
}
