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
import io.vertx.core.json.JsonObject;
import org.lorislab.vertx.sql.mapper.SqlEnum;
import org.lorislab.vertx.sql.mapper.SqlEnumType;
import org.lorislab.vertx.sql.mapper.SqlMapper;
import org.lorislab.vertx.sql.mapper.SqlMapping;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

public class SqlMapperImpClassWriter {

    private SqlMapperImpClassWriter() {
    }

    private static void addEnum(MethodSpec.Builder mb, MethodInfo method, FieldInfo field, MergeMapping mapping) {

        // check the field annotation for default method mapping
        SqlEnumType type = mapping.enumType;
        if (type == SqlEnumType.DEFAULT) {
            SqlEnum me = field.element.getAnnotation(SqlEnum.class);
            if (me != null) {
                type = me.value();
            }
        }
        // set default
        if (type == SqlEnumType.DEFAULT) {
            type = SqlEnumType.STRING;
        }
        FieldType ft = FieldType.STRING;
        if (type == SqlEnumType.INTEGER) {
            ft = FieldType.INTEGER;
        }

        String tmp = method.resultVar + field.name.substring(0, 1).toUpperCase() + field.name.substring(1);
        if (mapping.column == null) {
            if (!mapping.alias) {
                mb.addStatement("$N $N = $N.get$N($N.$N)", ft.rowMethod, tmp, method.row, ft.rowMethod, method.returnModel.metamodel, field.constName);
            } else {
                mb.addStatement("$N $N = $N.get$N($N + $S)", ft.rowMethod, tmp, method.row, ft.rowMethod, method.aliasVar, field.column);
            }
        } else {
            if (!mapping.alias) {
                mb.addStatement("$N $N = $N.get$N($S)", ft.rowMethod, tmp, method.row, ft.rowMethod, mapping.column);
            } else {
                mb.addStatement("$N $N = $N.get$N($N + $S)", ft.rowMethod, tmp, method.row, ft.rowMethod, method.aliasVar, mapping.column);
            }
        }
        mb.beginControlFlow("if ($N != null)", tmp);
        if (type == SqlEnumType.INTEGER) {
            mb.addStatement("$N.$N = $T.values()[$N]", method.resultVar, field.name, field.element, tmp);
        } else {
            mb.addStatement("$N.$N = $T.valueOf($N)", method.resultVar, field.name, field.element, tmp);
        }
        mb.endControlFlow();
    }

    private static void addClass(MethodSpec.Builder mb, MethodInfo method, FieldInfo field, FieldType type, MergeMapping mapping) {
        if (mapping.column == null) {
            if (!mapping.alias) {
                mb.addStatement("$N.$N = $N.get$N($N.$N)", method.resultVar, field.name, method.row, type.rowMethod, method.returnModel.metamodel, field.constName);
            } else {
                mb.addStatement("$N.$N = $N.get$N($N + $S)", method.resultVar, field.name, method.row, type.rowMethod, method.aliasVar, field.column);
            }
        } else {
            if (!mapping.alias) {
                mb.addStatement("$N.$N = $N.get$N($S)", method.resultVar, field.name, method.row, type.rowMethod, mapping.column);
            } else {
                mb.addStatement("$N.$N = $N.get$N($N + $S)", method.resultVar, field.name, method.row, type.rowMethod, method.aliasVar, mapping.column);
            }
        }
    }

    private static void addJsonObject(MethodSpec.Builder mb, MethodInfo method, FieldInfo field, MergeMapping mapping) {
        if (mapping.column == null) {
            if (!mapping.alias) {
                mb.addStatement("$N.$N = $N.get($T.class, $N.getColumnIndex($N.$N))", method.resultVar, field.name, method.row, JsonObject.class, method.row, method.returnModel.metamodel, field.constName);
            } else {
                mb.addStatement("$N.$N = $N.get($T.class, $N.getColumnIndex($N + $S))", method.resultVar, field.name, method.row, JsonObject.class, method.row, method.aliasVar, field.column);
            }
        } else {
            if (!mapping.alias) {
                mb.addStatement("$N.$N = $N.get($T.class, $N.getColumnIndex($S))", method.resultVar, field.name, method.row, JsonObject.class, method.row, mapping.column);
            } else {
                mb.addStatement("$N.$N = $N.get($T.class, $N.getColumnIndex($N + $S))", method.resultVar, field.name, method.row, JsonObject.class, method.row, method.aliasVar, mapping.column);
            }
        }
    }

