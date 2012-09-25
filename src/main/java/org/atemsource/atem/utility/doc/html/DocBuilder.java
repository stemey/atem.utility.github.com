package org.atemsource.atem.utility.doc.html;

import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.log4j.Logger;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.CollectionAttribute;
import org.atemsource.atem.api.attribute.CollectionSortType;
import org.atemsource.atem.api.attribute.JavaMetaData;
import org.atemsource.atem.api.attribute.relation.ListAssociationAttribute;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.api.type.primitive.ChoiceType;
import org.atemsource.atem.api.type.primitive.DateType;
import org.atemsource.atem.api.type.primitive.IntegerType;
import org.atemsource.atem.api.type.primitive.TextType;
import org.atemsource.atem.impl.dynamic.DynamicEntity;
import org.atemsource.atem.impl.json.JsonUtils;
import org.atemsource.atem.spi.DynamicEntityTypeSubrepository;
import org.atemsource.atem.utility.schema.ValidTypes;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.JavaConverter;
import org.atemsource.atem.utility.transform.api.JavaTransformation;
import org.atemsource.atem.utility.transform.api.TransformationBuilderFactory;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.api.UniTransformation;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.transform.impl.builder.Embed;
import org.atemsource.atem.utility.transform.impl.builder.GenericTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.builder.SingleAttributeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.converter.ConverterUtils;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.stereotype.Component;

public class DocBuilder
{
	private static Logger logger = Logger.getLogger(DocBuilder.class);


	@Inject
	private EntityTypeRepository entityTypeRepository;

	private EntityTypeTransformation<EntityType, ?> entityTypeTransformation;


	private DynamicEntityTypeSubrepository<?> subrepository;

	private TransformationBuilderFactory transformationBuilderFactory;

	private void addConstantTransform(TypeTransformationBuilder<?, ?> transformationBuilder, final String attribute,
		final Class targetType, final Object value)
	{
		GenericTransformationBuilder arrayTransform =
			transformationBuilder.transformCustom(GenericTransformationBuilder.class);
		arrayTransform.to().addSingleAttribute(attribute, targetType);
		arrayTransform.transform(new JavaTransformation<Object, DynamicEntity>()
		{

			@Override
			public void mergeAB(Object a, DynamicEntity b, TransformationContext ctx)
			{
				b.put(attribute, value);
			}

			@Override
			public void mergeBA(DynamicEntity b, Object a, TransformationContext ctx)
			{
			}

		});
	}

	@SuppressWarnings("unchecked")
	private void addValuesForChoiceTypeTransform(TypeTransformationBuilder<?, ?> transformationBuilder, 
		final Class targetType)
	{
		GenericTransformationBuilder arrayTransform =
			transformationBuilder.transformCustom(GenericTransformationBuilder.class);
		Type type = getRepository().getType(targetType);
		arrayTransform.to().addMultiAssociationAttribute("values", type, CollectionSortType.ORDERABLE);
		arrayTransform.transform(new JavaTransformation<Object, ObjectNode>()
		{

			@Override
			public void mergeAB(Object a, ObjectNode b, TransformationContext ctx)
			{
				ChoiceType<?> choiceType = (ChoiceType<?>) a;
				ArrayNode arrayNode = b.arrayNode();
				for (Map.Entry<String, ?> entry : choiceType.getOptionsMap().entrySet())
				{
					arrayNode.add(String.valueOf(entry.getValue()));
				}
				b.put("values", arrayNode);
			}

			@Override
			public void mergeBA(ObjectNode b, Object a, TransformationContext ctx)
			{
			}

		});
	}

	private void createDateTypeTransformation(EntityTypeTransformation<Type, ?> superTransformation)
	{
		EntityTypeBuilder typeBuilder = subrepository.createBuilder("date-type");
		TypeTransformationBuilder<DateType, ?> transformationBuilder =
			transformationBuilderFactory.create(DateType.class, typeBuilder);
		addConstantTransform(transformationBuilder, "format", String.class, "dd-MM-yyyy");
		transformationBuilder.includeSuper(superTransformation);
		transformationBuilder.buildTypeTransformation();
	}

