package org.atemsource.atem.utility.transform.impl.builder;

import org.atemsource.atem.api.attribute.CollectionAttribute;
import org.atemsource.atem.api.attribute.CollectionSortType;
import org.atemsource.atem.api.attribute.MapAttribute;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.api.type.PrimitiveType;
import org.atemsource.atem.api.type.Type;

public class TransformationTargetTypeBuilder {

	private GenericTransformationBuilder<?, ?> genericTransformationBuilder;

	public TransformationTargetTypeBuilder(
			GenericTransformationBuilder<?,?> genericTransformationBuilder) {
		this.genericTransformationBuilder=genericTransformationBuilder;
	}

	public <K, V, R> MapAttribute<K, V, R> addMapAssociationAttribute(
			String code, Type<K> keyType, Type<V> valueType) {
		 MapAttribute<K, V, R> attribute = getEntityTypeBuilder().addMapAssociationAttribute(code, keyType, valueType);
		genericTransformationBuilder.addTargetAttribute(attribute);
		return attribute;
	}

	public <J, R> CollectionAttribute<J, R> addMultiAssociationAttribute(
			String code, Type<J> targetType,
			CollectionSortType collectionSortType) {
		 CollectionAttribute<J, R> attribute = getEntityTypeBuilder().addMultiAssociationAttribute(code, targetType,
				collectionSortType);
		genericTransformationBuilder.addTargetAttribute(attribute);
		return attribute;
	}

	public <J> SingleAttribute<J> addPrimitiveAttribute(String code,
			PrimitiveType<J> type) {
		 SingleAttribute<J> attribute = getEntityTypeBuilder().addPrimitiveAttribute(code, type);
		genericTransformationBuilder.addTargetAttribute(attribute);
		return attribute;
	}

	public <J> SingleAttribute<J> addSingleAssociationAttribute(String code,
			EntityType<J> targetType) {
		SingleAttribute<J> attribute= getEntityTypeBuilder().addSingleAssociationAttribute(code, targetType);
		genericTransformationBuilder.addTargetAttribute(attribute);
		return attribute;
	}

	public <J> SingleAttribute<J> addSingleAttribute(String code, Type<J> type) {
		 SingleAttribute<J> attribute = getEntityTypeBuilder().addSingleAttribute(code, type);
		genericTransformationBuilder.addTargetAttribute(attribute);
		return attribute;
	}

	private EntityTypeBuilder getEntityTypeBuilder() {
		return genericTransformationBuilder.getTargetTypeBuilder();
	}

	
}
