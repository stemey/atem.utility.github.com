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

	private TypeFilter typeFilter;

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

	protected Collection<EntityType<?>> getSourceTypes()
	{
		Collection<EntityType<?>> types = entityTypeRepository.getEntityTypes();
		List<EntityType<?>> filteredList = new ArrayList<EntityType<?>>();
		for (EntityType<?> entityType : types)
		{
			if (!typeFilter.isExcluded(entityType))
			{
				filteredList.add(entityType);
			}
		}
		return filteredList;
	}

	public DynamicEntityTypeSubrepository<?> getSubRepository()
	{
		return subRepository;
	}

	public TransformationBuilderFactory getTransformationBuilderFactory()
	{
		return transformationBuilderFactory;
	}

	public TypeFilter getTypeFilter()
	{
		return typeFilter;
	}

	public void setAttributeNameConverter(AttributeNameConverter attributeNameConverter)
	{
		this.attributeNameConverter = attributeNameConverter;
	}

	public void setBindingSessionClass(Class<BindingSession> bindingSessionClass)
	{
		this.bindingSessionClass = bindingSessionClass;
	}

	public void setFilters(List<AttributeFilter> filters)
	{
		this.filters = filters;
	}

	public void setSubRepository(DynamicEntityTypeSubrepository<?> subRepository)
	{
		this.subRepository = subRepository;
	}

	public void setTransformationBuilderFactory(TransformationBuilderFactory transformationBuilderFactory)
	{
		this.transformationBuilderFactory = transformationBuilderFactory;
	}

	public void setTypeFilter(TypeFilter typeFilter)
	{
		this.typeFilter = typeFilter;
	}

}