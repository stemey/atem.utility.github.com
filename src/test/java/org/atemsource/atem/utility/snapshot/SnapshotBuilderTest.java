/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.snapshot;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import junit.framework.Assert;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.impl.dynamic.DynamicEntity;
import org.atemsource.atem.pojo.EntityA;
import org.atemsource.atem.pojo.EntityB;
import org.atemsource.atem.utility.transform.api.Transformation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@ContextConfiguration(locations = {"classpath:/test/meta/utility/snapshot.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class SnapshotBuilderTest
{

	@Inject
	private SnapshotBuilderFactory snapshotBuilderFactory;

	@Inject
	private EntityTypeRepository entityTypeRepository;

	@Test
	public void test()
	{
		EntityA entityA = new EntityA();
		entityA.setIntP(100);
		entityA.setBooleanO(true);
		List<EntityB> list = new ArrayList<EntityB>();
		EntityB b1 = new EntityB();
		b1.setInteger(5);
		list.add(b1);
		entityA.setList(list);

		EntityType<EntityB> entityTypeB = entityTypeRepository.getEntityType(EntityB.class);
		SnapshotBuilder subBuilder = snapshotBuilderFactory.create(entityTypeB);
		subBuilder.include("integer");
		Transformation<?, ?> subTransformation = subBuilder.create();

		EntityType<EntityA> entityTypeA = entityTypeRepository.getEntityType(EntityA.class);
		SnapshotBuilder builder = snapshotBuilderFactory.create(entityTypeA);
		builder.include("intP");
		builder.include("list").cascade(subTransformation);

		Transformation<EntityA, DynamicEntity> snapshotting = (Transformation<EntityA, DynamicEntity>) builder.create();

		DynamicEntity snapshot = snapshotting.getAB().convert(entityA, null);

		EntityA restored = snapshotting.getBA().convert(snapshot, null);

		Assert.assertFalse(((List) snapshot.get("list")).get(0) instanceof EntityB);
		Assert.assertEquals(100, restored.getIntP());
		Assert.assertNotSame(entityA.getList(), restored.getList());
		Assert.assertEquals(5, restored.getList().get(0).getInteger());
		Assert.assertEquals(null, restored.getBooleanO());

	}

}
