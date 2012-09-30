package org.atemsource.atem.utility.doc.html;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.inject.Inject;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.domain.DomainA;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = { "classpath:/test/meta/utility/doc-html.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class HtmlDocGeneratorTest {

	@Inject
	private EntityTypeRepository entityTypeRepository;

	@Inject
	private HtmlDocGenerator htmlDocGenerator;

	@Test
	public void test() {
		EntityType<DomainA> entityType = entityTypeRepository
				.getEntityType(DomainA.class);
		String html = htmlDocGenerator.generate(entityType);
		System.out.println(html);
	}

	@Test
	public void testFiles() throws IOException {
		URL resource = getClass().getResource("generated");
		File baseDir = new File(resource.getFile());
		baseDir.mkdirs();
		htmlDocGenerator.generate(baseDir);
	}

}
