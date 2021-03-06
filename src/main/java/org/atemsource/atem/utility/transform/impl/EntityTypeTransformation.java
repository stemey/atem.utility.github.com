/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.transform.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.transform.api.AttributeTransformation;
import org.atemsource.atem.utility.transform.api.JavaTransformation;
import org.atemsource.atem.utility.transform.api.Transformation;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.api.UniTransformation;
import org.atemsource.atem.utility.transform.impl.builder.TransformationFinder;
import org.atemsource.atem.utility.transform.impl.transformation.AbstractOneToOneAttributeTransformation;
import org.atemsource.atem.utility.transform.impl.transformation.OneToOneAttributeTransformation;
import org.atemsource.atem.utility.transform.impl.transformation.TransformUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * The EntityTypeTransformation is a transformation from a source EntityType to
 * a target EntityType. It contains AttributeTransformation that may transform a
 * single source attribute to a single target attribute. it may aso contain
 * anonymuous JavaTransformations. The AB direction is supported but the reverse
 * BA transformation ma not. There is currently no method to find that out. The
 * transformation also has sub and super transformations to transform sub and
 * super types of the source and target type. The deaul is that each source type
 * is associated with a target type by an EntityTypeTransformation. If this is
 * not the case you need to use a TransformationFinder. Then you can choose the
 * source type based on source valuesme at transformation ti.
 */
@Component
@Scope("prototype")
public class EntityTypeTransformation<A, B> implements Transformation<A, B> {
	private final List<AttributeTransformation<A, B>> embeddedTransformations = new ArrayList<AttributeTransformation<A, B>>();

	private EntityType<A> entityTypeA;

	private EntityType<B> entityTypeB;

	private TransformationFinder<A, B> finder;

	private final List<JavaTransformation<A, B>> javaTransformations = new ArrayList<JavaTransformation<A, B>>();

	private final Set<EntityTypeTransformation<A, B>> subTransformations = new HashSet<EntityTypeTransformation<A, B>>();

	private EntityTypeTransformation<A, B> superTransformation;

	private Transformation<A, B> typeConverter;

	/**
	 * add a transfromation for a subtype of this type A and type B.
	 */
	public void addSubTransformation(
			EntityTypeTransformation<A, B> subTransformation) {
		Iterator<EntityTypeTransformation<A, B>> iterator = subTransformations
				.iterator();
		while (iterator.hasNext()) {
			EntityTypeTransformation<A, B> existing = iterator.next();
			if (existing.getEntityTypeA().equals(
					subTransformation.getEntityTypeA())
					&& existing.getEntityTypeB().equals(
							subTransformation.getEntityTypeB())) {
				iterator.remove();
				break;
			}
		}
		subTransformations.add(subTransformation);
	}

	/**
	 * add an attribute transfromation that transforms n attributes of typ A to
	 * m attributes of type B.
	 */
	public void addTransformation(AttributeTransformation<A, B> transformation) {
		this.embeddedTransformations.add(transformation);
	}

	/**
	 * add a custom transformation from type A to type B.
	 */
	public void addTransformation(JavaTransformation<A, B> javaTransformation) {
		javaTransformations.add(javaTransformation);
	}

	private A createA(B b, TransformationContext ctx) {
		if (b == null) {
			return null;
		}
		if (ctx.isTransformed(b)) {
			return (A) ctx.getTransformed(b);
		} else {
			A valueA = TransformUtils.findTargetEntity(getBA(), b, null, ctx,
					null,null);
			if (valueA == null) {
				valueA = getTypeConverter().getBA().convert(b, ctx);
			}
			ctx.transformed(b, valueA);
			transformBAChildren(b, valueA, ctx);
			return valueA;
		}
	}

	private B createB(A a, TransformationContext ctx) {
		if (a == null) {
			return null;
		}
		if (ctx.isTransformed(a)) {
			return (B) ctx.getTransformed(a);
		} else {
			B valueB = TransformUtils.findTargetEntity(getAB(), a, null, ctx,
					null, null);
			if (valueB == null) {
				valueB = getTypeConverter().getAB().convert(a, ctx);
			}
			ctx.transformed(a, valueB);
			transformABChildren(a, valueB, ctx);
			return valueB;
		}
	}

