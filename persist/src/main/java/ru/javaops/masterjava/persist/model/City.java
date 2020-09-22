package ru.javaops.masterjava.persist.model;

import lombok.*;

/**
 * 22.09.2020
 *
 * @author a.chernyavskiy0n
 */
@Data
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class City extends BaseEntity{
    private @NonNull String value;

    public City(String id, String value) {
        this(value);
        this.id = Integer.valueOf(id);
    }
}
