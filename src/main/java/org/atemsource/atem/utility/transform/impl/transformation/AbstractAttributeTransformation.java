/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.transform.impl.transformation;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.path.AttributePath;
import org.atemsource.atem.utility.transform.api.AttributeTransformation;
import org.atemsource.atem.utility.transform.api.Transformation;
import org.atemsource.atem.utility.transform.api.UniTransformation;

public abstract class AbstractAttributeTransformation<A, B> implements
		AttributeTransformation<A, B> {

	private Set<AttributePath> attributeAs;

	private Set<AttributePath> attributeBs;

	public void setAttributeAs(Set<AttributePath> attributeAs) {
		this.attributeAs = attributeAs;
	}

	public void setAttributeBs(Set<AttributePath> attributeBs) {
		this.attributeBs = attributeBs;
	}

	@Inject
	protected EntityTypeRepository entityTypeRepository;

	private Map<String, Object> meta;

	private EntityType typeA;

	private EntityType typeB;

	public AbstractAttributeTransformation() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.atemsource.atem.utility.transform.impl.transformation.
	 * AttributeTransformation#getAttributeA()
	 */
	public Set<AttributePath> getAttributeAs() {
		return attributeAs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.atemsource.atem.utility.transform.impl.transformation.
	 * AttributeTransformation#getAttributeB()
	 */
	public Set<AttributePath> getAttributeBs() {
		return attributeBs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.atemsource.atem.utility.transform.impl.transformation.
	 * AttributeTransformation#getMeta()
	 */
	public Map<String, Object> getMeta() {
		return meta;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.atemsource.atem.utility.transform.impl.transformation.
	 * AttributeTransformation#getTypeA()
	 */
	public EntityType getTypeA() {
		return typeA;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.atemsource.atem.utility.transform.impl.transformation.
	 * AttributeTransformation#getTypeB()
	 */
	public EntityType getTypeB() {
		return typeB;
	}

	public void setMeta(Map<String, Object> meta) {
		this.meta = meta;
	}

	public void setTypeA(EntityType typeA) {
		this.typeA = typeA;
	}

	public void setTypeB(EntityType typeB) {
		this.typeB = typeB;
	}
}
