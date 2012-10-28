package org.atemsource.atem.utility.validation;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.CollectionAttribute;
import org.atemsource.atem.api.attribute.CollectionSortType;
import org.atemsource.atem.api.attribute.MapAttribute;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.infrastructure.exception.ConversionException;
import org.atemsource.atem.api.path.AttributePath;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.api.view.View;
import org.atemsource.atem.api.view.ViewVisitor;
import org.atemsource.atem.api.view.Visitor;
import org.atemsource.atem.utility.path.AttributePathBuilder;
import org.atemsource.atem.utility.path.AttributePathBuilderFactory;
import org.atemsource.atem.utility.transform.api.constraint.Constraint;


public class ValidationVisitor implements ViewVisitor<ValidationContext>
{
	private AttributePathBuilderFactory attributePathBuilderFactory;

	private Collection<? extends SingleAttribute<? extends Constraint>> constraintAttributess;

	private final Stack<GraphNode> entityStack = new Stack<ValidationVisitor.GraphNode>();

	public AttributePathBuilderFactory getAttributePathBuilderFactory()
	{
		return attributePathBuilderFactory;
	}

	private Constraint[] getConstraints(Attribute<?, ?> attribute)
	{
		Constraint[] constraints = new Constraint[this.constraintAttributess.size()];
		int index = 0;
		for (SingleAttribute<? extends Constraint> constraintAttribute : this.constraintAttributess)
		{
			constraints[index++] = constraintAttribute.getValue(attribute);
		}
		return constraints;
	}

	public void setAttributePathBuilderFactory(AttributePathBuilderFactory attributePathBuilderFactory)
	{
		this.attributePathBuilderFactory = attributePathBuilderFactory;
	}

	public void setConstraints(Collection<? extends SingleAttribute<? extends Constraint>> constraints)
	{
		this.constraintAttributess = constraints;
	}

	public void setEntity(Object entity)
	{
		entityStack.push(new GraphNode(null, entity));
	}

	protected void validateValue(ValidationContext context, Attribute<Object, ?> attribute,
		Visitor<ValidationContext> visitor, Object value, AttributePath elementPath)
	{
		boolean correctType = attribute.getTargetType().isInstance(value);
		if (!correctType)
		{
			context.addTypeMismatchError(elementPath, attribute.getTargetType(), value.getClass().getName());
			return;
		}
		Type<Object> targetType = context.getEntityType(value);
		if (targetType instanceof EntityType<?> && ((EntityType) targetType).isAbstractType())
		{
			context.addTypeMismatchError(elementPath, targetType, targetType.getCode());
			return;
		}
		Constraint[] constraints = getConstraints(attribute);
		for (Constraint constraint : constraints)
		{
			if (!constraint.isValid(value))
			{
				context.addConstraintError(elementPath, constraint);
			}
		}
		if (visitor != null)
		{
			visitor.visit(context);
		}

	}

	@Override
	public void visit(ValidationContext context, Attribute attribute)
	{
		visit(context, attribute, null);

	}

	@Override
	public void visit(ValidationContext context, Attribute attribute, Visitor<ValidationContext> visitor)
	{
		AttributePathBuilder pathBuilder = attributePathBuilderFactory.createBuilder(entityStack.peek().getPath());
		pathBuilder.addAttribute(attribute);
		AttributePath path = pathBuilder.createPath();

		if (attribute instanceof CollectionAttribute<?, ?>)
		{
			visitCollection(context, (CollectionAttribute<?, ?>) attribute, visitor);
		}
		else if (attribute instanceof MapAttribute<?, ?, ?>)
		{
			visitMap(context, (MapAttribute<?, ?, ?>) attribute, visitor);
		}
		else
		{
			visitSingle(context, (SingleAttribute<?>) attribute, visitor, path);
		}

	}

	protected void visitAttribute(ValidationContext context, Attribute attribute,
		Visitor<ValidationContext> attributeVisitor, Object value, AttributePath newPath)
	{
		if (attributeVisitor != null)
		{
			entityStack.push(new GraphNode(newPath, value));
			attributeVisitor.visit(context);
			entityStack.pop();
		}
	}

	private void visitCollection(ValidationContext context, CollectionAttribute<?, ?> attribute,
		Visitor<ValidationContext> visitor)
	{
		GraphNode node = entityStack.peek();
		Object entity = node.getEntity();
		if (node.getEntity() != null)
		{
			if (attribute.getValue(entity) == null)
			{
				if (attribute.isRequired())
				{
					context.addRequiredError(node.getPath());
				}
			}
			else
			{
				int index = 0;
				Iterator<?> iterator = attribute.getIterator(node.getEntity());
				for (; iterator.hasNext();)
				{
					Object value = iterator.next();
					AttributePath elementPath;
					if (attribute.getCollectionSortType() != CollectionSortType.NONE)
					{
						elementPath = attributePathBuilderFactory.createBuilder(node.getPath()).addIndex(index).createPath();
						index++;
					}
					else
					{
						elementPath = node.getPath();
					}
					validateValue(context, (CollectionAttribute<Object, ?>) attribute, visitor, value, elementPath);
				}
			}
		}
	}

	private <K, V> void visitMap(ValidationContext context, MapAttribute<K, V, ?> attribute,
		Visitor<ValidationContext> visitor)
	{
		GraphNode node = entityStack.peek();
		Object entity = node.getEntity();
		if (node.getEntity() != null)
		{
			if (attribute.getValue(entity) == null)
			{
				if (attribute.isRequired())
				{
					context.addRequiredError(node.getPath());
				}
			}
			else
			{
				Set<K> keySet = attribute.getKeySet(node.getEntity());
				for (K key : keySet)
				{
					V value = attribute.getElement(node.getEntity(), key);
					boolean correctType = attribute.getTargetType().getJavaType().isInstance(value);
					AttributePath elementPath;
					if (attribute.isSorted())
					{
						elementPath = attributePathBuilderFactory.createBuilder(node.getPath()).addMapKey(key).createPath();
					}
					else
					{
						elementPath = node.getPath();
					}
					validateValue(context, (Attribute<Object, ?>) attribute, visitor, value, elementPath);
				}
			}
		}
	}

	private <V> void visitSingle(ValidationContext context, SingleAttribute<V> attribute,
		Visitor<ValidationContext> visitor, AttributePath newPath)
	{
		GraphNode node = entityStack.peek();
		if (node.getEntity() != null)
		{

			SingleAttribute generalAttribute = attribute;
			try
			{
				// will throw ConversionEception for primitive types
				V value = attribute.getValue(node.getEntity());
				// validate target type
				if (value == null)
				{
					if (generalAttribute.isRequired())
					{
						context.addRequiredError(newPath);
					}
				}
				else
				{
					validateValue(context, generalAttribute, visitor, value, newPath);

				}
			}
			catch (ConversionException e)
			{
				context.addTypeMismatchError(newPath, generalAttribute.getTargetType(), e.getValueAsText());
			}

		}
	}

	@Override
	public boolean visitSubView(ValidationContext context, View view)
	{
		if (((EntityType<?>) view).isInstance(entityStack.peek().getEntity()))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean visitSuperView(ValidationContext context, View view)
	{
		return true;
	}

	private static class GraphNode
	{
		private final Object entity;

		private final AttributePath path;

		public GraphNode(AttributePath path, Object entity)
		{
			super();
			this.path = path;
			this.entity = entity;
		}

		public Object getEntity()
		{
			return entity;
		}

		public AttributePath getPath()
		{
			return path;
		}
	}

}
