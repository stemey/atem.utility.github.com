package org.atemsource.atem.utility.transform.api;

import java.util.Locale;
import org.atemsource.atem.api.type.EntityType;


public interface TransformationContext
{
	public Object getAttribute(String key);

	<J> EntityType<J> getEntityTypeByA(J entity);

	<J> EntityType<J> getEntityTypeByB(J entity);

	public Locale getLocale();

	public Object getTransformed(Object a);

	public boolean isTransformed(Object a);

	public void transformed(Object original, Object transformed);
}
