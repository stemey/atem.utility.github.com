package org.atemsource.atem.utility.doc.html;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.impl.dynamic.DynamicEntity;
import org.atemsource.atem.impl.dynamic.DynamicEntityImpl;

public class HtmlDocGenerator {
public String generate(EntityType<?> entityType) {
	VelocityEngine engine= new VelocityEngine();
	StringWriter writer = new StringWriter();
	Context context = new VelocityContext();
	
	DynamicEntity e= new DynamicEntityImpl();
	e.put("code","Hallo");
	ArrayList attributes = new ArrayList();
	DynamicEntity a = new DynamicEntityImpl();
	a.put("code","aHallo");
	a.put("targetType","HalloType");
	attributes.add(a);
	attributes.add(attributes);
	e.put("attributes",attributes);
	
	context.put("entityType", e);
	Reader reader = new InputStreamReader(getClass().getResourceAsStream("entityType.vm"));
	engine.evaluate(context, writer, "log", reader);
	return writer.toString();
}
}
