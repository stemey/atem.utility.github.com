package org.atemsource.atem.utility.schema;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD, FIELD})
public @interface ValidTypes
{
	Class[] value() default {};
}
