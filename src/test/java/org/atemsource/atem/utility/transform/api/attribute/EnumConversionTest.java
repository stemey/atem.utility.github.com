package org.atemsource.atem.utility.transform.api.attribute;

import javax.inject.Inject;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.pojo.EntityC;
import org.atemsource.atem.spi.DynamicEntityTypeSubrepository;
import org.atemsource.atem.utility.domain.Vehicle;
import org.atemsource.atem.utility.transform.api.TransformationBuilderFactory;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.api.constraint.PossibleValues;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.transform.impl.converter.EnumToStringConverter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = { "classpath:/test/meta/utility/transform.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class EnumConversionTest {

	@Inject
	private TransformationBuilderFactory transformationBuilderFactory;

	@Inject
	private DynamicEntityTypeSubrepository dynamicEntityTypeSubrepository;

	@Inject
	private EntityTypeRepository entityTypeRepository;

	private EntityTypeTransformation<?, ?> createEntityBTransformation() {
		TypeTransformationBuilder<?, ?> bBuilder = transformationBuilderFactory
				.create(EntityC.class,
						dynamicEntityTypeSubrepository.createBuilder("EntityC"));
		bBuilder.transform().from("vehicle").to("vehicle")
				.convert(new EnumToStringConverter(Vehicle.class));
		return bBuilder.buildTypeTransformation();
	}

	@Test
	public void test() {
		EntityType typeB = (EntityType) createEntityBTransformation()
				.getTypeB();
		Attribute attribute = typeB.getAttribute("vehicle");
		EntityType<Attribute> attributeType = entityTypeRepository
				.getEntityType(Attribute.class);
		Attribute metaAttribute = attributeType
				.getMetaAttribute(PossibleValues.META_ATTRIBUTE_CODE);
		PossibleValues value = (PossibleValues) metaAttribute
				.getValue(attribute);
		Assert.assertNotNull(value);
		Assert.assertEquals(2, value.getValues().length);
	}

}
