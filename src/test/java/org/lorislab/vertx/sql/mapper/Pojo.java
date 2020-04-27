package org.lorislab.vertx.sql.mapper;

import io.vertx.core.json.JsonObject;

import java.util.Set;

public class Pojo {

    public String id;

    public Integer version;

    public String messageId;

    public String parent;

    public String processId;

    public String processVersion;

    public PojoStatus status;

    public JsonObject data;

    public Set<Long> createdFrom;

}
