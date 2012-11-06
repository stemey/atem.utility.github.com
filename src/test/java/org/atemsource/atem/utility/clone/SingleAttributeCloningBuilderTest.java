package org.atemsource.atem.utility.clone;

import javax.inject.Inject;
import junit.framework.Assert;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.pojo.EntityA;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@ContextConfiguration(locations = {"classpath:/test/meta/pojo/entitytype.xml",
	"classpath:/meta/utility/clone-example.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class SingleAttributeCloningBuilderTest
{

	@Inject
	private CloningBuilderFactory cloningBuilderFactory;

	@Inject
	private EntityTypeRepository entityTypeRepository;

	@Test
	public void test()
	{
		EntityType<EntityA> entityType = entityTypeRepository.getEntityType(EntityA.class);
		CloningBuilder cloningBuilder = cloningBuilderFactory.create(entityType);
		cloningBuilder.include("longO");
		Cloning cloning = cloningBuilder.create();

		EntityA a = new EntityA();
		a.setLongO(1000L);
		EntityA clone = cloning.clone(a);
		Assert.assertEquals(1000L, clone.getLongO().longValue());
	}
}
