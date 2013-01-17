package org.atemsource.atem.utility.transform.api.constraint;
/**
* This class defines the meta data to be attached to a strig attribute that was derived from an enum attribute. 
*/
public interface PossibleValues
{
	public static final String META_ATTRIBUTE_CODE = PossibleValues.class.getName().replace('.', '_');

	public String[] getValues();
}
