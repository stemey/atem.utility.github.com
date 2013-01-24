package org.atemsource.atem.utility.common;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.view.View;
import org.atemsource.atem.api.view.ViewVisitor;
import org.atemsource.atem.api.view.Visitor;


public class TargetTypeExistenceAsserter implements ViewVisitor<Object>
{
	@Override
	public void visit(Object context, Attribute attribute)
	{
		if (attribute.getTargetType() == null)
		{
			throw new IllegalStateException("attribute " + attribute.getEntityType().getCode() + "." + attribute.getCode()
				+ " does not have a target type");
		}
	}

	@Override
	public void visit(Object context, Attribute attribute, Visitor<Object> targetTypeVisitor)
	{
		if (attribute.getTargetType() == null)
		{
			throw new IllegalStateException("attribute " + attribute.getEntityType().getCode() + "." + attribute.getCode()
				+ " does not have a target type");
		}
		else
		{
			targetTypeVisitor.visit(context);
		}
	}

	@Override
	public boolean visitSubView(Object context, View view)
	{
		return true;
	}

	@Override
	public boolean visitSuperView(Object context, View view)
	{
		return true;
	}
}