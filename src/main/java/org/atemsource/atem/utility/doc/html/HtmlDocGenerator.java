package org.atemsource.atem.utility.doc.html;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.inject.Inject;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.impl.dynamic.DynamicEntity;
import org.atemsource.atem.impl.dynamic.DynamicEntityImpl;
import org.atemsource.atem.utility.transform.api.SimpleTransformationContext;
import org.springframework.stereotype.Component;

@Component
public class HtmlDocGenerator {
	@Inject
	private DocBuilder docBuilder;

	public String generate(EntityType<?> entityType) {
		VelocityEngine engine = new VelocityEngine();
		String directory=new File(getClass().getResource("entityType.vm").getFile()).getParent();
		engine.addProperty("file.resource.loader.path", directory);

		
		StringWriter writer = new StringWriter();
		Context context = new VelocityContext();
		Object entityTypeDocument = docBuilder.getEntityTypeTransformation()
				.getAB().convert(entityType, new SimpleTransformationContext());

		context.put("entityType", entityTypeDocument);
		Reader reader = new InputStreamReader(getClass().getResourceAsStream(
				"entityType.vm"));
		engine.evaluate(context, writer, "log", reader);
		return writer.toString();
	}
}
