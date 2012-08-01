package org.atemsource.atem.utility.transform.api;

import java.util.Locale;

public interface TransformationContext {
	public boolean isTransformed(Object a);

	public Object getTransformed(Object a);

	public Object getAttribute(String key);

	public Locale getLocale();

	public void transformed(Object original, Object transformed);
}
