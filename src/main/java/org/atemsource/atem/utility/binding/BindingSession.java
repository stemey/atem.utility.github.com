package org.atemsource.atem.utility.binding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.spi.DynamicEntityTypeSubrepository;
import org.atemsource.atem.utility.transform.api.AttributeNameConverter;
import org.atemsource.atem.utility.transform.api.TransformationBuilderFactory;
import org.atemsource.atem.utility.transform.api.TypeNameConverter;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.visitor.HierachyVisitor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
/**
* Used internally by the binder.
*
*/
@Component
@Scope("prototype")
public class BindingSession {

	@Inject
	private EntityTypeRepository entityTypeRepository;

	private Logger logger = Logger.getLogger(getClass());

	private AttributeNameConverter attributeNameConverters;

	private List<AttributeConverter> attributeConverters;

	public void createEntityType(EntityType<?> entityType) {
		String jsonTypeName = typeNameConverter.convert(entityType);
		if (transformations.get(entityType.getCode()) != null) {
			return;
		}
		EntityType<?> jsonType = entityTypeRepository
				.getEntityType(jsonTypeName);
		if (true)// jsonType == null)
		{
			TypeTransformationBuilder<?, ?> transformationBuilder = createTransformationBuilder(
					entityType);
			// transformations.put(jsonTypeName,
			// transformationBuilder.getReference());
			TransformationContext context = new TransformationContext(
					this,
					transformationBuilder);
			TransformationVisitor visitor = new TransformationVisitor(
					typeNameConverter, attributeFilters, attributeNameConverter, attributeConverters);
			context.addTransformationBuilder(transformationBuilder);
			HierachyVisitor.visit(entityType,visitor, context);
			EntityTypeTransformation<?, ?> transformation = transformationBuilder
					.buildTypeTransformation();

			onTypeCreated(transformation);
		}

		logger.debug("finished to transform " + entityType.getCode());
	}

	

	public void setAttributeConverters(List<AttributeConverter> attributeConverters) {
		this.attributeConverters = attributeConverters;
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

	private DynamicEntityTypeSubrepository<?> subRepository;

	private TransformationBuilderFactory transformationBuilderFactory;

	private Map<String, EntityTypeTransformation<?, ?>> transformations = new ConcurrentHashMap<String, EntityTypeTransformation<?, ?>>();

	private Map<String, EntityTypeBuilder> builders = new HashMap<String, EntityTypeBuilder>();

	public TypeTransformationBuilder<?, ?> createTransformationBuilder(
			 EntityType<?> entityType) {
		String newtypeCode = typeNameConverter.convert(entityType);
		EntityTypeBuilder builder = builders.get(newtypeCode);
		if (builder == null) {
			builder = subRepository.createBuilder(newtypeCode);
			builders.put(newtypeCode, builder);
		}
		TypeTransformationBuilder<?, ?> transformationBuilder = transformationBuilderFactory
				.create(entityType, builder);
		Object exist = transformations.put(entityType.getCode(),
				transformationBuilder.getReference());
		if (exist != null) {
			throw new IllegalStateException("transforming "
					+ entityType.getCode() + " twice");
		}
		return transformationBuilder;
	}
	public void onTypeCreated(EntityTypeTransformation transformation) {
		for (BindingListener listener:listeners) {
			listener.finished(transformation);
		}
	}
	private AttributeNameConverter attributeNameConverter;
	private TypeNameConverter typeNameConverter;
	private List<AttributeFilter> attributeFilters;
private List<BindingListener> listeners= new ArrayList<BindingListener>();
	public Map<String, EntityTypeTransformation<?, ?>> getTransformations() {
		return transformations;
	}

	public void setTransformations(
			Map<String, EntityTypeTransformation<?, ?>> transformations) {
		this.transformations = transformations;
	}

	public TypeNameConverter getTypeNameConverter() {
		return typeNameConverter;
	}

	public void setTypeNameConverter(TypeNameConverter typeNameConverter) {
		this.typeNameConverter = typeNameConverter;
	}

	public AttributeNameConverter getAttributeNameConverter() {
		return attributeNameConverter;
	}

	public void setAttributeNameConverter(
			AttributeNameConverter attributeNameConverter) {
		this.attributeNameConverter = attributeNameConverter;
	}

	public List<AttributeFilter> getAttributeFilters() {
		return attributeFilters;
	}

	public void setAttributeFilters(List<AttributeFilter> attributeFilters) {
		this.attributeFilters = attributeFilters;
	}

	public void process(Collection<EntityType<?>> entityTypes) {
		for (EntityType<?> entityType : entityTypes) {
			createEntityType(entityType);
		}
	}
	
	public void addListener(BindingListener listener) {
		this.listeners.add(listener);
	}

	public EntityTypeTransformation<?,?> getTransformation(String code) {
		return transformations.get(code);
	}

	
}
