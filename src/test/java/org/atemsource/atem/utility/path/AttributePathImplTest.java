/*******************************************************************************
 * Stefan Meyer, 2012
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import junit.framework.Assert;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.path.AttributePath;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.pojo.EntityA;
import org.atemsource.atem.pojo.EntityB;
import org.atemsource.atem.pojo.EntityB2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = { "classpath:/test/meta/pojo/entitytype.xml",
		"classpath:/meta/utility/path-example.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class AttributePathImplTest {
	@Inject
	private AttributePathBuilderFactory builderFactory;

	@Inject
	private EntityTypeRepository entityTypeRepository;

	@Test
	public void testPrimitive() {
		EntityType<?> entityType = entityTypeRepository
				.getEntityType(EntityB.class);
		AttributePath path = builderFactory.createAttributePath("integer",
				entityType);
		EntityB b = new EntityB();
		path.getAttribute().setValue(b, 6);
		Assert.assertEquals(6, path.getAttribute().getValue(b));
		Assert.assertEquals(int.class, path.getAttribute().getTargetType()
				.getJavaType());
		Assert.assertEquals(EntityB.class, path.getAttribute().getEntityType()
				.getJavaType());
	}

	@Test
	public void testExtend() {
		EntityType<?> entityType = entityTypeRepository
				.getEntityType(EntityA.class);
		AttributePath path1 = builderFactory.createAttributePath("list",
				entityType);
		AttributePath path2 = builderFactory.createAttributePath("integer",
				entityTypeRepository
				.getEntityType(EntityB.class));
		AttributePath path =builderFactory.createBuilder(path1).addIndex(0).addPath(path2).createPath();
		EntityA a = new EntityA();
		EntityB b = new EntityB2();
		ArrayList<EntityB> list = new ArrayList<EntityB>();
		list.add(b);
		a.setList(list);
		path.getAttribute().setValue(a, 6);
		Assert.assertEquals(6, path.getAttribute().getValue(a));	}

	@Test
	public void testSingleAssociation() {
		EntityType<?> entityType = entityTypeRepository
				.getEntityType(EntityA.class);
		AttributePath path = builderFactory.createAttributePath("entityB",
				entityType);
		EntityA a = new EntityA();
		EntityB b = new EntityB2();
		path.getAttribute().setValue(a, b);
		Assert.assertEquals(b, path.getAttribute().getValue(a));
		Assert.assertEquals(EntityB.class, path.getAttribute().getTargetType()
				.getJavaType());
	}

	@Test
	public void testSubtypeProperty() {
		EntityType<?> entityType = entityTypeRepository
				.getEntityType(EntityA.class);
		AttributePath path = builderFactory.createAttributePath("entityB.value",
				entityType);
		EntityA a = new EntityA();
		EntityB2 b = new EntityB2();
		a.setEntityB(b);
		path.getAttribute().setValue(a, "hi");
		Assert.assertEquals("hi", path.getAttribute().getValue(a));
		Assert.assertEquals(String.class, path.getAttribute().getTargetType()
				.getJavaType());
	}

	@Test
	public void testSubtypePropertyButWrongType() {
		EntityType<?> entityType = entityTypeRepository
				.getEntityType(EntityA.class);
		AttributePath path = builderFactory.createAttributePath("entityB.value",
				entityType);
		EntityA a = new EntityA();
		EntityB b = new EntityB();
		a.setEntityB(b);
		path.getAttribute().setValue(a, "hi");
		Assert.assertEquals(null, path.getAttribute().getValue(a));
		Assert.assertEquals(String.class, path.getAttribute().getTargetType()
				.getJavaType());
	}

	@Test
	public void testIndexCollection() {
		EntityType<?> entityType = entityTypeRepository
				.getEntityType(EntityA.class);
		AttributePath path = builderFactory.createAttributePath("list.0.integer",
				entityType);
		EntityA a = new EntityA();
		EntityB b = new EntityB2();
		ArrayList<EntityB> list = new ArrayList<EntityB>();
		list.add(b);
		a.setList(list);
		path.getAttribute().setValue(a, 6);
		Assert.assertEquals(6, path.getAttribute().getValue(a));
	}

	@Test
	public void testMap() {
		EntityType<?> entityType = entityTypeRepository
				.getEntityType(EntityA.class);
		AttributePath path = builderFactory.createAttributePath("map.k1.integer",
				entityType);
		EntityA a = new EntityA();
		EntityB b = new EntityB2();
		Map<String,EntityB> map = new HashMap<String, EntityB>();
		map.put("k1",b);
		a.setMap(map);
		path.getAttribute().setValue(a, 6);
		Assert.assertEquals(6, path.getAttribute().getValue(a));
	}
}
