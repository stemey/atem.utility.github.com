package org.atemsource.atem.utility.transform.api.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface Version
{
	/**
	 * the lower inclusive boundary of the valid version range
	 * 
	 * @return the version
	 */
	String from() default Versions.MIN;

	/**
	 * the upper exclusive boundary of the valid version range
	 * 
	 * @return the version
	 */
	String until() default Versions.MAX;
}
