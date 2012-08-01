package org.atemsource.atem.utility.compare;

import java.io.IOException;
import java.util.Set;

import javax.inject.Inject;

import junit.framework.Assert;

import org.atemsource.atem.api.EntityTypeRepository;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@ContextConfiguration(locations = {"classpath:/test/meta/utility/compare/json-compare.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class JsonComparatorTest
{
	@Inject
	private EntityTypeRepository entityTypeRepository;

	@Inject
	private JsonComparator jsonComparator;

	@Inject
	private ObjectMapper objectMapper;

	@Test
	public void test() throws JsonProcessingException, IOException
	{

		Comparison nodeComparison = jsonComparator.getNodeComparison();

		JsonNode node1 = objectMapper.readTree(getClass().getResourceAsStream("/test/meta/utility/compare/json1.js"));
		JsonNode node2 = objectMapper.readTree(getClass().getResourceAsStream("/test/meta/utility/compare/json2.js"));

		Set<Difference> differences = nodeComparison.getDifferences(node1, node2);
		for (Difference diff : differences)
		{
			System.out.println(diff);
		}
		Assert.assertEquals(5, differences.size());

	}
}
