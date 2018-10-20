package Annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by 王彦鹏 on 2017-09-18.
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited

public @interface PropertyName {
    String value() default "";// 属性类型为Class;
}
