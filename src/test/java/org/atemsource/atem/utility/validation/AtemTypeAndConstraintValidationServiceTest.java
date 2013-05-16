package org.atemsource.atem.utility.validation;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Inject;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.impl.common.attribute.SingleAttributeImpl;
import org.atemsource.atem.impl.json.JsonEntityTypeImpl;
import org.atemsource.atem.impl.json.JsonEntityTypeRepository;
import org.atemsource.atem.impl.pojo.attribute.AtemAnnotationTargetClassResolver;
import org.atemsource.atem.utility.transform.api.constraint.Constraint;
import org.atemsource.atem.utility.transform.api.constraint.DateFormat;
import org.codehaus.jackson.JsonParser.Feature;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.hibernate.validator.constraints.br.CNPJ;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@ContextConfiguration(locations = {"classpath:/test/meta/utility/json-validation.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class AtemTypeAndConstraintValidationServiceTest
{

	private static ObjectMapper mapper;

	@Inject
	private EntityTypeRepository entityTypeRepository;

	@Resource(name = "atem-validation-json-repository")
	private JsonEntityTypeRepository repository;

	@BeforeClass
	public static void setup()
	{
		mapper = new ObjectMapper();
		mapper.getJsonFactory().enable(Feature.ALLOW_UNQUOTED_FIELD_NAMES);
		mapper.getJsonFactory().enable(Feature.ALLOW_SINGLE_QUOTES);

	}

	@Test
	public void testAbstractSupertype() throws JsonProcessingException, IOException
	{

		EntityTypeBuilder superbuilder = repository.createBuilder("supertest5");
		superbuilder.setAbstract(true);
		EntityType<ObjectNode> supertype = (EntityType<ObjectNode>) superbuilder.createEntityType();

		EntityTypeBuilder builder = repository.createBuilder("test5");
		builder.addSingleAttribute("textProperty", String.class);
		builder.superType(supertype);
		EntityType<ObjectNode> type = (EntityType<ObjectNode>) builder.createEntityType();

		ObjectNode node = (ObjectNode) mapper.readTree("{ext_type:'supertest4',textProperty:'12'}");
		ValidationService validationService = type.getService(ValidationService.class);
		SimpleValidationContext context = new SimpleValidationContext(entityTypeRepository);
		validationService.validate(supertype, context, node);
		Assert.assertEquals(1, context.getErrors().size());

	}

	@Test
	public void testAbstractSupertypeAssocaition() throws JsonProcessingException, IOException
	{

		EntityTypeBuilder subBuilder = repository.createBuilder("sub7");
		subBuilder.setAbstract(true);
		EntityType<?> subType = subBuilder.createEntityType();

		EntityTypeBuilder builder = repository.createBuilder("test7");
		builder.addSingleAttribute("property", subType);
		EntityType<ObjectNode> type = (EntityType<ObjectNode>) builder.createEntityType();

		ObjectNode node = (ObjectNode) mapper.readTree("{property:{ext_type:'sub7',textProperty:'12'}}");
		ValidationService validationService = type.getService(ValidationService.class);
		SimpleValidationContext context = new SimpleValidationContext(entityTypeRepository);
		validationService.validate(type, context, node);
		Assert.assertEquals(1, context.getErrors().size());
	}

	@Test
	public void testIgnoreSubType() throws JsonProcessingException, IOException
	{

		EntityTypeBuilder superbuilder = repository.createBuilder("supertest4");
		EntityType<ObjectNode> supertype = (EntityType<ObjectNode>) superbuilder.createEntityType();

		EntityTypeBuilder builder = repository.createBuilder("test4");
		builder.addSingleAttribute("textProperty", String.class);
		builder.superType(supertype);
		EntityType<ObjectNode> type = (EntityType<ObjectNode>) builder.createEntityType();

		ObjectNode node = (ObjectNode) mapper.readTree("{ext_type:'supertest3',textProperty:12}");
		ValidationService validationService = type.getService(ValidationService.class);
		SimpleValidationContext context = new SimpleValidationContext(entityTypeRepository);
		validationService.validate(supertype, context, node);
		Assert.assertEquals(0, context.getErrors().size());

	}

	@Test
	public void testNegative() throws JsonProcessingException, IOException
	{

		EntityTypeBuilder subBuilder = repository.createBuilder("sub1");
		subBuilder.addSingleAttribute("textProperty", String.class);
		EntityType<?> subType = subBuilder.createEntityType();

		EntityTypeBuilder builder = repository.createBuilder("test12");
		builder.addSingleAttribute("property", subType);
		EntityType<ObjectNode> type = (EntityType<ObjectNode>) builder.createEntityType();

		ObjectNode node = (ObjectNode) mapper.readTree("{property:{ext_type:'subrr',textProperty:'12'}}");
		ValidationService validationService = type.getService(ValidationService.class);
		SimpleValidationContext context = new SimpleValidationContext(entityTypeRepository);
		validationService.validate(type, context, node);
		Assert.assertEquals(1, context.getErrors().size());

	}

	@Test
	public void testSubtypeMatch() throws JsonProcessingException, IOException
	{

		EntityTypeBuilder superbuilder = repository.createBuilder("supertest3");
		EntityType<ObjectNode> supertype = (EntityType<ObjectNode>) superbuilder.createEntityType();

		EntityTypeBuilder builder = repository.createBuilder("test3");
		builder.addSingleAttribute("textProperty", String.class);
		builder.superType(supertype);
		EntityType<ObjectNode> type = (EntityType<ObjectNode>) builder.createEntityType();

		ObjectNode node = (ObjectNode) mapper.readTree("{ext_type:'test3',textProperty:'12'}");
		ValidationService validationService = type.getService(ValidationService.class);
		SimpleValidationContext context = new SimpleValidationContext(entityTypeRepository);
		validationService.validate(supertype, context, node);
		Assert.assertEquals(0, context.getErrors().size());

	}

	@Test
	public void testTypeMismatch() throws JsonProcessingException, IOException
	{

		EntityTypeBuilder builder = repository.createBuilder("test1");
		builder.addSingleAttribute("textProperty", String.class);
		EntityType<ObjectNode> type = (EntityType<ObjectNode>) builder.createEntityType();

		ObjectNode node = (ObjectNode) mapper.readTree("{ext_type:'test1',textProperty:12}");
		ValidationService validationService = type.getService(ValidationService.class);
		SimpleValidationContext context = new SimpleValidationContext(entityTypeRepository);
		validationService.validate(type, context, node);
		Assert.assertEquals(1, context.getErrors().size());

	}

	private SimpleValidationContext testTypeFormat(String dateString) throws JsonProcessingException, IOException
	{

		EntityTypeBuilder builder = repository.createBuilder("testTypeFormat"+dateString);
		builder.addSingleAttribute("textProperty", String.class);
		EntityType<ObjectNode> type = (EntityType<ObjectNode>) builder.createEntityType();

		ObjectNode node = (ObjectNode) mapper.readTree("{ext_type:'testTypeFormat"+dateString+"',textProperty:'"+dateString+"'}");
		AtemTypeAndConstraintValidationService validationService = (AtemTypeAndConstraintValidationService) type.getService(ValidationService.class);
		SimpleValidationContext context = new SimpleValidationContext(entityTypeRepository);
		SingleAttributeImpl<DateFormat> ca= new SingleAttributeImpl<DateFormat>(){
			@Override
			public DateFormat getValue(Object entity) {
				return new DateFormat("dd.MM.yyyy", new SimpleDateFormat("dd.MM.yyyy"));
			}};
		ca.setCode(DateFormat.META_ATTRIBUTE_CODE);
		List<SingleAttribute<? extends Constraint>>constraintAttributes= new LinkedList<SingleAttribute<? extends Constraint>>();
		constraintAttributes.add(ca);
		validationService.validate(type, context, node,constraintAttributes);
		return context;
	}
	
	@Test
	public void testTypeFormatIncorrect() throws JsonProcessingException, IOException {
		SimpleValidationContext context = testTypeFormat("66");
		Assert.assertEquals(1, context.getErrors().size());

	}

	@Test
	public void testTypeFormatCorrect() throws JsonProcessingException, IOException {
		SimpleValidationContext context = testTypeFormat("01.01.2001");
		Assert.assertEquals(0, context.getErrors().size());

	}

}
