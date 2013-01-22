package org.atemsource.atem.utility.observer;

import javax.inject.Inject;

import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.view.View;
import org.atemsource.atem.api.view.ViewVisitor;
import org.atemsource.atem.api.view.Visitor;
import org.atemsource.atem.utility.compare.Comparison;
import org.atemsource.atem.utility.transform.api.Transformation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
public class EntityObserverDefinition
{
	@Inject
	private BeanLocator beanLocator;

	private Comparison snapshotComparison;

	private Transformation<Object, Object> snapshotting;

	public void assertTargetTypesExist()
	{
		snapshotComparison.visit(new ViewVisitor<Object>() {

			@Override
			public void visit(Object context, Attribute attribute)
			{
				if (attribute.getTargetType() == null)
				{
					throw new IllegalStateException("attribute " + attribute.getEntityType().getCode() + "."
						+ attribute.getCode() + " does not have a target type");
				}
			}

			@Override
			public void visit(Object context, Attribute attribute, Visitor<Object> targetTypeVisitor)
			{
				if (attribute.getTargetType() == null)
				{
					throw new IllegalStateException("attribute " + attribute.getEntityType().getCode() + "."
						+ attribute.getCode() + " does not have a target type");
				}
				else
				{
					targetTypeVisitor.visit(context);
				}
			}

			@Override
			public boolean visitSubView(Object context, View view)
			{
				return true;
			}

			@Override
			public boolean visitSuperView(Object context, View view)
			{
				return true;
			}
		}, null);
	}

	public EntityObserver create()
	{
		EntityObserver entityObserver = beanLocator.getInstance(EntityObserver.class);
		entityObserver.setComparison(snapshotComparison);
		entityObserver.setEntityType((EntityType<?>) snapshotting.getTypeA());
		entityObserver.setSnapshotting(snapshotting.getAB());
		return entityObserver;

	}

	public void setComparison(Comparison snapshotComparison)
	{
		this.snapshotComparison = snapshotComparison;
	}

	public void setSnapshotting(Transformation<Object, Object> snapshotting)
	{
		this.snapshotting = snapshotting;
	}

}
