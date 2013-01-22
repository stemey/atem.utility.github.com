/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.observer;

import javax.inject.Inject;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.pojo.EntityA;
import org.atemsource.atem.pojo.EntityB;
import org.atemsource.atem.utility.compare.AttributeChange;
import org.atemsource.atem.utility.compare.Comparison;
import org.atemsource.atem.utility.compare.ComparisonBuilder;
import org.atemsource.atem.utility.compare.ComparisonBuilderFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@ContextConfiguration(locations = {"classpath:/test/meta/utility/observer.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class EntityObserverTest
{

	@Inject
	private ComparisonBuilderFactory comparisonBuilderFactory;

	private final Mockery context = new JUnit4Mockery()
	{
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	@Inject
	private EntityObserverFactory entityObserverFactory;

	@Inject
	private EntityTypeRepository entityTypeRepository;

	@Test
	public void test()
	{
		EntityType<EntityA> entityType = entityTypeRepository.getEntityType(EntityA.class);
		ComparisonBuilder comparisonBuilder = comparisonBuilderFactory.create(entityType);
		comparisonBuilder.include("booleanP");
		comparisonBuilder.include("entityB").cascade().include("integer");
		Comparison comparison = comparisonBuilder.create();

		EntityA entityA = new EntityA();

		EntityObserver entityObserver = entityObserverFactory.create(comparison);
		entityObserver.setEntity(entityA);

		// creta first snapshot
		entityObserver.check();

		entityA.setBooleanP(true);
		final SingleAttributeListener listener1 = context.mock(SingleAttributeListener.class, "listener1");
		context.checking(new Expectations()
		{
			{
				one(listener1).onEvent(with(any(AttributeChange.class)));
			}
		});
		WatchHandle watch1 = entityObserver.watch("booleanP", listener1);
		entityObserver.check();
		watch1.unwatch();

		entityA.setEntityB(new EntityB());
		final SingleAttributeListener listener2 = context.mock(SingleAttributeListener.class, "listener2");
		context.checking(new Expectations()
		{
			{
				one(listener2).onEvent(with(any(AttributeChange.class)));
			}
		});
		WatchHandle watch2 = entityObserver.watch("entityB", listener2);
		entityObserver.check();
		watch2.unwatch();

		entityA.getEntityB().setInteger(12);
		final SingleAttributeListener listener3 = context.mock(SingleAttributeListener.class, "listener3");
		context.checking(new Expectations()
		{
			{
				one(listener3).onEvent(with(any(AttributeChange.class)));
			}
		});
		WatchHandle watch3 = entityObserver.watch("entityB.integer", listener3);
		entityObserver.check();
		watch3.unwatch();

	}
}
