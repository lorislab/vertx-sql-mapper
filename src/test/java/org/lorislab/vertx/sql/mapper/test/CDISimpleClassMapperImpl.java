package org.lorislab.vertx.sql.mapper.test;

import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import java.util.Arrays;
import java.util.HashSet;
import javax.enterprise.context.ApplicationScoped;
import org.lorislab.vertx.sql.mapper.data.CustomAnnotation;

@ApplicationScoped
@CustomAnnotation
public class CDISimpleClassMapperImpl extends  CDISimpleClassMapper {
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
}
