/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.compare;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import junit.framework.Assert;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.pojo.EntityA;
import org.atemsource.atem.pojo.EntityB;
import org.atemsource.atem.pojo.EntityB2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@ContextConfiguration(locations = {"classpath:/test/meta/pojo/entitytype.xml", "classpath:/meta/utility/compare.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class OrderableCollectionComparatorTest
{

	@Inject
	private EntityTypeRepository entityTypeRepository;

	@Inject
	private ComparisonBuilderFactory comparisonBuilderFactory;

	private Comparison comparison;

	private Comparison comparisonAssociative;

	public EntityA createEntityA()
	{
		EntityA a = new EntityA();
		a.setNumber(0);
		return a;
	}

	public EntityB createEntityB()
	{
		EntityB b = new EntityB();
		b.setInteger(0);
		return b;
	}

	@PostConstruct
	public void start()
	{
		EntityType<EntityA> entityType = entityTypeRepository.getEntityType(EntityA.class);
		ComparisonBuilder comparisonBuilder = comparisonBuilderFactory.create(entityType);
		comparisonBuilder.include("list");

		comparison = comparisonBuilder.create();

		ComparisonBuilder comparisonBuilderAssociative = comparisonBuilderFactory.create(entityType);
		ComparisonBuilder subBuilder = comparisonBuilderAssociative.include("list").cascade();
		subBuilder.include("integer");

		comparisonAssociative = comparisonBuilderAssociative.create();

	}

	@Test
	public void testAdded()
	{
		EntityA a1 = createEntityA();
		EntityB b1 = createEntityB();
		EntityA a2 = createEntityA();
		EntityB b2 = createEntityB();
		b1.setInteger(1);
		b2.setInteger(5);
		a2.getList().add(b2);

		Set<Difference> differences = comparisonAssociative.getDifferences(a1, a2);

		Assert.assertEquals(1, differences.size());
		Addition change = (Addition) differences.iterator().next();
		Assert.assertEquals(b2, change.getValue());
		Assert.assertEquals("list.0", change.getPath().toString());
	}

	@Test
	public void testElementModifed()
	{

		EntityA a1 = createEntityA();
		EntityB b1 = createEntityB();
		EntityA a2 = createEntityA();
		EntityB b2 = createEntityB();
		b1.setInteger(1);
		a1.getList().add(b1);
		b2.setInteger(2);
		a2.getList().add(b2);

		Set<Difference> differences = comparisonAssociative.getDifferences(a1, a2);

		Assert.assertEquals(1, differences.size());
		AttributeChange change = (AttributeChange) differences.iterator().next();
		Assert.assertEquals(1, change.getOldValue());
		Assert.assertEquals(2, change.getNewValue());
		Assert.assertEquals("list.0.integer", change.getPath().toString());
	}

	@Test
	public void testElementModifedPrimitive()
	{

		EntityA a1 = createEntityA();
		EntityB b1 = createEntityB();
		EntityA a2 = createEntityA();
		EntityB b2 = createEntityB();
		b1.setInteger(1);
		a1.getList().add(b1);
		b2.setInteger(2);
		a2.getList().add(b2);

		Set<Difference> differences = comparison.getDifferences(a1, a2);

		Assert.assertEquals(1, differences.size());
		AttributeChange change = (AttributeChange) differences.iterator().next();
		Assert.assertEquals(b1, change.getOldValue());
		Assert.assertEquals(b2, change.getNewValue());
		Assert.assertEquals("list.0", change.getPath().toString());
	}

	@Test
	public void testElementTypeChange()
	{

		EntityA a1 = createEntityA();
		EntityB b1 = createEntityB();
		EntityA a2 = createEntityA();
		EntityB b2 = new EntityB2();
		b1.setInteger(1);
		a1.getList().add(b1);
		b2.setInteger(1);
		a2.getList().add(b2);

		Set<Difference> differences = comparisonAssociative.getDifferences(a1, a2);

		Assert.assertEquals(1, differences.size());
		AttributeChange change = (AttributeChange) differences.iterator().next();
		Assert.assertEquals(b1, change.getOldValue());
		Assert.assertEquals(b2, change.getNewValue());
		Assert.assertEquals("list.0", change.getPath().toString());
	}

	@Test
	public void testElementTypeChangePrimitive()
	{

		EntityA a1 = createEntityA();
		EntityB b1 = createEntityB();
		EntityA a2 = createEntityA();
		EntityB b2 = new EntityB2();
		b1.setInteger(1);
		a1.getList().add(b1);
		b2.setInteger(1);
		a2.getList().add(b2);

		Set<Difference> differences = comparison.getDifferences(a1, a2);

		Assert.assertEquals(1, differences.size());
		AttributeChange change = (AttributeChange) differences.iterator().next();
		Assert.assertEquals(b1, change.getOldValue());
		Assert.assertEquals(b2, change.getNewValue());
		Assert.assertEquals("list.0", change.getPath().toString());
	}

	@Test
	public void testElementUnmodifedPrimitive()
	{

		EntityA a1 = createEntityA();
		EntityB b1 = createEntityB();
		EntityA a2 = createEntityA();
		EntityB b2 = createEntityB();
		b1.setInteger(1);
		a1.getList().add(b1);
		b2.setInteger(1);
		a2.getList().add(b2);

		Set<Difference> differences = comparison.getDifferences(a1, a2);

		Assert.assertEquals(0, differences.size());
	}

	@Test
	public void testMoved()
	{
		EntityA a1 = createEntityA();
		EntityB b1 = createEntityB();
		EntityA a2 = createEntityA();
		EntityB b2 = createEntityB();
		b1.setInteger(1);
		b2.setInteger(2);
		a1.getList().add(b1);
		a1.getList().add(b2);
		a2.getList().add(b2);
		a2.getList().add(b1);

		Set<Difference> differences = comparisonAssociative.getDifferences(a1, a2);

		Assert.assertEquals(2, differences.size());
		for (Difference diff : differences)
		{
			Rearrangement change = (Rearrangement) diff;
			Assert.assertEquals("list", change.getPath().toString());
			if (change.getValue() == b1)
			{
				Assert.assertEquals("list.1", change.getNewPath().toString());
				Assert.assertEquals("list.0", change.getOldPath().toString());
				Assert.assertEquals(0, change.getOldIndex());
				Assert.assertEquals(1, change.getNewIndex());
			}
			else
			{
				Assert.assertEquals("list.0", change.getNewPath().toString());
				Assert.assertEquals("list.1", change.getOldPath().toString());
				Assert.assertEquals(1, change.getOldIndex());
				Assert.assertEquals(0, change.getNewIndex());

			}
		}

	}

	@Test
	public void testRemoved()
	{
		EntityA a1 = createEntityA();
		EntityB b1 = createEntityB();
		EntityA a2 = createEntityA();
		EntityB b2 = createEntityB();
		b1.setInteger(1);
		b2.setInteger(5);
		a1.getList().add(b1);

		Set<Difference> differences = comparisonAssociative.getDifferences(a1, a2);

		Assert.assertEquals(1, differences.size());
		Removal change = (Removal) differences.iterator().next();
		Assert.assertEquals(b1, change.getValue());
		Assert.assertEquals("list.0", change.getPath().toString());
	}

	@Test
	public void testUnmodified()
	{
		EntityA a1 = createEntityA();
		EntityB b1 = createEntityB();
		EntityA a2 = createEntityA();
		EntityB b2 = createEntityB();
		b1.setInteger(1);
		a1.getList().add(b1);
		b2.setInteger(1);
		a2.getList().add(b2);

		Set<Difference> differences = comparisonAssociative.getDifferences(a1, a2);

		Assert.assertEquals(0, differences.size());
	}

}
