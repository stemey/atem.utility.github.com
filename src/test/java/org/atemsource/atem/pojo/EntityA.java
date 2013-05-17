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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.atemsource.atem.api.attribute.annotation.Association;
import org.atemsource.atem.api.attribute.annotation.MapAssociation;


public class EntityA implements Serializable
{
	private Boolean booleanO;

	private boolean booleanP;

	private EntityB entityB;

	private Integer intO;
	
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private int intP;

	@Association(targetType = EntityB.class)
	private List<EntityB> list = new ArrayList<EntityB>();

	@Association(targetType = String.class)
	private List<String> stringList = new ArrayList<String>();

	@Association(targetType = EntityB.class)
	private Set<EntityB> aset = new HashSet<EntityB>();

	@MapAssociation(keyType = String.class, targetType = EntityB.class)
	private Map<String, EntityB> map = new HashMap<String, EntityB>();

	@MapAssociation(keyType = String.class, targetType = Integer.class)
	private Map<String, Integer> string2int = new HashMap<String, Integer>();

	private Long longO;

	private long longP;

	private Number number;

	private Object[] objectArray;

	private List objectList;

	private String string;

	private String[] stringArray;

	public Set<EntityB> getAset()
	{
		return aset;
	}

	public Boolean getBooleanO()
	{
		return booleanO;
	}

	public EntityB getEntityB()
	{
		return entityB;
	}

	public Integer getIntO()
	{
		return intO;
	}

	public int getIntP()
	{
		return intP;
	}

	public List<EntityB> getList()
	{
		return list;
	}

	public Long getLongO()
	{
		return longO;
	}

	public long getLongP()
	{
		return longP;
	}

	public Map<String, EntityB> getMap()
	{
		return map;
	}

	public Number getNumber()
	{
		return number;
	}

	public Object[] getObjectArray()
	{
		return objectArray;
	}

	public List getObjectList()
	{
		return objectList;
	}

	public String getString()
	{
		return string;
	}

	public Map<String, Integer> getString2int()
	{
		return string2int;
	}

	public String[] getStringArray()
	{
		return stringArray;
	}

	public List<String> getStringList()
	{
		return stringList;
	}

	public boolean isBooleanP()
	{
		return booleanP;
	}

	public void setAset(Set<EntityB> set)
	{
		this.aset = set;
	}

	public void setBooleanO(Boolean booleanO)
	{
		this.booleanO = booleanO;
	}

	public void setBooleanP(boolean booleanP)
	{
		this.booleanP = booleanP;
	}

	public void setEntityB(EntityB entityB)
	{
		this.entityB = entityB;
	}

	public void setIntO(Integer intO)
	{
		this.intO = intO;
	}

	public void setIntP(int intP)
	{
		this.intP = intP;
	}

	public void setList(List<EntityB> list)
	{
		this.list = list;
	}

	public void setLongO(Long longO)
	{
		this.longO = longO;
	}

	public void setLongP(long longP)
	{
		this.longP = longP;
	}

	public void setMap(Map<String, EntityB> map)
	{
		this.map = map;
	}

	public void setNumber(Number number)
	{
		this.number = number;
	}

	public void setObjectArray(Object[] objectArray)
	{
		this.objectArray = objectArray;
	}

	public void setObjectList(List objectList)
	{
		this.objectList = objectList;
	}

	public void setString(String string)
	{
		this.string = string;
	}

	public void setString2int(Map<String, Integer> string2int)
	{
		this.string2int = string2int;
	}

	public void setStringArray(String[] stringArray)
	{
		this.stringArray = stringArray;
	}

	public void setStringList(List<String> stringList)
	{
		this.stringList = stringList;
	}
}
