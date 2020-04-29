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

/**
 * Enumeration mapping types.
 */
public enum SqlEnumType {

    /**
     * The default is equal to STRING.
     */
    DEFAULT,
    /**
     * Mapping from string column to enumeration type.
     */
    STRING,
    /**
     * Mapping from integer column to enumeration type.
     */
    INTEGER;
}
