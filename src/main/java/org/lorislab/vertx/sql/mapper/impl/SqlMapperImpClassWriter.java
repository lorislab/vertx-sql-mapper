package org.lorislab.vertx.sql.mapper.impl;

import com.squareup.javapoet.*;
import io.vertx.core.json.JsonObject;
import org.lorislab.vertx.sql.mapper.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SqlMapperImpClassWriter {

    private SqlMapperImpClassWriter() {}

    private static TypeSpec.Builder createClass(TypeElement beanType, String name, String instance) {
        TypeName inter = TypeName.get(beanType.asType());
        TypeSpec.Builder classBuilder= TypeSpec
                .classBuilder(name)
                .addSuperinterface(inter)
                .addModifiers(Modifier.PUBLIC);

        classBuilder.addField(FieldSpec.builder(inter, instance)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("new $N()", name)
                .build());

        return classBuilder;
    }

    private static MethodSpec createMethod(ExecutableElement element) {
        // check input parameter
        if (element.getParameters() == null || element.getParameters().isEmpty()) {
            throw new IllegalStateException("Method required 'row' or 'row, alias' combination of input parameters");
        }

        // read input parameter
        int size = element.getParameters().size();
        Name methodName = element.getSimpleName();
        Name row = null;
        String alias = null;

        // read 1 parameter
        VariableElement p = element.getParameters().get(0);
        String tmp = p.asType().toString();
         if (Types.STRING.equals(tmp)) {
            alias = p.getSimpleName().toString();
        } else if (Types.ROWS.contains(tmp)) {
            row = p.getSimpleName();
        } else {
            throw new IllegalStateException("Wrong first type of the input parameter! Required type string or row! Method: " + methodName);
        }

        // read 2 parameter
        if (size == 2) {
            p = element.getParameters().get(1);
            tmp = p.asType().toString();
            if (Types.STRING.equals(tmp)) {
                alias = p.getSimpleName().toString();
            } else if (Types.ROWS.contains(tmp)) {
                row = p.getSimpleName();
            } else {
                throw new IllegalStateException("Wrong second type of the input parameter! Required type string or row! Method: " + methodName);
            }
        }

        // the row parameter is required
        if (size == 1 && row == null) {
            throw new IllegalStateException("Missing row input parameter! Method: " + methodName);
        }
        // the row and alias parameter is required
        if (size == 2 && (row == null || alias == null)) {
            throw new IllegalStateException("Wrong type of the input parameter! Method: " + methodName);
        }

        // read return type fields
        TypeName returnType = TypeName.get(element.getReturnType());
        DeclaredType ddd = (DeclaredType)element.getReturnType();
        List<Element> fields = new ArrayList<>();
        for (Element f : ddd.asElement().getEnclosedElements()) {
            if (f.getKind() == ElementKind.FIELD && f.getModifiers().contains(Modifier.PUBLIC)) {
                fields.add(f);
            } else {
                // ignore
            }
        }

        SqlMapping[] am = element.getAnnotationsByType(SqlMapping.class);
        Map<String, SqlMapping> mappings =
                Stream.of(am).collect(Collectors.toMap(SqlMapping::target, Function.identity()));

        // create method
        String result = "result";
        MethodSpec.Builder mb = MethodSpec.overriding(element)
                .beginControlFlow("if ($N == null)", row)
                .addStatement("return null")
                .endControlFlow();

        String aliasVar = null;
        if (alias != null) {
            aliasVar = "a";
            mb.addStatement("String $N = \"\"", aliasVar);
            mb.beginControlFlow("if ($N != null && !$N.isBlank())", alias, alias);
            mb.addStatement("$N = $N + \".\"", aliasVar, alias);
            mb.endControlFlow();
        }

        // create instance
        mb.addStatement("$T $N = new $T()", returnType, result, returnType);

        // add mapping of return type fields
        for (Element field : fields) {
            addFieldMapping(mb, field, result, row, aliasVar, mappings);
        }

        // return statement
        mb.addStatement("return result");
        return mb.build();
    }

    private static void addEnumFieldMapping(MethodSpec.Builder mb, Element field, String result, Name row, String column, String alias, SqlEnumType enumType) {
        SqlEnumType type = enumType;
        // if no mapping
        if (type == SqlEnumType.DEFAULT) {
            // check the field annotation
            SqlEnum me = field.getAnnotation(SqlEnum.class);
            if (me != null) {
                type = me.value();
            }
        }
        // set default
        if (type == SqlEnumType.DEFAULT) {
            type = SqlEnumType.STRING;
        }

        String name = field.getSimpleName().toString();
        TypeMirror tm = field.asType();
        String tmp = result + name.substring(0, 1).toUpperCase() + name.substring(1);
        switch (type) {
            case STRING:
                if (alias == null) {
                    mb.addStatement("String $N = $N.getString($S)", tmp, row, column);
                } else {
                    mb.addStatement("String $N = $N.getString($N + $S)", tmp, row, alias, column);
                }
                mb.beginControlFlow("if ($N != null)", tmp);
                mb.addStatement("$N.$N = $T.valueOf($N)", result, name, tm, tmp);
                mb.endControlFlow();
                break;
            case INTEGER:
                if (alias == null) {
                    mb.addStatement("Integer $N = $N.getInteger($S)", tmp, row, column);
                } else {
                    mb.addStatement("Integer $N = $N.getInteger($N + $S)", tmp, row, alias, column);
                }
                mb.beginControlFlow("if ($N != null)", tmp);
                mb.addStatement("$N.$N = $T.values()[$N]", result, name, tm, tmp);
                mb.endControlFlow();
                break;
            default:
                System.out.println("ERROR enum mapping for " + name + " and type " + tm.toString());
        }
    }

    private static void addClassFieldMapping(MethodSpec.Builder mb, Element field, String result, Name row, String column, String alias) {
        Name name = field.getSimpleName();
        TypeMirror tm = field.asType();
        switch (tm.toString()) {
            case Types.STRING:
                if (alias == null) {
                    mb.addStatement("$N.$N = $N.getString($S)", result, name, row, column);
                } else {
                    mb.addStatement("$N.$N = $N.getString($N + $S)", result, name, row, alias, column);
                }
                break;
            case Types.LONG:
                if (alias == null) {
                    mb.addStatement("$N.$N = $N.getLong($S)", result, name, row, column);
                } else {
                    mb.addStatement("$N.$N = $N.getLong($N + $S)", result, name, row, alias, column);
                }
                break;
            case Types.INTEGER:
                if (alias == null) {
                    mb.addStatement("$N.$N = $N.getInteger($S)", result, name, row, column);
                } else {
                    mb.addStatement("$N.$N = $N.getInteger($N + $S)", result, name, row, alias, column);
                }
                break;
            case Types.JSON_OBJECT:
                if (alias == null) {
                    mb.addStatement("$N.$N = $N.get($T.class, $N.getColumnIndex($S))", result, name, row, JsonObject.class, row, column);
                } else {
                    mb.addStatement("$N.$N = $N.get($T.class, $N.getColumnIndex($N + $S))", result, name, row, JsonObject.class, row, alias, column);
                }
                break;
            default:
                System.out.println("FIELD " + tm.getKind() + " type " + tm + " name " + name);
        }
    }

    private static void addInterfaceFieldMapping(MethodSpec.Builder mb, Element field, String result, Name row, String column, String alias) {
        Name name = field.getSimpleName();
        DeclaredType dt = (DeclaredType)field.asType();
        TypeElement tdd = (TypeElement) dt.asElement();
        switch (tdd.getQualifiedName().toString()) {
            case Types.SET:
                switch (dt.getTypeArguments().get(0).toString()) {
                    case Types.STRING:
                        if (alias == null) {
                            mb.addStatement("$N.$N = new $T<>($T.asList($N.getStringArray($S)))", result, name, HashSet.class, Arrays.class, row, column);
                        } else {
                            mb.addStatement("$N.$N = new $T<>($T.asList($N.getStringArray($N + $S)))", result, name, HashSet.class, Arrays.class, row, alias, column);
                        }
                        break;
                    case Types.LONG:
                        if (alias == null) {
                            mb.addStatement("$N.$N = new $T<>($T.asList($N.getLongArray($S)))", result, name, HashSet.class, Arrays.class, row, column);
                        } else {
                            mb.addStatement("$N.$N = new $T<>($T.asList($N.getLongArray($N + $S)))", result, name, HashSet.class, Arrays.class, row, alias, column);
                        }
                        break;
                    case Types.INTEGER:
                        if (alias == null) {
                            mb.addStatement("$N.$N = new $T<>($T.asList($N.getIntegerArray($S)))", result, name, HashSet.class, Arrays.class, row, column);
                        } else {
                            mb.addStatement("$N.$N = new $T<>($T.asList($N.getIntegerArray($N + $S)))", result, name, HashSet.class, Arrays.class, row, alias, column);
                        }
                        break;
                }
                break;
            case Types.LIST:
                switch (dt.getTypeArguments().get(0).toString()) {
                    case Types.STRING:
                        if (alias == null) {
                            mb.addStatement("$N.$N = new $T.asList($N.getStringArray($S))", result, name, Arrays.class, row, column);
                        } else {
                            mb.addStatement("$N.$N = new $T.asList($N.getStringArray($N + $S))", result, name, Arrays.class, row, alias, column);
                        }
                        break;
                    case Types.LONG:
                        if (alias == null) {
                            mb.addStatement("$N.$N = new $T.asList($N.getLongArray($S))", result, name, Arrays.class, row, column);
                        } else {
                            mb.addStatement("$N.$N = new $T.asList($N.getLongArray($N + $S))", result, name, Arrays.class, row, alias, column);
                        }
                        break;
                    case Types.INTEGER:
                        if (alias == null) {
                            mb.addStatement("$N.$N = new $T.asList($N.getIntegerArray($S))", result, name, Arrays.class, row, alias, column);
                        } else {
                            mb.addStatement("$N.$N = new $T.asList($N.getIntegerArray($N + $S))", result, name, Arrays.class, row, alias, column);
                        }
                        break;
                }
                break;
        }
    }

    private static void addFieldMapping(MethodSpec.Builder mb, Element field, String result, Name row, String alias, Map<String, SqlMapping> mappings) {
        DeclaredType dt = (DeclaredType)field.asType();

        String name = field.getSimpleName().toString();
        SqlColumn ac = field.getAnnotation(SqlColumn.class);
        if (ac != null && !ac.value().isBlank()) {
            name = ac.value();
        }
        String column = name;
        SqlEnumType enumType = SqlEnumType.DEFAULT;
        SqlMapping mapping = mappings.get(name);
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
                column = mapping.alias() + "." + column;
                // ignore global alias
                alias = null;
            }
            enumType = mapping.enumType();
        }
        switch (dt.asElement().getKind()) {
            case INTERFACE:
                addInterfaceFieldMapping(mb, field, result, row, column, alias);
                break;
            case ENUM:
                addEnumFieldMapping(mb, field, result, row, column, alias, enumType);
                break;
            default:
                addClassFieldMapping(mb, field, result, row, column, alias);
        }
    }

    private static MethodSpec createStaticForMethod(MethodSpec method, String suffix, String instance) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(method.name + suffix)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(method.returnType)
                .addParameters(method.parameters);

        int size = method.parameters.size();
        if (size == 1) {
           builder.addStatement("return $N.$N($N)", instance, method.name, method.parameters.get(0).name);
        } else {
            builder.addStatement("return $N.$N($N,$N)", instance, method.name, method.parameters.get(0).name, method.parameters.get(1).name);
        }
        return builder.build();
    }

    public static void invoke(ProcessingEnvironment env, TypeElement beanType) throws IOException {

        List<ExecutableElement> methods  = new ArrayList<>();
        beanType.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.METHOD)
                .map(e -> (ExecutableElement) e)
                .filter(e -> !e.isDefault())
                .forEach(methods::add);

        // create class
        SqlMapper mapper = beanType.getAnnotation(SqlMapper.class);
        String name = beanType.getSimpleName() + mapper.suffix();
        TypeSpec.Builder classBuilder = createClass(beanType, name, mapper.instanceName());

        // create methods
        for (ExecutableElement element : methods) {
            MethodSpec method = createMethod(element);
            classBuilder.addMethod(method);
            // create static method
            if (mapper.staticMethod()) {
                MethodSpec staticMethod = createStaticForMethod(method, mapper.staticMethodSuffix(), mapper.instanceName());
                classBuilder.addMethod(staticMethod);
            }
        }

        // save implementation class
        JavaFile javaFile = JavaFile
                .builder(ClassName.get(beanType).packageName(), classBuilder.build())
                .indent("    ")
                .build();
        javaFile.writeTo(env.getFiler());
    }
}
