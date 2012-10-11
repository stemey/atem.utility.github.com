package org.atemsource.atem.utility.transform.api.attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import junit.framework.Assert;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.impl.dynamic.DynamicEntity;
import org.atemsource.atem.pojo.EntityA;
import org.atemsource.atem.pojo.EntityB;
import org.atemsource.atem.spi.DynamicEntityTypeSubrepository;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.JavaConverter;
import org.atemsource.atem.utility.transform.api.SimpleTransformationContext;
import org.atemsource.atem.utility.transform.api.TransformationBuilderFactory;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.transform.impl.builder.CollectionToMap;
import org.atemsource.atem.utility.transform.impl.converter.ConverterUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@ContextConfiguration(locations = {"classpath:/test/meta/utility/transform.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class CollectionToMapTest
{

	private static EntityTypeTransformation<?, ?> entityBTransformation;

	@Inject
	private DynamicEntityTypeSubrepository dynamicEntityTypeSubrepository;

	@Inject
	private EntityTypeRepository entityTypeRepository;

	@Inject
	private TransformationBuilderFactory transformationBuilderFactory;

	private EntityTypeTransformation<?, ?> createEntityBTransformation()
	{
		if (entityBTransformation == null)
		{
			TypeTransformationBuilder<?, ?> bBuilder =
				transformationBuilderFactory.create(EntityB.class, dynamicEntityTypeSubrepository.createBuilder("EntityB"));
			bBuilder.transform().from("integer").to("i");
			entityBTransformation = bBuilder.buildTypeTransformation();
		}
		return entityBTransformation;
	}

	private Converter<?, ?> createKeyConverter()
	{
		JavaConverter<EntityB, String> javaConverter = new JavaConverter<EntityB, String>()
		{

			@Override
			public String convertAB(EntityB a, TransformationContext ctx)
			{
				return String.valueOf(a.getInteger());
			}

			@Override
			public EntityB convertBA(String b, TransformationContext ctx)
			{
				// TODO Auto-generated method stub
				return null;
			}
		};
		return ConverterUtils.create(javaConverter);
	}

	@Test
	public void test()
	{
		TypeTransformationBuilder<?, ?> builder =
			transformationBuilderFactory.create(EntityA.class,
				dynamicEntityTypeSubrepository.createBuilder("collectionToMap"));
		CollectionToMap transformCustom = builder.transformCustom(CollectionToMap.class);
		transformCustom.from("list").to("map");
		transformCustom.convert(createEntityBTransformation());
		transformCustom.keyConvert(createKeyConverter());
		EntityTypeTransformation<EntityA, DynamicEntity> entityTypeTransformation =
			(EntityTypeTransformation<EntityA, DynamicEntity>) builder.buildTypeTransformation();
		EntityType<?> typeB = entityTypeTransformation.getEntityTypeB();

		EntityA a = new EntityA();
		List<EntityB> bs = new ArrayList<EntityB>();
		EntityB b1 = new EntityB();
		b1.setInteger(1);
		bs.add(b1);
		EntityB b2 = new EntityB();
		b2.setInteger(2);
		bs.add(b2);
		a.setList(bs);
		DynamicEntity b =
			entityTypeTransformation.getAB().convert(a, new SimpleTransformationContext(entityTypeRepository));
		Map<String, DynamicEntity> actualMap = (Map<String, DynamicEntity>) b.get("map");
		Assert.assertEquals(1, actualMap.get("1").get("i"));
		Assert.assertEquals(2, actualMap.get("2").get("i"));
	}

}
