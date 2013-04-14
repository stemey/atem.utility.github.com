package org.atemsource.atem.utility.transform.api;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.EntityType;
import org.codehaus.jackson.node.ObjectNode;

/**
 * If the type information is missing from the json this ctx will behave
 * properly.
 * 
 * @author stemey
 * 
 */
public class JacksonTransformationContext extends SimpleTransformationContext {

	private EntityType<ObjectNode> genericType;

	public JacksonTransformationContext(EntityTypeRepository entityTypeRepository) {
		super(entityTypeRepository);
		genericType = entityTypeRepository.getEntityType(ObjectNode.class);
	}

	/**
	 * if json type information is missing then this will return null.
	 */
	@Override
	public <J> EntityType<J> getEntityTypeByA(J entity) {
		EntityType<J> entityTypeByA = super.getEntityTypeByA(entity);
		if (entityTypeByA == genericType) {
			return null;
		} else {
			return entityTypeByA;
		}
	}

	/**
	 * if json type information is missing then this will return null.
	 */
	@Override
	public <J> EntityType<J> getEntityTypeByB(J entity) {
		EntityType<J> entityTypeByB = super.getEntityTypeByB(entity);
		if (entityTypeByB == genericType) {
			return null;
		} else {
			return entityTypeByB;
		}
	}

}
