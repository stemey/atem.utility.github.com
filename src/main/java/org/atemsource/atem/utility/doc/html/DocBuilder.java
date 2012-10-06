package org.atemsource.atem.utility.doc.html;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.log4j.Logger;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.CollectionAttribute;
import org.atemsource.atem.api.attribute.CollectionSortType;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.api.type.PrimitiveType;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.api.type.primitive.ChoiceType;
import org.atemsource.atem.api.type.primitive.DateType;
import org.atemsource.atem.api.type.primitive.IntegerType;
import org.atemsource.atem.api.type.primitive.TextType;
import org.atemsource.atem.impl.dynamic.DynamicEntity;
import org.atemsource.atem.spi.DynamicEntityTypeSubrepository;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.JavaTransformation;
import org.atemsource.atem.utility.transform.api.JavaUniConverter;
import org.atemsource.atem.utility.transform.api.TransformationBuilderFactory;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.api.constraint.DateFormat;
import org.atemsource.atem.utility.transform.api.constraint.PossibleValues;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.transform.impl.builder.GenericTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.builder.OneToOneAttributeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.builder.SingleAttributeTransformationBuilder;


public class DocBuilder
{
	private static Logger logger = Logger.getLogger(DocBuilder.class);

	@Inject
	private EntityTypeRepository entityTypeRepository;

	private EntityTypeTransformation<EntityType, ?> entityTypeTransformation;

	private DynamicEntityTypeSubrepository<?> subrepository;

	private TransformationBuilderFactory transformationBuilderFactory;

