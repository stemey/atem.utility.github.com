package org.atemsource.atem.utility.jackson.domain;

import java.util.List;

import org.atemsource.atem.api.attribute.annotation.Association;
import org.atemsource.atem.utility.transform.api.annotation.Version;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;


public class DomainA
{
	private DomainB domainB;

	@Association(targetType = DomainB.class)
	private List<DomainB> domainBs;

	@Version(until = "1.1")
	private String field10;

	@Version(from = "1.1")
	private String field11;

	@JsonIgnore
	private String ignoredProperty;

	@JsonProperty("renamed_property")
	private String renamedProperty;

	public DomainB getDomainB()
	{
		return domainB;
	}

	public List<DomainB> getDomainBs()
	{
		return domainBs;
	}

	public String getField10()
	{
		return field10;
	}

	public String getField11()
	{
		return field11;
	}

	public String getIgnoredProperty()
	{
		return ignoredProperty;
	}

	public String getRenamedProperty()
	{
		return renamedProperty;
	}

	public void setDomainB(DomainB domainB)
	{
		this.domainB = domainB;
	}

	public void setDomainBs(List<DomainB> domainBs)
	{
		this.domainBs = domainBs;
	}

	public void setField10(String field10)
	{
		this.field10 = field10;
	}

	public void setField11(String field11)
	{
		this.field11 = field11;
	}

	public void setIgnoredProperty(String ignoredProperty)
	{
		this.ignoredProperty = ignoredProperty;
	}

	public void setRenamedProperty(String renamedProperty)
	{
		this.renamedProperty = renamedProperty;
	}
}