    private static void addList(MethodSpec.Builder mb, MethodInfo method, FieldInfo field, FieldType type, MergeMapping mapping) {
        if (mapping.column == null) {
            if (!mapping.alias) {
                mb.addStatement("$N.$N = new $T.asList($N.get$NArray($N.$N))", method.resultVar, field.name, Arrays.class, method.row, type.rowMethod, method.returnModel.metamodel, field.constName);
            } else {
                mb.addStatement("$N.$N = new $T.asList($N.get$NArray($N + $S))", method.resultVar, field.name, Arrays.class, method.row, type.rowMethod, method.aliasVar, field.column);
            }
        } else {
            if (!mapping.alias) {
                mb.addStatement("$N.$N = new $T.asList($N.get$NArray($S))", method.resultVar, field.name, Arrays.class, method.row, type.rowMethod, mapping.column);
            } else {
                mb.addStatement("$N.$N = new $T.asList($N.get$NArray($N + $S))", method.resultVar, field.name, Arrays.class, method.row, type.rowMethod, method.aliasVar, mapping.column);
            }
        }
    }

    private static void addSet(MethodSpec.Builder mb, MethodInfo method, FieldInfo field, FieldType type, MergeMapping mapping) {
        if (mapping.column == null) {
            if (!mapping.alias) {
                mb.addStatement("$N.$N = new $T<>($T.asList($N.get$NArray($N.$N)))", method.resultVar, field.name, HashSet.class, Arrays.class, method.row, type.rowMethod, method.returnModel.metamodel, field.constName);
            } else {
                mb.addStatement("$N.$N = new $T<>($T.asList($N.get$NArray($N + $S)))", method.resultVar, field.name, HashSet.class, Arrays.class, method.row, type.rowMethod, method.aliasVar, field.column);
            }
        } else {
            if (!mapping.alias) {
                mb.addStatement("$N.$N = new $T<>($T.asList($N.get$NArray($S)))", method.resultVar, field.name, HashSet.class, Arrays.class, method.row, type.rowMethod, mapping.column);
            } else {
                mb.addStatement("$N.$N = new $T<>($T.asList($N.get$NArray($N + $S)))", method.resultVar, field.name, HashSet.class, Arrays.class, method.row, type.rowMethod, method.aliasVar, mapping.column);
            }
        }
    }

    private static class MergeMapping {
        boolean alias = false;
        String column = null;
        boolean ignore = false;
        SqlEnumType enumType = SqlEnumType.DEFAULT;
    }

    private static MergeMapping merge(MethodInfo method, FieldInfo field) {
        MergeMapping merge = new MergeMapping();
        merge.alias = method.alias != null;
        SqlMapping mapping = method.mappings.get(field.name);
        if (mapping != null) {
            // ignore field
            merge.ignore = mapping.ignore();
            // add column name
            if (!mapping.column().isBlank()) {
                merge.column = mapping.column();
            }
            // add alias to column
            if (!mapping.alias().isBlank()) {
                if (merge.column == null) {
                    merge.column = field.column;
                }
                merge.column = mapping.alias() + "." + merge.column;
                // ignore global alias
                merge.alias = false;
            }
            merge.enumType = mapping.enumType();
        }
        return merge;
    }
    private static void addCollection(ProcessingEnvironment env, MethodSpec.Builder mb, MethodInfo method, FieldInfo field, MergeMapping mapping, FieldType type) {
        String item = field.declaredType.getTypeArguments().get(0).toString();
        FieldType itemType = FieldType.from(item);
        if (itemType != null && itemType.isField()) {
            if (FieldType.SET == type) {
                addSet(mb, method, field, itemType, mapping);
            } else if (FieldType.LIST == type) {
                addList(mb, method, field, itemType, mapping);
            }
        } else {
            env.getMessager().printMessage(Diagnostic.Kind.ERROR, "Not supported collection item type " + item + " for the field " + field.name, field.element);
        }
    }

