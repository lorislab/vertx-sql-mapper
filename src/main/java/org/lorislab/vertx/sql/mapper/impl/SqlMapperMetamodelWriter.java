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

import com.squareup.javapoet.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import java.io.IOException;

public class SqlMapperMetamodelWriter {

    private SqlMapperMetamodelWriter() {}

    public static void generateMetamodel(ProcessingEnvironment env, TypeInfo model) throws IOException {
        // create meta model interface
        TypeSpec.Builder classBuilder = TypeSpec.interfaceBuilder(model.metamodel).addModifiers(Modifier.PUBLIC);

        // add all public fields
        for (FieldInfo field : model.fields) {
            classBuilder.addField(
                    FieldSpec.builder(TypeName.get(String.class),
                            field.constName, Modifier.STATIC, Modifier.FINAL, Modifier.PUBLIC)
                            .initializer("$S", field.column)
                            .build()
            );
        }

        // save metamodel interface
        JavaFile javaFile = JavaFile
                .builder(ClassName.get(model.type).packageName(), classBuilder.build())
                .skipJavaLangImports(true)
                .indent("    ")
                .build();
        javaFile.writeTo(env.getFiler());
    }
}
