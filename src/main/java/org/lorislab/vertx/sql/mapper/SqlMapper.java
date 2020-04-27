package org.lorislab.vertx.sql.mapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface SqlMapper {

    String CLASS = "org.lorislab.vertx.sql.mapper.SqlMapper";

    String suffix() default "Impl";

    String staticMethodSuffix() default "S";

    boolean staticMethod() default true;

    String instanceName() default "INSTANCE";

}
