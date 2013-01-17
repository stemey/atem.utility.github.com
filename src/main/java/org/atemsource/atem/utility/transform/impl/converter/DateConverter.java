package org.atemsource.atem.utility.transform.impl.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.atemsource.atem.api.infrastructure.exception.ConversionException;
import org.atemsource.atem.utility.transform.api.JavaConverter;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.api.constraint.DateFormat;
/**
* This converter converts a date value into a string value. This converter will attach the dateformat as meta attribute to the target attribute.
*/
public class DateConverter implements JavaConverter<Date, String>,Constraining {
	private DateFormat dateFormat;
	private SimpleDateFormat simpleDateFormat;

/**
* define the pattern according to java.text.Dateformat.
*/
	public void setPattern(String pattern) {
		simpleDateFormat = new SimpleDateFormat(pattern);
		this.dateFormat = new DateFormat(pattern, simpleDateFormat);
	}

	@Override
	public String convertAB(Date a, TransformationContext ctx) {
		if (a == null) {
			return null;
		} else {
			return simpleDateFormat.format(a);
		}
	}

	@Override
	public Date convertBA(String b, TransformationContext ctx) {
		if (b == null) {
			return null;
		} else {
			try {
				return simpleDateFormat.parse(b);
			} catch (ParseException e) {
				throw new ConversionException(b,Date.class);
			}
		}
	}

	@Override
	public String[] getConstraintNamesAB() {
		return new String[]{DateFormat.META_ATTRIBUTE_CODE};
	}

	@Override
	public Object getConstraintAB(String name) {
		return dateFormat;
	}

	@Override
	public String[] getConstraintNamesBA() {
		return new String[0];
	}

	@Override
	public Object getConstraintBA(String name) {
		return null;
	}
}
