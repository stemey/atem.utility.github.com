package org.atemsource.atem.utility.validation;

import org.atemsource.atem.api.path.AttributePath;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.transform.api.constraint.Constraint;


public interface ValidationContext
{

	void addConstraintError(AttributePath path, Constraint constraint);

	void addRequiredError(AttributePath path);

	void addTypeMismatchError(AttributePath path, Type targetType, String actualValue);

	<T> Type<T> getEntityType(T value);

}
