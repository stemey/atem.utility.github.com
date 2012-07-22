package org.atemsource.atem.utility.jackson;

import java.util.ArrayList;

import javax.inject.Inject;

import junit.framework.Assert;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.jackson.domain.DomainA;
import org.atemsource.atem.utility.jackson.domain.DomainB;
import org.atemsource.atem.utility.jackson.domain.SubdomainA;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.node.TextNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@ContextConfiguration(locations = {"classpath:/test/meta/utility/jackson.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class JacksonBindingProcessorTest
{
	
	@Inject
	private JacksonBindingProcessor jacksonBindingProcessor;

	@Inject
	private EntityTypeRepository entityTypeRepository;

	@Test
	public void test()
	{
		
		jacksonBindingProcessor.createEntityType(SubdomainA.class, "1.1");
		EntityType<Object> entityType = entityTypeRepository.getEntityType("json:1.1:domain:" + DomainA.class.getSimpleName());
		Assert.assertNotNull(entityType);
		Assert.assertNotNull(entityType.getAttribute("renamed_property"));
		
		SubdomainA subdomainA = new SubdomainA();
		subdomainA.setDomainB(new DomainB());
		subdomainA.setField10("hallo");
		subdomainA.setField11("bye");
		subdomainA.setDomainBs(new ArrayList<DomainB>());		
		subdomainA.setSubField("sub");
		
		EntityTypeTransformation<DomainA, ObjectNode> transformation = (EntityTypeTransformation<DomainA, ObjectNode>) jacksonBindingProcessor.getTransformation(entityTypeRepository.getEntityType(SubdomainA.class));
		ObjectNode node = transformation.createB(subdomainA);
		Assert.assertEquals("sub", ((TextNode)node.get("sub")).toString());
	}

	@Test
	public void test10()
	{
		jacksonBindingProcessor.createEntityType(SubdomainA.class, "1.0");
		EntityType<Object> entityType = entityTypeRepository.getEntityType("json:1.0:domain:" + DomainA.class.getSimpleName());
		Assert.assertNotNull(entityType);
		Assert.assertNull(entityType.getAttribute("field11"));
		Assert.assertNotNull(entityType.getAttribute("field10"));
		Assert.assertEquals(4, entityType.getAttributes().size());
		

		
	}

	

	@Test
	public void test11()
	{
		EntityType<Object> entityType = entityTypeRepository.getEntityType("json:1.1:domain:" + DomainA.class.getSimpleName());
		Assert.assertNotNull(entityType);
		Assert.assertNull(entityType.getAttribute("field10"));
		Assert.assertNotNull(entityType.getAttribute("field11"));
		Assert.assertEquals(4, entityType.getAttributes().size());
	}
}
