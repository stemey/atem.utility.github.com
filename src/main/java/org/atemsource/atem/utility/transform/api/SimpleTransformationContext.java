package org.atemsource.atem.utility.transform.api;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SimpleTransformationContext implements TransformationContext {
	private Map<Object, Object> transformedObjects = new HashMap<Object, Object>();
	private Locale locale = Locale.getDefault();
	private Map<String, Object> attributes = new HashMap<String, Object>();

	public boolean isTransformed(Object a) {
		return transformedObjects.containsKey(a);
	}

	public Object getTransformed(Object a) {
		return transformedObjects.get(a);
	}

	public Object getAttribute(String key) {
		return attributes.get(key);
	}

	public Locale getLocale() {
		return locale;
	}

	public void transformed(Object original, Object transformed) {
		transformedObjects.put(original, transformed);
	}
}
