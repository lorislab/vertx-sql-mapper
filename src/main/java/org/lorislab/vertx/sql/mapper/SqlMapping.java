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
package org.lorislab.vertx.sql.mapper;

import java.lang.annotation.*;

/**
 * SQL mapping optional configuration.
 */
@Repeatable(SqlMappings.class)
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface SqlMapping {

    /**
     * The field name.
     * @return the field name.
     */
    String field();

    /**
     * The column name. Default is field or {@code SqlColumn} value.
     * @return the column name.
     */
    String column() default "";

    /**
     * Ignore field for the mapping
     * @return ignore field flag.
     */
    boolean ignore() default false;

    /**
     * The alias for the field.
     * @return alias for the mapping of the field.
     */
    String alias() default "";

    /**
     * Only for enumeration. Define the enumeration mapping type.
     * @return the enumeration mapping type.
     */
    SqlEnumType enumType() default SqlEnumType.DEFAULT;
}
