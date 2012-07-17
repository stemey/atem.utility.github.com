package org.atemsource.atem.utility.jackson;

import org.atemsource.atem.utility.transform.api.JavaConverter;
import org.codehaus.jackson.node.ObjectNode;


public class NodeConversion implements JavaConverter<ObjectNode, ObjectNode>
{

	@Override
	public ObjectNode convertAB(ObjectNode a)
	{
		// we should clone
		return a;
	}

	@Override
	public ObjectNode convertBA(ObjectNode b)
	{
		// we should clone
		return b;
	}

}
