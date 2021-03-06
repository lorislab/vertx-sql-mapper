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

import org.lorislab.vertx.sql.mapper.SqlMapper;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.*;

public class ClassInfo {

    TypeElement type;

    String name;

    String superName;

    boolean isInterface;

    SqlMapper mapper;

    AnnotationMirror mapperMirror;

    List<MethodInfo> methods = new ArrayList<>();

    Map<String, TypeInfo> models = new HashMap<>();

    public static ClassInfo build(TypeElement beanType, ProcessingEnvironment processingEnv) {
        if (beanType == null) {
            return null;
        }

        ClassInfo clazz = new ClassInfo();
        clazz.type = beanType;
        clazz.mapper = beanType.getAnnotation(SqlMapper.class);

        Element actionElement = processingEnv.getElementUtils().getTypeElement(SqlMapper.class.getName());
        TypeMirror sqlMapperType = actionElement.asType();
        clazz.mapperMirror = beanType.getAnnotationMirrors().stream().filter(x -> x.getAnnotationType().equals(sqlMapperType)).findFirst().orElse(null);

        clazz.superName = beanType.getSimpleName().toString();
        clazz.name = clazz.superName + clazz.mapper.suffix();
        clazz.isInterface = beanType.getKind() == ElementKind.INTERFACE;

        beanType.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.METHOD)
                .map(e -> (ExecutableElement) e)
                .filter(e ->
                        (clazz.isInterface && !e.isDefault()) ||
                       (!clazz.isInterface && e.getModifiers().contains(Modifier.ABSTRACT))
                )
                .map(e -> MethodInfo.build(e, clazz.models))
                .filter(Objects::nonNull)
                .forEach(clazz::addMethod);

        return clazz;
    }

    private void addMethod(MethodInfo method) {
        methods.add(method);
        models.putIfAbsent(method.returnModel.name, method.returnModel);
    }
}
