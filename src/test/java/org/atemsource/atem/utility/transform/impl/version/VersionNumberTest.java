package org.atemsource.atem.utility.transform.impl.version;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


public class VersionNumberTest
{

	private VersionNumber v10;

	private VersionNumber v11;

	private VersionNumber v12;

	private VersionResolver versionResolver;

	@Before
	public void setup()
	{
		this.versionResolver = new VersionResolver(2, "\\.");
		v12 = versionResolver.create("1.2");
		v11 = versionResolver.create("1.1");
		v10 = versionResolver.create("1.0");
	}

	@Test
	public void testBetween()
	{
		Assert.assertTrue(v11.isBetween(v10, v12));
		Assert.assertFalse(v12.isBetween(v10, v11));
		Assert.assertFalse(v10.isBetween(v11, v12));
		Assert.assertTrue(v10.isBetween(null, v12));
		Assert.assertTrue(v12.isBetween(v11, null));
		Assert.assertTrue(v12.isBetween(null, null));

	}

	@Test
	public void testIsGreater()
	{
		Assert.assertTrue(v11.isGreater(v10));

	}

}
