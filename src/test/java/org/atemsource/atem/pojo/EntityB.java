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
package org.atemsource.atem.pojo;

import java.io.Serializable;

import org.atemsource.atem.annotation.TestAnnotation;
import org.atemsource.atem.utility.domain.Vehicle;
import org.atemsource.atem.utility.transform.api.annotation.Conversion;

public class EntityB implements Serializable {
	@TestAnnotation
	private int integer;

	private String id;

	public String getId() {
		return id;
	}

	private Object object;

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public void setId(String id) {
		this.id = id;
	}

	private EntityA singleA;

	public int getInteger() {
		return integer;
	}

	public EntityA getSingleA() {
		return singleA;
	}

	public void setInteger(int integer) {
		this.integer = integer;
	}

	public void setSingleA(EntityA singleA) {
		this.singleA = singleA;
	}
}
