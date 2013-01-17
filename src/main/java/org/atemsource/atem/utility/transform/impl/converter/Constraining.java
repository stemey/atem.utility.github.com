package org.atemsource.atem.utility.transform.impl.converter;
/**
* An optional interface for converters. It providesm means for the converter to apply meta data to the source and target attribute of EntityTypeTransformations.
*/
public interface Constraining {

	/**
	* return the meta attribute names for the AB direction of a trnasformation
	*/
	public String[] getConstraintNamesAB();

	/**
	* return the meta attribute value for the given name for the AB direction of a trnasformation
	*/
	public Object getConstraintAB(String name);

	/**
	* return the meta attribute names for the AB direction of a trnasformation
	*/
	public String[] getConstraintNamesBA();

	/**
	* return the meta attribute value for the given name for the AB direction of a trnasformation
	*/
	public Object getConstraintBA(String name);
}
