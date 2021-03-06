package org.atemsource.atem.utility.schema;

import javax.annotation.PostConstruct;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.CollectionAttribute;
import org.atemsource.atem.api.attribute.relation.ListAssociationAttribute;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.api.type.primitive.DateType;
import org.atemsource.atem.api.type.primitive.IntegerType;
import org.atemsource.atem.api.type.primitive.TextType;
import org.atemsource.atem.spi.DynamicEntityTypeSubrepository;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.JavaConverter;
import org.atemsource.atem.utility.transform.api.JavaTransformation;
import org.atemsource.atem.utility.transform.api.TransformationBuilderFactory;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.transform.impl.builder.CollectionToMap;
import org.atemsource.atem.utility.transform.impl.builder.Embed;
import org.atemsource.atem.utility.transform.impl.builder.GenericTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.builder.OneToOneAttributeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.builder.SingleAttributeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.converter.ConverterUtils;
import org.codehaus.jackson.node.ObjectNode;


public class SchemaBuilder
{
	private EntityTypeTransformation<?, ?> primitiveTypeTransformation;

	private DynamicEntityTypeSubrepository<?> subrepository;

	private EntityTypeTransformation<EntityType, ?> transformation;

	private TransformationBuilderFactory transformationBuilderFactory;

	private void createDateTypeTransformation(EntityTypeTransformation<Type, ?> superTransformation)
	{
		EntityTypeBuilder typeBuilder = subrepository.createBuilder("date-type");
		TypeTransformationBuilder<DateType, ?> transformationBuilder =
			transformationBuilderFactory.create(DateType.class, typeBuilder);
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
		transformationBuilder.includeSuper(superTransformation);
		transformationBuilder.buildTypeTransformation();
	}

	protected EntityTypeTransformation<?, ?> createTypeTransformation(EntityTypeTransformation basicTransformation)
	{
		EntityTypeBuilder typeBuilder = subrepository.createBuilder("primitive-type");
		TypeTransformationBuilder<Type, ?> transformationBuilder =
			transformationBuilderFactory.create(Type.class, typeBuilder);
		createTextTypeTransformation((EntityTypeTransformation<Type, ?>) transformationBuilder.getReference());
		createIntegerTypeTransformation((EntityTypeTransformation<Type, ?>) transformationBuilder.getReference());
		transformationBuilder.transform().from("code").to("id");
		transformationBuilder.includeSuper(basicTransformation);
		return transformationBuilder.buildTypeTransformation();
	}

	private Converter<Attribute<?, ?>, String> getAttributeKeyConverter()
	{
		JavaConverter<Attribute<?, ?>, String> javaConverter = new JavaConverter<Attribute<?, ?>, String>()
		{

			@Override
			public String convertAB(Attribute a, TransformationContext ctx)
			{
				return a.getCode();
			}

			@Override
			public Attribute convertBA(String b, TransformationContext ctx)
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
		SingleAttributeTransformationBuilder requiredTransformer = (SingleAttributeTransformationBuilder) transformationBuilder.transform();
		requiredTransformer.from("required");
		requiredTransformer.to("required");
		transformationBuilder.transformCustom(Embed.class).from("targetType").transform(getTypeTransformation());
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
		GenericTransformationBuilder arrayAttributeBuilder =
			transformationBuilder.transformCustom(GenericTransformationBuilder.class);
		arrayAttributeBuilder.to().addSingleAttribute("array", String.class);
		arrayAttributeBuilder.transform(new JavaTransformation<Object, ObjectNode>()
		{

			@Override
			public void mergeAB(Object a, ObjectNode b, TransformationContext ctx)
			{
				b.put("array", true);
			}

			@Override
			public void mergeBA(ObjectNode b, Object a, TransformationContext ctx)
			{
			}

		});
		transformationBuilder.includeSuper(attributeTransformation);
		transformationBuilder.buildTypeTransformation();
	}

	private void getSingleAttributeTransformation(EntityTypeTransformation attributeTransformation)
	{
		EntityTypeBuilder typeBuilder = subrepository.createBuilder("single-attribute");
		TypeTransformationBuilder<SingleAttribute, ?> transformationBuilder =
			transformationBuilderFactory.create(SingleAttribute.class, typeBuilder);
		transformationBuilder.transformCustom(Embed.class).from("targetType").transform(getTypeTransformation());
		transformationBuilder.includeSuper(attributeTransformation);
		transformationBuilder.buildTypeTransformation();
	}

	public DynamicEntityTypeSubrepository<?> getSubrepository()
	{
		return subrepository;
	}

	public EntityTypeTransformation<EntityType, ?> getTransformation()
	{
		return transformation;
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

		EntityTypeBuilder basicTypeBuilder = subrepository.createBuilder("basic-type");
		TypeTransformationBuilder<Type, ?> basicTransformationBuilder =
			transformationBuilderFactory.create(Type.class, basicTypeBuilder);
		basicTransformationBuilder.transform().from("code").to("id");
		primitiveTypeTransformation = createTypeTransformation(basicTransformationBuilder.getReference());

		TypeTransformationBuilder<EntityType, ?> transformationBuilder =
			transformationBuilderFactory.create(EntityType.class, typeBuilder);
		transformationBuilder.transformCustom(CollectionToMap.class).from("attributes").to("attributes")
			.keyConvert(getAttributeKeyConverter()).convert(getAttributeTransformation());
		// we cannot do this because the supertype of EntityType is not Type.
		// transformationBuilder.transform().from("code").to("id");
		transformationBuilder.includeSuper(basicTransformationBuilder.buildTypeTransformation());
		transformation = transformationBuilder.buildTypeTransformation();

	}

	public void setSubrepository(DynamicEntityTypeSubrepository<?> subrepository)
	{
		this.subrepository = subrepository;
	}

	public void setTransformationBuilderFactory(TransformationBuilderFactory transformationBuilderFactory)
	{
		this.transformationBuilderFactory = transformationBuilderFactory;
	}
}