	@Override
	public UniTransformation<A, B> getAB() {
		return new UniTransformation<A, B>() {

			@Override
			public B convert(A a, TransformationContext ctx) {

				if (a == null) {
					return null;
				}
				EntityType<A> entityType = ctx.getEntityTypeByA(a);
				if (entityType==null) {
					entityType=entityTypeA;
				}
				if (finder != null) {

					UniTransformation<A, B> ab = finder.getAB(a, ctx);
					if (ab != null) {
						return ab.convert(a, ctx);
					}
				}
				EntityTypeTransformation<A, B> transformationByTypeA = getTransformationByTypeA(entityType);
				if (transformationByTypeA == null) {
					throw new IllegalStateException(
							"cannot find transformation for "
									+ entityType.getCode());
				}
				return transformationByTypeA.createB(a, ctx);
			}

			@Override
			public Type<A> getSourceType() {
				return entityTypeA;
			}

			@Override
			public Type<B> getTargetType() {
				return entityTypeB;
			}

			@Override
			public B merge(A a, B b, TransformationContext ctx) {
				EntityType<A> entityType = ctx.getEntityTypeByA(a);
				if (entityType==null) {
					// if no type information on object then use the standard
					entityType=entityTypeA;
				}
				if (finder != null) {
					UniTransformation<A, B> ab = finder.getAB(a, ctx);
					if (ab != null) {
						return ab.merge(a, b, ctx);
					}
				}
				EntityTypeTransformation<A, B> transformation = getTransformationByTypeA(entityType);
				if (transformation != null) {
					transformation.transformABChildren(a, b, ctx);
				} else {
					throw new IllegalStateException(
							"cannot find transformation for "
									+ entityType.getCode());
				}
				return b;
			}

			@Override
			public Type<? extends B> getTargetType(Type<? extends A> sourceType) {
				return getTypeB(sourceType);
			}

			@Override
			public OneToOneAttributeTransformation<A, B> getAttributeTransformationByTarget(
					String attributeCode) {
				return (OneToOneAttributeTransformation<A, B>) EntityTypeTransformation.this
						.getAttributeTransformationByB(attributeCode);
			}

			@Override
			public OneToOneAttributeTransformation<A, B> getAttributeTransformationBySource(
					String attributeCode) {
				return (OneToOneAttributeTransformation<A, B>) EntityTypeTransformation.this
						.getAttributeTransformationByB(attributeCode);
			}
		};
	}

	protected Type<? extends B> getTypeB(Type<? extends A> sourceType) {
		return getTransformationByTypeA((EntityType<?>) sourceType)
				.getEntityTypeB();
	}

	protected Type<? extends A> getTypeA(Type<? extends B> sourceType) {
		return getTransformationByTypeB((EntityType<?>) sourceType)
				.getEntityTypeA();
	}

