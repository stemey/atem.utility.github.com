package org.atemsource.atem.utility.clone;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import junit.framework.Assert;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.pojo.EntityA;
import org.atemsource.atem.pojo.EntityB;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@ContextConfiguration(locations = {"classpath:/test/meta/pojo/entitytype.xml",
	"classpath:/meta/utility/clone-example.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class CollectionAttributeCloningBuilderTest
{

	@Inject
	private CloningBuilderFactory cloningBuilderFactory;

	@Inject
	private EntityTypeRepository entityTypeRepository;

	@Test
	public void testComplexSet()
	{
		EntityType<EntityA> entityType = entityTypeRepository.getEntityType(EntityA.class);
		CloningBuilder cloningBuilder = cloningBuilderFactory.create(entityType);
		cloningBuilder.include("aset");
		Cloning cloning = cloningBuilder.create();

		EntityA a = new EntityA();
		Set<EntityB> aset = new HashSet<EntityB>();
		EntityB entityB = new EntityB();
		entityB.setInteger(33);
		aset.add(entityB);
		a.setAset(aset);
		EntityA clone = cloning.clone(a);
		Assert.assertEquals(33, clone.getAset().iterator().next().getInteger());
	}

	@Test
	public void testPrimitiveList()
	{
		EntityType<EntityA> entityType = entityTypeRepository.getEntityType(EntityA.class);
		CloningBuilder cloningBuilder = cloningBuilderFactory.create(entityType);
		cloningBuilder.include("stringList");
		Cloning cloning = cloningBuilder.create();

		EntityA a = new EntityA();
		ArrayList<String> stringList = new ArrayList<String>();
		a.setStringList(stringList);
		stringList.add("hallo");
		EntityA clone = cloning.clone(a);
		Assert.assertEquals("hallo", clone.getStringList().get(0));
	}

}
