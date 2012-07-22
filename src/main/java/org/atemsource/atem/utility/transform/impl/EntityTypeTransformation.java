/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.transform.impl;

import java.util.Set;

import javax.inject.Inject;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.transform.api.Transformation;
import org.atemsource.atem.utility.transform.api.UniTransformation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class EntityTypeTransformation<A, B> extends EntityTransformation
		implements Transformation<A, B> {
	private EntityType<A> entityTypeA;

	private EntityType<B> entityTypeB;

	private EntityTypeTransformation<A, B> superTransformation;
	private Set<EntityTypeTransformation<A, B>> subTransformations;

	@Inject
	private EntityTypeRepository entityTypeRepository;

	public A createA(B b) {
		A valueA = (A) getTypeConverter().getBA().convert(b);
		transformBAChildren(b, valueA);
		return valueA;
	}

	public B createB(A a) {
		if (a == null) {
			return null;
		}
		B valueB = (B) getTypeConverter().getAB().convert(a);
		transformABChildren(a, valueB);
		return valueB;
	}

	protected UniTransformation getAB(Object value) {

		EntityType<?> entityType = entityTypeRepository.getEntityType(value);
		return getTransformationByTypeA(entityType).getAB();
	}

	protected UniTransformation getBA(Object value) {

		EntityType<?> entityType = entityTypeRepository.getEntityType(value);
		return getTransformationByTypeA(entityType).getBA();
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

	protected EntityTypeTransformation<A, B> getTransformationByTypeA(
			EntityType<?> entityType) {

		if (entityType.equals(getEntityTypeA())) {
			return this;
		} else if (entityType.isAssignableFrom(getEntityTypeA())) {
			return superTransformation.getTransformationByTypeA(entityType);
		} else {
			for (EntityTypeTransformation<A, B> subTransformation : subTransformations) {
				EntityTypeTransformation<A, B> transformation = subTransformation
						.getTransformationByTypeA(entityType);
				if (transformation != null) {
					return transformation;
				}
			}
		}
		throw new IllegalArgumentException(
				"cannot transformation entity of type " + entityType.getCode());
	}

	@Override
	protected void transformABChildren(Object valueA, Object valueB) {
		if (superTransformation != null) {
			superTransformation.transformABChildren(valueA, valueB);
		}
		super.transformABChildren(valueA, valueB);
	}

	@Override
	protected void transformBAChildren(Object valueB, Object valueA) {
		if (superTransformation != null) {
			superTransformation.transformBAChildren(valueB, valueA);
		}
		super.transformBAChildren(valueB, valueA);
	}

	public EntityTypeTransformation<A, B> getSuperTransformation() {
		return superTransformation;
	}

	public void setSuperTransformation(
			EntityTypeTransformation<A, B> superTransformation) {
		this.superTransformation = superTransformation;
	}

	@Override
	public UniTransformation<A, B> getAB() {
		return new UniTransformation<A, B>() {

			@Override
			public B convert(A a) {
				EntityType<A> entityType = entityTypeRepository
						.getEntityType(a);
				return getTransformationByTypeA(entityType).createB(a);
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
			public B merge(A a, B b) {
				EntityType<A> entityType = entityTypeRepository
						.getEntityType(a);
				getTransformationByTypeA(entityType).transformABChildren(a, b);
				return b;
			}
		};
	}

	@Override
	public UniTransformation<B, A> getBA() {
		return new UniTransformation<B, A>() {

			@Override
			public A convert(B b) {
				EntityType<B> entityType = entityTypeRepository
						.getEntityType(b);
				return getTransformationByTypeB(entityType).createA(b);
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
			public A merge(B b, A a) {
				EntityType<B> entityType = entityTypeRepository
						.getEntityType(b);
				getTransformationByTypeB(entityType).transformBAChildren(b, a);

				return a;
			}

		};
	}

	public EntityType<A> getEntityTypeA() {
		return entityTypeA;
	}

	public EntityType<B> getEntityTypeB() {
		return entityTypeB;
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

}
