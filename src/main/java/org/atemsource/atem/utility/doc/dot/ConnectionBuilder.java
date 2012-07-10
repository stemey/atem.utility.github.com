package org.atemsource.atem.utility.doc.dot;

public class ConnectionBuilder
{
	private String arrowType;

	private String label;

	private String source;

	private String target;

	public void arrowType(String arrowType)
	{
		this.arrowType = arrowType;
	}

	public void create(StringBuilder builder)
	{
		builder.append(source);
		builder.append(" -> ");
		builder.append(target);
		builder.append(" [label=\"");
		builder.append(label);
		builder.append("\"");
		if (arrowType != null)
		{
			builder.append(",arrowhead=\"");
			builder.append(arrowType);
			builder.append("\"");
		}
		builder.append("]\r\n");
	}

	public void label(String label)
	{
		this.label = label;
	}

	public void source(String source)
	{
		this.source = source;
	}

	public void target(String target)
	{
		this.target = target;

	}

}
