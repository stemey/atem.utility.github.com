package org.atemsource.atem.utility.path;

import java.lang.annotation.Annotation;
import java.util.List;

import org.atemsource.atem.api.attribute.Accessor;

public class PathAccessor implements Accessor {

	private List<AttributePathElement> path;

	public PathAccessor(List<AttributePathElement> path) {
		this.path=path;
	}
	
	public Object getValue(Object entity)
	{
		for (int index = 0; index < path.size(); index++)
		{
			if (entity == null)
			{
				return null;
			}
			AttributePathElement attribute = path.get(index);
			if (attribute.getSourceType().getType().getJavaType().isAssignableFrom(entity.getClass()))
			{
				entity = attribute.getValue(entity);
			}
			else
			{
				// e.g.: defined on a sibling type
				return null;
			}
		}
		return entity;
	}

	@Override
	public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
		return null;
	}

	@Override
	public boolean isReadable() {
		return true;
	}

	@Override
	public boolean isWritable() {
		return true;
	}

	@Override
	public void setValue(Object entity, Object value) {
		for (int index = 0; index < path.size() - 1; index++)
		{
			AttributePathElement attribute = path.get(index);
			if (attribute.getSourceType().getType().getJavaType().isAssignableFrom(entity.getClass()))
			{
				Object newEntity = attribute.getValue(entity);
				if (newEntity == null)
				{
					throw new NullPointerException("entity is null cannot set value " + this);
				}
				else
				{
					entity = newEntity;
				}
			} else {
				// e.g.: defined on a sibling type
				return;
			}

		}

		AttributePathElement attribute = path.get(path.size() - 1);
		if (attribute.getSourceType().getType().getJavaType().isAssignableFrom(entity.getClass()))
		{
			attribute.setValue(entity, value);
		}
		else
		{
			// e.g.: defined on a sibling type
			return;
		}
	}

}