	@Override
	public UniTransformation<B, A> getBA() {
		return new UniTransformation<B, A>() {

			@Override
			public A convert(B b, TransformationContext ctx) {

				EntityType<B> entityType = ctx.getEntityTypeByB(b);
				if (entityType == null) {
					entityType = entityTypeB;
				}
				if (finder != null) {
					UniTransformation<B, A> ba = finder.getBA(b, ctx);
					if (ba != null) {
						return ba.convert(b, ctx);
					}
				}
				return getTransformationByTypeB(entityType).createA(b, ctx);
			}

			@Override
			public Type<B> getSourceType() {
				return entityTypeB;
			}

			@Override
			public Type<A> getTargetType() {
				return entityTypeA;
			}

			@Override
			public A merge(B b, A a, TransformationContext ctx) {
				EntityType<B> entityType = ctx.getEntityTypeByB(b);
				if (entityType == null) {
					entityType = entityTypeB;
				}
				if (finder != null) {
					UniTransformation<B, A> ba = finder.getBA(b, ctx);
					if (ba != null) {
						return ba.merge(b, a, ctx);
					}
				}
				getTransformationByTypeB(entityType).transformBAChildren(b, a,
						ctx);

				return a;
			}

			@Override
			public Type<? extends A> getTargetType(Type<? extends B> sourceType) {
				return getTypeA(sourceType);
			}

			@Override
			public OneToOneAttributeTransformation<B, A> getAttributeTransformationByTarget(
					String attributeCode) {
				return (OneToOneAttributeTransformation<B, A>) TransformUtils
						.reverse(EntityTypeTransformation.this
								.getAttributeTransformationByA(attributeCode));
			}

			@Override
			public OneToOneAttributeTransformation<B, A> getAttributeTransformationBySource(
					String attributeCode) {
				return (OneToOneAttributeTransformation<B, A>) TransformUtils
						.reverse(EntityTypeTransformation.this
								.getAttributeTransformationByB(attributeCode));
			}

		};
	}

	private EntityTypeTransformation<A, B> getBySubtypesA(
			EntityType<?> entityType) {
		if (entityType.equals(getEntityTypeA())) {
			return this;
		}
		EntityTypeTransformation<A, B> transformation = null;
		for (EntityTypeTransformation<A, B> subTransformation : subTransformations) {
			EntityTypeTransformation<A, B> otherTransformation = subTransformation
					.getBySubtypesA(entityType);
			if (otherTransformation != null) {
				if (transformation != null) {
					throw new IllegalStateException(
							"there are two transformations for the same type");
				} else {
					transformation = otherTransformation;
				}
			}

		}
		return transformation;
	}

	private EntityTypeTransformation<A, B> getBySubtypesB(
			EntityType<?> entityType) {
		if (entityType.equals(getEntityTypeB())) {
			return this;
		} else if (!getEntityTypeB().isAssignableFrom(entityType)) {
			return null;
		}
		EntityTypeTransformation<A, B> transformation = null;
		for (EntityTypeTransformation<A, B> subTransformation : subTransformations) {
			EntityTypeTransformation<A, B> otherTransformation = subTransformation
					.getBySubtypesB(entityType);
			if (otherTransformation != null) {
				if (transformation != null) {
					throw new IllegalStateException(
							"there are two transformations for the same type");
				} else {
					transformation = otherTransformation;
				}
			}

		}
		if (transformation != null) {
			return transformation;
		} else {
			return this;
		}
	}

	public EntityType<A> getEntityTypeA() {
		return entityTypeA;
	}

	public EntityType<B> getEntityTypeB() {
		return entityTypeB;
	}

	public EntityTypeTransformation<A, B> getSuperTransformation() {
		return superTransformation;
	}

	public EntityTypeTransformation<A, B> getTransformationByTypeA(
			EntityType<?> entityType) {

		if (entityType.equals(getEntityTypeA())) {
			return this;
		} else if (entityType.isAssignableFrom(getEntityTypeA())) {
			EntityTypeTransformation<A, B> transformation = superTransformation
					.getTransformationByTypeA(entityType);
			if (transformation == null) {
				return this;
			} else {
				return transformation;
			}
		} else if (getEntityTypeA().isAssignableFrom(entityType)) {
			EntityTypeTransformation<A, B> transformation = getBySubtypesA(entityType);
			if (transformation == null) {
				return this;
			} else {
				return transformation;
			}
		}
		return this;
	}

	public EntityTypeTransformation<A, B> getTransformationByTypeB(
			EntityType<?> entityType) {

		if (entityType.equals(getEntityTypeB())) {
			return this;
		} else if (entityType.isAssignableFrom(getEntityTypeB())) {
			return superTransformation.getTransformationByTypeB(entityType);
		} else {
			return getBySubtypesB(entityType);
		}
	}

	@Override
	public Type getTypeA() {
		return getEntityTypeA();
	}

	@Override
	public Type getTypeB() {
		return getEntityTypeB();
	}

