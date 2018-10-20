package Annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 对于对象描述的自定义注解
 * @author 王彦鹏
 *
 */
//@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface StructIndex {
	int value() default 0;
	
	
}
