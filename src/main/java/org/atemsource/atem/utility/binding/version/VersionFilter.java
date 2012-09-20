package org.atemsource.atem.utility.binding.version;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.JavaMetaData;
import org.atemsource.atem.utility.binding.AttributeFilter;
import org.atemsource.atem.utility.transform.api.annotation.Version;
import org.atemsource.atem.utility.transform.impl.version.VersionNumber;
import org.atemsource.atem.utility.transform.impl.version.VersionResolver;


public class VersionFilter implements AttributeFilter
{

	private final VersionNumber versionNumber;

	private final VersionResolver versionResolver;

	public VersionFilter(String version, VersionResolver versionResolver)
	{
		super();
		this.versionNumber = versionResolver.create(version);
		this.versionResolver = versionResolver;
	}

	public VersionFilter(VersionNumber versionNumber, VersionResolver versionResolver)
	{
		super();
		this.versionNumber = versionNumber;
		this.versionResolver = versionResolver;
	}

	@Override
	public boolean isExcluded(Attribute attribute)
	{
		if (attribute instanceof JavaMetaData)
		{
			JavaMetaData javaAttribute = (JavaMetaData) attribute;
			Version version = javaAttribute.getAnnotation(Version.class);
			if (version == null)
			{
				return false;
			}
			else
			{
				VersionNumber from = versionResolver.create(version.from());
				VersionNumber until = versionResolver.create(version.until());
				boolean exclude = !versionNumber.isBetween(from, until);
				return exclude;
			}
		}
		else
		{
			return false;
		}
	}

}
