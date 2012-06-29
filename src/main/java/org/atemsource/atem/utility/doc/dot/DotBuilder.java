package org.atemsource.atem.utility.doc.dot;

import java.util.HashSet;
import java.util.Set;


public class DotBuilder
{

	private Set<NodeBuilder> nodes = new HashSet<NodeBuilder>();

	public String create()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("digraph{\r\n");
		Set<ConnectionBuilder> connections = new HashSet<ConnectionBuilder>();
		for (NodeBuilder node : nodes)
		{
			node.create(builder);
			connections.addAll(node.getConnections());
		}
		for (ConnectionBuilder connection : connections)
		{
			connection.create(builder);
		}
		builder.append("}");
		return builder.toString();
	}

	public NodeBuilder createNode(String id)
	{
		NodeBuilder nodeBuilder = new NodeBuilder(id);
		nodes.add(nodeBuilder);
		return nodeBuilder;
	}

}