	private void createEnumTypeTransformation(EntityTypeTransformation<Type, ?> superTransformation)
	{
		EntityTypeBuilder typeBuilder = subrepository.createBuilder("enum-type");
		TypeTransformationBuilder<ChoiceType, ?> transformationBuilder =
			transformationBuilderFactory.create(ChoiceType.class, typeBuilder);
		addValuesForChoiceTypeTransform(transformationBuilder, String.class);
		addConstantTransform(transformationBuilder, "type", String.class, "text");
		transformationBuilder.includeSuper(superTransformation);
		transformationBuilder.buildTypeTransformation();
	}

	private void createIntegerTypeTransformation(EntityTypeTransformation<Type, ?> superTransformation)
	{
		EntityTypeBuilder typeBuilder = subrepository.createBuilder("integer-type");
		TypeTransformationBuilder<IntegerType, ?> transformationBuilder =
			transformationBuilderFactory.create(IntegerType.class, typeBuilder);
		transformationBuilder.includeSuper(superTransformation);
		transformationBuilder.buildTypeTransformation();
	}

	private void createListTypeTransformation(EntityTypeTransformation<Type, ?> superTransformation)
	{
		EntityTypeBuilder typeBuilder = subrepository.createBuilder("list-type");
		TypeTransformationBuilder<ListAssociationAttribute, ?> transformationBuilder =
			transformationBuilderFactory.create(ListAssociationAttribute.class, typeBuilder);
		transformationBuilder.includeSuper(superTransformation);
		transformationBuilder.buildTypeTransformation();
	}

	private void createTextTypeTransformation(EntityTypeTransformation<Type, ?> superTransformation)
	{
		EntityTypeBuilder typeBuilder = subrepository.createBuilder("text-type");
		TypeTransformationBuilder<TextType, ?> transformationBuilder =
			transformationBuilderFactory.create(TextType.class, typeBuilder);
		transformationBuilder.transform().to("max-length").from("maxLength");
		transformationBuilder.includeSuper(superTransformation);
		transformationBuilder.buildTypeTransformation();
	}



	protected EntityTypeTransformation<?, ?> createPrimitiveTypeRefTransformation(EntityTypeTransformation<?,?> typeRefTransformation)
	{
		EntityTypeBuilder typeBuilder = subrepository.createBuilder("schema.primitivetype");
		TypeTransformationBuilder<Type, ?> transformationBuilder =
			transformationBuilderFactory.create(Type.class, typeBuilder);
		createTextTypeTransformation((EntityTypeTransformation<Type, ?>) transformationBuilder.getReference());
		createEnumTypeTransformation((EntityTypeTransformation<Type, ?>) transformationBuilder.getReference());
		createDateTypeTransformation((EntityTypeTransformation<Type, ?>) transformationBuilder.getReference());
		createIntegerTypeTransformation((EntityTypeTransformation<Type, ?>) transformationBuilder.getReference());
		transformationBuilder.includeSuper(typeRefTransformation);
		
		// TODO missing super transformation typeTransformation
		return transformationBuilder.buildTypeTransformation();
	}

	private Converter<Attribute<?, ?>, String> getAttributeKeyConverter()
	{
		JavaConverter<Attribute<?, ?>, String> javaConverter = new JavaConverter<Attribute<?, ?>, String>()
		{

			@Override
			public String convertAB(Attribute a)
			{
				return a.getCode();
			}

			@Override
			public Attribute convertBA(String b)
			{
				return null;
			}
		};
		return ConverterUtils.create(javaConverter);
	}

