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

import org.lorislab.vertx.sql.mapper.SqlMapping;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MethodInfo {

    String name;

    TypeInfo returnModel;

    ExecutableElement element;

    int parameterSize;

    String row;

    String alias;

    String aliasVar = "a";

    Map<String, SqlMapping> mappings;

    String resultVar = "result";


    public static MethodInfo build(ExecutableElement element, Map<String, TypeInfo> models) {
        if (element == null) {
            return null;
        }
        if (element.getParameters() == null || element.getParameters().isEmpty()) {
            throw new IllegalStateException("Method required 'row' or 'row, alias' combination of input parameters");
        }
        MethodInfo method = new MethodInfo();
        method.element = element;
        method.parameterSize = element.getParameters().size();
        method.name = element.getSimpleName().toString();

        // read parameters
        readParameter(element, method, 0);
        if (method.parameterSize == 2) {
            readParameter(element, method, 1);
        }
        // the row parameter is required
        if (method.parameterSize == 1 && method.row == null) {
            throw new IllegalStateException("Missing row input parameter! Method: " + method.name);
        }
        // the row and alias parameter is required
        if (method.parameterSize == 2 && (method.row == null || method.alias == null)) {
            throw new IllegalStateException("Wrong type of the input parameter! Method: " + method.name);
        }

        TypeElement returnType = getTypeElement(element.getReturnType());
        method.returnModel = models.get(returnType.toString());
        if (method.returnModel == null) {
            method.returnModel = TypeInfo.build(returnType);
        }

        SqlMapping[] am = element.getAnnotationsByType(SqlMapping.class);
        method.mappings = Stream.of(am).collect(Collectors.toMap(SqlMapping::field, Function.identity()));

        return method;
    }

    private static void readParameter(ExecutableElement element, MethodInfo method, int index) {
        VariableElement p = element.getParameters().get(index);
        FieldType type = FieldType.from(p.asType().toString());
        if (FieldType.STRING == type) {
            method.alias = p.getSimpleName().toString();
        } else if (type == FieldType.ROW_MUTINY || type == FieldType.ROW ) {
            method.row = p.getSimpleName().toString();
        } else {
            throw new IllegalStateException("Wrong " + (index+1) + " type of the input parameter! Required type string or Row! Method: " + method.name);
        }
    }

    private static TypeElement getTypeElement(TypeMirror type) {
        DeclaredType dt = (DeclaredType)type;
        return (TypeElement)dt.asElement();
    }
}
