package org.atemsource.atem.utility.jackson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.infrastructure.exception.TechnicalException;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.impl.MetaLogs;
import org.atemsource.atem.impl.common.infrastructure.ClasspathScanner;
import org.atemsource.atem.spi.DynamicEntityTypeSubrepository;
import org.atemsource.atem.utility.transform.api.Binding;
import org.atemsource.atem.utility.transform.api.DerivedType;
import org.atemsource.atem.utility.transform.api.JavaUniConverter;
import org.atemsource.atem.utility.transform.api.TransformationBuilderFactory;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.DerivationMetaAttributeRegistrar;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.transform.impl.version.VersionResolver;
import org.codehaus.jackson.node.ObjectNode;


public class JacksonBindingProcessor
{
	private final class VersionRepositoryManager implements RepositoryManager
	{
		private JavaUniConverter<String, String> typeNameConverter;

		private String version;

		public VersionRepositoryManager(String version, JavaUniConverter<String, String> typeNameConverter)
		{
			this.typeNameConverter = typeNameConverter;
			this.version = version;
		}

		@Override
		public TypeTransformationBuilder<?, ?> createTransformationBuilder(EntityType<?> entityType)
		{
			String newTypeCode = typeNameConverter.convert(entityType.getCode());

			return JacksonBindingProcessor.this.createTransformationBuilder(newTypeCode, entityType);
		}

		@Override
		public DerivedType getDerivedType(EntityType<?> targetType)
		{
			String derivedTypeName = typeNameConverter.convert(targetType.getCode());
			EntityType<?> entityType = entityTypeRepository.getEntityType(derivedTypeName);
			EntityType<?> metaType = entityTypeRepository.getEntityType(EntityType.class);
			if (entityType == null)
			{
				return null;
			}
			else
			{
				return (DerivedType) metaType.getMetaAttribute(DerivationMetaAttributeRegistrar.DERIVED_FROM).getValue(
					entityType);
			}
		}

		@Override
		public void onTypeCreated(EntityType<?> entityType)
		{
			JacksonBindingProcessor.this.addVersionAndExternalName(version, entityType);
		}
	}

	private JavaUniConverter<String, String> attributeNameConverter;

	@Inject
	private EntityTypeRepository entityTypeRepository;

	private String includedPackage;

	private DynamicEntityTypeSubrepository<ObjectNode> jsonRepository;

	private String paket;

	private String prefix;

	@Inject
	private ClasspathScanner scanner;

	private TransformationBuilderFactory transformationBuilderFactory;

	private JavaUniConverter<String, String> typeNameConverter;

	private VersionResolver versionResolver;

	private List<String> versions;

	private void addVersionAndExternalName(String version, EntityType<?> type)
	{
		SingleAttribute<Binding> bindingAttribute =
			(SingleAttribute<Binding>) entityTypeRepository.getEntityType(EntityType.class).getMetaAttribute("binding");
		Binding binding = new Binding();
		binding.setVersion(version);
		bindingAttribute.setValue(type, binding);
		MetaLogs.LOG.info("added version to type " + type.getCode());
	}

	private void createEntityType(Class<?> clazz, final String version)
	{
		List<AttributeFilter> filters = new ArrayList<AttributeFilter>();
		if (version != null)
		{
			filters.add(new VersionFilter(version, versionResolver));
		}
		filters.add(new IgnoreFilter());
		EntityType<?> entityType = entityTypeRepository.getEntityType(clazz);
		String jsonTypeName = typeNameConverter.convert(entityType.getCode());
		EntityType<?> jsonType = entityTypeRepository.getEntityType(jsonTypeName);
		if (jsonType == null)
		{
			TypeTransformationBuilder<?, ?> transformationBuilder = createTransformationBuilder(jsonTypeName, entityType);
			TransformationContext context =
				new TransformationContext(new VersionRepositoryManager(version, typeNameConverter), transformationBuilder);
			entityType.visit(new TransformationVisitor(entityTypeRepository, filters, attributeNameConverter), context);
			EntityTypeTransformation<?, ?> transformation = transformationBuilder.buildTypeTransformation();
			addVersionAndExternalName(version, (EntityType<?>) transformation.getTypeB());
		}
	}

	public TypeTransformationBuilder<?, ?> createTransformationBuilder(String newtypeCode, EntityType<?> entityType)
	{
		EntityTypeBuilder builder = jsonRepository.createBuilder(newtypeCode);
		return transformationBuilderFactory.create(entityType, builder);
	}

	public JavaUniConverter<String, String> getAttributeNameConverter()
	{
		return attributeNameConverter;
	}

	public String getIncludedPackage()
	{
		return includedPackage;
	}

	public DynamicEntityTypeSubrepository<ObjectNode> getJsonRepository()
	{
		return jsonRepository;
	}

	public String getPaket()
	{
		return paket;
	}

	public String getPrefix()
	{
		return prefix;
	}

	public TransformationBuilderFactory getTransformationBuilderFactory()
	{
		return transformationBuilderFactory;
	}

	public JavaUniConverter<String, String> getTypeNameConverter()
	{
		return typeNameConverter;
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
	public void initialize()
	{
		if (attributeNameConverter == null)
		{
			attributeNameConverter = new JavaUniConverter<String, String>() {

				@Override
				public String convert(String a)
				{
					return a;
				}
			};
		}
		scan();
	}

	public void scan()
	{
		try
		{
			Collection<Class<?>> classes = scanner.findClasses(includedPackage);

			if (versions != null && versions.size() > 0)
			{
				for (String version : versions)
				{
					scanSingleVersion(classes, version);
				}
			}
			else
			{
				scanSingleVersion(classes, null);
			}

		}
		catch (IOException e)
		{
			throw new TechnicalException("cannot scan package " + includedPackage, e);
		}
	}

	private void scanSingleVersion(Collection<Class<?>> classes, String version)
	{
		// TODO dont change the instance variable
		typeNameConverter = new TypeNameConverter(prefix, version, paket);
		for (Class<?> clazz : classes)
		{
			String entityName = clazz.getName();
			createEntityType(clazz, version);
		}
	}

	public void setAttributeNameConverter(JavaUniConverter<String, String> attributeNameConverter)
	{
		this.attributeNameConverter = attributeNameConverter;
	}

	public void setIncludedPackage(String includedPackage)
	{
		this.includedPackage = includedPackage;
	}

	public void setJsonRepository(DynamicEntityTypeSubrepository<ObjectNode> jsonRepository)
	{
		this.jsonRepository = jsonRepository;
	}

	public void setPaket(String paket)
	{
		this.paket = paket;
	}

	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}

	public void setTransformationBuilderFactory(TransformationBuilderFactory transformationBuilderFactory)
	{
		this.transformationBuilderFactory = transformationBuilderFactory;
	}

	public void setVersionResolver(VersionResolver versionResolver)
	{
		this.versionResolver = versionResolver;
	}

	public void setVersions(List<String> versions)
	{
		this.versions = versions;
	}

}
