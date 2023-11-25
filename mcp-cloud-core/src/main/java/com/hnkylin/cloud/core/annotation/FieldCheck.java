package com.hnkylin.cloud.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldCheck {

    /**
     * 参数校验错误默认返回的信息
     *
     * @return
     */
    public String defaultMessage() default "";

    /**
     * 不允许为空
     *
     * @return
     */
    public boolean notNull() default false;

    /**
     * 为空时返回信息
     *
     * @return
     */
    public String notNullMessage() default "";

    /**
     * 只允许数字
     *
     * @return
     */
    public boolean numeric() default false;

    /**
     * 只允许数字错误信息
     *
     * @return
     */
    public String numericMessage() default "";

    /**
     * 只对字符串、List起效，最小长度
     *
     * @return
     */
    public int minLen() default -1;

    /**
     * 只对字符串、List起效，最大长度
     *
     * @return
     */
    public int maxLen() default -1;

    /**
     * maxLen的错误信息
     *
     * @return
     */
    public String minLenMessage() default "";

    /**
     * maxLen的错误信息
     *
     * @return
     */
    public String maxLenMessage() default "";

    /**
     * 最小数字
     *
     * @return
     */
    public double minNum() default -999999999;

    /**
     * 最大数字
     *
     * @return
     */
    public double maxNum() default -999999999;

    /**
     * minNum错误信息
     *
     * @return
     */
    public String minNumMessage() default "";

    /**
     * maxNum错误信息
     *
     * @return
     */
    public String maxNumMessage() default "";
}
