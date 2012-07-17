package org.atemsource.atem.utility.jackson;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.common.EntityOperationReference;
import org.atemsource.atem.utility.compare.Comparison;
import org.atemsource.atem.utility.compare.ComparisonAttributeBuilder;
import org.atemsource.atem.utility.compare.ComparisonBuilder;
import org.atemsource.atem.utility.compare.ComparisonBuilderFactory;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;


public class JsonComparator
{

	private Comparison arrayComparison;

	private Comparison comparison;

	private ComparisonBuilderFactory comparisonBuilderFactory;

	@Inject
	private EntityTypeRepository entityTypeRepository;

	public Comparison getArrayComparison()
	{
		return arrayComparison;
	}

	public Comparison getNodeComparison()
	{
		return comparison;
	}

	@PostConstruct
	public void initialize()
	{
		EntityType<Object> arrayType = entityTypeRepository.getEntityType(ArrayNode.class.getName());
		EntityType<Object> jsonType = entityTypeRepository.getEntityType(ObjectNode.class.getName());

		ComparisonBuilder arrayBuilder = comparisonBuilderFactory.create(arrayType);
		ComparisonBuilder builder = comparisonBuilderFactory.create(jsonType);

		EntityOperationReference<Comparison> arrayComparisonRef = arrayBuilder.getReference();
		EntityOperationReference<Comparison> nodeComparisonRef = builder.getReference();

		ComparisonAttributeBuilder arrayComparisonBuilder = arrayBuilder.include("children");
		arrayComparisonBuilder.cascadeDynamically(jsonType, nodeComparisonRef);
		arrayComparisonBuilder.cascadeDynamically(arrayType, arrayComparisonRef);
		arrayComparison = arrayBuilder.create();

		ComparisonAttributeBuilder propertiesComparisonBuilder = builder.include("properties");
		propertiesComparisonBuilder.cascadeDynamically(jsonType, nodeComparisonRef);
		propertiesComparisonBuilder.cascadeDynamically(arrayType, arrayComparisonRef);
		comparison = builder.create();

	}

	public void setArrayComparison(Comparison arrayComparison)
	{
		this.arrayComparison = arrayComparison;
	}

	public void setComparisonBuilderFactory(ComparisonBuilderFactory comparisonBuilderFactory)
	{
		this.comparisonBuilderFactory = comparisonBuilderFactory;
	}
}
