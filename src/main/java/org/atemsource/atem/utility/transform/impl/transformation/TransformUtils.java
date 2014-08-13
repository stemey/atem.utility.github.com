package org.atemsource.atem.utility.transform.impl.transformation;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.atemsource.atem.api.attribute.AssociationAttribute;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.service.FindByIdService;
import org.atemsource.atem.api.service.IdentityAttributeService;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.path.AttributePath;
import org.atemsource.atem.utility.transform.api.AttributeTransformation;
import org.atemsource.atem.utility.transform.api.Transformation;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.api.UniTransformation;
import org.atemsource.atem.utility.transform.service.CreationService;

/**
 * 
 * @author stefan
 * 
 */
public class TransformUtils {
	public static <A, B> B findTargetEntity(
			UniTransformation<A, B> transformation, A source, B target,
			TransformationContext context, Attribute<?,?> attribute, Object targetParent) {

		EntityType<A> entityTypeByA = context.getEntityTypeByA(source);
		if (entityTypeByA == null) {
			Type<A> sourceType = transformation.getSourceType();
			if (sourceType instanceof EntityType) {
				entityTypeByA = (EntityType<A>) sourceType;
			}
		}
		Type<? extends B> typeB;
		if (entityTypeByA == null) {
			typeB = transformation.getTargetType();
		} else {
			typeB = transformation.getTargetType(entityTypeByA);
		}

		if (typeB instanceof EntityType) {
			EntityType<Object> targetType = (EntityType<Object>) typeB;
			IdentityAttributeService identityService = targetType
					.getService(IdentityAttributeService.class);
			Serializable id = target == null ? null : identityService.getId(
					targetType, (Object) target);

			// check if the existing target has the right type
			if (target != null) {
				EntityType<B> targetInstanceType = context
						.getEntityTypeByB(target);
				if (!targetInstanceType.equals(targetType)) {
					target = null;
				}
			}

			// find the persistent entity to merge onto that
			FindByIdService findByIdService = targetType
					.getService(FindByIdService.class);
			if (identityService != null && findByIdService != null) {
				SingleAttribute<? extends Serializable> idAttribute = identityService
						.getIdAttribute(targetType);
				OneToOneAttributeTransformation<A, B> attributeTransformation = transformation
						.getAttributeTransformationByTarget(idAttribute
								.getCode());
				if (attributeTransformation != null) {
					Object idA = attributeTransformation.getAttributeA()
							.getValue(source);
					if (idA != id) {

						Transformation idConverter = attributeTransformation
								.getTransformation();
						Object idB;
						if (idConverter != null) {
							idB = idConverter.getAB().convert(idA, context);
						} else {
							idB = idA;
						}
						target = (B) findByIdService.findById(targetType,
								(Serializable) idB);
					}
				}
			}
		} else {
			target = null;
		}

		if ( target == null && entityTypeByA != null) {
			if (attribute!=null ) {
				if (attribute instanceof AssociationAttribute) {
					AssociationAttribute associationAttribute=(AssociationAttribute) attribute;
					target=(B) associationAttribute.createTarget((EntityType) typeB, targetParent);
				}
			}else{
			CreationService<B, A> creationService = ((EntityType<B>) typeB)
					.getService(CreationService.class);
			if (creationService != null) {
				target = creationService.create((EntityType<B>) typeB,
						entityTypeByA, source);
			}
			}
		}

		return target;

	}

	public static <A, B> Transformation<B, A> reverse(
			final Transformation<A, B> original) {
		if (original == null) {
			return null;
		}
		return new Transformation<B, A>() {

			@Override
			public Type getTypeA() {
				return original.getTypeB();
			}

			@Override
			public Type getTypeB() {
				return original.getTypeA();
			}

			@Override
			public UniTransformation<B, A> getAB() {
				return original.getBA();
			}

			@Override
			public UniTransformation<A, B> getBA() {
				return original.getAB();
			}

			@Override
			public AttributeTransformation<B, A> getAttributeTransformationByA(
					String attributeCode) {
				return reverse((OneToOneAttributeTransformation) original
						.getAttributeTransformationByB(attributeCode));
			}

			@Override
			public AttributeTransformation<B, A> getAttributeTransformationByB(
					String attributeCode) {
				return reverse((OneToOneAttributeTransformation) original
						.getAttributeTransformationByA(attributeCode));
			}
		};
	}

	public static <A, B> OneToOneAttributeTransformation<B, A> reverse(
			final OneToOneAttributeTransformation<A, B> original) {
		if (original == null) {
			return null;
		}
		return new OneToOneAttributeTransformation<B, A>() {

			public void mergeBA(A a, B b, TransformationContext ctx) {
				original.mergeAB(a, b, ctx);
			}

			public void mergeAB(B b, A a, TransformationContext ctx) {
				original.mergeBA(b, a, ctx);
			}

			@Override
			public Set<AttributePath> getAttributeAs() {
				return original.getAttributeBs();
			}

			@Override
			public Set<AttributePath> getAttributeBs() {
				return original.getAttributeAs();
			}

			@Override
			public Map<String, Object> getMeta() {
				return original.getMeta();
			}

			@Override
			public EntityType getTypeA() {
				return original.getTypeB();
			}

			@Override
			public EntityType getTypeB() {
				return original.getTypeA();
			}

			@Override
			public Transformation getTransformation() {
				return (Transformation) TransformUtils.reverse(original
						.getTransformation());
			}

			@Override
			public AttributePath getAttributeA() {
				return original.getAttributeB();
			}

			@Override
			public AttributePath getAttributeB() {
				return original.getAttributeA();
			}

		};
	}
}
