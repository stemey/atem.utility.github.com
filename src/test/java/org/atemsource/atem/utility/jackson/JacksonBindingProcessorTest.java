package org.atemsource.atem.utility.jackson;

import javax.inject.Inject;

import junit.framework.Assert;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.jackson.domain.DomainA;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@ContextConfiguration(locations = {"classpath:/test/meta/utility/jackson.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class JacksonBindingProcessorTest
{

	@Inject
	private EntityTypeRepository entityTypeRepository;

	@Test
	public void test()
	{
		EntityType<Object> entityType = entityTypeRepository.getEntityType("json:1.1:" + DomainA.class.getName());
		Assert.assertNotNull(entityType);
		Assert.assertNotNull(entityType.getAttribute("renamed_property"));
	}

	@Test
	public void test10()
	{
		EntityType<Object> entityType = entityTypeRepository.getEntityType("json:1.0:" + DomainA.class.getName());
		Assert.assertNotNull(entityType);
		Assert.assertNull(entityType.getAttribute("field11"));
		Assert.assertNotNull(entityType.getAttribute("field10"));
		Assert.assertEquals(4, entityType.getAttributes().size());
	}

	@Test
	public void test11()
	{
		EntityType<Object> entityType = entityTypeRepository.getEntityType("json:1.1:" + DomainA.class.getName());
		Assert.assertNotNull(entityType);
		Assert.assertNull(entityType.getAttribute("field10"));
		Assert.assertNotNull(entityType.getAttribute("field11"));
		Assert.assertEquals(4, entityType.getAttributes().size());
	}
}
