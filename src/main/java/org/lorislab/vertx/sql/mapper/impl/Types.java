package org.lorislab.vertx.sql.mapper.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface Types {

    String LIST = "java.util.List";

    String SET = "java.util.Set";

    String STRING = "java.lang.String";

    String LONG = "java.lang.Long";

    String INTEGER = "java.lang.Integer";

    String JSON_OBJECT = "io.vertx.core.json.JsonObject";

    String ROW = "io.vertx.sqlclient.Row";

    String ROW_MUTINY = "io.vertx.mutiny.sqlclient.Row";

    Set<String> ROWS = new HashSet<>(Arrays.asList(ROW, ROW_MUTINY));
}
