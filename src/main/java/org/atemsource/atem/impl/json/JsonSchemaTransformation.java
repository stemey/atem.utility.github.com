package org.atemsource.atem.impl.json;

import javax.annotation.Resource;

import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.impl.json.schema.JsonSchema;
import org.atemsource.atem.spi.DynamicEntityTypeSubrepository;
import org.atemsource.atem.utility.transform.api.AttributeTransformationBuilder;
import org.atemsource.atem.utility.transform.api.Transformation;
import org.atemsource.atem.utility.transform.api.TransformationBuilderFactory;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.builder.CollectionAttributeTransformationBuilder;
import org.codehaus.jackson.node.ObjectNode;


public class JsonSchemaTransformation
{

	@Resource(name = "atem-json-repository")
	private DynamicEntityTypeSubrepository<ObjectNode> jsonRepository;

	@Resource
	private TransformationBuilderFactory transformationBuilderFactory;

	public Transformation<JsonSchema, ObjectNode> createTransformation()
	{
		EntityTypeBuilder jsonSchemaBuilder = jsonRepository.createBuilder("json:" + JsonSchema.class.getName());
		TypeTransformationBuilder<JsonSchema, ?> builder =
			transformationBuilderFactory.create(JsonSchema.class, jsonSchemaBuilder);
		builder.transformPrimitives();
		// builder.putDefaultConverter(new JsonSchemaUriConverter());
		AttributeTransformationBuilder<JsonSchema, ?> superType = builder.transform();
		superType.fromMethod().getSuperType();
		superType.to("extends");
		CollectionAttributeTransformationBuilder<JsonSchema, ?> properties = builder.transformCollection();
		properties.fromMethod().getProperties();
		// properties.convert(converter);
		return null;

	}
}