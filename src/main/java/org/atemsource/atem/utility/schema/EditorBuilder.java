package org.atemsource.atem.utility.schema;

import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.log4j.Logger;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.CollectionAttribute;
import org.atemsource.atem.api.attribute.CollectionSortType;
import org.atemsource.atem.api.attribute.JavaMetaData;
import org.atemsource.atem.api.attribute.primitive.ChoiceType;
import org.atemsource.atem.api.attribute.primitive.DateType;
import org.atemsource.atem.api.attribute.primitive.IntegerType;
import org.atemsource.atem.api.attribute.primitive.TextType;
import org.atemsource.atem.api.attribute.relation.ListAssociationAttribute;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.impl.json.JsonUtils;
import org.atemsource.atem.spi.DynamicEntityTypeSubrepository;
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


public class EditorBuilder
{
	private static Logger logger = Logger.getLogger(EditorBuilder.class);

	private EntityTypeTransformation<?, ?> basicTypeTransformation;

	@Inject
	private EntityTypeRepository entityTypeRepository;

	private EntityTypeTransformation<EntityType, ?> entityTypeTransformation;

	private EntityTypeTransformation<?, ?> primitiveTypeTransformation;

	private DynamicEntityTypeSubrepository<?> subrepository;

	private TransformationBuilderFactory transformationBuilderFactory;

	private void addArrayTransform(TypeTransformationBuilder<?, ?> transformationBuilder, final String attribute,
		final Class targetType, final Object value)
	{
		GenericTransformationBuilder arrayTransform =
			transformationBuilder.transformCustom(GenericTransformationBuilder.class);
		arrayTransform.to().addSingleAttribute(attribute, targetType);
		arrayTransform.transform(new JavaTransformation<Object, ObjectNode>()
		{

			@Override
			public void mergeAB(Object a, ObjectNode b, TransformationContext ctx)
			{
				b.put(attribute, JsonUtils.convertToJson(value));
			}

			@Override
			public void mergeBA(ObjectNode b, Object a, TransformationContext ctx)
			{
			}

		});
	}

	private void addValuesTransform(TypeTransformationBuilder<?, ?> transformationBuilder, final String attribute,
		final Class targetType)
	{
		GenericTransformationBuilder arrayTransform =
			transformationBuilder.transformCustom(GenericTransformationBuilder.class);
		Type type = getRepository().getType(targetType);
		arrayTransform.to().addMultiAssociationAttribute(attribute, type, CollectionSortType.ORDERABLE);
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
				b.put(attribute, arrayNode);
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
		addArrayTransform(transformationBuilder, "format", String.class, "dd-MM-yyyy");
		transformationBuilder.includeSuper(superTransformation);
		transformationBuilder.buildTypeTransformation();
	}

