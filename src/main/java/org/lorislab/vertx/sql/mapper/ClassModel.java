package org.lorislab.vertx.sql.mapper;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import java.util.ArrayList;
import java.util.List;

public class ClassModel {

    private final ProcessingEnvironment env;

    private final List<ExecutableElement> methods  = new ArrayList<>();

    public ClassModel(ProcessingEnvironment env) {
        this.env = env;
    }

    public ProcessingEnvironment getEnv() {
        return env;
    }

    public void addMethod(ExecutableElement e) {
        methods.add(e);
    }

    public List<ExecutableElement> getMethods() {
        return methods;
    }
}
