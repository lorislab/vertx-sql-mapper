package org.lorislab.vertx.sql.mapper;

import java.lang.annotation.*;

@Repeatable(SqlMappings.class)
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface SqlMapping {

    String target();

    String column() default "";

    boolean ignore() default false;

    String alias() default "";

    SqlEnumType enumType() default SqlEnumType.DEFAULT;
}
