package org.atemsource.atem.utility.validation;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import javax.swing.SpringLayout.Constraints;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.CollectionAttribute;
import org.atemsource.atem.api.attribute.CollectionSortType;
import org.atemsource.atem.api.attribute.MapAttribute;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.infrastructure.exception.ConversionException;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.view.AttributeVisitor;
import org.atemsource.atem.api.view.View;
import org.atemsource.atem.api.view.ViewVisitor;
import org.atemsource.atem.utility.path.AttributePath;
import org.atemsource.atem.utility.path.AttributePathBuilder;
import org.atemsource.atem.utility.path.AttributePathBuilderFactory;
import org.atemsource.atem.utility.transform.api.constraint.Constraint;

public class ValidationVisitor implements ViewVisitor<ValidationContext> {
	private AttributePathBuilderFactory attributePathBuilderFactory;

	public void setEntity(Object entity) {
		entityStack.push(new GraphNode(null, entity));
	}

	private static class GraphNode {
		private AttributePath path;

		public GraphNode(AttributePath path, Object entity) {
			super();
			this.path = path;
			this.entity = entity;
		}

		public AttributePath getPath() {
			return path;
		}

		public Object getEntity() {
			return entity;
		}

		private Object entity;
	}

	private Stack<GraphNode> entityStack = new Stack<ValidationVisitor.GraphNode>();

	@Override
	public void visit(ValidationContext context, Attribute attribute) {
		visit(context, attribute, null);

	}

	private void visitSingle(ValidationContext context,
			SingleAttribute<?> attribute, AttributeVisitor attributeVisitor,AttributePath newPath) {
		GraphNode node = entityStack.peek();
		if (node.getEntity() != null) {

			try {
				Object value = attribute.getValue(node.getEntity());
				if (value == null) {
					if (attribute.isRequired()) {
						context.addRequiredError(newPath);
					}
				} else {
					Constraint[] constraints = getConstraints(attribute);
					for (Constraint constraint : constraints) {
						if (!constraint.isValid(value)) {
							context.addConstraintError(newPath,
									constraint);
						}
					}
					visitAttribute(context, attribute, attributeVisitor, value,newPath);

				}
			} catch (ConversionException e) {
				context.addTypeMismatchError(newPath,
						attribute.getTargetType(), e.getValueAsText());
			}

		}
	}

	private Constraint[] getConstraints(Attribute<?, ?> attribute) {
		Constraint[] constraints = new Constraint[this.constraintAttributess
				.size()];
		int index = 0;
		for (SingleAttribute<Constraint> constraintAttribute : this.constraintAttributess) {
			constraints[index++] = constraintAttribute.getValue(attribute);
		}
		return constraints;
	}

	private void visitCollection(ValidationContext context,
			CollectionAttribute<?, ?> attribute,
			AttributeVisitor attributeVisitor) {
		GraphNode node = entityStack.peek();
		Object entity = node.getEntity();
		if (node.getEntity() != null) {
			if (attribute.getValue(entity) == null) {
				if (attribute.isRequired()) {
					context.addRequiredError(node.getPath());
				}
			} else {
				int index = 0;
				Iterator<?> iterator = attribute.getIterator(node.getEntity());
				for (; iterator.hasNext();) {
					Object value = iterator.next();
					AttributePath elementPath;
					if (attribute.getCollectionSortType() != CollectionSortType.NONE) {
						elementPath = attributePathBuilderFactory
								.createBuilder(node.getPath()).addIndex(index)
								.createPath();
						index++;
					} else {
						elementPath = node.getPath();
					}
					boolean correctType = attribute.getTargetType()
							.getJavaType().isInstance(value);
					if (!correctType) {
						context.addTypeMismatchError(elementPath,
								attribute.getTargetType(), value.getClass().getName());
					} else {
						Constraint[] constraints = getConstraints(attribute);
						for (Constraint constraint : constraints) {
							if (!constraint.isValid(value)) {
								context.addConstraintError(elementPath,
										constraint);
							}
						}
						if (attributeVisitor != null) {
							attributeVisitor.visit(context);
						}
					}
				}
			}
		}
	}

	private <K, V> void visitMap(ValidationContext context,
			MapAttribute<K, V, ?> attribute, AttributeVisitor attributeVisitor) {
		GraphNode node = entityStack.peek();
		Object entity = node.getEntity();
		if (node.getEntity() != null) {
			if (attribute.getValue(entity) == null) {
				if (attribute.isRequired()) {
					context.addRequiredError(node.getPath());
				}
			} else {
				Set<K> keySet = attribute.getKeySet(node.getEntity());
				for (K key : keySet) {
					V value = attribute.getElement(node.getEntity(), key);
					boolean correctType = attribute.getTargetType()
							.getJavaType().isInstance(value);
					AttributePath elementPath;
					if (attribute.getCollectionSortType() != CollectionSortType.NONE) {
						elementPath = attributePathBuilderFactory
								.createBuilder(node.getPath()).addMapKey(key)
								.createPath();
					} else {
						elementPath = node.getPath();
					}
					if (!correctType) {
						context.addTypeMismatchError(elementPath,
								attribute.getTargetType(), value.getClass().getName());
					} else {
						Constraint[] constraints = getConstraints(attribute);
						for (Constraint constraint : constraints) {
							if (!constraint.isValid(value)) {
								context.addConstraintError(elementPath,
										constraint);
							}
						}
					}
				}
			}
		}
	}

	private Collection<? extends SingleAttribute<Constraint>> constraintAttributess;

	public void setConstraints(
			Collection<? extends SingleAttribute<Constraint>> constraints) {
		this.constraintAttributess = constraints;
	}

	@Override
	public void visit(ValidationContext context, Attribute attribute,
			AttributeVisitor attributeVisitor) {
		AttributePathBuilder pathBuilder = attributePathBuilderFactory
				.createBuilder(entityStack.peek().getPath());
		pathBuilder.addAttribute(attribute);
		AttributePath path = pathBuilder.createPath();

		if (attribute instanceof CollectionAttribute<?, ?>) {
			visitCollection(context, (CollectionAttribute<?, ?>) attribute,
					attributeVisitor);
		} else if (attribute instanceof MapAttribute<?, ?, ?>) {
			visitMap(context, (MapAttribute<?, ?, ?>) attribute,
					attributeVisitor);
		} else {
			visitSingle(context, (SingleAttribute<?>) attribute,
					attributeVisitor,path);
		}

	}

	protected void visitAttribute(ValidationContext context,
			Attribute attribute, AttributeVisitor attributeVisitor, Object value,AttributePath newPath) {
		entityStack.push(new GraphNode(newPath, value));
		attributeVisitor.visit(context);
		entityStack.pop();
	}

	@Override
	public void visitSubView(ValidationContext context, View view) {
		if (((EntityType<?>) view).isAssignableFrom(entityStack.peek()
				.getEntity())) {
			view.visit(this, context);
		}
	}

	@Override
	public void visitSuperView(ValidationContext context, View view) {
		view.visit(this, context);
	}

	public AttributePathBuilderFactory getAttributePathBuilderFactory() {
		return attributePathBuilderFactory;
	}

	public void setAttributePathBuilderFactory(
			AttributePathBuilderFactory attributePathBuilderFactory) {
		this.attributePathBuilderFactory = attributePathBuilderFactory;
	}

}
