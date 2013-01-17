package org.atemsource.atem.utility.transform.api;

import java.util.Locale;
import org.atemsource.atem.api.type.EntityType;

/**
* The transformationContext can be used to provide parameters to transformations like Locale. It also keeps track of all transformed entities, so that circular references in object graphs  can be resolved. Also the context provides methods to find the entityType of objects. This will be removed in the future, but is currently necessary for versione transformations with non unique external type codes.
*/
public interface TransformationContext
{
/**
* get an attribute by a name
*/
	public Object getAttribute(String key);

	<J> EntityType<J> getEntityTypeByA(J entity);

	<J> EntityType<J> getEntityTypeByB(J entity);

/**
* the Locale of the transformation
*/
	public Locale getLocale();

/**
* get the transformed version of an object. TODO an objec maybe referenced many times and the transformation maybe different. So we need to provide the transformation re too.
*/
	public Object getTransformed(Object entity);

	public boolean isTransformed(Object entity);

/**
* notify the context of the transformation of an entity.
*/
	public void transformed(Object original, Object transformed);
}
