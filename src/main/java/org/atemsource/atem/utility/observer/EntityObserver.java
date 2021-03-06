/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.observer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.impl.MetaLogs;
import org.atemsource.atem.utility.compare.Comparison;
import org.atemsource.atem.utility.compare.Difference;
import org.atemsource.atem.utility.path.AttributePath;
import org.atemsource.atem.utility.path.AttributePathBuilderFactory;
import org.atemsource.atem.utility.transform.api.SimpleTransformationContext;
import org.atemsource.atem.utility.transform.api.UniTransformation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
public class EntityObserver
{

	private final List<AttributeListener> attributeListeners = new ArrayList<AttributeListener>();

	@Inject
	private AttributePathBuilderFactory attributePathBuilderFactory;

	@Inject
	private BeanLocator beanLocator;

	private Comparison comparison;

	private EntityType<?> entityType;

	@Inject
	private EntityTypeRepository entityTypeRepository;

	private EntityHandle handle;

	private final Map<String, Set<SingleAttributeListener>> listeners =
		new HashMap<String, Set<SingleAttributeListener>>();

	private Object previousSnapshot;

	private UniTransformation<Object, Object> snapshotting;

	public boolean check()
	{
		boolean events = false;
		Object entity = handle.getEntity();
		Object snapshot = snapshotting.convert(entity, new SimpleTransformationContext(entityTypeRepository));
		if (previousSnapshot != null)
		{
			EntityObserverContext ctx = beanLocator.getInstance(EntityObserverContext.class);
			ctx.setEntityType(entityType);
			Set<Difference> differences = comparison.getDifferences(ctx, previousSnapshot, snapshot);
			events = differences.size() > 0;
			if (events)
			{
				MetaLogs.LOG.debug("found " + differences.size() + " differences.");
				for (AttributeListener attributeListener : attributeListeners)
				{
					attributeListener.onEvent(differences);
				}
				for (Difference difference : differences)
				{
					AttributePath path = difference.getPath();
					Set<SingleAttributeListener> attributeListeners = new HashSet<SingleAttributeListener>();
					if (listeners.get(path.getAsString()) != null)
						attributeListeners.addAll(listeners.get(path.getAsString()));
					// TODO we need to dispatch to partial paths too
					if (!attributeListeners.isEmpty())
					{
						for (SingleAttributeListener attributeListener : attributeListeners)
						{
							attributeListener.onEvent(difference);
						}
					}
				}
			}
		}
		previousSnapshot = snapshot;
		return events;
	}

	public int checkUntilNoDifferences(int maxIterations)
	{
		boolean differences = false;
		int iteration = 0;
		do
		{
			differences = check();
			iteration++;
		}
		while (differences && iteration < maxIterations);
		return iteration;
	}

	protected EntityHandle getHandle()
	{
		return handle;
	}

	public void setComparison(Comparison comparison)
	{
		this.comparison = comparison;
	}

	public void setEntity(final Object entity)
	{
		this.handle = new EntityHandle() {

			@Override
			public Object getEntity()
			{
				return entity;
			}
		};
	}

	public void setEntityType(EntityType<?> entityType)
	{
		this.entityType = entityType;
	}

	public void setHandle(EntityHandle handle)
	{
		this.handle = handle;
	}

	public void setSnapshotting(UniTransformation<Object, Object> snapshotting)
	{
		this.snapshotting = snapshotting;
	}

	public void unwatch(AttributePath path, Object listener)
	{

		if (listener instanceof SingleAttributeListener)
		{
			Set<SingleAttributeListener> attributeListeners = listeners.get(path);
			if (attributeListeners != null)
			{
				attributeListeners.remove(listener);
			}
		}
		else
		{
			attributeListeners.remove(listener);
		}
	}

	public WatchHandle watch(AttributeListener listener)
	{
		attributeListeners.add(listener);
		return new WatchHandle(this, null, listener);
	}

	public WatchHandle watch(String path, SingleAttributeListener listener)
	{
		AttributePath attributePath = attributePathBuilderFactory.createAttributePath(path, entityType);
		Set<SingleAttributeListener> attributeListeners = listeners.get(attributePath.getAsString());
		if (attributeListeners == null)
		{
			attributeListeners = new HashSet<SingleAttributeListener>();
			listeners.put(attributePath.getAsString(), attributeListeners);
		}
		attributeListeners.add(listener);
		return new WatchHandle(this, attributePath, listener);
	}
}
