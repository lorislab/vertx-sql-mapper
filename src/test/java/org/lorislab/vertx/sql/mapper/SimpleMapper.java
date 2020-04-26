package org.lorislab.vertx.sql.mapper;

import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.RowSet;

@SqlMapper
public interface SimpleMapper {

    Pojo map(Row row);

    Pojo map(Row row, String alias);

    Pojo map(RowSet<Row> rowSet);

    Pojo map(RowSet<Row> rowSet, String alias);

    Pojo map(RowIterator<Row> iterator);

    Pojo map(RowIterator<Row> iterator, String alias);

    @SqlMapping(target = "id", source = "uid")
    @SqlMapping(target = "status", ignore = true)
    @SqlMapping(target = "parent", source = "p", alias = "x")
    Pojo mapSqlMapping(Row row);

    @SqlMapping(target = "id", source = "uid")
    @SqlMapping(target = "status", ignore = true)
    @SqlMapping(target = "parent", source = "p", alias = "x")
    Pojo mapSqlMapping(Row row, String alias);
}
