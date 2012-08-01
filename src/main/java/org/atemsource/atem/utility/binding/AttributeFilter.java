package org.atemsource.atem.utility.binding;

import org.atemsource.atem.api.attribute.Attribute;


public interface AttributeFilter
{

	boolean isExcluded(Attribute attribute);

}
