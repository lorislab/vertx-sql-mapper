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
        if (resultStatus != null) {
            result.status = ModelStatus.valueOf(resultStatus);
        }
        Integer resultStatusInteger = row.getInteger(Model_.STATUS_INTEGER);
        if (resultStatusInteger != null) {
            result.statusInteger = ModelStatus.values()[resultStatusInteger];
        }
        result.data = row.get(JsonObject.class, row.getColumnIndex(Model_.DATA));
        result.createdFrom = new HashSet<>(Arrays.asList(row.getLongArray(Model_.CREATED_FROM)));
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
        result.id = row.getString(a + "id");
        result.version = row.getInteger(a + "version");
        result.messageId = row.getString(a + "messageId");
        result.parent = row.getString(a + "parent");
        result.processId = row.getString(a + "process");
        result.processVersion = row.getString(a + "processVersion");
        String resultStatus = row.getString(a + "status");
        if (resultStatus != null) {
            result.status = ModelStatus.valueOf(resultStatus);
        }
        Integer resultStatusInteger = row.getInteger(a + "statusInteger");
        if (resultStatusInteger != null) {
            result.statusInteger = ModelStatus.values()[resultStatusInteger];
        }
        result.data = row.get(JsonObject.class, row.getColumnIndex(a + "data"));
        result.createdFrom = new HashSet<>(Arrays.asList(row.getLongArray(a + "createdFrom")));
        return result;
    }

    public static Model mapS(Row row, String alias) {
        return INSTANCE.map(row, alias);
    }

    @Override
    @SqlMappings({
            @SqlMapping(target = "id", column = "uid"),
            @SqlMapping(target = "status", ignore = true),
            @SqlMapping(target = "statusInteger", ignore = true),
            @SqlMapping(target = "parent", column = "p", alias = "x")
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
        return result;
    }

    public static Model mapSqlMappingS(Row row) {
        return INSTANCE.mapSqlMapping(row);
    }

    @Override
    @SqlMappings({
            @SqlMapping(target = "id", column = "uid"),
            @SqlMapping(target = "status", ignore = true),
            @SqlMapping(target = "statusInteger", enumType = SqlEnumType.STRING),
            @SqlMapping(target = "parent", column = "p", alias = "x")
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
        result.version = row.getInteger(a + "version");
        result.messageId = row.getString(a + "messageId");
        result.parent = row.getString("x.p");
        result.processId = row.getString(a + "process");
        result.processVersion = row.getString(a + "processVersion");
        String resultStatusInteger = row.getString(a + "statusInteger");
        if (resultStatusInteger != null) {
            result.statusInteger = ModelStatus.valueOf(resultStatusInteger);
        }
        result.data = row.get(JsonObject.class, row.getColumnIndex(a + "data"));
        result.createdFrom = new HashSet<>(Arrays.asList(row.getLongArray(a + "createdFrom")));
        return result;
    }

    public static Model mapSqlMappingS(Row row, String alias) {
        return INSTANCE.mapSqlMapping(row, alias);
    }
}
