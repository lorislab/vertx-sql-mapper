/*
 * Copyright 2020 lorislab.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lorislab.vertx.sql.mapper.impl;

import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Set;

public enum FieldType {

    SHORT(Short.class),

    FLOAT(Float.class),

    DOUBLE(Double.class),

    TEMPORAL(Temporal.class),

    LOCAL_DATE(LocalDate.class),

    LOCAL_TIME(LocalTime.class),

    LOCAL_DATE_TIME(LocalDateTime.class),

    OFFSET_TIME(OffsetTime.class),

    OFFSET_DATE_TIME(OffsetDateTime.class),

    BUFFER("io.vertx.core.buffer.Buffer", "Buffer", false, true),

    UUID(java.util.UUID.class),

    BIG_DECIMAL(BigDecimal.class),

    LIST(List.class.getName(), null, true, true),

    SET(Set.class.getName(), null, true, true),

    STRING(String.class, true),

    LONG(Long.class),

    BOOLEAN(Boolean.class),

    INTEGER(Integer.class, true),

    JSON_OBJECT("io.vertx.core.json.JsonObject", null, false, true),

    ROW("io.vertx.sqlclient.Row", null, false, false),

    ROW_MUTINY("io.vertx.mutiny.sqlclient.Row", null, false, false);

    String clazz;

    String rowMethod;

    boolean collection;

    boolean field;

    boolean enumType;

    FieldType(Class<?> c) {
        this(c.getName(), c.getSimpleName(), false, true);
    }

    FieldType(Class<?> c, boolean enumType) {
        this(c.getName(), c.getSimpleName(), false, true);
        this.enumType = enumType;
    }

    FieldType(String clazz, String rowMethod, boolean collection, boolean field) {
        this.clazz = clazz;
        this.rowMethod = rowMethod;
        this.collection = collection;
        this.field = field;
    }

    public boolean isEnumType() {
        return enumType;
    }

    public boolean isField() {
        return field;
    }

    public String getClazz() {
        return clazz;
    }

    public String getRowMethod() {
        return rowMethod;
    }

    public boolean isCollection() {
        return collection;
    }

    public static FieldType from(String value) {
        for (FieldType t : FieldType.values()) {
            if (t.clazz.equals(value)) {
                return t;
            }
        }
        return null;
    }

}
