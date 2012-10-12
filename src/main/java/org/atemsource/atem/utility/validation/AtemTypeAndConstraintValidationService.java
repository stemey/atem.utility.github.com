package org.atemsource.atem.utility.validation;

import java.util.ArrayList;
import java.util.List;
import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.path.AttributePathBuilderFactory;
import org.atemsource.atem.utility.transform.api.constraint.Constraint;


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
		ValidationVisitor visitor = new ValidationVisitor();
		visitor.setAttributePathBuilderFactory(attributePathBuilderFactory);
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
		visitor.setEntity(entity);
		visitor.setConstraints(constraintAttributes);
		entityType.visit(visitor, context);
	}
}