    private static void addField(ProcessingEnvironment env, MethodSpec.Builder mb, MethodInfo method, FieldInfo field, MergeMapping mapping) {
        if (field.kind == ElementKind.ENUM) {
            addEnum(mb, method, field, mapping);
        } else {
            FieldType type = FieldType.from(field.qualifiedName);
            if (type != null && type.isField()) {
                if (type.isCollection()) {
                    addCollection(env, mb, method, field, mapping, type);
                } else {
                    if (FieldType.JSON_OBJECT == type) {
                        addJsonObject(mb, method, field, mapping);
                    } else {
                        addClass(mb, method, field, type, mapping);
                    }
                }
            } else {
                env.getMessager().printMessage(Diagnostic.Kind.ERROR, "Not supported type " + field.qualifiedName + " for the field " + field.name, field.element);
            }
        }
    }

    private static MethodSpec createMethod(ProcessingEnvironment env, MethodInfo method) {

        // create method
        MethodSpec.Builder mb = MethodSpec.overriding(method.element)
                .beginControlFlow("if ($N == null)", method.row)
                .addStatement("return null")
                .endControlFlow();

        if (method.alias != null) {
            mb.addStatement("String $N = \"\"", method.aliasVar);
            mb.beginControlFlow("if ($N != null && !$N.isBlank())", method.alias, method.alias);
            mb.addStatement("$N = $N + \".\"", method.aliasVar, method.alias);
            mb.endControlFlow();
        }

        // create instance
        mb.addStatement("$T $N = new $T()", method.returnModel.type, method.resultVar, method.returnModel.type);

        // add mapping of return type fields
        if (method.returnModel.fields != null) {
            for (FieldInfo field : method.returnModel.fields) {
                MergeMapping mapping = merge(method, field);
                if (!mapping.ignore) {
                    addField(env, mb, method, field, mapping);
                }
            }
        }

        // return statement
        mb.addStatement("return $N", method.resultVar);
        return mb.build();
    }

    public static void createMapper(ProcessingEnvironment env, ClassInfo clazz) throws IOException {

        // create metamodels for the types
        for (TypeInfo model : clazz.models.values()) {
            SqlMapperMetamodelWriter.generateMetamodel(env, model);
        }

        // create class
        TypeSpec.Builder classBuilder = createClass(clazz);

        // create methods
        for (MethodInfo item : clazz.methods) {
            MethodSpec method = createMethod(env, item);
            classBuilder.addMethod(method);
            // create static method
            if (clazz.mapper.staticMethod()) {
                MethodSpec staticMethod = createStaticForMethod(method, clazz.mapper);
                classBuilder.addMethod(staticMethod);
            }
        }

        // save implementation class
        JavaFile javaFile = JavaFile
                .builder(ClassName.get(clazz.type).packageName(), classBuilder.build())
                .skipJavaLangImports(true)
                .indent("    ")
                .build();
        javaFile.writeTo(env.getFiler());
    }

    private static TypeSpec.Builder createClass(ClassInfo clazz) {
        TypeName type = TypeName.get(clazz.type.asType());

        // create class
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(clazz.name).addModifiers(Modifier.PUBLIC);
        if (clazz.isInterface) {
            classBuilder.addSuperinterface(type);
        } else {
            classBuilder.superclass(type);
        }

        // add static instance
        classBuilder.addField(
                FieldSpec.builder(type, clazz.mapper.instanceName())
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("new $N()", clazz.name)
                        .build()
        );

        return classBuilder;
    }

    private static MethodSpec createStaticForMethod(MethodSpec method, SqlMapper mapper) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(method.name + mapper.staticMethodSuffix())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(method.returnType)
                .addParameters(method.parameters);

        int size = method.parameters.size();
        if (size == 1) {
            builder.addStatement("return $N.$N($N)", mapper.instanceName(), method.name, method.parameters.get(0).name);
        } else {
            builder.addStatement("return $N.$N($N,$N)", mapper.instanceName(), method.name, method.parameters.get(0).name, method.parameters.get(1).name);
        }
        return builder.build();
    }
}
