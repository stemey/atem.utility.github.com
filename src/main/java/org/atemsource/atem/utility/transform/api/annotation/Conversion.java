package org.atemsource.atem.utility.transform.api.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.atemsource.atem.utility.transform.api.JavaConverter;


@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface Conversion
{
	Class<? extends JavaConverter<?, ?>> value();
}
