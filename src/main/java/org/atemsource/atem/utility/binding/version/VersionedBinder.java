package org.atemsource.atem.utility.binding.version;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.impl.json.JsonEntityTypeImpl;
import org.atemsource.atem.impl.meta.DerivedObject;
import org.atemsource.atem.utility.binding.AbstractBinder;
import org.atemsource.atem.utility.binding.AttributeFilter;
import org.atemsource.atem.utility.binding.BindingListener;
import org.atemsource.atem.utility.binding.BindingSession;
import org.atemsource.atem.utility.binding.jackson.JacksonAttributeNameConverter;
import org.atemsource.atem.utility.transform.api.TypeNameConverter;
import org.atemsource.atem.utility.transform.api.meta.Binding;
import org.atemsource.atem.utility.transform.api.meta.DerivedType;
import org.atemsource.atem.utility.transform.impl.BindingMetaAttributeRegistrar;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.transform.impl.version.VersionResolver;

/**
* The version binder creates a target type for each source type and version.
*/
public class VersionedBinder extends AbstractBinder
{

	private String prefix;

	private VersionResolver versionResolver;

	private List<String> versions;

	protected TypeNameConverter createTypeCode(final String version)
	{
		return new VersionTypeNameConverter(prefix, version);
	}

	public String getPrefix()
	{
		return prefix;
	}

	public <A, B> EntityTypeTransformation<A, B> getTransformation(Class<A> sourceClass, String version)
	{

		String targetTypeCode = createTypeCode(version).convert(entityTypeRepository.getEntityType(sourceClass));
		EntityType<Object> entityType = entityTypeRepository.getEntityType(targetTypeCode);
		if (entityType == null)
		{
			return null;
		}
		SingleAttribute<DerivedType> metaAttribute =
			(SingleAttribute<DerivedType>) entityTypeRepository.getEntityType(EntityType.class).getMetaAttribute(
				DerivedObject.META_ATTRIBUTE_CODE);
		return (EntityTypeTransformation<A, B>) metaAttribute.getValue(entityType).getTransformation();
	}

	public VersionResolver getVersionResolver()
	{
		return versionResolver;
	}

	public List<String> getVersions()
	{
		return versions;
	}

	@PostConstruct
	public void process()
	{

		if (versions != null && versions.size() > 0)
		{
			for (final String version : versions)
			{
				if (getAttributeNameConverter() == null)
				{
					setAttributeNameConverter(new JacksonAttributeNameConverter());
				}
				@SuppressWarnings("unchecked")
				BindingSession bindingSession = (BindingSession) beanLocator.getInstance(getBindingSessionClass());
				bindingSession.setTypeNameConverter(createTypeCode(version));
				bindingSession.setAttributeNameConverter(getAttributeNameConverter());
				bindingSession.setAttributeConverters(attributeConverters);
				bindingSession.setSubRepository(getSubRepository());
				bindingSession.setTransformationBuilderFactory(getTransformationBuilderFactory());
				bindingSession.addListener(new VersionBindingListener(version));
				List<AttributeFilter> filters = new ArrayList<AttributeFilter>();
				filters.addAll(getFilters());
				filters.add(new VersionFilter(version, versionResolver));
				bindingSession.setAttributeFilters(filters);
				bindingSession.process(getTypeFilter().getEntityTypes());
			}
		}

	}

/**
*
* define the prefix for that target type codes.
*/
	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}
/**
* The versionResolver resolves the version of attributes and types.
*/
	public void setVersionResolver(VersionResolver versionResolver)
	{
		this.versionResolver = versionResolver;
	}

/**
* The list of versions to be used.
*/
	public void setVersions(List<String> versions)
	{
		this.versions = versions;
	}

	private final class VersionBindingListener implements BindingListener
	{
		private final String version;

		private VersionBindingListener(String version)
		{
			this.version = version;
		}

		@Override
		public void finished(EntityTypeTransformation<?, ?> transformation)
		{
			SingleAttribute<Binding> bindingAttribute =
				(SingleAttribute<Binding>) entityTypeRepository.getEntityType(EntityType.class).getMetaAttribute(
					BindingMetaAttributeRegistrar.BINDING);
			EntityType targetType = (EntityType) transformation.getTypeB();
			if (bindingAttribute != null)
			{
				Binding binding = new Binding();
				binding.setVersion(version);
				binding.setExternalTypeCode(((JsonEntityTypeImpl) targetType).getExternalTypeCode());
				bindingAttribute.setValue(targetType, binding);
			}
			logger.info("added version to type " + targetType.getCode());
		}
	}

}
