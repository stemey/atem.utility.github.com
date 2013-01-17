/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.transform.impl.converter;

import org.atemsource.atem.utility.transform.api.JavaConverter;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.api.constraint.PossibleValues;

/**
* Converts an enum into a string. It will attach all possible string values as a meta attribute to the target attribute. 
*/
public class EnumToStringConverter implements JavaConverter<Enum<?>, String>, Constraining
{

	private final Class<? extends Enum> enumClass;

	private final StringPossibleValues possibleValues;

	public EnumToStringConverter(Class<? extends Enum> enumClass)
	{
		this.enumClass = enumClass;
		String[] values = new String[enumClass.getEnumConstants().length];
		for (int i = 0; i < enumClass.getEnumConstants().length; i++)
		{
			values[i] = convertAB(enumClass.getEnumConstants()[i], null);
		}
		this.possibleValues = new StringPossibleValues(values);
	}

	@Override
	public String convertAB(Enum<?> a, TransformationContext ctx)
	{
		if (a == null)
		{
			return null;
		}
		else
		{
			return a.name();
		}
	}

	@Override
	public Enum<?> convertBA(String b, TransformationContext ctx)
	{
		if (b == null || b.isEmpty())
		{
			return null;
		}
		else
		{
			return Enum.valueOf(enumClass, b);
		}
	}

	@Override
	public Object getConstraintAB(String name)
	{
		return possibleValues;
	}

	@Override
	public Object getConstraintBA(String name)
	{
		return null;
	}

	@Override
	public String[] getConstraintNamesAB()
	{
		return new String[]{PossibleValues.META_ATTRIBUTE_CODE};
	}

	@Override
	public String[] getConstraintNamesBA()
	{
		return new String[]{};
	}

}
