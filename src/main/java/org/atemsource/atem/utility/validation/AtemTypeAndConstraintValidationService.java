package org.atemsource.atem.utility.validation;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.path.AttributePathBuilderFactory;
import org.atemsource.atem.utility.transform.api.constraint.Constraint;
import org.atemsource.atem.utility.visitor.HierachyVisitor;


public class AtemTypeAndConstraintValidationService implements ValidationService
{
	private AttributePathBuilderFactory attributePathBuilderFactory;

	public AttributePathBuilderFactory getAttributePathBuilderFactory()
	{
		return attributePathBuilderFactory;
	}

	public void setAttributePathBuilderFactory(AttributePathBuilderFactory attributePathBuilderFactory)
	{
		this.attributePathBuilderFactory = attributePathBuilderFactory;
	}
	

	@Override
	public <J> void validate(EntityType<J> entityType, ValidationContext context, J entity)
	{
		List<SingleAttribute<? extends Constraint>> constraintAttributes =findConstraints();
		validate(entityType, context, entity, constraintAttributes);
	}

	protected <J> void validate(EntityType<J> entityType, ValidationContext context, J entity,
			List<SingleAttribute<? extends Constraint>> constraintAttributes) {
		if (entityType.isAbstractType()) {
			context.addTypeMismatchError(null, entityType, entity.toString());
		}
		ValidationVisitor visitor = new ValidationVisitor();
		visitor.setAttributePathBuilderFactory(attributePathBuilderFactory);
		visitor.setEntity(entity);
		visitor.setConstraints(constraintAttributes);
		HierachyVisitor.visit(entityType,visitor, context);
	}

	protected List<SingleAttribute<? extends Constraint>> findConstraints() {
		EntityType<Attribute> attributeType =
				BeanLocator.getInstance().getInstance(EntityTypeRepository.class).getEntityType(Attribute.class);
		List<SingleAttribute<? extends Constraint>> constraintAttributes =
				new ArrayList<SingleAttribute<? extends Constraint>>();
		for (Attribute attribute : attributeType.getMetaAttributes())
		{
			if (attribute.getTargetType().getJavaType().isAssignableFrom(Constraint.class))
			{
				constraintAttributes.add((SingleAttribute<? extends Constraint>) attribute);
			}
		}
		return constraintAttributes;
	}
}
