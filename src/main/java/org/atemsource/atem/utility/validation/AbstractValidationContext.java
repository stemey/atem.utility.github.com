package org.atemsource.atem.utility.validation;

import java.util.ArrayList;
import java.util.Collection;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.path.AttributePath;
import org.atemsource.atem.utility.transform.api.constraint.Constraint;


public abstract class AbstractValidationContext implements ValidationContext
{

	private final Collection<Error> errors = new ArrayList<SimpleValidationContext.Error>();

	public AbstractValidationContext()
	{
		super();
	}

	@Override
	public void addConstraintError(AttributePath path, Constraint constraint)
	{
		errors.add(new Error(path.getAsString(), constraint.getMessage()));
	}

	@Override
	public void addRequiredError(AttributePath path)
	{
		errors.add(new Error(path.getAsString(), "validation.required"));
	}

	@Override
	public void addTypeMismatchError(AttributePath path, Type expectedType, String actualValue)
	{
		errors.add(new Error(path == null ? "" : path.getAsString(), "validation.typemismatch"));
	}

	public Collection<Error> getErrors()
	{
		return errors;
	}

	public static class Error
	{
		private final String message;

		private final String path;

		public Error(String path, String message)
		{
			super();
			this.path = path;
			this.message = message;
		}

		public String getMessage()
		{
			return message;
		}

		public String getPath()
		{
			return path;
		}
	}

}