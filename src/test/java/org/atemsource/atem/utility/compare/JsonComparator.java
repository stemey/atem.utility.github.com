package org.atemsource.atem.utility.compare;

import java.io.IOException;
import java.util.Set;

import javax.inject.Inject;

import junit.framework.Assert;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.common.EntityOperationReference;
import org.atemsource.atem.utility.transform.api.TransformationBuilderFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = {"classpath:/test/meta/utility/compare/json-compare.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class JsonComparator {
@Inject
private EntityTypeRepository entityTypeRepository;

@Inject
private ObjectMapper objectMapper;

@Inject
private ComparisonBuilderFactory comparisonBuilderFactory;
	@Test
	public void test() throws JsonProcessingException, IOException {
		
				EntityType<Object> arrayType = entityTypeRepository.getEntityType(ArrayNode.class.getName());
		EntityType<Object> jsonType = entityTypeRepository.getEntityType(ObjectNode.class.getName());

		ComparisonBuilder arrayBuilder = comparisonBuilderFactory.create(arrayType);
		ComparisonBuilder builder = comparisonBuilderFactory.create(jsonType);
		
		EntityOperationReference<Comparison> arrayComparisonRef=arrayBuilder.getReference();
		EntityOperationReference<Comparison> nodeComparisonRef= builder.getReference();

		ComparisonAttributeBuilder arrayComparisonBuilder = arrayBuilder.include("children");
		arrayComparisonBuilder.cascadeDynamically(jsonType, nodeComparisonRef);
		arrayComparisonBuilder.cascadeDynamically(arrayType, arrayComparisonRef);
		Comparison arrayComparison = arrayBuilder.create();
		
		ComparisonAttributeBuilder propertiesComparisonBuilder = builder.include("properties");
		propertiesComparisonBuilder.cascadeDynamically(jsonType, nodeComparisonRef);
		propertiesComparisonBuilder.cascadeDynamically(arrayType, arrayComparisonRef);
		Comparison nodeComparison = builder.create();
		
		
		JsonNode node1 = objectMapper.readTree(getClass().getResourceAsStream("/test/meta/utility/compare/json1.js"));
		JsonNode node2 = objectMapper.readTree(getClass().getResourceAsStream("/test/meta/utility/compare/json2.js"));
		
		Set<Difference> differences = nodeComparison.getDifferences(node1, node2);
		for (Difference diff:differences) {
			System.out.println(diff);
		}
		Assert.assertEquals(5,differences.size());
		
	}
}
