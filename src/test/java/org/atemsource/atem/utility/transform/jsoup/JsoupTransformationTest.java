package org.atemsource.atem.utility.transform.jsoup;

import java.io.IOException;

import javax.annotation.Resource;
import javax.inject.Inject;

import junit.framework.Assert;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.path.AttributePath;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.impl.dynamic.DynamicEntity;
import org.atemsource.atem.impl.dynamic.DynamicEntityImpl;
import org.atemsource.atem.spi.DynamicEntityTypeSubrepository;
import org.atemsource.atem.utility.path.AttributePathBuilderFactory;
import org.atemsource.atem.utility.transform.api.SimpleTransformationContext;
import org.atemsource.atem.utility.transform.api.TransformationBuilderFactory;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.transform.impl.converter.StringToLongConverter;
import org.codehaus.plexus.util.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = {"classpath:/test/meta/utility/transform-jsoup.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class JsoupTransformationTest {

	@Inject
	private TransformationBuilderFactory transformationBuilderFactory;
	
	@Inject
	private EntityTypeRepository entityTypeRepository;
	
	
	@Resource(name="atem-dynamicEntity-repository")
	private DynamicEntityTypeSubrepository dynamicEntityRepository;
	
	@Inject
	private AttributePathBuilderFactory attributePathBuilderFactory;
	
	 
	@Test
	public void test() throws IOException {
		EntityType<Element> entityType = entityTypeRepository.getEntityType(Element.class);
//		String html=FileUtils.fileRead("/test/meta/utility/test.html");
		Document document = Jsoup.parse(getClass().getResourceAsStream("/test/meta/utility/test.html"),"UTF-8","");
		
		
		TypeTransformationBuilder<Element, ?> rowBuilder = transformationBuilderFactory.create(entityType, dynamicEntityRepository.createBuilder("row"));
		rowBuilder.transform().from("1:text():td:eq(0)").to("name");
		rowBuilder.transform().from("1:text():td:eq(1)").to("height").convert(new StringToLongConverter());
		EntityTypeTransformation<Element, ?> rowTransformation = rowBuilder.buildTypeTransformation();
		
		TypeTransformationBuilder<Element, DynamicEntity> tableBuilder = (TypeTransformationBuilder<Element, DynamicEntity>) transformationBuilderFactory.create(entityType, dynamicEntityRepository.createBuilder("table"));
		tableBuilder.transformCollection().from("*::tr").to("people").convert(rowTransformation);
		EntityTypeTransformation<Element, DynamicEntity> tableTransformation = tableBuilder.buildTypeTransformation();
		
		DynamicEntity html = tableTransformation.getAB().convert(document, new SimpleTransformationContext(entityTypeRepository));
		
		AttributePath path = attributePathBuilderFactory.create("people.0.name", (EntityType<?>) tableTransformation.getTypeB());
		AttributePath path2 = attributePathBuilderFactory.create("people.1.height", (EntityType<?>) tableTransformation.getTypeB());
		
		Assert.assertEquals("Willi",path.getAttribute().getValue(html));
		Assert.assertEquals(500L,path2.getAttribute().getValue(html));
		
	}
}
