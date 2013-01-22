package org.atemsource.atem.utility.observer;

import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.compare.Comparison;
import org.atemsource.atem.utility.transform.api.Transformation;


public class ObserverFactory
{
	private final BeanLocator beanLocator;

	private final Comparison snapshotComparison;

	private final Transformation<Object, Object> transformation;

	public ObserverFactory(Transformation<Object, Object> transformation, Comparison snapshotComparison)
	{
		this.transformation = transformation;
		this.snapshotComparison = snapshotComparison;
		this.beanLocator = BeanLocator.getInstance();
	}

	public EntityObserver create()
	{
		EntityObserver entityObserver = beanLocator.getInstance(EntityObserver.class);
		entityObserver.setComparison(snapshotComparison);
		entityObserver.setEntityType((EntityType<?>) transformation.getTypeA());
		entityObserver.setSnapshotting(transformation.getAB());
		return entityObserver;

	}
}
