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
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.transform.api.AttributeTransformation;
import org.atemsource.atem.utility.transform.api.JavaTransformation;
import org.atemsource.atem.utility.transform.api.Transformation;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.api.UniTransformation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class EntityTypeTransformation<A, B> implements Transformation<A, B> {
	private EntityType<A> entityTypeA;

	private EntityType<B> entityTypeB;
	
	private List<JavaTransformation<A, B>> javaTransformations= new ArrayList<JavaTransformation<A,B>>();
public void addTransformation(JavaTransformation<A, B> javaTransformation) {
	javaTransformations.add(javaTransformation);
}

	@Inject
	private EntityTypeRepository entityTypeRepository;

	private Set<EntityTypeTransformation<A, B>> subTransformations = new HashSet<EntityTypeTransformation<A, B>>();

	private EntityTypeTransformation<A, B> superTransformation;

	public void addSubTransformation(
			EntityTypeTransformation<A, B> subTransformation) {
		this.subTransformations.add(subTransformation);
	}

	private A createA(B b, TransformationContext ctx) {
		if (b == null) {
			return null;
		}
		if (ctx.isTransformed(b)) {
			return (A) ctx.getTransformed(b);
		} else {
			A valueA = (A) getTypeConverter().getBA().convert(b, ctx);
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
			B valueB = (B) getTypeConverter().getAB().convert(a, ctx);
			ctx.transformed(a, valueB);
			transformABChildren(a, valueB, ctx);
			return valueB;
		}
	}

	private List<AttributeTransformation<A, B>> embeddedTransformations = new ArrayList<AttributeTransformation<A, B>>();

	public void addTransformation(AttributeTransformation<A, B> transformation) {
		this.embeddedTransformations.add(transformation);
	}

	private Transformation<A, B> typeConverter;

	public Transformation<A, B> getTypeConverter() {
		return typeConverter;
	}

	public void setTypeConverter(Transformation<A, B> typeConverter) {
		this.typeConverter = typeConverter;
	}

	@Override
	public UniTransformation<A, B> getAB() {
		return new UniTransformation<A, B>() {

			@Override
			public B convert(A a, TransformationContext ctx) {

				if (a == null) {
					return null;
				}
				EntityType<A> entityType = entityTypeRepository
						.getEntityType(a);
				EntityTypeTransformation<A, B> transformationByTypeA = getTransformationByTypeA(entityType);
				if (transformationByTypeA==null) {
					throw new IllegalStateException("cannot find transformation for "+entityType.getCode());
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
				EntityType<A> entityType = entityTypeRepository
						.getEntityType(a);
				getTransformationByTypeA(entityType).transformABChildren(a, b,
						ctx);
				return b;
			}
		};
	}

	protected UniTransformation getAB(Object value) {

		EntityType<?> entityType = entityTypeRepository.getEntityType(value);
		return getTransformationByTypeA(entityType).getAB();
	}

	@Override
	public UniTransformation<B, A> getBA() {
		return new UniTransformation<B, A>() {

			@Override
			public A convert(B b, TransformationContext ctx) {
				EntityType<B> entityType = entityTypeRepository
						.getEntityType(b);
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
				EntityType<B> entityType = entityTypeRepository
						.getEntityType(b);
				getTransformationByTypeB(entityType).transformBAChildren(b, a,
						ctx);

				return a;
			}

		};
	}

	protected UniTransformation getBA(Object value) {

		EntityType<?> entityType = entityTypeRepository.getEntityType(value);
		return getTransformationByTypeA(entityType).getBA();
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

	protected EntityTypeTransformation<A, B> getTransformationByTypeA(
			EntityType<?> entityType) {

		if (entityType.equals(getEntityTypeA())) {
			return this;
		} else if (entityType.isAssignableFrom(getEntityTypeA())) {
			return superTransformation.getTransformationByTypeA(entityType);
		} else if (getEntityTypeA().isAssignableFrom(entityType)) {
			for (EntityTypeTransformation<A, B> subTransformation : subTransformations) {
				EntityTypeTransformation<A, B> transformation = subTransformation
						.getTransformationByTypeA(entityType);
				if (transformation != null) {
					return transformation;
				}
			}
			 return this;
		}
		return null;
	}

	protected EntityTypeTransformation<A, B> getTransformationByTypeB(
			EntityType<?> entityType) {

		if (entityType.equals(getEntityTypeB())) {
			return this;
		} else if (entityType.isAssignableFrom(getEntityTypeB())) {
			return superTransformation.getTransformationByTypeB(entityType);
		} else {
			for (EntityTypeTransformation<A, B> subTransformation : subTransformations) {
				EntityTypeTransformation<A, B> transformation = subTransformation
						.getTransformationByTypeB(entityType);
				if (transformation != null) {
					return transformation;
				}
			}
		}
		throw new IllegalArgumentException(
				"cannot transformation entity of type " + entityType.getCode());
	}

	@Override
	public Type getTypeA() {
		return getEntityTypeA();
	}

	@Override
	public Type getTypeB() {
		return getEntityTypeB();
	}

	public void setEntityTypeA(EntityType<A> entityTypeA) {
		this.entityTypeA = entityTypeA;
	}

	public void setEntityTypeB(EntityType<B> entityTypeB) {
		this.entityTypeB = entityTypeB;
	}

	public void setSuperTransformation(
			EntityTypeTransformation<A, B> superTransformation) {
		this.superTransformation = superTransformation;

	}

	protected void transformABChildren(A valueA, B valueB,
			TransformationContext ctx) {
		if (superTransformation != null) {
			superTransformation.transformABChildren(valueA, valueB, ctx);
		}
		for (JavaTransformation<A, B> transformation : javaTransformations) {
			transformation.mergeAB(valueA, valueB);
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
			transformation.mergeBA(valueB, valueA);
		}
		for (AttributeTransformation<A, B> transformation : embeddedTransformations) {
			transformation.mergeBA(valueB, valueA, ctx);
		}
	}

}