	public Transformation<A, B> getTypeConverter() {
		return typeConverter;
	}

	public void setEntityTypeA(EntityType<A> entityTypeA) {
		this.entityTypeA = entityTypeA;
	}

	public void setEntityTypeB(EntityType<B> entityTypeB) {
		this.entityTypeB = entityTypeB;
	}

	public void setFinder(TransformationFinder<A, B> finder) {
		this.finder = finder;
	}

	public void setSuperTransformation(
			EntityTypeTransformation<A, B> superTransformation) {
		this.superTransformation = superTransformation;

	}

	/**
	 * The Type Converter creates instances of the target type from the source
	 * type. The type's attribute will usually be transformed by the contained
	 * transformations.
	 * 
	 * @param typeConverter
	 */
	public void setTypeConverter(Transformation<A, B> typeConverter) {
		this.typeConverter = typeConverter;
	}

	protected void transformABChildren(A valueA, B valueB,
			TransformationContext ctx) {
		if (superTransformation != null) {
			superTransformation.transformABChildren(valueA, valueB, ctx);
		}
		for (JavaTransformation<A, B> transformation : javaTransformations) {
			transformation.mergeAB(valueA, valueB, ctx);
		}
		for (AttributeTransformation<A, B> transformation : embeddedTransformations) {
			transformation.mergeAB(valueA, valueB, ctx);
		}
	}

	protected void transformBAChildren(B valueB, A valueA,
			TransformationContext ctx) {
		if (superTransformation != null) {
			superTransformation.transformBAChildren(valueB, valueA, ctx);
		}
		for (JavaTransformation<A, B> transformation : javaTransformations) {
			transformation.mergeBA(valueB, valueA, ctx);
		}
		for (AttributeTransformation<A, B> transformation : embeddedTransformations) {
			transformation.mergeBA(valueB, valueA, ctx);
		}
	}

	@Override
	public AbstractOneToOneAttributeTransformation<A, B> getAttributeTransformationByA(
			String attributeCode) {
		for (AttributeTransformation<A, B> embedded : embeddedTransformations) {
			if (embedded instanceof AbstractOneToOneAttributeTransformation) {
				AbstractOneToOneAttributeTransformation<A, B> oneToOne = (AbstractOneToOneAttributeTransformation<A, B>) embedded;
				if (oneToOne.getAttributeA().getAsString()
						.equals(attributeCode)) {
					return oneToOne;
				}
			}
		}
		if (superTransformation != null) {
			return superTransformation
					.getAttributeTransformationByA(attributeCode);
		}
		return null;
	}

	@Override
	public AbstractOneToOneAttributeTransformation<A, B> getAttributeTransformationByB(
			String attributeCode) {
		for (AttributeTransformation<A, B> embedded : embeddedTransformations) {
			if (embedded instanceof AbstractOneToOneAttributeTransformation) {
				AbstractOneToOneAttributeTransformation<A, B> oneToOne = (AbstractOneToOneAttributeTransformation<A, B>) embedded;
				if (oneToOne.getAttributeB().getAsString()
						.equals(attributeCode)) {
					return oneToOne;
				}
			}
		}
		if (superTransformation != null) {
			return superTransformation
					.getAttributeTransformationByB(attributeCode);
		}
		return null;
	}

	public Attribute getDerivedAttribute(Attribute originalAttribute) {
		AbstractOneToOneAttributeTransformation<A, B> attributeTransformationByA = getAttributeTransformationByA(originalAttribute
				.getCode());
		if (attributeTransformationByA != null) {
			return attributeTransformationByA.getAttributeB().getAttribute();
		} else {
			return null;
		}
	}

	public Attribute getOriginalAttribute(Attribute derivedAttribute) {
		AbstractOneToOneAttributeTransformation<A, B> attributeTransformationByB = getAttributeTransformationByB(derivedAttribute
				.getCode());
		if (attributeTransformationByB != null) {
			return attributeTransformationByB.getAttributeA().getAttribute();
		} else {
			return null;
		}
	}

}
