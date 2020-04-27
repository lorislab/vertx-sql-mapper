package org.lorislab.vertx.sql.mapper.test;

import io.vertx.core.json.JsonObject;
import org.lorislab.vertx.sql.mapper.SqlColumn;
import org.lorislab.vertx.sql.mapper.SqlEnum;
import org.lorislab.vertx.sql.mapper.SqlEnumType;

import java.util.Set;

public class Model {

    public String id;

    public Integer version;

    public String messageId;

    public String parent;

    @SqlColumn("process")
    public String processId;

    public String processVersion;

    public ModelStatus status;

    @SqlEnum(SqlEnumType.INTEGER)
    public ModelStatus statusInteger;

    public JsonObject data;

    public Set<Long> createdFrom;

}
