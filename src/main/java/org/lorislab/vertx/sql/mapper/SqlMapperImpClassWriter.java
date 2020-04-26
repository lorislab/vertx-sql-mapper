package org.lorislab.vertx.sql.mapper;

import com.squareup.javapoet.*;
import io.vertx.core.json.JsonObject;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class SqlMapperImpClassWriter {

    private final TypeElement beanType;
    private final ClassModel  classModel;

    public SqlMapperImpClassWriter(TypeElement beanType, ClassModel classModel) {
        this.beanType = beanType;
        this.classModel = classModel;
    }

    public void invoke() throws IOException {
        String name = beanType.getSimpleName() + "Impl";
        TypeSpec.Builder classBuilder = TypeSpec
                .classBuilder(name)
                .addSuperinterface(TypeName.get(beanType.asType()))
                .addModifiers(Modifier.PUBLIC);

        FieldSpec instance = FieldSpec.builder(TypeName.get(beanType.asType()), "INSTANCE")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("new $N()", name)
                .build();
        classBuilder.addField(instance);

        for (ExecutableElement element : classModel.getMethods()) {
            VariableElement p1 = element.getParameters().get(0);

            TypeName returnType = TypeName.get(element.getReturnType());
            TypeName parameterType = TypeName.get(p1.asType());
            DeclaredType ddd = (DeclaredType)element.getReturnType();

            Element ee = ddd.asElement();
                    //classModel.getEnv().getElementUtils().getTypeElement(element.getReturnType().toString());

            List<Element> fields = new ArrayList<>();
            for (Element f : ee.getEnclosedElements()) {
                if (f.getKind() == ElementKind.FIELD) {
                    fields.add(f);
                }
            }

            MethodSpec.Builder mb = MethodSpec.overriding(element)
                    .beginControlFlow("if ($N == null)", p1.getSimpleName())
                    .addStatement("return null")
                    .endControlFlow()
                    .addStatement("$T result = new $T()", returnType, returnType);

            Name row = p1.getSimpleName();
            for (Element field : fields) {
                Name fName = field.getSimpleName();
                TypeMirror tm = field.asType();
                DeclaredType dt = (DeclaredType)tm;
                Element eee = dt.asElement();
                if (eee.getKind() == ElementKind.INTERFACE) {
                    TypeElement tdd = (TypeElement) eee;

                    System.out.println("### " + tdd.getQualifiedName() + " " + dt.getTypeArguments());
                    switch (tdd.getQualifiedName().toString()) {
                        case "java.util.Set":
                            TypeMirror etm = dt.getTypeArguments().get(0);
                            switch (etm.toString()) {
                                case "java.lang.String":
                                    mb.addStatement("result.$N = new $T<>($T.asList($N.getStringArray($S)))", fName, HashSet.class, Arrays.class, row, fName);
                                    break;
                                case "java.lang.Long":
                                    mb.addStatement("result.$N = new $T<>($T.asList($N.getLongArray($S)))", fName, HashSet.class, Arrays.class, row, fName);
                                    break;
                            }
                    }
                } else if (eee.getKind() == ElementKind.ENUM) {
                    String tmp = "result_" + fName;
                    mb.addStatement("String $N = $N.getString($S)", tmp, row, fName);
                    mb.beginControlFlow("if ($N != null)", tmp);
                    mb.addStatement("result.$N = $T.valueOf($N)", fName, tm, tmp);
                    mb.endControlFlow();
                } else {

                    switch (tm.toString()) {
                        case "java.lang.String":
                            mb.addStatement("result.$N = $N.getString($S)", fName, row, fName);
                            break;
                        case "java.lang.Long":
                            mb.addStatement("result.$N = $N.getLong($S)", fName, row, fName);
                            break;
                        case "java.lang.Integer":
                            mb.addStatement("result.$N = $N.getInteger($S)", fName, row, fName);
                            break;
                        case "io.vertx.core.json.JsonObject":
                            mb.addStatement("result.$N = $N.get($T.class, $N.getColumnIndex($S))", fName, row, JsonObject.class, row, fName);
                            break;
                        default:
                            System.out.println("FIELD " + tm.getKind() + " type " + tm + " name " + fName);
                    }
                }
            }

            MethodSpec method = mb.addStatement("return result")
                    .build();
            classBuilder.addMethod(method);

            MethodSpec staticMethod = MethodSpec.methodBuilder(method.name + "S")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(returnType)
                    .addParameter(parameterType, "row")
                    .addStatement("return INSTANCE.$N(row)", method.name)
                    .build();
            classBuilder.addMethod(staticMethod);

        }

        JavaFile javaFile = JavaFile.builder(ClassName.get(beanType).packageName(), classBuilder.build()).indent("    ")
                .build();
        javaFile.writeTo(classModel.getEnv().getFiler());
    }
}
