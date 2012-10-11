package org.atemsource.atem.utility.binding.jackson;

import org.atemsource.atem.utility.transform.api.JavaConverter;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.codehaus.jackson.node.ObjectNode;


public class NodeConversion implements JavaConverter<ObjectNode, ObjectNode>
{

	@Override
	public ObjectNode convertAB(ObjectNode a, TransformationContext ctx)
	{
		// we should clone
		return a;
	}

	@Override
	public ObjectNode convertBA(ObjectNode b, TransformationContext ctx)
	{
		// we should clone
		return b;
	}

}
