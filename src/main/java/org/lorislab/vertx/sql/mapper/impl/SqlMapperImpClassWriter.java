package org.lorislab.vertx.sql.mapper.impl;

import com.squareup.javapoet.*;
import io.vertx.core.json.JsonObject;
import org.lorislab.vertx.sql.mapper.SqlEnum;
import org.lorislab.vertx.sql.mapper.SqlEnumType;
import org.lorislab.vertx.sql.mapper.SqlMapper;
import org.lorislab.vertx.sql.mapper.SqlMapping;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

public class SqlMapperImpClassWriter {

    private SqlMapperImpClassWriter() {}

    private static void addEnumFieldMapping(MethodSpec.Builder mb, MethodInfo method, FieldInfo field, String column, boolean alias, SqlEnumType enumType) {

        // check the field annotation for default method mapping
        SqlEnumType type = enumType;
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

        String tmp = method.resultVar + field.name.substring(0, 1).toUpperCase() + field.name.substring(1);
        switch (type) {
            case STRING:
                if (column == null) {
                    if (!alias) {
                        mb.addStatement("String $N = $N.getString($N.$N)", tmp, method.row, method.returnModel.metamodel, field.constName);
                    } else {
                        mb.addStatement("String $N = $N.getString($N + $S)", tmp, method.row, method.aliasVar, field.column);
                    }
                } else {
                    if (!alias) {
                        mb.addStatement("String $N = $N.getString($S)", tmp, method.row, column);
                    } else {
                        mb.addStatement("String $N = $N.getString($N + $S)", tmp, method.row, method.aliasVar, column);
                    }
                }
                mb.beginControlFlow("if ($N != null)", tmp);
                mb.addStatement("$N.$N = $T.valueOf($N)", method.resultVar, field.name, field.element, tmp);
                mb.endControlFlow();
                break;
            case INTEGER:
                if (column == null) {
                    if (!alias) {
                        mb.addStatement("Integer $N = $N.getInteger($N.$N)", tmp, method.row, method.returnModel.metamodel, field.constName);
                    } else {
                        mb.addStatement("Integer $N = $N.getInteger($N + $S)", tmp, method.row, method.aliasVar, field.column);
                    }
                } else {
                    if (!alias) {
                        mb.addStatement("Integer $N = $N.getInteger($S)", tmp, method.row, column);
                    } else {
                        mb.addStatement("Integer $N = $N.getInteger($N + $S)", tmp, method.row, method.aliasVar, column);
                    }
                }
                mb.beginControlFlow("if ($N != null)", tmp);
                mb.addStatement("$N.$N = $T.values()[$N]", method.resultVar, field.name, field.element, tmp);
                mb.endControlFlow();
                break;
            default:
                System.out.println("ERROR enum mapping for " + field.name + " and type " + field.element);
        }
    }

    private static void addClassFieldMapping(MethodSpec.Builder mb, MethodInfo method, FieldInfo field, String column, boolean alias) {
        switch (field.qualifiedName) {
            case Types.STRING:
                if (column == null) {
                    if (!alias) {
                        mb.addStatement("$N.$N = $N.getString($N.$N)", method.resultVar, field.name, method.row, method.returnModel.metamodel, field.constName);
                    } else {
                        mb.addStatement("$N.$N = $N.getString($N + $S)", method.resultVar, field.name, method.row, method.aliasVar, field.column);
                    }
                } else {
                    if (!alias) {
                        mb.addStatement("$N.$N = $N.getString($S)", method.resultVar, field.name, method.row, column);
                    } else {
                        mb.addStatement("$N.$N = $N.getString($N + $S)", method.resultVar, field.name, method.row, method.aliasVar, column);
                    }
                }
                break;
            case Types.LONG:
                if (column == null) {
                    if (!alias) {
                        mb.addStatement("$N.$N = $N.getLong($N.$N)", method.resultVar, field.name, method.row, method.returnModel.metamodel, field.constName);
                    } else {
                        mb.addStatement("$N.$N = $N.getLong($N + $S)", method.resultVar, field.name, method.row, method.aliasVar, field.column);
                    }
                } else {
                    if (!alias) {
                        mb.addStatement("$N.$N = $N.getLong($S)", method.resultVar, field.name, method.row, column);
                    } else {
                        mb.addStatement("$N.$N = $N.getLong($N + $S)", method.resultVar, field.name, method.row, method.aliasVar, column);
                    }
                }
                break;
            case Types.INTEGER:
                if (column == null) {
                    if (!alias) {
                        mb.addStatement("$N.$N = $N.getInteger($N.$N)", method.resultVar, field.name, method.row, method.returnModel.metamodel, field.constName);
                    } else {
                        mb.addStatement("$N.$N = $N.getInteger($N + $S)", method.resultVar, field.name, method.row, method.aliasVar, field.column);
                    }
                } else {
                    if (!alias) {
                        mb.addStatement("$N.$N = $N.getInteger($S)", method.resultVar, field.name, method.row, column);
                    } else {
                        mb.addStatement("$N.$N = $N.getInteger($N + $S)", method.resultVar, field.name, method.row, method.aliasVar, column);
                    }
                }
                break;
            case Types.JSON_OBJECT:
                if (column == null) {
                    if (!alias) {
                        mb.addStatement("$N.$N = $N.get($T.class, $N.getColumnIndex($N.$N))", method.resultVar, field.name, method.row, JsonObject.class, method.row, method.returnModel.metamodel, field.constName);
                    } else {
                        mb.addStatement("$N.$N = $N.get($T.class, $N.getColumnIndex($N + $S))", method.resultVar, field.name, method.row, JsonObject.class, method.row, method.aliasVar, field.column);
                    }
                } else {
                    if (!alias) {
                        mb.addStatement("$N.$N = $N.get($T.class, $N.getColumnIndex($S))", method.resultVar, field.name, method.row, JsonObject.class, method.row, column);
                    } else {
                        mb.addStatement("$N.$N = $N.get($T.class, $N.getColumnIndex($N + $S))", method.resultVar, field.name, method.row, JsonObject.class, method.row, method.aliasVar, column);
                    }
                }
                break;
            default:
                System.out.println("FIELD " + field.element.getKind() + " type " + field.element.asType() + " name " + field.name);
        }
    }