	private TypeCodeToUrlConverter typeCodeToUrlConverter;

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
		arrayTransform.transform(new JavaTransformation<Object, DynamicEntity>()
		{

			@Override
			public void mergeAB(Object a, DynamicEntity b, TransformationContext ctx)
			{
				ChoiceType<?> choiceType = (ChoiceType<?>) a;
				List<String> arrayNode = new ArrayList<String>();
				for (Map.Entry<String, ?> entry : choiceType.getOptionsMap().entrySet())
				{
					arrayNode.add(String.valueOf(entry.getValue()));
				}
				b.put("values", arrayNode);
			}

			@Override
			public void mergeBA(DynamicEntity b, Object a, TransformationContext ctx)
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

	protected EntityTypeTransformation<?, ?> createPrimitiveTypeRefTransformation(
		EntityTypeTransformation<?, ?> typeRefTransformation)
	{
		EntityTypeBuilder typeBuilder = subrepository.createBuilder("schema.primitivetype");
		TypeTransformationBuilder<PrimitiveType, ?> transformationBuilder =
			transformationBuilderFactory.create(PrimitiveType.class, typeBuilder);
		EntityTypeTransformation<Type, ?> primitiveTypeRefTransformation =
			(EntityTypeTransformation<Type, ?>) transformationBuilder.getReference();
		createTextTypeTransformation(primitiveTypeRefTransformation);
		createEnumTypeTransformation(primitiveTypeRefTransformation);
		createDateTypeTransformation(primitiveTypeRefTransformation);
		createIntegerTypeTransformation(primitiveTypeRefTransformation);
		// createLongTypeTransformation(primitiveTypeRefTransformation);
		transformationBuilder.includeSuper(typeRefTransformation);

		return transformationBuilder.buildTypeTransformation();
	}

	private void createTextTypeTransformation(EntityTypeTransformation<Type, ?> superTransformation)
	{
		EntityTypeBuilder typeBuilder = subrepository.createBuilder("text-type");
		TypeTransformationBuilder<TextType, ?> transformationBuilder =
			transformationBuilderFactory.create(TextType.class, typeBuilder);
		transformationBuilder.includeSuper(superTransformation);
		transformationBuilder.buildTypeTransformation();
	}

	public EntityTypeTransformation<Type, ?> createTypeRefTransformation()
	{
		EntityTypeBuilder typeRefBuilder = subrepository.createBuilder("schema.type.ref");

		final TypeTransformationBuilder<Type, ?> typeRefTransformationBuilder =
			transformationBuilderFactory.create(Type.class, typeRefBuilder);

		typeRefTransformationBuilder.transform().from("code").to("name");

		EntityTypeBuilder entitytypeRefBuilder = subrepository.createBuilder("schema.entitytype.ref");
		final TypeTransformationBuilder<EntityType, ?> entityTypeRefTransformationBuilder =
			transformationBuilderFactory.create(EntityType.class, entitytypeRefBuilder);

		entityTypeRefTransformationBuilder.transform().from("code").to("url")
			.convert(new JavaUniConverter<String, String>()
			{

				@Override
				public String convert(String a)
				{
					return getUrlForEntityType(a);
				}

			});

		entityTypeRefTransformationBuilder.includeSuper(typeRefTransformationBuilder.getReference());
		entityTypeRefTransformationBuilder.buildTypeTransformation();
		createPrimitiveTypeRefTransformation(typeRefTransformationBuilder.getReference());
		return typeRefTransformationBuilder.buildTypeTransformation();

	}

	protected TypeTransformationBuilder<Type, ?> createTypeTransformationBuilder()
	{
		EntityTypeBuilder typeBuilder = subrepository.createBuilder("schema.type");
		TypeTransformationBuilder<Type, ?> typeTransformationBuilder =
			transformationBuilderFactory.create(Type.class, typeBuilder);
		typeTransformationBuilder.transform().from("code").to("type");
		typeTransformationBuilder.transform().from("code").to("typeLabel");
		return typeTransformationBuilder;
	}

	private Converter<Object, Object> getAttributeTransformation(EntityTypeTransformation typeRefTransformation)
	{
		EntityTypeBuilder typeBuilder = subrepository.createBuilder("attribute");
		TypeTransformationBuilder<Attribute, ?> transformationBuilder =
			transformationBuilderFactory.create(Attribute.class, typeBuilder);
		OneToOneAttributeTransformationBuilder requiredTransformer = transformationBuilder.transform();
		requiredTransformer.from("required");
		requiredTransformer.to("required");
		// transformationBuilder.transform().from("code").to("label");
		// this should be genral type transformation without attributes : schema.typeref
		transformationBuilder.transform().from("targetType").to("targetType").convert(typeRefTransformation);

		transformationBuilder.transform().from("code").to("name");
		transformationBuilder.transform().from("code").to("description");
		transformationBuilder.transformCollection().to("values")
			.from("@" + PossibleValues.META_ATTRIBUTE_CODE + ".values");
		transformationBuilder.transform().to("dateformat").from("@" + DateFormat.META_ATTRIBUTE_CODE + ".pattern");

		addConstantTransform(transformationBuilder, "template", String.class, "attribute.vm");

		getSingleAttributeTransformation(transformationBuilder.getReference(), typeRefTransformation);
		getListAttributeTransformation(transformationBuilder.getReference(), typeRefTransformation);
		return transformationBuilder.buildTypeTransformation();
	}

	public EntityTypeTransformation<EntityType, ?> getEntityTypeTransformation()
	{
		return entityTypeTransformation;
	}

	private void getListAttributeTransformation(EntityTypeTransformation attributeTransformation,
		EntityTypeTransformation typeRefTransformation)
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

	private void getSingleAttributeTransformation(EntityTypeTransformation attributeTransformation,
		EntityTypeTransformation typeRefTransformation)
	{
		EntityTypeBuilder typeBuilder = subrepository.createBuilder("single-attribute");
		TypeTransformationBuilder<SingleAttribute, ?> transformationBuilder =
			transformationBuilderFactory.create(SingleAttribute.class, typeBuilder);
		transformationBuilder.includeSuper(attributeTransformation);
		addConstantTransform(transformationBuilder, "array", Boolean.class, false);
		transformationBuilder.buildTypeTransformation();
	}

	public TransformationBuilderFactory getTransformationBuilderFactory()
	{
		return transformationBuilderFactory;
	}

	public TypeCodeToUrlConverter getTypeCodeToUrlConverter()
	{
		return typeCodeToUrlConverter;
	}

	protected String getUrlForEntityType(String typeCode)
	{
		return typeCodeToUrlConverter.getUrl(typeCode);
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

		entityTypeTransformationBuilder.transform().from("superEntityType").to("supertype")
			.convert(typeRefTransformation);
		entityTypeTransformationBuilder.transformCollection().from("subEntityTypes").to("subtypes")
			.convert(typeRefTransformation);

		typeTransformationBuilder.buildTypeTransformation();
		entityTypeTransformation = entityTypeTransformationBuilder.buildTypeTransformation();

	}

	public void setSubrepository(DynamicEntityTypeSubrepository<?> subrepository)
	{
		this.subrepository = subrepository;
	}

	public void setTransformationBuilderFactory(TransformationBuilderFactory transformationBuilderFactory)
	{
		this.transformationBuilderFactory = transformationBuilderFactory;
	}

	public void setTypeCodeToUrlConverter(TypeCodeToUrlConverter typeCodeToUrlConverter)
	{
		this.typeCodeToUrlConverter = typeCodeToUrlConverter;
	}

}
