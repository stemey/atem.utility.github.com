package org.atemsource.atem.utility.doc.dot;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.PrimitiveType;
import org.atemsource.atem.api.type.Type;


public class DotGenerator
{
	public static void main(String[] args)
	{

	}

	private Set<EntityType<?>> processedTypes = new HashSet<EntityType<?>>();

	private EntityType<?> root;

	public DotGenerator(EntityType<?> root)
	{
		super();
		this.root = root;
	}

	public String create()
	{
		DotBuilder builder = new DotBuilder();
		createDot(builder, root);
		return builder.create();
	}

	public void createDot(DotBuilder builder, EntityType<?> entityType)
	{
		if (processedTypes.contains(entityType))
		{
			return;
		}
		processedTypes.add(entityType);
		NodeBuilder nodeBuilder = builder.createNode(normalize(entityType));
		for (Attribute<?, ?> attribute : entityType.getDeclaredAttributes())
		{
			if (attribute.getTargetType() instanceof PrimitiveType<?>)
			{
				RecordBuilder recordBuilder = nodeBuilder.createRecord();
				recordBuilder.port(attribute.getCode());
				recordBuilder.label(attribute.getCode() + " : " + attribute.getTargetType().getCode());
			}
			else
			{
				if (attribute.getTargetType() == null)
				{
					Logger.getLogger("DOC").error(
						"cannot find target type for attribute " + entityType.getCode() + "::" + attribute.getCode());
				}
				else
				{
					ConnectionBuilder connectionBuilder = nodeBuilder.createConnection();
					connectionBuilder.target(normalize(attribute.getTargetType()));
					connectionBuilder.label(attribute.getCode());
					createDot(builder, (EntityType<?>) attribute.getTargetType());
				}
			}
		}
		EntityType<?> superEntityType = entityType.getSuperEntityType();
		if (superEntityType != null)
		{
			ConnectionBuilder connectionBuilder = nodeBuilder.createConnection();
			connectionBuilder.target(normalize(superEntityType));
			connectionBuilder.label("superType");
			createDot(builder, superEntityType);
		}

	}

	private String normalize(Type<?> type)
	{
		return type.getCode().replace('.', '_').replace('$', '_');
	}
}
