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

    public String name;

    public TypeInfo returnModel;

    public ExecutableElement element;

    public int parameterSize;

    public String row;

    public String alias;

    public String aliasVar = "a";

    public Map<String, SqlMapping> mappings;

    public String resultVar = "result";


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

        // read 1 parameter
        VariableElement p = element.getParameters().get(0);
        String tmp = p.asType().toString();
        if (Types.STRING.equals(tmp)) {
            method.alias = p.getSimpleName().toString();
        } else if (Types.ROWS.contains(tmp)) {
            method.row = p.getSimpleName().toString();
        } else {
            throw new IllegalStateException("Wrong first type of the input parameter! Required type string or row! Method: " + method.name);
        }

        // read 2 parameter
        if (method.parameterSize == 2) {
            p = element.getParameters().get(1);
            tmp = p.asType().toString();
            if (Types.STRING.equals(tmp)) {
                method.alias = p.getSimpleName().toString();
            } else if (Types.ROWS.contains(tmp)) {
                method.row = p.getSimpleName().toString();
            } else {
                throw new IllegalStateException("Wrong second type of the input parameter! Required type string or row! Method: " + method.name);
            }
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
        method.mappings = Stream.of(am).collect(Collectors.toMap(SqlMapping::target, Function.identity()));

        return method;
    }

    private static TypeElement getTypeElement(TypeMirror type) {
        DeclaredType dt = (DeclaredType)type;
        return (TypeElement)dt.asElement();
    }
}
