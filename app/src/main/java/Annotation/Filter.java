package Annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import Filter.IFilter;


@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Filter {
	Class<?>[] value() default {IFilter.class};// 属性类型为Class;

}
