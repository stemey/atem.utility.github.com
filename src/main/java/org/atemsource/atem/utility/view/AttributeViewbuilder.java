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
package org.atemsource.atem.utility.view;

public class AttributeViewbuilder
{
	private String attribute;

	private ViewBuilder cascadedViewBuilder;

	public AttributeViewbuilder(String attribute)
	{
		super();
		this.attribute = attribute;
	}

	private AttributeViewbuilder(String attribute, ViewBuilder cascadedViewBuilder)
	{
		super();
		this.attribute = attribute;
		this.cascadedViewBuilder = cascadedViewBuilder;
	}

	public String getAttribute()
	{
		return attribute;
	}

	public ViewBuilder getCascadedViewBuilder()
	{
		return cascadedViewBuilder;
	}

	public void setViewBuilder(ViewBuilder childViewBuilder)
	{
		cascadedViewBuilder = childViewBuilder;
	}
}
