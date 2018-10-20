package Annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ColAttrib {
	/**
	 * 列属性标志
	 * @return
	 */
	ColumnAttrib[] value();
}
