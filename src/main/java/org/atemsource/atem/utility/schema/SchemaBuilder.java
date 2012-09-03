package org.atemsource.atem.utility.schema;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.primitive.IntegerType;
import org.atemsource.atem.api.attribute.primitive.TextType;
import org.atemsource.atem.api.attribute.relation.ListAssociationAttribute;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.spi.DynamicEntityTypeSubrepository;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.JavaConverter;
import org.atemsource.atem.utility.transform.api.TransformationBuilderFactory;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.transform.impl.builder.CollectionToMap;
import org.atemsource.atem.utility.transform.impl.builder.SingleAttributeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.converter.ConverterUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class SchemaBuilder {
	@Inject
	private TransformationBuilderFactory transformationBuilderFactory;
	@Resource(name="atem-json-repository")
	private DynamicEntityTypeSubrepository<?> subrepository;

	public EntityTypeTransformation<EntityType, ?> init() {
		EntityTypeBuilder typeBuilder = subrepository.createBuilder("schema");
		TypeTransformationBuilder<EntityType, ?> transformationBuilder = transformationBuilderFactory
				.create(EntityType.class, typeBuilder);
		transformationBuilder.transformCustom(CollectionToMap.class).from("attributes").to("attributes")
				.keyConvert(getAttributeKeyConverter())
				.convert(getAttributeTransformation());
		return transformationBuilder.buildTypeTransformation();

	}

	private Converter<Object, Object> getAttributeTransformation() {
		return getSingleAttributeTransformation();
	}

	private Converter<Object, Object> getSingleAttributeTransformation() {
		EntityTypeBuilder typeBuilder = subrepository
				.createBuilder("single-attribute");
		TypeTransformationBuilder<SingleAttribute, ?> transformationBuilder = transformationBuilderFactory
				.create(SingleAttribute.class, typeBuilder);
		SingleAttributeTransformationBuilder<SingleAttribute, ?> requiredTransformer = transformationBuilder
				.transform();
		requiredTransformer.from("required");
		requiredTransformer.to("required");
		SingleAttributeTransformationBuilder<SingleAttribute, ?> typeTransformer = transformationBuilder
				.transform();
		typeTransformer.from("targetType");
		typeTransformer.to("type").convert(getTypeTransformation());
		return transformationBuilder.buildTypeTransformation();
	}

	private Converter<Object, Object> getListAttributeTransformation() {
		EntityTypeBuilder typeBuilder = subrepository
				.createBuilder("list-attribute");
		TypeTransformationBuilder<SingleAttribute, ?> transformationBuilder = transformationBuilderFactory
				.create(SingleAttribute.class, typeBuilder);
		SingleAttributeTransformationBuilder<SingleAttribute, ?> requiredTransformer = transformationBuilder
				.transform();
		requiredTransformer.from("required");
		requiredTransformer.to("required");
		SingleAttributeTransformationBuilder<SingleAttribute, ?> typeTransformer = transformationBuilder
				.transform();
		typeTransformer.from("targetType");
		typeTransformer.to("type").convert(getTypeTransformation());
		return transformationBuilder.buildTypeTransformation();
	}

	private void createTextTypeTransformation(
			EntityTypeTransformation<Type, ?> superTransformation) {
		EntityTypeBuilder typeBuilder = subrepository
				.createBuilder("text-type");
		TypeTransformationBuilder<TextType, ?> transformationBuilder = transformationBuilderFactory
				.create(TextType.class, typeBuilder);
		transformationBuilder.transform().to("max-length").from("maxLength");
		transformationBuilder.includeSuper(superTransformation);
		transformationBuilder.buildTypeTransformation();
	}

	private void createIntegerTypeTransformation(
			EntityTypeTransformation<Type, ?> superTransformation) {
		EntityTypeBuilder typeBuilder = subrepository
				.createBuilder("integer-type");
		TypeTransformationBuilder<IntegerType, ?> transformationBuilder = transformationBuilderFactory
				.create(IntegerType.class, typeBuilder);
		transformationBuilder.includeSuper(superTransformation);
		transformationBuilder.buildTypeTransformation();
	}

	private void createListTypeTransformation(
			EntityTypeTransformation<Type, ?> superTransformation) {
		EntityTypeBuilder typeBuilder = subrepository
				.createBuilder("list-type");
		TypeTransformationBuilder<ListAssociationAttribute, ?> transformationBuilder = transformationBuilderFactory
				.create(ListAssociationAttribute.class, typeBuilder);
		transformationBuilder.includeSuper(superTransformation);
		transformationBuilder.buildTypeTransformation();
	}

	private Converter<Type, ?> getTypeTransformation() {
		EntityTypeBuilder typeBuilder = subrepository
				.createBuilder("primitive-type");
		TypeTransformationBuilder<Type, ?> transformationBuilder = transformationBuilderFactory
				.create(Type.class, typeBuilder);
		createTextTypeTransformation((EntityTypeTransformation<Type, ?>) transformationBuilder
				.getReference());
		createIntegerTypeTransformation((EntityTypeTransformation<Type, ?>) transformationBuilder
				.getReference());
		transformationBuilder.transform().from("code").to("id");
		return transformationBuilder.buildTypeTransformation();
	}

	private Converter<Attribute<?, ?>, String> getAttributeKeyConverter() {
		JavaConverter<Attribute<?, ?>, String> javaConverter = new JavaConverter<Attribute<?, ?>, String>() {

			public String convertAB(Attribute a) {
				return a.getCode();
			}

			public Attribute convertBA(String b) {
				return null;
			}
		};
		return ConverterUtils.create(javaConverter);
	}
}
