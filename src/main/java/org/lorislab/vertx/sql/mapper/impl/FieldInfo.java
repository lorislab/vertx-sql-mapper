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

    public String name;

    public String constName;

    public String column;

    public Element element;

    public ElementKind kind;

    public String qualifiedName;

    public DeclaredType declaredType;

    public static FieldInfo build(Element element) {
        if (element == null) {
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

        SqlColumn ac = element.getAnnotation(SqlColumn.class);
        if (ac != null && !ac.value().isBlank()) {
            field.column = ac.value();
        }

        field.constName = field.name
                .replaceAll(REGEX, REPLACEMENT)
                .toUpperCase();

        return field;
    }
}
