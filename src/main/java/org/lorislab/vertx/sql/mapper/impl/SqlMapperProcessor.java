package org.lorislab.vertx.sql.mapper.impl;

import org.lorislab.vertx.sql.mapper.SqlMapper;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

@SupportedAnnotationTypes(SqlMapper.CLASS)
public class SqlMapperProcessor extends AbstractProcessor {

    private static final int MAX_ROUND = 20;
    private int round;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWith(SqlMapper.class).forEach(this::generateMetamodel);
        if (!roundEnv.processingOver() && round > MAX_ROUND) {
            messager().printMessage(Diagnostic.Kind.ERROR, "SqlMapper possible processing loop detected (" + (MAX_ROUND+1)+ ")");
        }
        round++;
        return false;
    }

    private void generateMetamodel(Element element) {
        TypeElement typeElement = (TypeElement) element;
        if (element.getKind() == ElementKind.INTERFACE ||
                (element.getKind() == ElementKind.CLASS && element.getModifiers().contains(Modifier.ABSTRACT))) {
            messager().printMessage(Diagnostic.Kind.NOTE, "SqlMapper processing interface: " + element);
            writeImplClass(typeElement);
        } else {
            messager().printMessage(Diagnostic.Kind.ERROR, "SqlMapper is not interface or abstract class. The element: " + element + " kind: " + element.getKind() + " will be ignored.");
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(SqlMapper.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    private void writeImplClass(TypeElement element) {
        try {
            ClassInfo clazzInfo = ClassInfo.build(element);
            SqlMapperImpClassWriter.createMapper(processingEnv, clazzInfo);
        } catch (IOException e) {
            e.printStackTrace();
            messager().printMessage(Diagnostic.Kind.ERROR, "Writing SqlMapper implementation class failed", element);
        }
    }

    private Messager messager() {
        return processingEnv.getMessager();
    }

}
