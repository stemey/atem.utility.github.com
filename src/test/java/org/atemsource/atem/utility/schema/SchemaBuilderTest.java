package org.atemsource.atem.utility.schema;

import javax.inject.Inject;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.domain.SimpleEntity;
import org.atemsource.atem.utility.transform.api.SimpleTransformationContext;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = { "classpath:/test/meta/utility/schema.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class SchemaBuilderTest {

	@Inject
	private EntityTypeRepository entityTypeRepository;
	
	@Inject
	private SchemaBuilder schemaBuilder;
	
	@Test
	public void test() {
		EntityType<SimpleEntity> entityType = entityTypeRepository.getEntityType(SimpleEntity.class);
		EntityTypeTransformation<EntityType, ?> transformation = schemaBuilder.init();
		ObjectNode schema = (ObjectNode) transformation.getAB().convert(entityType,new SimpleTransformationContext());
	    System.out.println(schema.toString());

	}

}
