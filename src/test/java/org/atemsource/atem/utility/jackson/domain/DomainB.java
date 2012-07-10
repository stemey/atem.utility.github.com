package org.atemsource.atem.utility.jackson.domain;

public class DomainB
{
	private boolean bField;

	private DomainA reverse;

	public DomainA getReverse()
	{
		return reverse;
	}

	public boolean isbField()
	{
		return bField;
	}

	public void setbField(boolean bField)
	{
		this.bField = bField;
	}

	public void setReverse(DomainA reverse)
	{
		this.reverse = reverse;
	}
}
