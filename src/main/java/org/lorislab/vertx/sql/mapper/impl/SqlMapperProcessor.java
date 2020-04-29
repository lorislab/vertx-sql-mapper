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
