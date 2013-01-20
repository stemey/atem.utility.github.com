package org.atemsource.atem.utility.observer;

import javax.inject.Inject;

import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.compare.Comparison;
import org.atemsource.atem.utility.transform.api.Transformation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class EntityObserverDefinition {
	@Inject
	private BeanLocator beanLocator;


	public void setComparison(Comparison snapshotComparison) {
		this.snapshotComparison = snapshotComparison;
	}


	public void setSnapshotting(Transformation<Object, Object> snapshotting) {
		this.snapshotting = snapshotting;
	}

	private Comparison snapshotComparison;
	private Transformation<Object, Object> snapshotting;

	public EntityObserver create() {
		EntityObserver entityObserver = beanLocator.getInstance(EntityObserver.class);
		entityObserver.setComparison(snapshotComparison);
		entityObserver.setEntityType((EntityType<?>) snapshotting.getTypeA());
		entityObserver.setSnapshotting(snapshotting.getAB());
		return entityObserver;

	}
}
