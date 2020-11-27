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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * SQL mapper annotation.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface SqlMapper {

    String CLASS = "org.lorislab.vertx.sql.mapper.SqlMapper";

    /**
     * Generate mapper as CDI bean
     *
     * @return generate mapper as CDI bean.
     */
    boolean cdi() default true;

    /**
     * Mapper CDI bean scoped.
     * @return the mapper CDI bean scoped.
     */
    String cdiScoped() default "javax.enterprise.context.ApplicationScoped";

    /**
     * Mapper CDI bean annotations.
     * @return the mapper CDI bean annotations.
     */
    Class[] anno() default {};

    /**
     * Define the suffix of the mapper implementation class.
     * @return the suffix of the mapper implementation class.
     */
    String suffix() default "Impl";

    /**
     * Define the suffix of the static method.
     * @return the suffix of the static method.
     */
    String staticMethodSuffix() default "S";

    /**
     * Disable or enabled static method generator.
     * @return the flag to disable or enable static method.
     */
    boolean staticMethod() default false;

    /**
     * Define the static field name of the mapper.
     * @return the static field name.
     */
    String instanceName() default "INSTANCE";

    /**
     * Create implementation instance static field.
     *
     * @return implementation instance static field.
     */
    boolean instanceField() default false;
}
