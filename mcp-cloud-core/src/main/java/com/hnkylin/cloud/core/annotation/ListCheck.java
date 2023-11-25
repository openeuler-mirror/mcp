package com.hnkylin.cloud.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ListCheck {

    public boolean notNull() default false;

    public String notNullMessage() default "";

    public int minLen() default -1;

    public String minLenMessage() default "";

    public int maxLen() default -1;

    public String maxLenMessage() default "";

    public String defaultMessage() default "";
}
