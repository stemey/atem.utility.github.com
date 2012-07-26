package org.atemsource.atem.utility.transform.impl.binding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.infrastructure.exception.TechnicalException;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.impl.common.infrastructure.CandidateResolver;
import org.atemsource.atem.impl.common.infrastructure.ClasspathScanner;
import org.atemsource.atem.spi.DynamicEntityTypeSubrepository;
import org.atemsource.atem.utility.jackson.ClassHierachyComparator;
import org.atemsource.atem.utility.transform.api.Binding;
import org.atemsource.atem.utility.transform.api.DerivedType;
import org.atemsource.atem.utility.transform.api.JavaUniConverter;
import org.atemsource.atem.utility.transform.api.TransformationBuilderFactory;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.BindingMetaAttributeRegistrar;
import org.atemsource.atem.utility.transform.impl.DerivationMetaAttributeRegistrar;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.transform.impl.version.VersionResolver;


public class BindingProcessor
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
			TypeTransformationBuilder<?, ?> transformationBuilder =
				BindingProcessor.this.createTransformationBuilder(newTypeCode, entityType);
			transformations.put(newTypeCode, transformationBuilder.getReference());
			return transformationBuilder;
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
		public EntityType getEntityTypeReference(EntityType<?> originalType)
		{
			String derivedTypeName = typeNameConverter.convert(originalType.getCode());
			return entityTypeRepository.getEntityType(derivedTypeName);
		}

		public JavaUniConverter<String, String> getTypeNameConverter()
		{
			return typeNameConverter;
		}

		@Override
		public void onTypeCreated(EntityType<?> entityType)
		{
			BindingProcessor.this.addVersionAndExternalName(version, entityType);
		}
	}

	private JavaUniConverter<String, String> attributeNameConverter;

	private Map<String, EntityTypeBuilder> builders = new HashMap<String, EntityTypeBuilder>();

	private CandidateResolver candidateResolver;

	@Inject
	private DerivationMetaAttributeRegistrar derivationMetaAttributeRegistrar;

	@Inject
	protected EntityTypeRepository entityTypeRepository;

	List<AttributeFilter> filters = new ArrayList<AttributeFilter>();

	private String includedPackage;

	private Logger logger = Logger.getLogger(getClass());

	private String paket;

	private String prefix;

	@Inject
	protected ClasspathScanner scanner;

	private DynamicEntityTypeSubrepository<?> subRepository;

	private TransformationBuilderFactory transformationBuilderFactory;

	private Map<String, EntityTypeTransformation<?, ?>> transformations =
		new ConcurrentHashMap<String, EntityTypeTransformation<?, ?>>();

	private JavaUniConverter<String, String> typeNameConverter;

	private VersionResolver versionResolver;

	private List<String> versions;

	private void addVersionAndExternalName(String version, EntityType<?> type)
	{
		SingleAttribute<Binding> bindingAttribute =
			(SingleAttribute<Binding>) entityTypeRepository.getEntityType(EntityType.class).getMetaAttribute(
				BindingMetaAttributeRegistrar.BINDING);
		if (bindingAttribute != null)
		{
			Binding binding = new Binding();
			binding.setVersion(version);
			bindingAttribute.setValue(type, binding);
		}
		logger.info("added version to type " + type.getCode());
	}

	public void createEntityType(Class<?> clazz, final String version)
	{
		logger.debug("started to transform " + clazz.getName() + " version:" + version);
		EntityType<?> entityType = entityTypeRepository.getEntityType(clazz);
		String jsonTypeName = typeNameConverter.convert(entityType.getCode());
		if (transformations.get(entityType.getCode()) != null)
		{
			return;
		}
		EntityType<?> jsonType = entityTypeRepository.getEntityType(jsonTypeName);
		if (true)// jsonType == null)
		{
			TypeTransformationBuilder<?, ?> transformationBuilder = createTransformationBuilder(jsonTypeName, entityType);
			// transformations.put(jsonTypeName, transformationBuilder.getReference());
			TransformationContext context =
				new TransformationContext(new VersionRepositoryManager(version, typeNameConverter), transformationBuilder);
			context.setTransformations(transformations);
			TransformationVisitor visitor =
				new TransformationVisitor(typeNameConverter, entityTypeRepository, filters, attributeNameConverter);
			context.addTransformationBuilder(transformationBuilder);
			entityType.visit(visitor, context);
			EntityTypeTransformation<?, ?> transformation = transformationBuilder.buildTypeTransformation();

			addVersionAndExternalName(version, (EntityType<?>) transformation.getTypeB());
		}

		logger.debug("finished to transform " + clazz.getName() + " version:" + version);
	}

	public TypeTransformationBuilder<?, ?> createTransformationBuilder(String newtypeCode, EntityType<?> entityType)
	{

		EntityTypeBuilder builder = builders.get(newtypeCode);
		if (builder == null)
		{
			builder = subRepository.createBuilder(newtypeCode);
			builders.put(newtypeCode, builder);
		}
		TypeTransformationBuilder<?, ?> transformationBuilder = transformationBuilderFactory.create(entityType, builder);
		Object exist = transformations.put(entityType.getCode(), transformationBuilder.getReference());
		if (exist != null)
		{
			throw new IllegalStateException("transforming " + entityType.getCode() + " twice");
		}
		return transformationBuilder;
	}

	public JavaUniConverter<String, String> getAttributeNameConverter()
	{
		return attributeNameConverter;
	}

	public List<AttributeFilter> getFilters()
	{
		return filters;
	}

	public String getIncludedPackage()
	{
		return includedPackage;
	}

	public String getPaket()
	{
		return paket;
	}

	public String getPrefix()
	{
		return prefix;
	}

	public EntityTypeTransformation<?, ?> getTransformation(EntityType<?> type)
	{
		return transformations.get(type.getCode());
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
	};

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
		if (typeNameConverter == null)
		{
			typeNameConverter = new SimpleTypeNameConverter(prefix);
		}
		try
		{
			Collection<Class<?>> classes = scanner.findClasses(includedPackage, candidateResolver);
			List<Class<?>> sortedClasses = new ArrayList<Class<?>>();
			sortedClasses.addAll(classes);
			Collections.sort(sortedClasses, new ClassHierachyComparator());

			if (versions != null && versions.size() > 0)
			{
				for (String version : versions)
				{
					for (Class clazz : sortedClasses)
					{
						String externalTypeName = typeNameConverter.convert(clazz.getName());
						builders.put(externalTypeName, subRepository.createBuilder(externalTypeName));
					}
				}
			}
			else
			{
				// typeNameConverter = new TypeNameConverter(prefix, null, paket);
				for (Class clazz : sortedClasses)
				{
					String externalTypeName = typeNameConverter.convert(clazz.getName());
					builders.put(externalTypeName, subRepository.createBuilder(externalTypeName));
				}
			}

			if (versions != null && versions.size() > 0)
			{
				for (String version : versions)
				{
					scanSingleVersion(sortedClasses, version);
				}
			}
			else
			{
				scanSingleVersion(sortedClasses, null);
			}

		}
		catch (IOException e)
		{
			throw new TechnicalException("cannot scan package " + includedPackage, e);
		}
	}

	public void scanSingleVersion(Collection<Class<?>> classes, String version)
	{
		transformations.clear();
		// TODO dont change the instance variable
		// typeNameConverter = new TypeNameConverter(prefix, version, paket);
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

	public void setCandidateResolver(CandidateResolver candidateResolver)
	{
		this.candidateResolver = candidateResolver;
	}

	public void setFilters(List<AttributeFilter> filters)
	{
		this.filters = filters;
	}

	public void setIncludedPackage(String includedPackage)
	{
		this.includedPackage = includedPackage;
	}

	public void setPaket(String paket)
	{
		this.paket = paket;
	}

	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}

	public void setSubRepository(DynamicEntityTypeSubrepository<?> subRepository)
	{
		this.subRepository = subRepository;
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
