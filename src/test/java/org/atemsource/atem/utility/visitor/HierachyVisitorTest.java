package org.atemsource.atem.utility.visitor;

import static org.junit.Assert.*;

import javax.inject.Inject;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.view.View;
import org.atemsource.atem.api.view.ViewVisitor;
import org.atemsource.atem.api.view.Visitor;
import org.atemsource.atem.utility.domain.DomainA;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = { "classpath:/test/meta/utility/visitor.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class HierachyVisitorTest {

	private final class Visitr implements ViewVisitor<Object> {
		int attributeCount=0;
		int typeCount=0;
		@Override
		public void visit(Object context, Attribute attribute) {
			attributeCount++;
		}

		@Override
		public void visit(Object context, Attribute attribute, Visitor<Object> targetTypeVisitor) {
			attributeCount++;
			typeCount++;
			targetTypeVisitor.visit(null);
		}

		@Override
		public void visitSubView(Object context, View view, Visitor<Object> subViewVisitor) {
			typeCount++;
			subViewVisitor.visit(null);
		}

		@Override
		public void visitSuperView(Object context, View view, Visitor<Object> superViewVisitor) {
			typeCount++;
			superViewVisitor.visit(null);
		}
	}
	@Inject
	private EntityTypeRepository entityTypeRepository;
	@Test
	public void test() {
		
		Visitr visitor = new Visitr();
		HierachyVisitor.visit(entityTypeRepository.getEntityType(Middle.class), visitor, null);
		Assert.assertEquals(3,visitor.typeCount);
		Assert.assertEquals(5,visitor.attributeCount);
	}

}
