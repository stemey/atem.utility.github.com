package org.atemsource.atem.utility.schema;

import javax.inject.Inject;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.domain.SimpleEntity;
import org.atemsource.atem.utility.domain.VerySimpleEntity;
import org.atemsource.atem.utility.transform.api.SimpleTransformationContext;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@ContextConfiguration(locations = {"classpath:/test/meta/utility/schema.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class SchemaBuilderTest
{

	@Inject
	private EntityTypeRepository entityTypeRepository;

	@Inject
	private SchemaBuilder schemaBuilder;

	@Test
	public void testSimpleEntity()
	{
		EntityType<SimpleEntity> entityType = entityTypeRepository.getEntityType(SimpleEntity.class);
		ObjectNode schema =
			(ObjectNode) schemaBuilder.getTransformation().getAB()
				.convert(entityType, new SimpleTransformationContext(entityTypeRepository));
		System.out.println(schema.toString());

	}

	@Test
	public void testVerySimpleEntity()
	{
		EntityType<VerySimpleEntity> entityType = entityTypeRepository.getEntityType(VerySimpleEntity.class);
		ObjectNode schema =
			(ObjectNode) schemaBuilder.getTransformation().getAB()
				.convert(entityType, new SimpleTransformationContext(entityTypeRepository));
		System.out.println(schema.toString());

	}

}
