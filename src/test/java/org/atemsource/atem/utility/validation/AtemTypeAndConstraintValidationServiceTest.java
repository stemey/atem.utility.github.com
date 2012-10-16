package org.atemsource.atem.utility.validation;

import java.io.IOException;

import javax.annotation.Resource;

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
		mapper.getJsonFactory().enable(Feature.ALLOW_SINGLE_QUOTES);
		
	}
	
	@Test
	public void testTypeMismatch() throws JsonProcessingException, IOException {
		
		EntityTypeBuilder builder = repository.createBuilder("test1");
		builder.addSingleAttribute("textProperty", String.class);
		EntityType<ObjectNode> type = (EntityType<ObjectNode>) builder.createEntityType();
		
		ObjectNode node= (ObjectNode) mapper.readTree("{ext_type:'test1',textProperty:12}");
		ValidationService validationService = type.getService(ValidationService.class);
		SimpleValidationContext context= new SimpleValidationContext();
		validationService.validate(type, context, node);
		Assert.assertEquals(1,context.getErrors().size());
		
	}
	
	@Test
	public void testSubtypeMatch() throws JsonProcessingException, IOException {
		
		EntityTypeBuilder superbuilder = repository.createBuilder("supertest3");
		EntityType<ObjectNode> supertype = (EntityType<ObjectNode>) superbuilder.createEntityType();
		
		EntityTypeBuilder builder = repository.createBuilder("test3");
		builder.addSingleAttribute("textProperty", String.class);
		builder.superType(supertype);
		EntityType<ObjectNode> type = (EntityType<ObjectNode>) builder.createEntityType();
		
		ObjectNode node= (ObjectNode) mapper.readTree("{ext_type:'test3',textProperty:'12'}");
		ValidationService validationService = type.getService(ValidationService.class);
		SimpleValidationContext context= new SimpleValidationContext();
		validationService.validate(supertype, context, node);
		Assert.assertEquals(0,context.getErrors().size());
		
	}
	
	@Test
	public void testAbstractSupertype() throws JsonProcessingException, IOException {
		
		EntityTypeBuilder superbuilder = repository.createBuilder("supertest5");
		superbuilder.setAbstract(true);
		EntityType<ObjectNode> supertype = (EntityType<ObjectNode>) superbuilder.createEntityType();
		
		EntityTypeBuilder builder = repository.createBuilder("test5");
		builder.addSingleAttribute("textProperty", String.class);
		builder.superType(supertype);
		EntityType<ObjectNode> type = (EntityType<ObjectNode>) builder.createEntityType();
		
		ObjectNode node= (ObjectNode) mapper.readTree("{ext_type:'supertest4',textProperty:'12'}");
		ValidationService validationService = type.getService(ValidationService.class);
		SimpleValidationContext context= new SimpleValidationContext();
		validationService.validate(supertype, context, node);
		Assert.assertEquals(1,context.getErrors().size());
		
	}
	
	@Test
	public void testAbstractSupertypeAssocaition() throws JsonProcessingException, IOException {
	
	EntityTypeBuilder subBuilder = repository.createBuilder("sub7");
	subBuilder.setAbstract(true);
	EntityType<?> subType = subBuilder.createEntityType();
	
	EntityTypeBuilder builder = repository.createBuilder("test7");
	builder.addSingleAttribute("property", subType);
	EntityType<ObjectNode> type = (EntityType<ObjectNode>) builder.createEntityType();
	
	ObjectNode node= (ObjectNode) mapper.readTree("{property:{ext_type:'sub7',textProperty:'12'}}");
	ValidationService validationService = type.getService(ValidationService.class);
	SimpleValidationContext context= new SimpleValidationContext();
	validationService.validate(type, context, node);
	Assert.assertEquals(1,context.getErrors().size());
	}
	
	@Test
	public void testIgnoreSubType() throws JsonProcessingException, IOException {
		
		EntityTypeBuilder superbuilder = repository.createBuilder("supertest4");
		EntityType<ObjectNode> supertype = (EntityType<ObjectNode>) superbuilder.createEntityType();
		
		EntityTypeBuilder builder = repository.createBuilder("test4");
		builder.addSingleAttribute("textProperty", String.class);
		builder.superType(supertype);
		EntityType<ObjectNode> type = (EntityType<ObjectNode>) builder.createEntityType();
		
		ObjectNode node= (ObjectNode) mapper.readTree("{ext_type:'supertest3',textProperty:12}");
		ValidationService validationService = type.getService(ValidationService.class);
		SimpleValidationContext context= new SimpleValidationContext();
		validationService.validate(supertype, context, node);
		Assert.assertEquals(0,context.getErrors().size());
		
	}
	

	@Test
	public void testNegative() throws JsonProcessingException, IOException {
		
		EntityTypeBuilder subBuilder = repository.createBuilder("sub1");
		subBuilder.addSingleAttribute("textProperty", String.class);
		EntityType<?> subType = subBuilder.createEntityType();
		
		EntityTypeBuilder builder = repository.createBuilder("test12");
		builder.addSingleAttribute("property", subType);
		EntityType<ObjectNode> type = (EntityType<ObjectNode>) builder.createEntityType();
		
		ObjectNode node= (ObjectNode) mapper.readTree("{property:{ext_type:'subrr',textProperty:'12'}}");
		ValidationService validationService = type.getService(ValidationService.class);
		SimpleValidationContext context= new SimpleValidationContext();
		validationService.validate(type, context, node);
		Assert.assertEquals(1,context.getErrors().size());
		
	}

}