    private static String getMethodName(String type) {
        switch (type) {
            case Types.STRING:
                return "String";
            case Types.LONG:
                return "Long";
            case Types.INTEGER:
                return "Integer";
        }
        return null;
    }

    private static void addInterfaceFieldMapping(MethodSpec.Builder mb, MethodInfo method, FieldInfo field, String column, boolean alias) {
        String methodName = getMethodName(field.declaredType.getTypeArguments().get(0).toString());
        if (methodName == null) {
            System.out.println("Not supported " + field.name + " type " + field.declaredType.getTypeArguments().get(0));
            return;
        }
        if (Types.SET.equals(field.qualifiedName)) {
            if (column == null) {
                if (!alias) {
                    mb.addStatement("$N.$N = new $T<>($T.asList($N.get$NArray($N.$N)))", method.resultVar, field.name, HashSet.class, Arrays.class, method.row, methodName, method.returnModel.metamodel, field.constName);
                } else {
                    mb.addStatement("$N.$N = new $T<>($T.asList($N.get$NArray($N + $S)))", method.resultVar, field.name, HashSet.class, Arrays.class, method.row, methodName, method.aliasVar, field.column);
                }
            } else {
                if (!alias) {
                    mb.addStatement("$N.$N = new $T<>($T.asList($N.get$NArray($S)))", method.resultVar, field.name, HashSet.class, Arrays.class, method.row, methodName, column);
                } else {
                    mb.addStatement("$N.$N = new $T<>($T.asList($N.get$NArray($N + $S)))", method.resultVar, field.name, HashSet.class, Arrays.class, method.row, methodName, method.aliasVar, column);
                }
            }
        } else if (Types.LIST.equals(field.qualifiedName)) {
            if (column == null) {
                if (!alias) {
                    mb.addStatement("$N.$N = new $T.asList($N.get$NArray($N.$N))", method.resultVar, field.name, Arrays.class, method.row, methodName, method.returnModel.metamodel, field.constName);
                } else {
                    mb.addStatement("$N.$N = new $T.asList($N.get$NArray($N + $S))", method.resultVar, field.name, Arrays.class, method.row, methodName, method.aliasVar, field.column);
                }
            } else {
                if (!alias) {
                    mb.addStatement("$N.$N = new $T.asList($N.get$NArray($S))", method.resultVar, field.name, Arrays.class, method.row, methodName, column);
                } else {
                    mb.addStatement("$N.$N = new $T.asList($N.get$NArray($N + $S))", method.resultVar, field.name, Arrays.class, method.row, methodName, method.aliasVar, column);
                }
            }
        } else {
            System.out.println("Not supported " + field.name + " type " + field.qualifiedName);
        }
    }

    private static void addFieldMapping(MethodSpec.Builder mb, MethodInfo method, FieldInfo field) {

        String column = null;
        boolean alias = method.alias != null;
        SqlEnumType enumType = SqlEnumType.DEFAULT;

        // apply mapping from the method
        SqlMapping mapping = method.mappings.get(field.name);
        if (mapping != null) {
            // ignore field
            if (mapping.ignore()) {
                return;
            }
            // add column name
            if (!mapping.column().isBlank()) {
                column = mapping.column();
            }
            // add alias to column
            if (!mapping.alias().isBlank()) {
                if (column == null) {
                    column = field.column;
                }
                column = mapping.alias() + "." + column;
                // ignore global alias
                alias = false;
            }
            enumType = mapping.enumType();
        }

        switch (field.kind) {
            case INTERFACE:
                addInterfaceFieldMapping(mb, method, field, column, alias);
                break;
            case ENUM:
                addEnumFieldMapping(mb, method, field, column, alias, enumType);
                break;
            default:
                addClassFieldMapping(mb, method, field, column, alias);
        }
    }

    private static MethodSpec createMethod(MethodInfo method) {

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
                addFieldMapping(mb, method, field);
            }
        }

        // return statement
        mb.addStatement("return $N", method.resultVar);
        return mb.build();
    }

    public static void createMapper(ProcessingEnvironment env, ClassInfo clazz) throws IOException {

        // create metamodels for the types
        for (TypeInfo model: clazz.models.values()) {
            SqlMapperMetamodelWriter.generateMetamodel(env, model);
        }

        // create class
        TypeSpec.Builder classBuilder = createClass(clazz);

        // create methods
        for (MethodInfo item : clazz.methods) {
            MethodSpec method = createMethod(item);
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
