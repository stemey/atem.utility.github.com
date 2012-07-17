/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.compare;

import org.atemsource.atem.utility.path.AttributePath;


public class Rearrangement extends Difference
{

	private int newIndex;

	private AttributePath newPath;

	private int oldIndex;

	private AttributePath oldPath;

	private Object value;

	public int getNewIndex()
	{
		return newIndex;
	}

	public AttributePath getNewPath()
	{
		return newPath;
	}

	public int getOldIndex()
	{
		return oldIndex;
	}

	public AttributePath getOldPath()
	{
		return oldPath;
	}

	public Object getValue()
	{
		return value;
	}

	public void setNewIndex(int newIndex)
	{
		this.newIndex = newIndex;
	}

	public void setNewPath(AttributePath newPath)
	{
		this.newPath = newPath;
	}

	public void setOldIndex(int oldIndex)
	{
		this.oldIndex = oldIndex;
	}

	public void setOldPath(AttributePath oldPath)
	{
		this.oldPath = oldPath;
	}

	public void setValue(Object value)
	{
		this.value = value;
	}

	@Override
	public String toString()
	{
		return "moved " + getPath() + " from index " + oldIndex + " to " + newIndex;
	}
}
