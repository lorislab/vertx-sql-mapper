package org.lorislab.vertx.sql.mapper.test;

import io.vertx.sqlclient.Row;
import org.lorislab.vertx.sql.mapper.SqlEnumType;
import org.lorislab.vertx.sql.mapper.SqlMapper;
import org.lorislab.vertx.sql.mapper.SqlMapping;

@SqlMapper
public interface SimpleMapper {

    Model map(Row row);

    Model map(Row row, String alias);

    @SqlMapping(target = "id", column = "uid")
    @SqlMapping(target = "status", ignore = true)
    @SqlMapping(target = "statusInteger", ignore = true)
    @SqlMapping(target = "parent", column = "p", alias = "x")
    Model mapSqlMapping(Row row);

    @SqlMapping(target = "id", column = "uid")
    @SqlMapping(target = "status", ignore = true)
    @SqlMapping(target = "statusInteger", enumType = SqlEnumType.STRING)
    @SqlMapping(target = "parent", column = "p", alias = "x")
    Model mapSqlMapping(Row row, String alias);
}
