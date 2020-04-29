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

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.*;

public class TypeInfo {

    String name;

    String metamodel;

    TypeElement type;

    Map<String, FieldInfo>  map = new HashMap<>();

    List<FieldInfo>  fields = new ArrayList<>();

    public static TypeInfo build(TypeElement type) {
        if (type == null) {
            return null;
        }
        TypeInfo model = new TypeInfo();
        model.type = type;
        model.name = model.type.getSimpleName().toString();
        model.metamodel = model.name + "_";

        List<? extends Element> elements = type.getEnclosedElements();
        if (elements!= null) {
            elements.stream()
                    .filter(f -> f.getKind() == ElementKind.FIELD)
                    .filter(f -> f.getModifiers().contains(Modifier.PUBLIC))
                    .map(FieldInfo::build)
                    .filter(Objects::nonNull)
                    .forEach(model::addField);
        }
        return model;
    }

    private void addField(FieldInfo f) {
        fields.add(f);
        map.put(f.name, f);
    }

}
