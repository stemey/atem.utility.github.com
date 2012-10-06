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
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.node.TextNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@ContextConfiguration(locations = {"classpath:/test/meta/utility/binding/version.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class VersionBinderTest
{
	
	@Inject
	private VersionedBinder binder;

	@Inject
	private EntityTypeRepository entityTypeRepository;
	
	

	@Test
	public void test()
	{
		
		EntityTypeTransformation<DomainA,ObjectNode> transformation = binder.getTransformation(DomainA.class,"1.1");
		Assert.assertNotNull(transformation);
		}

	@Test
	public void testRenaming()
	{
		
		EntityTypeTransformation<DomainA,ObjectNode> transformation = binder.getTransformation(DomainA.class,"1.1");
		EntityType<ObjectNode> entityType=(EntityType<ObjectNode>) transformation.getTypeB();
		Assert.assertNotNull(entityType.getAttribute("renamed_property"));
		
		}



	@Test
	public void test10()
	{
		EntityTypeTransformation<DomainA,ObjectNode> transformation = binder.getTransformation(DomainA.class,"1.0");
		EntityType<ObjectNode> entityType=(EntityType<ObjectNode>) transformation.getTypeB();
		Assert.assertNotNull(entityType);
		Assert.assertNull(entityType.getAttribute("field11"));
		Assert.assertNotNull(entityType.getAttribute("field10"));
		Assert.assertEquals(5, entityType.getAttributes().size());
		

		
	}

	

	@Test
	public void test11()
	{
		EntityTypeTransformation<DomainA,ObjectNode> transformation = binder.getTransformation(DomainA.class,"1.1");
		EntityType<ObjectNode> entityType=(EntityType<ObjectNode>) transformation.getTypeB();
		Assert.assertNotNull(entityType);
		Assert.assertNull(entityType.getAttribute("field10"));
		Assert.assertNotNull(entityType.getAttribute("field11"));
		Assert.assertEquals(5, entityType.getAttributes().size());
	}
	
	@Test
	public void testIgnore()
	{
		EntityTypeTransformation<DomainA,ObjectNode> transformation = binder.getTransformation(DomainA.class,"1.1");
		EntityType<ObjectNode> entityType=(EntityType<ObjectNode>) transformation.getTypeB();
		Assert.assertNull(entityType.getAttribute("ignoredProperty"));
	}
}
