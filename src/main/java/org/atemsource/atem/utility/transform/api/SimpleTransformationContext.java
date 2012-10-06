package org.atemsource.atem.utility.transform.api;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.EntityType;


public class SimpleTransformationContext implements TransformationContext
{
	private final Map<String, Object> attributes = new HashMap<String, Object>();

	protected final EntityTypeRepository entityTypeRepository;

	private final Locale locale = Locale.getDefault();

	private final Map<Object, Object> transformedObjects = new HashMap<Object, Object>();

	public SimpleTransformationContext(EntityTypeRepository entityTypeRepository)
	{
		super();
		this.entityTypeRepository = entityTypeRepository;
	}

	@Override
	public Object getAttribute(String key)
	{
		return attributes.get(key);
	}

	@Override
	public <J> EntityType<J> getEntityTypeByA(J entity)
	{
		return entityTypeRepository.getEntityType(entity);
	}

	@Override
	public <J> EntityType<J> getEntityTypeByB(J entity)
	{
		return entityTypeRepository.getEntityType(entity);
	}

	@Override
	public Locale getLocale()
	{
		return locale;
	}

	@Override
	public Object getTransformed(Object a)
	{
		return transformedObjects.get(a);
	}

	@Override
	public boolean isTransformed(Object a)
	{
		return transformedObjects.containsKey(a);
	}

	@Override
	public void transformed(Object original, Object transformed)
	{
		transformedObjects.put(original, transformed);
	}
}