	private void createEnumTypeTransformation(EntityTypeTransformation<Type, ?> superTransformation)
	{
		EntityTypeBuilder typeBuilder = subrepository.createBuilder("enum-type");
		TypeTransformationBuilder<ChoiceType, ?> transformationBuilder =
			transformationBuilderFactory.create(ChoiceType.class, typeBuilder);
		addValuesTransform(transformationBuilder, "values", String.class);
		addArrayTransform(transformationBuilder, "type", String.class, "text");
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

	private EntityTypeTransformation<Type, ?> createTypeRef()
	{
		// TODO EntityType does not inherit from type. code is defined in Type.
		EntityTypeBuilder basicTypeBuilder = subrepository.createBuilder("type-ref");
		TypeTransformationBuilder<Type, ?> basicTransformationBuilder =
			transformationBuilderFactory.create(Type.class, basicTypeBuilder);
		basicTransformationBuilder.transform().from("code").to("type");
		basicTransformationBuilder.transform().from("code").to("label");
		return basicTransformationBuilder.buildTypeTransformation();
	}

	protected EntityTypeTransformation<?, ?> createTypeTransformation()
	{
		EntityTypeBuilder typeBuilder = subrepository.createBuilder("primitive-type");
		TypeTransformationBuilder<Type, ?> transformationBuilder =
			transformationBuilderFactory.create(Type.class, typeBuilder);
		createTextTypeTransformation((EntityTypeTransformation<Type, ?>) transformationBuilder.getReference());
		createEnumTypeTransformation((EntityTypeTransformation<Type, ?>) transformationBuilder.getReference());
		createDateTypeTransformation((EntityTypeTransformation<Type, ?>) transformationBuilder.getReference());
		createIntegerTypeTransformation((EntityTypeTransformation<Type, ?>) transformationBuilder.getReference());
		// transformationBuilder.transform().from("code").to("id");
		// transformationBuilder.includeSuper(basicTypeTransformation);
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

	private Converter<Object, Object> getAttributeTransformation()
	{
		EntityTypeBuilder typeBuilder = subrepository.createBuilder("attribute");
		TypeTransformationBuilder<Attribute, ?> transformationBuilder =
			transformationBuilderFactory.create(Attribute.class, typeBuilder);
		SingleAttributeTransformationBuilder<Attribute, ?> requiredTransformer = transformationBuilder.transform();
		requiredTransformer.from("required");
		requiredTransformer.to("required");
		// transformationBuilder.transform().from("code").to("label");
		transformationBuilder.transformCustom(Embed.class).from("targetType").transform(getTypeTransformation());

		transformValidTypes(transformationBuilder,
			(EntityTypeTransformation<EntityType, ObjectNode>) basicTypeTransformation);

		transformationBuilder.transform().from("code").to("label");
		transformationBuilder.transform().from("code").to("code");
		transformationBuilder.transform().from("writable").to("writable");
		// transformationBuilder.transform().from("@max-length").to("max-length");
		getSingleAttributeTransformation(transformationBuilder.getReference());
		getListAttributeTransformation(transformationBuilder.getReference());
		return transformationBuilder.buildTypeTransformation();
	}

	private void getListAttributeTransformation(EntityTypeTransformation attributeTransformation)
	{
		EntityTypeBuilder typeBuilder = subrepository.createBuilder("list-attribute");
		TypeTransformationBuilder<CollectionAttribute, ?> transformationBuilder =
			transformationBuilderFactory.create(CollectionAttribute.class, typeBuilder);
		addArrayTransform(transformationBuilder, "array", Boolean.class, true);
		transformationBuilder.includeSuper(attributeTransformation);
		transformationBuilder.buildTypeTransformation();
	}

	private EntityTypeRepository getRepository()
	{
		return entityTypeRepository;
	}

	private void getSingleAttributeTransformation(EntityTypeTransformation attributeTransformation)
	{
		EntityTypeBuilder typeBuilder = subrepository.createBuilder("single-attribute");
		TypeTransformationBuilder<SingleAttribute, ?> transformationBuilder =
			transformationBuilderFactory.create(SingleAttribute.class, typeBuilder);
		transformationBuilder.transformCustom(Embed.class).from("targetType").transform(basicTypeTransformation);
		transformationBuilder.includeSuper(attributeTransformation);
		addArrayTransform(transformationBuilder, "array", Boolean.class, false);
		transformationBuilder.buildTypeTransformation();
	}

	public DynamicEntityTypeSubrepository<?> getSubrepository()
	{
		return subrepository;
	}

	public EntityTypeTransformation<EntityType, ?> getTransformation()
	{
		return entityTypeTransformation;
	}

	public TransformationBuilderFactory getTransformationBuilderFactory()
	{
		return transformationBuilderFactory;
	}

	private EntityTypeTransformation<?, ?> getTypeTransformation()
	{
		return primitiveTypeTransformation;
	}

	@PostConstruct
	public void init()
	{
		EntityTypeBuilder typeBuilder = subrepository.createBuilder("schema");

		EntityTypeTransformation<Type, ?> typeRefTransformation = createTypeRef();

		EntityTypeBuilder basicTypeBuilder = subrepository.createBuilder("basic-type");
		TypeTransformationBuilder<Type, ?> basicTransformationBuilder =
			transformationBuilderFactory.create(Type.class, basicTypeBuilder);
		basicTransformationBuilder.transform().from("code").to("type");
		basicTransformationBuilder.transform().from("code").to("typeLabel");

		basicTypeTransformation = basicTransformationBuilder.getReference();

		primitiveTypeTransformation = createTypeTransformation();

		final TypeTransformationBuilder<EntityType, ?> transformationBuilder =
			transformationBuilderFactory.create(EntityType.class, typeBuilder);

		transformationBuilder.transformCollection().from("attributes").to("attributes")
			.convert(getAttributeTransformation());
		// TODO add filter for writable attributes

		basicTransformationBuilder.buildTypeTransformation();
		transformationBuilder.includeSuper(basicTypeTransformation);

		// transformationBuilder.transformCollection().from("subEntityTypes").to("validTypes")
		// .convert(transformationBuilder.getReference());

		transformationBuilder.transformCustom(GenericTransformationBuilder.class)
			.transform(new JavaTransformation<EntityType, ObjectNode>()
			{

				@Override
				public void mergeAB(EntityType a, ObjectNode b, TransformationContext ctx)
				{
					b.put("type_property", "ext_type");
				}

				@Override
				public void mergeBA(ObjectNode b, EntityType a, TransformationContext ctx)
				{
				}

			}).to().addSingleAttribute("type-property", entityTypeRepository.getType(String.class));

		entityTypeTransformation = transformationBuilder.buildTypeTransformation();

	}

	public void setSubrepository(DynamicEntityTypeSubrepository<?> subrepository)
	{
		this.subrepository = subrepository;
	}

	public void setTransformationBuilderFactory(TransformationBuilderFactory transformationBuilderFactory)
	{
		this.transformationBuilderFactory = transformationBuilderFactory;
	}

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
