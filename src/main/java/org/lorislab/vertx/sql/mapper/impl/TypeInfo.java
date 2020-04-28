package org.lorislab.vertx.sql.mapper.impl;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.*;

public class TypeInfo {

    public String name;

    public String metamodel;

    public TypeElement type;

    public Map<String, FieldInfo>  map = new HashMap<>();

    public List<FieldInfo>  fields = new ArrayList<>();

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
