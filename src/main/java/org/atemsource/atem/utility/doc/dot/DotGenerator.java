package org.atemsource.atem.utility.doc.dot;

import java.util.HashMap;
import java.util.Map;
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

	private String basePackage;

	private Map<EntityType<?>, NodeBuilder> processedTypes = new HashMap<EntityType<?>, NodeBuilder>();

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

	public NodeBuilder createDot(DotBuilder builder, EntityType<?> entityType)
	{
		if (processedTypes.containsKey(entityType))
		{
			return processedTypes.get(entityType);
		}
		NodeBuilder nodeBuilder = builder.createNode(normalizeId(entityType), getLabel(entityType));
		processedTypes.put(entityType, nodeBuilder);
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
					connectionBuilder.target(normalizeId(attribute.getTargetType()));
					connectionBuilder.label(attribute.getCode());
					createDot(builder, (EntityType<?>) attribute.getTargetType());
				}
			}
		}
		EntityType<?> superEntityType = entityType.getSuperEntityType();
		if (superEntityType != null)
		{
			NodeBuilder parent = createDot(builder, superEntityType);
			if (parent != null)
			{
				ConnectionBuilder connectionBuilder = nodeBuilder.createConnection();
				connectionBuilder.target(normalizeId(superEntityType));
				connectionBuilder.label("extends");
				connectionBuilder.arrowType("empty");
			}
		}
		Set<EntityType> subEntityTypes = entityType.getSubEntityTypes(true);
		if (subEntityTypes != null)
		{
			for (EntityType subtype : subEntityTypes)
			{
				if (subtype != entityType)
				{
					NodeBuilder subTypeBuilder = createDot(builder, subtype);
					ConnectionBuilder connectionBuilder = subTypeBuilder.createConnection();
					connectionBuilder.target(normalizeId(subtype));
					connectionBuilder.label("extends");
					connectionBuilder.arrowType("empty");
					connectionBuilder.constraint(true);
				}
			}
		}
		return nodeBuilder;

	}

	public void exclude(EntityType<?> type)
	{
		processedTypes.put(type, null);
	}

	private String getLabel(Type<?> type)
	{
		String name = type.getCode();
		if (basePackage != null && name.startsWith(basePackage))
		{
			return name.substring(basePackage.length() + 1);
		}
		else
		{
			return name;
		}
	}

	private String normalizeId(Type<?> type)
	{
		return type.getCode().replace('.', '_').replace('$', '_').replace(':', '_');
	}

	public void setBasePackage(String basePackage)
	{
		this.basePackage = basePackage;
	}
}
