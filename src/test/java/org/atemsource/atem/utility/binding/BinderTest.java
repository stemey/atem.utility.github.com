package org.atemsource.atem.utility.binding;

import java.util.ArrayList;

import javax.inject.Inject;

import junit.framework.Assert;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.binding.Binder;
import org.atemsource.atem.utility.binding.version.VersionedBinder;
import org.atemsource.atem.utility.domain.DomainA;
import org.atemsource.atem.utility.domain.DomainB;
import org.atemsource.atem.utility.domain.SubdomainA;
import org.atemsource.atem.utility.transform.api.DerivedType;
import org.atemsource.atem.utility.transform.api.SimpleTransformationContext;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.codehaus.jackson.node.BooleanNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.node.TextNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = { "classpath:/test/meta/utility/binding/standard.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class BinderTest {

	@Inject
	private Binder binder;

	@Inject
	private EntityTypeRepository entityTypeRepository;

	@Test
	public void test() {

		EntityTypeTransformation<DomainA, ObjectNode> transformation = binder
				.getTransformation(DomainA.class);
		Assert.assertNotNull(transformation);
	}

	@Test
	public void testSuper() {

		EntityTypeTransformation<SubdomainA, ObjectNode> transformation = binder
				.getTransformation(SubdomainA.class);

		SubdomainA subdomainA = new SubdomainA();
		subdomainA.setField10("hallo");
		subdomainA.setField11("bye");
		subdomainA.setSubField("sub");

		ObjectNode node = transformation.getAB().convert(subdomainA,
				new SimpleTransformationContext());
		Assert.assertEquals("sub",
				((TextNode) node.get("subField")).getValueAsText());
		Assert.assertEquals("bye",
				((TextNode) node.get("field11")).getValueAsText());
	}

	@Test
	public void testCircularGraph() {

		EntityTypeTransformation<DomainA, ObjectNode> transformation = binder
				.getTransformation(DomainA.class);

		SubdomainA subdomainA = new SubdomainA();
		subdomainA.setField10("hallo");
		subdomainA.setField11("bye");
		subdomainA.setSubField("sub");
		DomainB domainB = new DomainB();
		domainB.setReverse(subdomainA);
		domainB.setBField(true);
		subdomainA.setDomainB(domainB);

		ObjectNode node = transformation.getAB().convert(subdomainA,new SimpleTransformationContext());
		ObjectNode nodeB = (ObjectNode) node.get("domainB");
		Assert.assertEquals(true,
				((BooleanNode) nodeB.get("bField")).getBooleanValue());
		Assert.assertTrue(node == nodeB.get("reverse"));
	}

	@Test
	public void testSub() {

		EntityTypeTransformation<DomainA, ObjectNode> transformation = binder
				.getTransformation(DomainA.class);

		SubdomainA subdomainA = new SubdomainA();
		subdomainA.setField10("hallo");
		subdomainA.setField11("bye");
		subdomainA.setSubField("sub");

		ObjectNode node = transformation.getAB().convert(subdomainA,
				new SimpleTransformationContext());
		Assert.assertEquals("sub",
				((TextNode) node.get("subField")).getValueAsText());
		Assert.assertEquals("bye",
				((TextNode) node.get("field11")).getValueAsText());
	}

}
