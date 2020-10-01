package ru.javaops.masterjava.persist.model;

import lombok.*;

/**
 * 22.09.2020
 *
 * @author a.chernyavskiy0n
 */
@Data
@RequiredArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class City extends AbstractNamedEntity {
    City (Integer id, String value) {
        this.id = id;
        this.setName(value);
    }

}
