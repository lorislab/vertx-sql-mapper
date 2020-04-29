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
import org.lorislab.vertx.sql.mapper.impl.SqlMapperProcessor;
import org.lorislab.vertx.sql.mapper.test.Model_;
import org.lorislab.vertx.sql.mapper.test.SimpleMapper;
import org.lorislab.vertx.sql.mapper.test.SimpleMapperImpl;


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
                    .generatedSourceFile("org/lorislab/vertx/sql/mapper/test/SimpleMapperImpl")
                    .hasSourceEquivalentTo(source(SimpleMapperImpl.class));
            assertThat(compilation.status()).isEqualTo(Compilation.Status.SUCCESS);


            assertAbout(compilations()).that(compilation)
                    .generatedSourceFile("org/lorislab/vertx/sql/mapper/test/Model_")
                    .hasSourceEquivalentTo(source(Model_.class));
            assertThat(compilation.status()).isEqualTo(Compilation.Status.SUCCESS);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JavaFileObject source(Class<?> clazz) {
        return JavaFileObjects.forResource(clazz.getCanonicalName().replace('.', '/') + ".java");
    }
}
