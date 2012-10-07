package org.atemsource.atem.utility.validation;

import java.io.IOException;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.inject.Inject;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.impl.json.JsonEntityTypeRepository;
import org.codehaus.jackson.JsonParser.Feature;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = {"classpath:/test/meta/utility/json-validation.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class AtemTypeAndConstraintValidationServiceTest {

	@Resource(name="atem-validation-json-repository")
	private JsonEntityTypeRepository repository;

	private static ObjectMapper mapper;

	@BeforeClass
	public static void setup() {
		mapper=new ObjectMapper();
		mapper.getJsonFactory().enable(Feature.ALLOW_UNQUOTED_FIELD_NAMES);
		
	}
	
	@Test
	public void testTypeMismatch() throws JsonProcessingException, IOException {
		
		EntityTypeBuilder builder = repository.createBuilder("test");
		builder.addSingleAttribute("textProperty", String.class);
		EntityType<ObjectNode> type = (EntityType<ObjectNode>) builder.createEntityType();
		
		ObjectNode node= (ObjectNode) mapper.readTree("{textProperty:12}");
		ValidationService validationService = type.getService(ValidationService.class);
		SimpleValidationContext context= new SimpleValidationContext();
		validationService.validate(type, context, node);
		Assert.assertEquals(1,context.getErrors().size());
		
	}
	
	@Test
	public void testWrongExtType() throws JsonProcessingException, IOException {
		
		EntityTypeBuilder builder = repository.createBuilder("test");
		builder.addSingleAttribute("textProperty", String.class);
		EntityType<ObjectNode> type = (EntityType<ObjectNode>) builder.createEntityType();
		
		ObjectNode node= (ObjectNode) mapper.readTree("{textProperty:12}");
		ValidationService validationService = type.getService(ValidationService.class);
		SimpleValidationContext context= new SimpleValidationContext();
		validationService.validate(type, context, node);
		Assert.assertEquals(1,context.getErrors().size());
		
	}

}
