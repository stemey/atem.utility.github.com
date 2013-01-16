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

/**
* The AbstractBinder defines the basic configuration properties for binding.
*/
public class AbstractBinder<J> implements org.atemsource.atem.api.type.TypeFilter<J>
{

	protected AttributeNameConverter attributeNameConverter;

	@Inject
	protected BeanLocator beanLocator;

	private Class<BindingSession> bindingSessionClass = BindingSession.class;

	@Inject
	protected DerivationMetaAttributeRegistrar derivationMetaAttributeRegistrar;

	@Inject
	protected EntityTypeRepository entityTypeRepository;

	List<AttributeFilter> filters = new ArrayList<AttributeFilter>();

	protected Logger logger = Logger.getLogger(getClass());

	protected DynamicEntityTypeSubrepository<?> subRepository;

	protected TransformationBuilderFactory transformationBuilderFactory;

	private org.atemsource.atem.api.type.TypeFilter<?> typeFilter;

	public org.atemsource.atem.api.type.TypeFilter<?> getTypeFilter() {
		return typeFilter;
	}

/**
* The TypeFilter defines the types to be bound.
*/
	public void setTypeFilter(org.atemsource.atem.api.type.TypeFilter<?> typeFilter) {
		this.typeFilter = typeFilter;
	}

	public AbstractBinder()
	{
		super();
	}

	public AttributeNameConverter getAttributeNameConverter()
	{
		return attributeNameConverter;
	}

	public Class<? extends BindingSession> getBindingSessionClass()
	{
		return bindingSessionClass;
	}

	@Override
	public Collection<? extends EntityType<J>> getEntityTypes()
	{
		return (Collection<? extends EntityType<J>>) subRepository.getEntityTypes();
	}

	public List<AttributeFilter> getFilters()
	{
		return filters;
	}

	

	public DynamicEntityTypeSubrepository<?> getSubRepository()
	{
		return subRepository;
	}

	public TransformationBuilderFactory getTransformationBuilderFactory()
	{
		return transformationBuilderFactory;
	}

	
/**
* define a startegy to create target attribute names. Optional.
*/
	public void setAttributeNameConverter(AttributeNameConverter attributeNameConverter)
	{
		this.attributeNameConverter = attributeNameConverter;
	}

/**
* 
*/
	public void setBindingSessionClass(Class<BindingSession> bindingSessionClass)
	{
		this.bindingSessionClass = bindingSessionClass;
	}

/**
* set a list of attribute filters. 
*/
	public void setFilters(List<AttributeFilter> filters)
	{
		this.filters = filters;
	}

/**
* define the subrepositories to create the target types in.
*/
	public void setSubRepository(DynamicEntityTypeSubrepository<?> subRepository)
	{
		this.subRepository = subRepository;
	}

/**
* define the transformationBUilderFactory. TODO subRepository in tansformationBUilderFactory should be the same as in AbstractBinder.
*/
	public void setTransformationBuilderFactory(TransformationBuilderFactory transformationBuilderFactory)
	{
		this.transformationBuilderFactory = transformationBuilderFactory;
	}


}
