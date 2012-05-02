/*******************************************************************************
 * Stefan Meyer, 2012
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.transform.impl;


import java.util.ArrayList;
import java.util.List;

import org.atemsource.atem.utility.transform.api.Transformation;


public class AbstractCompositeTransformation<A, B>
{

	private List<Transformation<A, B>> embeddedTransformations = new ArrayList<Transformation<A, B>>();

	public AbstractCompositeTransformation()
	{
		super();
	}

	public void addTransformation(Transformation<A, B> transformation)
	{
		this.embeddedTransformations.add(transformation);
	}

	protected void transformABChildren(A valueA, B valueB)
	{
		for (Transformation<A, B> transformation : embeddedTransformations)
		{
			transformation.getAB().merge(valueA, valueB, null);
		}
	}

	protected void transformBAChildren(B valueB, A valueA)
	{
		for (Transformation<A, B> transformation : embeddedTransformations)
		{
			transformation.getBA().merge(valueB, valueA, null);
		}
	}

}
