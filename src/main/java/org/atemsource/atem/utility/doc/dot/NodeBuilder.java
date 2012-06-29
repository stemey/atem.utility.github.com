package org.atemsource.atem.utility.doc.dot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class NodeBuilder
{

	private Set<ConnectionBuilder> connections = new HashSet<ConnectionBuilder>();

	private String id;

	private List<RecordBuilder> records = new ArrayList<RecordBuilder>();

	public NodeBuilder(String id)
	{
		this.id = id;
	}

	public void create(StringBuilder builder)
	{
		builder.append(id);
		builder.append(" [label=\"{");
		builder.append(id);
		for (int index = 0; index < records.size(); index++)
		{
			if (index < records.size() - 1)
			{
				builder.append("|");
				records.get(index).create(builder);
			}
		}
		builder.append("}\"]\r\n");
	}

	public ConnectionBuilder createConnection()
	{
		ConnectionBuilder connectionBuilder = new ConnectionBuilder();
		connectionBuilder.source(id);
		connections.add(connectionBuilder);
		return connectionBuilder;
	}

	public RecordBuilder createRecord()
	{
		RecordBuilder recordBuilder = new RecordBuilder();
		records.add(recordBuilder);
		return recordBuilder;
	}

	public Set<ConnectionBuilder> getConnections()
	{
		return connections;
	}

}
