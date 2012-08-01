package org.atemsource.atem.utility.binding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.spi.DynamicEntityTypeSubrepository;
import org.atemsource.atem.utility.transform.api.AttributeNameConverter;
import org.atemsource.atem.utility.transform.api.TransformationBuilderFactory;
import org.atemsource.atem.utility.transform.impl.DerivationMetaAttributeRegistrar;

public class AbstractBinder {

	@Inject
	protected EntityTypeRepository entityTypeRepository;

	public List<AttributeFilter> getFilters() {
		return filters;
	}
	
	@Inject
	protected DerivationMetaAttributeRegistrar derivationMetaAttributeRegistrar;

	public void setFilters(List<AttributeFilter> filters) {
		this.filters = filters;
	}

	private Class<BindingSession> bindingSessionClass= BindingSession.class;

	public Class<BindingSession> getBindingSessionClass() {
		return bindingSessionClass;
	}

	protected Collection<EntityType<?>> getSourceTypes() {
		Collection<EntityType<?>> types = entityTypeRepository.getEntityTypes();
		List<EntityType<?>> filteredList = new ArrayList<EntityType<?>>();
		for (EntityType<?> entityType : types) {
			if (!typeFilter.isExcluded(entityType)) {
				filteredList.add(entityType);
			}
		}
		return filteredList;
	}

	public TypeFilter getTypeFilter() {
		return typeFilter;
	}

	public void setTypeFilter(TypeFilter typeFilter) {
		this.typeFilter = typeFilter;
	}

	private TypeFilter typeFilter;

	public void setBindingSessionClass(Class<BindingSession> bindingSessionClass) {
		this.bindingSessionClass = bindingSessionClass;
	}

	public AttributeNameConverter getAttributeNameConverter() {
		return attributeNameConverter;
	}

	public void setAttributeNameConverter(
			AttributeNameConverter attributeNameConverter) {
		this.attributeNameConverter = attributeNameConverter;
	}

	public DynamicEntityTypeSubrepository<?> getSubRepository() {
		return subRepository;
	}

	public void setSubRepository(DynamicEntityTypeSubrepository<?> subRepository) {
		this.subRepository = subRepository;
	}

	public TransformationBuilderFactory getTransformationBuilderFactory() {
		return transformationBuilderFactory;
	}

	public void setTransformationBuilderFactory(
			TransformationBuilderFactory transformationBuilderFactory) {
		this.transformationBuilderFactory = transformationBuilderFactory;
	}

	protected Logger logger = Logger.getLogger(getClass());
	List<AttributeFilter> filters = new ArrayList<AttributeFilter>();
	protected AttributeNameConverter attributeNameConverter;
	@Inject
	protected BeanLocator beanLocator;
	protected DynamicEntityTypeSubrepository<?> subRepository;
	protected TransformationBuilderFactory transformationBuilderFactory;

	public AbstractBinder() {
		super();
	}

}