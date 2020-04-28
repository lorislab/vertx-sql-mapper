package org.lorislab.vertx.sql.mapper.impl;

import org.lorislab.vertx.sql.mapper.SqlMapper;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.*;

public class ClassInfo {

    public TypeElement type;

    public String name;

    public String superName;

    public boolean isInterface;

    public SqlMapper mapper;

    public List<MethodInfo> methods = new ArrayList<>();

    public Map<String, TypeInfo> models = new HashMap<>();

    public static ClassInfo build(TypeElement beanType) {
        if (beanType == null) {
            return null;
        }

        ClassInfo clazz = new ClassInfo();
        clazz.type = beanType;
        clazz.mapper = beanType.getAnnotation(SqlMapper.class);
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
