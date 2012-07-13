/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.compare;

import org.atemsource.atem.utility.path.AttributePath;


public class AttributeChange extends Difference
{
	private Object newValue;

	private Object oldValue;

	AttributeChange(AttributePath path, Object oldValue, Object newValue)
	{
		super();
		this.setPath(path);
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public Object getNewValue()
	{
		return newValue;
	}

	public Object getOldValue()
	{
		return oldValue;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("changed ");
		builder.append(oldValue);
		builder.append(" to ");
		builder.append(newValue);
		builder.append(" in ");
		builder.append(this.getPath());
		return builder.toString();

	}
}
