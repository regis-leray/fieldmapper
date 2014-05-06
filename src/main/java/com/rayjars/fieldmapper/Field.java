package com.rayjars.fieldmapper;

import java.lang.annotation.*;

@Inherited//inherited with proxy CGLIB
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Field {
    String name() default "";
    boolean required() default false;
    String defaultValue() default "";
}
