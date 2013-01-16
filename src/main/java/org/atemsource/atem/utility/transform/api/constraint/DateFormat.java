package org.atemsource.atem.utility.transform.api.constraint;

import java.text.ParseException;
/**
* This class defines the meta attribute to be attached to string attributes that are derived from date attribues.
*/
public class DateFormat implements Constraint {
	public static final String META_ATTRIBUTE_CODE = DateFormat.class.getName()
			.replace('.', '_');

	private String pattern;

	private java.text.DateFormat format;

	public DateFormat() {
		super();
	}

	public DateFormat(String pattern, java.text.DateFormat format) {
		super();
		this.pattern = pattern;
		this.format = format;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	@Override
	public boolean isValid(Object value) {
		String date = (String) value;
		try {
			format.parse(date);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

	@Override
	public String getMessage() {
		return "{validation.dateformat.invalid}";
	}
}
