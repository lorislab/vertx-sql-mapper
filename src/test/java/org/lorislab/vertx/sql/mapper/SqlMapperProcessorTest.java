package org.lorislab.vertx.sql.mapper;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.common.truth.Truth.assertThat;
import static com.google.testing.compile.CompilationSubject.compilations;
import static com.google.testing.compile.Compiler.javac;

import javax.tools.JavaFileObject;



import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class SqlMapperProcessorTest {

    private Compiler compiler;

    @BeforeEach
    public void init() {
        compiler = javac().withProcessors(new SqlMapperProcessor());
    }

    @Test
    public void generateSqlMapper() {
        try {
            Compilation compilation = compiler.compile(source(SimpleMapper.class));

            assertAbout(compilations()).that(compilation)
                    .generatedSourceFile("org/lorislab/vertx/sql/mapper/SimpleMapperImpl")
                    .hasSourceEquivalentTo(source(SimpleMapperImpl.class));
            assertThat(compilation.status()).isEqualTo(Compilation.Status.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JavaFileObject source(Class<?> clazz) {
        return JavaFileObjects.forResource(clazz.getCanonicalName().replace('.', '/') + ".java");
    }
}
