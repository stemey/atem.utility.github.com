package org.atemsource.atem.utility.transform.impl.version;

public class VersionNumber implements Comparable<VersionNumber>
{

	private int[] versions;

	public VersionNumber(int[] versions)
	{
		super();
		this.versions = versions;
	}

	@Override
	public int compareTo(VersionNumber versionNumber)
	{
		for (int index = 0; index < versions.length; index++)
		{
			int compare = versions[index] - versionNumber.versions[index];
			if (compare != 0)
			{
				return compare;
			}
		}
		return 0;
	}

	public boolean isBetween(VersionNumber from, VersionNumber until)
	{
		if (from == null && until == null)
		{
			return true;
		}
		else if (from == null)
		{
			return until.isGreater(this);
		}
		else if (until == null)
		{
			return !from.isGreater(this);
		}
		else
		{
			return until.isGreater(this) && !from.isGreater(this);
		}
	}

	public boolean isGreater(VersionNumber versionNumber)
	{
		return this.compareTo(versionNumber) > 0;
	}
}
