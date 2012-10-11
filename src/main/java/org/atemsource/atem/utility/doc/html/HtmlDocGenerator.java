package org.atemsource.atem.utility.doc.html;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.TypeFilter;
import org.atemsource.atem.utility.transform.api.SimpleTransformationContext;


public class HtmlDocGenerator
{

	private DocBuilder docBuilder;

	@Inject
	private EntityTypeRepository entityTypeRepository;

	private TypeCodeToUrlConverter typeCodeToUrlConverter;

	private TypeFilter<?> typeFilter;

	public String generate(EntityType<?> entityType)
	{
		StringWriter writer = new StringWriter();
		generate(entityType, writer);
		return writer.toString();
	}

	public void generate(EntityType<?> entityType, Writer writer)
	{
		VelocityEngine engine = new VelocityEngine();
		// String directory = new File(getClass().getResource("entitytype.vm").getFile()).getParent();
		// engine.addProperty("file.resource.loader.path", directory);
		engine.addProperty("class.resource.loader.class",
			"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		engine.addProperty("resource.loader", "class");

		Context context = new VelocityContext();
		Object entityTypeDocument =
			docBuilder.getEntityTypeTransformation().getAB()
				.convert(entityType, new SimpleTransformationContext(entityTypeRepository));

		context.put("entityType", entityTypeDocument);
		InputStream resourceAsStream = getClass().getResourceAsStream("entitytype.vm");
		Reader reader = new InputStreamReader(resourceAsStream);
		engine.evaluate(context, writer, "log", reader);
	}

	public void generate(File baseDir) throws IOException
	{
		for (EntityType<?> entityType : typeFilter.getEntityTypes())
		{
			File file = new File(baseDir, typeCodeToUrlConverter.getUrl(entityType.getCode()));
			file.getParentFile().mkdirs();
			FileWriter writer = new FileWriter(file);
			try
			{
				generate(entityType, writer);
				writer.flush();
			}
			finally
			{
				writer.close();
			}
		}
	}

	public DocBuilder getDocBuilder()
	{
		return docBuilder;
	}

	public TypeCodeToUrlConverter getTypeCodeToUrlConverter()
	{
		return typeCodeToUrlConverter;
	}

	public TypeFilter getTypeFilter()
	{
		return typeFilter;
	}

	@PostConstruct
	public void initialize()
	{
		docBuilder.setTypeCodeToUrlConverter(typeCodeToUrlConverter);
	}

	public void setDocBuilder(DocBuilder docBuilder)
	{
		this.docBuilder = docBuilder;
	}

	public void setTypeCodeToUrlConverter(TypeCodeToUrlConverter typeCodeToUrlConverter)
	{
		this.typeCodeToUrlConverter = typeCodeToUrlConverter;
	}

	public void setTypeFilter(TypeFilter typeFilter)
	{
		this.typeFilter = typeFilter;
	}
}
