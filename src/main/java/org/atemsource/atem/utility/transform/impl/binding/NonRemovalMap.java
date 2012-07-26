package org.atemsource.atem.utility.transform.impl.binding;

import java.util.HashMap;
import java.util.Map;


public class NonRemovalMap<A, B> extends HashMap<A, B>
{

	@Override
	public B put(A key, B value)
	{
		if (value == null)
		{
			throw new IllegalStateException("tried to remove entry for " + key);
		}
		return super.put(key, value);
	}

	@Override
	public void putAll(Map<? extends A, ? extends B> map)
	{
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public B remove(Object key)
	{
		throw new IllegalStateException("tried to remove entry for " + key);
	}

}
