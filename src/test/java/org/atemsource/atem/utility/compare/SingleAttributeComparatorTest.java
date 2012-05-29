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
import org.atemsource.atem.utility.compare.AttributeChange;
import org.atemsource.atem.utility.compare.Comparison;
import org.atemsource.atem.utility.compare.ComparisonBuilder;
import org.atemsource.atem.utility.compare.ComparisonBuilderFactory;
import org.atemsource.atem.utility.compare.Difference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@ContextConfiguration(locations = {"classpath:/test/meta/pojo/entitytype.xml", "classpath:/meta/utility/compare-example.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class SingleAttributeComparatorTest
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
		comparisonBuilder.include("number");

		comparison = comparisonBuilder.create();

		ComparisonBuilder comparisonBuilderAssociative = comparisonBuilderFactory.create(entityType);
		ComparisonBuilder subBuilder = comparisonBuilderAssociative.include("entityB").cascade();
		subBuilder.include("integer");

		comparisonAssociative = comparisonBuilderAssociative.create();

	}

	@Test
	public void testAssociatvieDifferent()
	{
		EntityA a1 = createEntityA();
		EntityB b1 = createEntityB();
		EntityA a2 = createEntityA();
		EntityB b2 = createEntityB();
		b1.setInteger(1);
		a1.setEntityB(b1);
		b2.setInteger(5);
		a2.setEntityB(b2);

		Set<Difference> differences = comparisonAssociative.getDifferences(a1, a2);

		Assert.assertEquals(1, differences.size());
		AttributeChange change = (AttributeChange) differences.iterator().next();
		Assert.assertEquals(1, change.getOldValue());
		Assert.assertEquals(5, change.getNewValue());
		Assert.assertEquals("entityB.integer", change.getPath().toString());
	}

	@Test
	public void testAssociatvieDifferentType()
	{
		EntityA a1 = createEntityA();
		EntityB b1 = createEntityB();
		EntityA a2 = createEntityA();
		EntityB b2 = new EntityB2();
		b1.setInteger(1);
		a1.setEntityB(b1);
		b2.setInteger(1);
		a2.setEntityB(b2);

		Set<Difference> differences = comparisonAssociative.getDifferences(a1, a2);

		Assert.assertEquals(1, differences.size());
		AttributeChange change = (AttributeChange) differences.iterator().next();
		Assert.assertEquals(b1, change.getOldValue());
		Assert.assertEquals(b2, change.getNewValue());
		Assert.assertEquals("entityB", change.getPath().toString());
	}

	@Test
	public void testAssociatvieEquals()
	{
		EntityA a1 = createEntityA();
		EntityB b = createEntityB();
		EntityA a2 = createEntityA();
		a1.setEntityB(b);
		a2.setEntityB(b);

		Set<Difference> differences = comparisonAssociative.getDifferences(a1, a2);

		Assert.assertTrue(differences.size() == 0);
	}

	@Test
	public void testPrimitiveDifferent()
	{

		EntityA a1 = createEntityA();
		EntityA a2 = createEntityA();
		a2.setNumber(3);

		Set<Difference> differences = comparison.getDifferences(a1, a2);

		Assert.assertEquals(1, differences.size());
		AttributeChange change = (AttributeChange) differences.iterator().next();
		Assert.assertEquals(0, change.getOldValue());
		Assert.assertEquals(3, change.getNewValue());
		Assert.assertEquals("number", change.getPath().toString());
	}

	@Test
	public void testPrimitiveEquals()
	{
		EntityA a1 = createEntityA();
		EntityA a2 = createEntityA();

		Set<Difference> differences = comparison.getDifferences(a1, a2);

		Assert.assertTrue(differences.size() == 0);
	}
}
