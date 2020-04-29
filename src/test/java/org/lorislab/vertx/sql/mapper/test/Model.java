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