	private Converter<Object, Object> getAttributeTransformation(EntityTypeTransformation typeRefTransformation)
	{
		EntityTypeBuilder typeBuilder = subrepository.createBuilder("attribute");
		TypeTransformationBuilder<Attribute, ?> transformationBuilder =
			transformationBuilderFactory.create(Attribute.class, typeBuilder);
		SingleAttributeTransformationBuilder<Attribute, ?> requiredTransformer = transformationBuilder.transform();
		requiredTransformer.from("required");
		requiredTransformer.to("required");
		// transformationBuilder.transform().from("code").to("label");
		// this should be genral type transformation without attributes : schema.typeref
		transformationBuilder.transform().from("targetType").to("targetType").convert(typeRefTransformation);


		transformationBuilder.transform().from("code").to("name");
		transformationBuilder.transform().from("code").to("description");
		addConstantTransform(transformationBuilder, "template", String.class, "attribute.vm");

		getSingleAttributeTransformation(transformationBuilder.getReference(),typeRefTransformation);
		getListAttributeTransformation(transformationBuilder.getReference(),typeRefTransformation);
		return transformationBuilder.buildTypeTransformation();
	}

	private void getListAttributeTransformation(EntityTypeTransformation attributeTransformation,EntityTypeTransformation typeRefTransformation)
	{
		EntityTypeBuilder typeBuilder = subrepository.createBuilder("list-attribute");
		TypeTransformationBuilder<CollectionAttribute, ?> transformationBuilder =
			transformationBuilderFactory.create(CollectionAttribute.class, typeBuilder);
		addConstantTransform(transformationBuilder, "array", Boolean.class, true);
		transformationBuilder.includeSuper(attributeTransformation);
		transformationBuilder.buildTypeTransformation();
	}

	private EntityTypeRepository getRepository()
	{
		return entityTypeRepository;
	}

	private void getSingleAttributeTransformation(EntityTypeTransformation attributeTransformation,EntityTypeTransformation typeRefTransformation)
	{
		EntityTypeBuilder typeBuilder = subrepository.createBuilder("single-attribute");
		TypeTransformationBuilder<SingleAttribute, ?> transformationBuilder =
			transformationBuilderFactory.create(SingleAttribute.class, typeBuilder);
		transformationBuilder.includeSuper(attributeTransformation);
		addConstantTransform(transformationBuilder, "array", Boolean.class, false);
		transformationBuilder.buildTypeTransformation();
	}


	public EntityTypeTransformation<EntityType, ?> getEntityTypeTransformation()
	{
		return entityTypeTransformation;
	}

	public TransformationBuilderFactory getTransformationBuilderFactory()
	{
		return transformationBuilderFactory;
	}

	public EntityTypeTransformation<Type,?> createTypeRefTransformation()
	{
		EntityTypeBuilder typeRefBuilder = subrepository.createBuilder("schema.type.ref");

		final TypeTransformationBuilder<Type, ?> typeRefTransformationBuilder =
			transformationBuilderFactory.create(Type.class, typeRefBuilder);

		typeRefTransformationBuilder.transform().from("code").to("name");
		
		EntityTypeBuilder entitytypeRefBuilder = subrepository.createBuilder("schema.entitytype.ref");
		final TypeTransformationBuilder<Type, ?> entityTypeRefTransformationBuilder =
				transformationBuilderFactory.create(Type.class, entitytypeRefBuilder);

		entityTypeRefTransformationBuilder.transform().from("code").to("url");
		
		entityTypeRefTransformationBuilder.includeSuper(typeRefTransformationBuilder.getReference());
		entityTypeRefTransformationBuilder.buildTypeTransformation();
		return  typeRefTransformationBuilder.buildTypeTransformation();

	}

