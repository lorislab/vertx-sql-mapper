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

import org.lorislab.vertx.sql.mapper.SqlColumn;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

public class FieldInfo {

    private static final String REGEX = "([a-z])([A-Z]+)";
    private static final String REPLACEMENT = "$1_$2";

    String name;

    String constName;

    String column;

    Element element;

    ElementKind kind;

    String qualifiedName;

    DeclaredType declaredType;

    public static FieldInfo build(Element element) {
        if (element == null) {
            return null;
        }
        SqlColumn ac = element.getAnnotation(SqlColumn.class);
        // ignore field
        if (ac != null && ac.ignore()) {
            return null;
        }
        FieldInfo field = new FieldInfo();
        field.element = element;
        field.name = element.getSimpleName().toString();
        field.column = field.name;

        TypeMirror t = element.asType();
        field.declaredType = (DeclaredType)t;
        TypeElement typeElement = (TypeElement) field.declaredType.asElement();
        field.kind = typeElement.getKind();
        field.qualifiedName = typeElement.getQualifiedName().toString();

        if (ac != null && !ac.value().isBlank()) {
            field.column = ac.value();
        }

        field.constName = field.name
                .replaceAll(REGEX, REPLACEMENT)
                .toUpperCase();

        return field;
    }
}
