package org.lorislab.vertx.sql.mapper;

import io.vertx.sqlclient.Row;

@SqlMapper
public interface SimpleMapper {

    Pojo map(Row row);

    Pojo map(Row row, String alias);

    @SqlMapping(target = "id", source = "uid")
    @SqlMapping(target = "status", ignore = true)
    @SqlMapping(target = "parent", source = "p", alias = "x")
    Pojo mapSqlMapping(Row row);

    @SqlMapping(target = "id", source = "uid")
    @SqlMapping(target = "status", ignore = true)
    @SqlMapping(target = "parent", source = "p", alias = "x")
    Pojo mapSqlMapping(Row row, String alias);
}
