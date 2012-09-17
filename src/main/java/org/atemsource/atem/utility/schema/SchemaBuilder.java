package org.atemsource.atem.utility.schema;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.CollectionAttribute;
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
import org.atemsource.atem.utility.transform.api.JavaTransformation;
import org.atemsource.atem.utility.transform.api.TransformationBuilderFactory;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.transform.impl.builder.CollectionToMap;
import org.atemsource.atem.utility.transform.impl.builder.Embed;
import org.atemsource.atem.utility.transform.impl.builder.GenericTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.builder.SingleAttributeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.converter.ConverterUtils;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
public class SchemaBuilder {
	@Inject
	private TransformationBuilderFactory transformationBuilderFactory;
	@Resource(name = "atem-json-repository")
	private DynamicEntityTypeSubrepository<?> subrepository;
	private EntityTypeTransformation<EntityType, ?> transformation;

	@PostConstruct
	public void init() {
		EntityTypeBuilder typeBuilder = subrepository.createBuilder("schema");
		
		EntityTypeBuilder basicTypeBuilder = subrepository
				.createBuilder("basic-type");
		TypeTransformationBuilder<Type, ?> basicTransformationBuilder = transformationBuilderFactory
				.create(Type.class, basicTypeBuilder);
		basicTransformationBuilder.transform().from("code").to("id");
		
		TypeTransformationBuilder<EntityType, ?> transformationBuilder = transformationBuilderFactory
				.create(EntityType.class, typeBuilder);
		transformationBuilder.transformCustom(CollectionToMap.class)
				.from("attributes").to("attributes")
				.keyConvert(getAttributeKeyConverter())
				.convert(getAttributeTransformation());
		// we cannot do this because the supertype of EntityType is not Type.
		//transformationBuilder.transform().from("code").to("id");
		transformationBuilder.includeSuper(basicTransformationBuilder.buildTypeTransformation());
		transformation = transformationBuilder.buildTypeTransformation();

	}

	public EntityTypeTransformation<EntityType, ?> getTransformation() {
		return transformation;
	}

	private Converter<Object, Object> getAttributeTransformation() {
		EntityTypeBuilder typeBuilder = subrepository
				.createBuilder("attribute");
		TypeTransformationBuilder<Attribute, ?> transformationBuilder = transformationBuilderFactory
				.create(Attribute.class, typeBuilder);
		SingleAttributeTransformationBuilder<Attribute, ?> requiredTransformer = transformationBuilder
				.transform();
		requiredTransformer.from("required");
		requiredTransformer.to("required");
		transformationBuilder
				.transformCustom(Embed.class)
				.from("targetType")
				.transform(
						(EntityTypeTransformation<?, ?>) getTypeTransformation());
		transformationBuilder.transform().from("@maxLength").to("max-length");
		getSingleAttributeTransformation(transformationBuilder.getReference());
		getListAttributeTransformation(transformationBuilder.getReference());
		return transformationBuilder.buildTypeTransformation();
	}

	private void getSingleAttributeTransformation(
			EntityTypeTransformation attributeTransformation) {
		EntityTypeBuilder typeBuilder = subrepository
				.createBuilder("single-attribute");
		TypeTransformationBuilder<SingleAttribute, ?> transformationBuilder = transformationBuilderFactory
				.create(SingleAttribute.class, typeBuilder);
		transformationBuilder
				.transformCustom(Embed.class)
				.from("targetType")
				.transform(
						(EntityTypeTransformation<?, ?>) getTypeTransformation());
		transformationBuilder.includeSuper(attributeTransformation);
		transformationBuilder.buildTypeTransformation();
	}

	private void getListAttributeTransformation(
			EntityTypeTransformation attributeTransformation) {
		EntityTypeBuilder typeBuilder = subrepository
				.createBuilder("list-attribute");
		TypeTransformationBuilder<CollectionAttribute, ?> transformationBuilder = transformationBuilderFactory
				.create(CollectionAttribute.class, typeBuilder);
		GenericTransformationBuilder arrayAttributeBuilder = transformationBuilder
				.transformCustom(GenericTransformationBuilder.class);
		arrayAttributeBuilder.to().addSingleAttribute("array", String.class);
		arrayAttributeBuilder
				.transform(new JavaTransformation<Object, ObjectNode>() {

					@Override
					public void mergeAB(Object a, ObjectNode b) {
						b.put("array", true);
					}

					@Override
					public void mergeBA(ObjectNode b, Object a) {
					}

				});
		transformationBuilder.includeSuper(attributeTransformation);
		transformationBuilder.buildTypeTransformation();
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

	private EntityTypeTransformation<?, ?> primitiveTypeTransformation;

	private EntityTypeTransformation<?, ?> getTypeTransformation() {
		if (primitiveTypeTransformation == null) {
			primitiveTypeTransformation = createTypeTransformation();
		}
		return primitiveTypeTransformation;
	}
	

	protected EntityTypeTransformation<?, ?> createTypeTransformation() {
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
