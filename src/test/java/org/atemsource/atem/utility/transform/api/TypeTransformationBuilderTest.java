/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.transform.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import junit.framework.Assert;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.impl.dynamic.DynamicEntity;
import org.atemsource.atem.pojo.EntityA;
import org.atemsource.atem.pojo.EntityB;
import org.atemsource.atem.pojo.TestAnnotation;
import org.atemsource.atem.spi.DynamicEntityTypeSubrepository;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.transform.impl.builder.GenericTransformationBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@ContextConfiguration(locations = {"classpath:/test/meta/utility/transform.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class TypeTransformationBuilderTest
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
			TypeTransformationBuilder<?, ?> bBuilder = transformationBuilderFactory.create();
			bBuilder.setSourceType(EntityB.class);
			bBuilder.setTargetTypeBuilder(dynamicEntityTypeSubrepository.createBuilder("testSingleAssociationB"));
			bBuilder.transform().from("integer").to("i");
			entityBTransformation = bBuilder.buildTypeTransformation();
		}
		return entityBTransformation;
	}

	@Test
	public void testAssociativeCollection()
	{
		TypeTransformationBuilder<?, ?> builder = transformationBuilderFactory.create();
		builder.setSourceType(EntityA.class);
		builder.setTargetTypeBuilder(dynamicEntityTypeSubrepository.createBuilder("testAssociativeCollection"));
		builder.transformCollection().from("list").to("list").convert(createEntityBTransformation());
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
		DynamicEntity b = entityTypeTransformation.getAB().convert(a, new SimpleTransformationContext());
		List<DynamicEntity> actualList = (List<DynamicEntity>) b.get("list");
		Assert.assertEquals(1, actualList.get(0).get("i"));
		Assert.assertEquals(2, actualList.get(1).get("i"));
	}

	@Test
	public void testAssociativeMap()
	{
		TypeTransformationBuilder<?, ?> builder = transformationBuilderFactory.create();
		builder.setSourceType(EntityA.class);
		builder.setTargetTypeBuilder(dynamicEntityTypeSubrepository.createBuilder("testAssociativeMap"));
		builder.transformMap().from("map").to("map").convert(createEntityBTransformation());
		EntityTypeTransformation<EntityA, DynamicEntity> entityTypeTransformation =
			(EntityTypeTransformation<EntityA, DynamicEntity>) builder.buildTypeTransformation();
		EntityType<?> typeB = entityTypeTransformation.getEntityTypeB();

		EntityA a = new EntityA();
		Map<String, EntityB> bs = new HashMap<String, EntityB>();
		EntityB b1 = new EntityB();
		b1.setInteger(1);
		bs.put("1", b1);
		EntityB b2 = new EntityB();
		b2.setInteger(2);
		bs.put("2", b2);
		a.setMap(bs);
		DynamicEntity b = entityTypeTransformation.getAB().convert(a, new SimpleTransformationContext());
		Map<String, DynamicEntity> actualMap = (Map<String, DynamicEntity>) b.get("map");
		Assert.assertEquals(1, actualMap.get("1").get("i"));
		Assert.assertEquals(2, actualMap.get("2").get("i"));
	}

	@Test
	public void testGeneric()
	{
		SimpleTransformationContext ctx = new SimpleTransformationContext();
		TypeTransformationBuilder<?, ?> builder = transformationBuilderFactory.create();
		builder.setSourceType(EntityA.class);
		builder.setTargetTypeBuilder(dynamicEntityTypeSubrepository.createBuilder("testGeneric"));
		GenericTransformationBuilder transformCustom = builder.transformCustom(GenericTransformationBuilder.class);
		transformCustom.getTargetTypeBuilder().addSingleAttribute("derivedValue",
			entityTypeRepository.getType(String.class));
		transformCustom.from("booleanO").from("intO").transform(new JavaTransformation<EntityA, DynamicEntity>()
		{

			@Override
			public void mergeAB(EntityA a, DynamicEntity b, TransformationContext ctx)
			{
				b.put("derivedValue", a.getBooleanO() + "" + a.getIntO());
			}

			@Override
			public void mergeBA(DynamicEntity b, EntityA a, TransformationContext ctx)
			{
			}

		});
		EntityTypeTransformation<EntityA, DynamicEntity> entityTypeTransformation =
			(EntityTypeTransformation<EntityA, DynamicEntity>) builder.buildTypeTransformation();
		EntityType<?> typeB = entityTypeTransformation.getEntityTypeB();

		EntityA a = new EntityA();
		a.setIntO(100);
		a.setBooleanO(Boolean.TRUE);
		DynamicEntity b = entityTypeTransformation.getAB().convert(a, ctx);
		Assert.assertEquals("true100", b.get("derivedValue"));
	}

	@Test
	public void testPrimitiveCollection()
	{
		TypeTransformationBuilder<?, ?> builder = transformationBuilderFactory.create();
		builder.setSourceType(EntityA.class);
		builder.setTargetTypeBuilder(dynamicEntityTypeSubrepository.createBuilder("testPrimitiveCollection"));
		builder.transformCollection().from("stringList").to("list");
		EntityTypeTransformation<EntityA, DynamicEntity> entityTypeTransformation =
			(EntityTypeTransformation<EntityA, DynamicEntity>) builder.buildTypeTransformation();
		EntityType<?> typeB = entityTypeTransformation.getEntityTypeB();

		EntityA a = new EntityA();
		List<String> stringList = new ArrayList<String>();
		stringList.add("1");
		stringList.add("2");
		a.setStringList(stringList);
		DynamicEntity b = entityTypeTransformation.getAB().convert(a, new SimpleTransformationContext());
		List<String> actualList = (List<String>) b.get("list");
		Assert.assertEquals("1", actualList.get(0));
		Assert.assertEquals("2", actualList.get(1));
	}

	@Test
	public void testPrimitiveMap()
	{
		TypeTransformationBuilder<?, ?> builder = transformationBuilderFactory.create();
		builder.setSourceType(EntityA.class);
		builder.setTargetTypeBuilder(dynamicEntityTypeSubrepository.createBuilder("testPrimitiveMap"));
		builder.transformMap().from("string2int").to("map");
		EntityTypeTransformation<EntityA, DynamicEntity> entityTypeTransformation =
			(EntityTypeTransformation<EntityA, DynamicEntity>) builder.buildTypeTransformation();
		EntityType<?> typeB = entityTypeTransformation.getEntityTypeB();

		EntityA a = new EntityA();
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("1", 1);
		map.put("2", 2);
		a.setString2int(map);
		DynamicEntity b = entityTypeTransformation.getAB().convert(a, new SimpleTransformationContext());
		Map<String, Integer> actualMap = (Map<String, Integer>) b.get("map");
		Assert.assertEquals(1, (int) actualMap.get("1"));
		Assert.assertEquals(2, (int) actualMap.get("2"));
	}

	@Test
	public void testSingleAssociation()
	{
		EntityTypeTransformation<?, ?> bTransformation = createEntityBTransformation();

		TypeTransformationBuilder<?, ?> builder = transformationBuilderFactory.create();
		builder.setSourceType(EntityA.class);
		builder.setTargetTypeBuilder(dynamicEntityTypeSubrepository.createBuilder("testSingleAssociation"));
		builder.transform().from("entityB").to("b").convert(bTransformation);
		EntityTypeTransformation<EntityA, DynamicEntity> entityTypeTransformation =
			(EntityTypeTransformation<EntityA, DynamicEntity>) builder.buildTypeTransformation();
		EntityType<?> typeB = entityTypeTransformation.getEntityTypeB();

		EntityB b = new EntityB();
		b.setInteger(5);
		EntityA a = new EntityA();
		a.setEntityB(b);
		DynamicEntity a2 = entityTypeTransformation.getAB().convert(a, new SimpleTransformationContext());
		Assert.assertEquals(5, ((DynamicEntity) a2.get("b")).get("i"));
	}

	@Test
	public void testSinglePrimitive()
	{
		EntityTypeTransformation<EntityB, DynamicEntity> entityTypeTransformation = createEntityTypeTransformation("testSinglePrimitive");

		EntityB a = new EntityB();
		a.setInteger(5);
		DynamicEntity b = entityTypeTransformation.getAB().convert(a, new SimpleTransformationContext());
		Assert.assertEquals(5, b.get("i"));
		
	}

	@Test
	public void testSinglePrimitiveDerivedAnnotation()
	{
		EntityTypeTransformation<EntityB, DynamicEntity> entityTypeTransformation = createEntityTypeTransformation("testDerivedAnnotation");

		EntityType<Attribute> attributeType = entityTypeRepository.getEntityType(Attribute.class);
		Attribute attr=((EntityType<?>)entityTypeTransformation.getTypeA()).getAttribute("integer");
		Attribute metaAttribute = attributeType.getMetaAttribute(TestAnnotation.class.getName());
		Object value = metaAttribute.getValue(attr);
		Assert.assertNotNull(value);
		
		Attribute derivedAttr=((EntityType<?>)entityTypeTransformation.getTypeB()).getAttribute("i");
		Object derivedAnnoation = metaAttribute.getValue(derivedAttr);
		Assert.assertEquals(value, derivedAnnoation);
	}

	protected EntityTypeTransformation<EntityB, DynamicEntity> createEntityTypeTransformation(String transformationId) {
		TypeTransformationBuilder<?, ?> builder = transformationBuilderFactory.create();
		builder.setSourceType(EntityB.class);
		builder.setTargetTypeBuilder(dynamicEntityTypeSubrepository.createBuilder(transformationId));
		builder.transform().from("integer").to("i");
		EntityTypeTransformation<EntityB, DynamicEntity> entityTypeTransformation =
			(EntityTypeTransformation<EntityB, DynamicEntity>) builder.buildTypeTransformation();
		EntityType<?> typeB = entityTypeTransformation.getEntityTypeB();
		return entityTypeTransformation;
	}
}
