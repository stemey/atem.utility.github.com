package org.atemsource.atem.utility.jackson;

import java.util.Comparator;


public class ClassHierachyComparator implements Comparator<Class<?>>
{

	@Override
	public int compare(Class<?> class1, Class<?> class2)
	{
		if (class1.equals(class2))
		{
			return 0;
		}
		else if (class1.isAssignableFrom(class2))
		{
			return -1;
		}
		else if (class2.isAssignableFrom(class1))
		{
			return 1;
		}
		else
		{
			return class1.getName().compareTo(class2.getName());
		}
	}

}
