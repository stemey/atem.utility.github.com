package org.atemsource.atem.utility.clone;

import java.util.HashMap;
import java.util.Map;
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
public class MapAttributeCloningBuilderTest
{

	@Inject
	private CloningBuilderFactory cloningBuilderFactory;

	@Inject
	private EntityTypeRepository entityTypeRepository;

	@Test
	public void testComplexMap()
	{
		EntityType<EntityA> entityType = entityTypeRepository.getEntityType(EntityA.class);
		CloningBuilder cloningBuilder = cloningBuilderFactory.create(entityType);
		cloningBuilder.include("map");
		Cloning cloning = cloningBuilder.create();

		EntityA a = new EntityA();
		Map<String, EntityB> map = new HashMap<String, EntityB>();
		EntityB entityB = new EntityB();
		entityB.setInteger(33);
		map.put("hallo", entityB);
		a.setMap(map);
		EntityA clone = cloning.clone(a);
		Assert.assertEquals(33, clone.getMap().get("hallo").getInteger());
	}

}
