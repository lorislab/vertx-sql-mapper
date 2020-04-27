package org.lorislab.vertx.sql.mapper.test;

import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import java.lang.Override;
import java.lang.String;
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
        result.id = row.getString("id");
        result.version = row.getInteger("version");
        result.messageId = row.getString("messageId");
        result.parent = row.getString("parent");
        result.processId = row.getString("process");
        result.processVersion = row.getString("processVersion");
        String resultStatus = row.getString("status");
        if (resultStatus != null) {
            result.status = ModelStatus.valueOf(resultStatus);
        }
        Integer resultStatusInteger = row.getInteger("statusInteger");
        if (resultStatusInteger != null) {
            result.statusInteger = ModelStatus.values()[resultStatusInteger];
        }
        result.data = row.get(JsonObject.class, row.getColumnIndex("data"));
        result.createdFrom = new HashSet<>(Arrays.asList(row.getLongArray("createdFrom")));
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
        result.version = row.getInteger("version");
        result.messageId = row.getString("messageId");
        result.parent = row.getString("x.p");
        result.processId = row.getString("process");
        result.processVersion = row.getString("processVersion");
        result.data = row.get(JsonObject.class, row.getColumnIndex("data"));
        result.createdFrom = new HashSet<>(Arrays.asList(row.getLongArray("createdFrom")));
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
