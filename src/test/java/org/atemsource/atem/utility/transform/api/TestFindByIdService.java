package org.atemsource.atem.utility.transform.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.service.FindByIdService;
import org.atemsource.atem.api.service.IdentityAttributeService;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.impl.common.service.AttributeIdentityService;

public class TestFindByIdService implements FindByIdService, IdentityAttributeService {
	private Map<Serializable, Object> entities = new HashMap<Serializable, Object>();
	private String attributeCode;

	public String getAttributeCode() {
		return attributeCode;
	}

	public void setAttributeCode(String attributeCode) {
		this.attributeCode = attributeCode;
	}

	@Override
	public <E> E findById(EntityType<E> entityType, Serializable id) {
		return (E) entities.get(id);
	}

	public void addEntity(Serializable id, Object entity) {
		this.entities.put(id, entity);
	}

	@Override
	public <E> Serializable getId(EntityType<E> entityType, E entity) {
		return getIdAttribute(entityType).getValue(entity);
	}

	@Override
	public Type<?> getIdType(EntityType<?> entityType) {
		return getIdAttribute(entityType).getTargetType();
	}

	@Override
	public SingleAttribute<? extends Serializable> getIdAttribute(EntityType<?> entityType) {
		return (SingleAttribute<? extends Serializable>) entityType.getAttribute(attributeCode);
	}

}
