package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.model.City;
import ru.javaops.masterjava.persist.model.Group;

/**
 * 22.09.2020
 *
 * @author a.chernyavskiy0n
 */
@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class GroupDao implements AbstractDao {

    @SqlUpdate("TRUNCATE groups")
    @Override
    public abstract void clean();

    public Group insert(Group group) {
        if (group.isNew()) {
            int id = insertGeneratedId(group);
            group.setId(id);
        } else {
            insertWitId(group);
        }
        return group;
    }

    @SqlUpdate("INSERT INTO groups (name, type) VALUES (:name, CAST(:flag AS GROUP_FLAG))")
    @GetGeneratedKeys
    abstract int insertGeneratedId(@BindBean Group group);

    @SqlUpdate("INSERT INTO groups (id, name, type) (:id, :name, CAST(:flag AS GROUP_FLAG))")
    abstract void insertWitId(@BindBean Group group);
}
