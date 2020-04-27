package org.lorislab.vertx.sql.mapper;

import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;

import java.util.Arrays;
import java.util.HashSet;

public class SimpleMapperImpl implements SimpleMapper {

    public static final SimpleMapper INSTANCE = new SimpleMapperImpl();

    @Override
    public Pojo map(Row row) {
        if (row == null) {
            return null;
        }
        Pojo result = new Pojo();
        result.id = row.getString("id");
        result.version = row.getInteger("version");
        result.messageId = row.getString("messageId");
        result.parent = row.getString("parent");
        result.processId = row.getString("processId");
        result.processVersion = row.getString("processVersion");
        String result_status = row.getString("status");
        if (result_status != null) {
            result.status = PojoStatus.valueOf(result_status);
        }
        result.data = row.get(JsonObject.class, row.getColumnIndex("data"));
        result.createdFrom = new HashSet<>(Arrays.asList(row.getLongArray("createdFrom")));
        return result;
    }

    @Override
    public Pojo map(Row row, String alias) {
        if (row == null) {
            return null;
        }
        String a = "";
        if (alias != null && !alias.isBlank()) {
            a = alias + ".";
        }
        Pojo result = new Pojo();
        result.id = row.getString(a + "id");
        result.version = row.getInteger(a + "version");
        result.messageId = row.getString(a + "messageId");
        result.parent = row.getString(a + "parent");
        result.processId = row.getString(a + "processId");
        result.processVersion = row.getString(a + "processVersion");
        String result_status = row.getString(a + "status");
        if (result_status != null) {
            result.status = PojoStatus.valueOf(result_status);
        }
        result.data = row.get(JsonObject.class, row.getColumnIndex(a + "data"));
        result.createdFrom = new HashSet<>(Arrays.asList(row.getLongArray(a + "createdFrom")));
        return result;
    }

    @Override
    public Pojo mapSqlMapping(Row row) {
        if (row == null) {
            return null;
        }
        Pojo result = new Pojo();
        result.id = row.getString("uid");
        result.version = row.getInteger("version");
        result.messageId = row.getString("messageId");
        result.parent = row.getString("x.parent");
        result.processId = row.getString("processId");
        result.processVersion = row.getString("processVersion");
        result.data = row.get(JsonObject.class, row.getColumnIndex("data"));
        result.createdFrom = new HashSet<>(Arrays.asList(row.getLongArray("createdFrom")));
        return result;
    }

    @Override
    public Pojo mapSqlMapping(Row row, String alias) {
        if (row == null) {
            return null;
        }
        String a = "";
        if (alias != null && !alias.isBlank()) {
            a = alias + ".";
        }
        Pojo result = new Pojo();
        result.id = row.getString(a + "uid");
        result.version = row.getInteger(a + "version");
        result.messageId = row.getString(a + "messageId");
        result.parent = row.getString("x.parent");
        result.processId = row.getString(a + "processId");
        result.processVersion = row.getString(a + "processVersion");
        result.data = row.get(JsonObject.class, row.getColumnIndex(a + "data"));
        result.createdFrom = new HashSet<>(Arrays.asList(row.getLongArray(a + "createdFrom")));
        return result;
    }

    public static Pojo mapS(Row row) {
        return INSTANCE.map(row);
    }

    public static Pojo mapS(Row row, String alias) {
        return INSTANCE.map(row, alias);
    }

    public static Pojo mapSqlMappingS(Row row) {
        return INSTANCE.mapSqlMapping(row);
    }

    public static Pojo mapSqlMappingS(Row row, String alias) {
        return INSTANCE.mapSqlMapping(row, alias);
    }
}
