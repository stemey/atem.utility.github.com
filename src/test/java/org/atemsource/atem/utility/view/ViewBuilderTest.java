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
package org.atemsource.atem.utility.view;

import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import junit.framework.Assert;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.PrimitiveType;
import org.atemsource.atem.pojo.EntityA;
import org.atemsource.atem.utility.common.View;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = { "classpath:/test/meta/pojo/entitytype.xml",
		"classpath:/meta/utility/view-example.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class ViewBuilderTest {

	@Inject
	private EntityTypeRepository entityTypeRepository;

	private Mockery context = new JUnit4Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	@Inject
	private ViewBuilderFactory factory;

	@Test
	public void test() {
		EntityType<EntityA> entityType = entityTypeRepository
				.getEntityType(EntityA.class);
		ViewBuilder viewBuilder = factory.create(entityType);
		viewBuilder.include("intP");
		ViewBuilder subViewBuilder = viewBuilder.include("list").cascade();
		subViewBuilder.include("integer");
		View view = viewBuilder.create();
		final Attribute listAttribute = entityType.getAttribute("list");
		final Attribute intPAttribute = entityType.getAttribute("intP");
		final Attribute integerAttribute = subViewBuilder.getEntityType()
				.getAttribute("integer");
		final ViewVisitor mockViewVisitor = context.mock(ViewVisitor.class);
		context.checking(new Expectations() {
			{
				oneOf(mockViewVisitor).visit(with(equal(null)),
						with(equal(listAttribute)),
						with(any(AttributeVisitor.class)));
				oneOf(mockViewVisitor).visit(with(equal(null)),
						with(equal(intPAttribute)));
				oneOf(mockViewVisitor).visit(with(equal(null)),
						with(equal(integerAttribute)),
						with(any(AttributeVisitor.class)));
			}
		});
		view.visit(mockViewVisitor, null);
	}

	@Test
	public void testRemoveAndIncludePrimitives() {
		EntityType<EntityA> entityType = entityTypeRepository
				.getEntityType(EntityA.class);
		ViewBuilder viewBuilder = factory.create(entityType);
		viewBuilder.includePrimitives(false);
		viewBuilder.remove("intP");
		View view = viewBuilder.create();
		final Attribute intPAttribute = entityType.getAttribute("intP");
		final AtomicInteger count=new AtomicInteger();
		final ViewVisitor mockViewVisitor = new ViewVisitor<Object>() {
			
			@Override
			public void visit(Object context, Attribute attribute) {
				
				if (attribute==intPAttribute) {
					Assert.fail("intP was removed");
				}else
				if (attribute.getTargetType() instanceof PrimitiveType<?>
						&& attribute instanceof SingleAttribute<?>) {
					count.incrementAndGet();
				} else {
					Assert.fail("not a single primitive " + attribute.getCode());
				}
			}

			@Override
			public void visit(Object context, Attribute attribute,
					AttributeVisitor<Object> attributeVisitor) {
				Assert.fail("not a primitive " + attribute.getCode());
			}
		};
		view.visit(mockViewVisitor, null);
		Assert.assertTrue(count.intValue()>2);
	}
}
