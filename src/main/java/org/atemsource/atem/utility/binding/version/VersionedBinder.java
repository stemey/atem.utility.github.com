package org.atemsource.atem.utility.binding.version;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.binding.AbstractBinder;
import org.atemsource.atem.utility.binding.AttributeFilter;
import org.atemsource.atem.utility.binding.BindingListener;
import org.atemsource.atem.utility.binding.BindingSession;
import org.atemsource.atem.utility.binding.jackson.JacksonAttributeNameConverter;
import org.atemsource.atem.utility.domain.DomainA;
import org.atemsource.atem.utility.transform.api.Binding;
import org.atemsource.atem.utility.transform.api.DerivedType;
import org.atemsource.atem.utility.transform.impl.BindingMetaAttributeRegistrar;
import org.atemsource.atem.utility.transform.impl.DerivationMetaAttributeRegistrar;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.transform.impl.version.VersionResolver;
import org.codehaus.jackson.node.ObjectNode;

public class VersionedBinder extends AbstractBinder {

	private final class VersionBindingListener implements BindingListener {
		private final String version;

		private VersionBindingListener(String version) {
			this.version = version;
		}

		@Override
		public void finished(EntityTypeTransformation<?, ?> transformation) {
			SingleAttribute<Binding> bindingAttribute = (SingleAttribute<Binding>) entityTypeRepository
					.getEntityType(EntityType.class).getMetaAttribute(
							BindingMetaAttributeRegistrar.BINDING);
			EntityType targetType = (EntityType) transformation.getTypeB();
			if (bindingAttribute != null) {
				Binding binding = new Binding();
				binding.setVersion(version);
				bindingAttribute.setValue(targetType, binding);
			}
			logger.info("added version to type " + targetType.getCode());
		}
	}

	private String prefix;

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public List<String> getVersions() {
		return versions;
	}

	public void setVersions(List<String> versions) {
		this.versions = versions;
	}

	private List<String> versions;

	@PostConstruct
	public void process() {
		
		if (versions != null && versions.size() > 0) {
			for (final String version : versions) {
				if (getAttributeNameConverter()==null) {
					setAttributeNameConverter(new JacksonAttributeNameConverter());
				}
				BindingSession bindingSession = beanLocator
						.getInstance(getBindingSessionClass());
				bindingSession
						.setTypeNameConverter(new VersionTypeNameConverter(
								prefix, version));
				bindingSession
						.setAttributeNameConverter(getAttributeNameConverter());
				bindingSession.setSubRepository(getSubRepository());
				bindingSession
						.setTransformationBuilderFactory(getTransformationBuilderFactory());
				bindingSession.addListener(new VersionBindingListener(version));
				List<AttributeFilter> filters = new ArrayList<AttributeFilter>();
				filters.addAll(getFilters());
				filters.add(new VersionFilter(version, versionResolver));
				bindingSession.setAttributeFilters(filters);
				bindingSession.process(getSourceTypes());
			}
		}

	}

	public VersionResolver getVersionResolver() {
		return versionResolver;
	}

	public void setVersionResolver(VersionResolver versionResolver) {
		this.versionResolver = versionResolver;
	}

	private VersionResolver versionResolver;

	public <A, B> EntityTypeTransformation<A, B> getTransformation(
			Class<A> sourceClass, String version) {

		String targetTypeCode = new VersionTypeNameConverter(prefix, version)
				.convert(entityTypeRepository.getEntityType(sourceClass));
		EntityType<Object> entityType = entityTypeRepository
				.getEntityType(targetTypeCode);
		SingleAttribute<DerivedType> metaAttribute = (SingleAttribute<DerivedType>) entityTypeRepository
				.getEntityType(EntityType.class).getMetaAttribute(
						DerivationMetaAttributeRegistrar.DERIVED_FROM);
		return (EntityTypeTransformation<A, B>) metaAttribute.getValue(
				entityType).getTransformation();
	}

}
