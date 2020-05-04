package org.lorislab.vertx.sql.mapper.test;

import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import java.util.Arrays;
import java.util.HashSet;
import org.lorislab.vertx.sql.mapper.SqlEnumType;
import org.lorislab.vertx.sql.mapper.SqlMapping;
import org.lorislab.vertx.sql.mapper.SqlMappings;

public class SimpleMapperImpl implements SimpleMapper {

    public static final SimpleMapper INSTANCE = new SimpleMapperImpl();

    @Override
    public Model map(Row row) {
        if (row == null) {
            return null;
        }
        Model result = new Model();
        result.id = row.getString(Model_.ID);
        result.version = row.getInteger(Model_.VERSION);
        result.messageId = row.getString(Model_.MESSAGE_ID);
        result.parent = row.getString(Model_.PARENT);
        result.processId = row.getString(Model_.PROCESS_ID);
        result.processVersion = row.getString(Model_.PROCESS_VERSION);
        String resultStatus = row.getString(Model_.STATUS);
        if (resultStatus != null && !resultStatus.isBlank()) {
            result.status = ModelStatus.valueOf(resultStatus);
        }
        Integer resultStatusInteger = row.getInteger(Model_.STATUS_INTEGER);
        if (0 <= resultStatusInteger && resultStatusInteger < ModelStatus.values().length) {
            result.statusInteger = ModelStatus.values()[resultStatusInteger];
        }
        result.data = row.get(JsonObject.class, row.getColumnIndex(Model_.DATA));
        result.createdFrom = new HashSet<>(Arrays.asList(row.getLongArray(Model_.CREATED_FROM)));
        result.time = row.getLocalTime(Model_.TIME);
        return result;
    }

    public static Model mapS(Row row) {
        return INSTANCE.map(row);
    }

    @Override
    public Model map(Row row, String alias) {
        if (row == null) {
            return null;
        }
        String a = "";
        if (alias != null && !alias.isBlank()) {
            a = alias + ".";
        }
        Model result = new Model();
        result.id = row.getString(a + Model_.ID);
        result.version = row.getInteger(a + Model_.VERSION);
        result.messageId = row.getString(a + Model_.MESSAGE_ID);
        result.parent = row.getString(a + Model_.PARENT);
        result.processId = row.getString(a + Model_.PROCESS_ID);
        result.processVersion = row.getString(a + Model_.PROCESS_VERSION);
        String resultStatus = row.getString(a + Model_.STATUS);
        if (resultStatus != null && !resultStatus.isBlank()) {
            result.status = ModelStatus.valueOf(resultStatus);
        }
        Integer resultStatusInteger = row.getInteger(a + Model_.STATUS_INTEGER);
        if (0 <= resultStatusInteger && resultStatusInteger < ModelStatus.values().length) {
            result.statusInteger = ModelStatus.values()[resultStatusInteger];
        }
        result.data = row.get(JsonObject.class, row.getColumnIndex(a + Model_.DATA));
        result.createdFrom = new HashSet<>(Arrays.asList(row.getLongArray(a + Model_.CREATED_FROM)));
        result.time = row.getLocalTime(a + Model_.TIME);
        return result;
    }

    public static Model mapS(Row row, String alias) {
        return INSTANCE.map(row, alias);
    }

    @Override
    @SqlMappings({
            @SqlMapping(field = "id", column = "uid"),
            @SqlMapping(field = "status", ignore = true),
            @SqlMapping(field = "statusInteger", ignore = true),
            @SqlMapping(field = "parent", column = "p", alias = "x")
    })
    public Model mapSqlMapping(Row row) {
        if (row == null) {
            return null;
        }
        Model result = new Model();
        result.id = row.getString("uid");
        result.version = row.getInteger(Model_.VERSION);
        result.messageId = row.getString(Model_.MESSAGE_ID);
        result.parent = row.getString("x.p");
        result.processId = row.getString(Model_.PROCESS_ID);
        result.processVersion = row.getString(Model_.PROCESS_VERSION);
        result.data = row.get(JsonObject.class, row.getColumnIndex(Model_.DATA));
        result.createdFrom = new HashSet<>(Arrays.asList(row.getLongArray(Model_.CREATED_FROM)));
        result.time = row.getLocalTime(Model_.TIME);
        return result;
    }

    public static Model mapSqlMappingS(Row row) {
        return INSTANCE.mapSqlMapping(row);
    }

    @Override
    @SqlMappings({
            @SqlMapping(field = "id", column = "uid"),
            @SqlMapping(field = "status", ignore = true),
            @SqlMapping(field = "statusInteger", enumType = SqlEnumType.STRING),
            @SqlMapping(field = "parent", column = "p", alias = "x")
    })
    public Model mapSqlMapping(Row row, String alias) {
        if (row == null) {
            return null;
        }
        String a = "";
        if (alias != null && !alias.isBlank()) {
            a = alias + ".";
        }
        Model result = new Model();
        result.id = row.getString(a + "uid");
        result.version = row.getInteger(a + Model_.VERSION);
        result.messageId = row.getString(a + Model_.MESSAGE_ID);
        result.parent = row.getString("x.p");
        result.processId = row.getString(a + Model_.PROCESS_ID);
        result.processVersion = row.getString(a + Model_.PROCESS_VERSION);
        String resultStatusInteger = row.getString(a + Model_.STATUS_INTEGER);
        if (resultStatusInteger != null && !resultStatusInteger.isBlank()) {
            result.statusInteger = ModelStatus.valueOf(resultStatusInteger);
        }
        result.data = row.get(JsonObject.class, row.getColumnIndex(a + Model_.DATA));
        result.createdFrom = new HashSet<>(Arrays.asList(row.getLongArray(a + Model_.CREATED_FROM)));
        result.time = row.getLocalTime(a + Model_.TIME);
        return result;
    }

    public static Model mapSqlMappingS(Row row, String alias) {
        return INSTANCE.mapSqlMapping(row, alias);
    }
}
