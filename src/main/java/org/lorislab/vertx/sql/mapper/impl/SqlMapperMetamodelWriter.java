package org.lorislab.vertx.sql.mapper.impl;

import com.squareup.javapoet.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import java.io.IOException;

public class SqlMapperMetamodelWriter {

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
