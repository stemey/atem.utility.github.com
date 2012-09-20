package org.atemsource.atem.utility.doc.html;

import static org.junit.Assert.*;

import javax.inject.Inject;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.domain.DomainA;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = { "classpath:/test/meta/utility/transform.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class HtmlDocGeneratorTest {

	@Inject
	private EntityTypeRepository entityTypeRepository;
	
	@Test
	public void test() {
		EntityType<DomainA> entityType = entityTypeRepository.getEntityType(DomainA.class);
		String html=new HtmlDocGenerator().generate(entityType);
		System.out.println(html);
	}

}