	@PostConstruct
	public void init()
	{
		EntityTypeBuilder entityTypeBuilder = subrepository.createBuilder("schema.entitytype");


		TypeTransformationBuilder<Type, ?> typeTransformationBuilder = createTypeTransformationBuilder();

		EntityTypeTransformation typeTransformation = typeTransformationBuilder.getReference();

	
		final TypeTransformationBuilder<EntityType, ?> entityTypeTransformationBuilder =
			transformationBuilderFactory.create(EntityType.class, entityTypeBuilder);
		
		EntityTypeTransformation<Type, ?> typeRefTransformation = createTypeRefTransformation();

		entityTypeTransformationBuilder.transformCollection().from("attributes").to("attributes")
			.convert(getAttributeTransformation(typeRefTransformation));

		entityTypeTransformationBuilder.includeSuper(typeTransformation);

		entityTypeTransformationBuilder.transform().from("code").to("name");
		entityTypeTransformationBuilder.transform().from("code").to("description");

		entityTypeTransformationBuilder.transform().from("superEntityType").to("supertype").convert(typeRefTransformation);
		entityTypeTransformationBuilder.transformCollection().from("subEntityTypes").to("subtypes").convert(typeRefTransformation);


		typeTransformationBuilder.buildTypeTransformation();
		entityTypeTransformation = entityTypeTransformationBuilder.buildTypeTransformation();

	}

	protected TypeTransformationBuilder<Type, ?> createTypeTransformationBuilder() {
		EntityTypeBuilder typeBuilder = subrepository.createBuilder("schema.type");
		TypeTransformationBuilder<Type, ?> typeTransformationBuilder =
			transformationBuilderFactory.create(Type.class, typeBuilder);
		typeTransformationBuilder.transform().from("code").to("type");
		typeTransformationBuilder.transform().from("code").to("typeLabel");
		return typeTransformationBuilder;
	}

	public void setSubrepository(DynamicEntityTypeSubrepository<?> subrepository)
	{
		this.subrepository = subrepository;
	}

	public void setTransformationBuilderFactory(TransformationBuilderFactory transformationBuilderFactory)
	{
		this.transformationBuilderFactory = transformationBuilderFactory;
	}

	@SuppressWarnings("unchecked")
	private void transformValidTypes(TypeTransformationBuilder<Attribute, ?> attributeTransformationBuilder,
		final EntityTypeTransformation<EntityType, ObjectNode> typeTransformation)
	{
		attributeTransformationBuilder
			.transformCustom(GenericTransformationBuilder.class)
			.transform(new JavaTransformation<Attribute<?, ?>, ObjectNode>()
			{

				@Override
				public void mergeAB(Attribute<?, ?> a, ObjectNode b, TransformationContext ctx)
				{
					logger.debug("transforming " + a.getEntityType().getEntityClass().getSimpleName() + "." + a.getCode());

					if (a.getTargetType() instanceof EntityType)
					{

						ValidTypes validTypes = ((JavaMetaData) a).getAnnotation(ValidTypes.class);

						ArrayNode validTypesArray = b.arrayNode();
						if (validTypes != null && validTypes.value().length == 0)
						{
							b.putNull("validTypes");
						}
						else if (validTypes != null && validTypes.value().length > 0)
						{
							for (Class clazz : validTypes.value())
							{
								EntityType type = entityTypeRepository.getEntityType(clazz);
								UniTransformation<EntityType, ObjectNode> ab = typeTransformation.getAB();
								ObjectNode value = ab.convert(type, ctx);
								validTypesArray.add(value);
							}
							b.put("validTypes", validTypesArray);
						}
						else
						{
							for (EntityType entityType : ((EntityType<?>) a.getTargetType()).getSubEntityTypes())
							{
								UniTransformation<EntityType, ObjectNode> ab = typeTransformation.getAB();
								ObjectNode value = ab.convert(entityType, ctx);
								validTypesArray.add(value);
							}
							if (validTypesArray.size() > 0)
							{
								b.put("validTypes", validTypesArray);
							}
						}
					}

				}

				@Override
				public void mergeBA(ObjectNode b, Attribute a, TransformationContext ctx)
				{
				}

			})
			.to()
			.addMultiAssociationAttribute("validTypes", entityTypeRepository.getType("schema"),
				CollectionSortType.ORDERABLE);
	}
}
