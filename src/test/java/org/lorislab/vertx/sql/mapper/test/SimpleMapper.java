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

import io.vertx.sqlclient.Row;
import org.lorislab.vertx.sql.mapper.SqlEnumType;
import org.lorislab.vertx.sql.mapper.SqlMapper;
import org.lorislab.vertx.sql.mapper.SqlMapping;

@SqlMapper(cdi = false, instanceField = true, staticMethod = true)
public interface SimpleMapper {

    Model map(Row row);

    Model map(Row row, String alias);

    @SqlMapping(field = "id", column = "uid")
    @SqlMapping(field = "status", ignore = true)
    @SqlMapping(field = "statusInteger", ignore = true)
    @SqlMapping(field = "parent", column = "p", alias = "x")
    Model mapSqlMapping(Row row);

    @SqlMapping(field = "id", column = "uid")
    @SqlMapping(field = "status", ignore = true)
    @SqlMapping(field = "statusInteger", enumType = SqlEnumType.STRING)
    @SqlMapping(field = "parent", column = "p", alias = "x")
    Model mapSqlMapping(Row row, String alias);
}
