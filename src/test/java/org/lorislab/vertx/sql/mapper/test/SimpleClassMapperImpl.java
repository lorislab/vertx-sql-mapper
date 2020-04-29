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
import io.vertx.sqlclient.Row;

import java.util.Arrays;
import java.util.HashSet;

public class SimpleClassMapperImpl extends  SimpleClassMapper {

    public static final SimpleClassMapper INSTANCE = new SimpleClassMapperImpl();

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

}
