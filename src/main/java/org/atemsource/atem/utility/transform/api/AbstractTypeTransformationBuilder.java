package org.atemsource.atem.utility.transform.api;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.CollectionAttribute;
import org.atemsource.atem.api.attribute.MapAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.api.type.PrimitiveType;
import org.atemsource.atem.impl.meta.DerivedObject;
import org.atemsource.atem.utility.path.AttributePathBuilderFactory;
import org.atemsource.atem.utility.transform.api.meta.DerivedType;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.transform.impl.builder.CollectionAttributeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.builder.MapAttributeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.builder.OneToOneAttributeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.builder.SingleAttributeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.builder.TransformationFinder;

public abstract class AbstractTypeTransformationBuilder<A, B> {

	@Inject
	BeanLocator beanLocator;

	ConverterFactory converterFactory;

	@Inject
	EntityTypeRepository entityTypeRepository;

	final static Logger logger = Logger
			.getLogger(AbstractTypeTransformationBuilder.class);

	@Inject
	private AttributePathBuilderFactory pathFactory;
	private EntityTypeTransformation<A, B> selfReference;
	private EntityTypeTransformation<?, ?> superTransformation;
	private EntityTypeBuilder targetTypeBuilder;
	private Transformation<A, B> transformation;
	private final List<AttributeTransformationBuilder> transformations = new ArrayList<AttributeTransformationBuilder>();

	public AbstractTypeTransformationBuilder() {
		super();
	}

	private void addDerivation(EntityTypeTransformation<?, ?> transformation,
			EntityType<?> newType, EntityType<?> originalType) {
		DerivedType deriveType = new DerivedType();
		deriveType.setOriginalType(originalType);
		deriveType.setTransformation(transformation);
		Attribute metaAttribute = entityTypeRepository.getEntityType(
				EntityType.class).getMetaAttribute(
				DerivedObject.META_ATTRIBUTE_CODE);
		if (metaAttribute != null) {
			metaAttribute.setValue(newType, deriveType);
		}
		
		
		logger.info("finished init of " + newType.getCode());
	}

	/**
	 * add another transformation.
	 */
	public void addTransformation(JavaTransformation<A, B> javaTransformation) {
		selfReference.addTransformation(javaTransformation);
	}

	/**
	 * build the transformation. It will not be modifiable afterwards.
	 */
	public EntityTypeTransformation<A, B> buildTypeTransformation() {
		for (AttributeTransformationBuilder transformation : transformations) {
			transformation.build(targetTypeBuilder);
		}
		targetTypeBuilder.setAbstract(getSourceType().isAbstractType());
		EntityType targetType = targetTypeBuilder.createEntityType();
		if (transformation != null) {
			selfReference.setTypeConverter(transformation);
		} else {
			selfReference.setTypeConverter(createTypeCreator(targetType));
		}
		selfReference.setEntityTypeB(targetType);
		selfReference.setEntityTypeA(getSourceType());
		if (superTransformation != null) {
			EntityTypeTransformation<A, B> superEntityTypeTransformation = (EntityTypeTransformation<A, B>) superTransformation;
			selfReference.setSuperTransformation(superEntityTypeTransformation);
			superEntityTypeTransformation.addSubTransformation(selfReference);
		}
		for (AttributeTransformationBuilder transformation : transformations) {
			selfReference.addTransformation(transformation.create(targetType));
		}
		addDerivation(selfReference, targetType, getSourceType());
		return selfReference;
	}

	protected TypeCreator createTypeCreator(EntityType targetType) {
		TypeCreator instance = beanLocator.getInstance(TypeCreator.class);
		instance.initialize(getSourceType(), targetType);
		return instance;
	}

	/**
	 * If the types in the type hierachy of source and target don't have a one
	 * to one relationship provide a custome mapping here.
	 */
	public AbstractTypeTransformationBuilder<A, B> finder(
			TransformationFinder<A, B> finder) {
		selfReference.setFinder(finder);
		return this;
	}

	public ConverterFactory getConverterFactory() {
		return converterFactory;
	}

	/**
	 * get a reference to the transformation. The transformation is not
	 * guaranteed to be complete at this point.
	 * 
	 */
	public EntityTypeTransformation<?, ?> getReference() {
		return selfReference;
	}


	/**
	 * if there i aone to one relationship between the types in source and
	 * target then a supr Transformation can be set here. The super
	 * transformation will be eecuted before this transformation.
	 */
	public void includeSuper(EntityTypeTransformation<?, ?> transformation) {
		targetTypeBuilder.superType((EntityType<?>) transformation.getTypeB());
		superTransformation = transformation;
	}

	@PostConstruct
	public void initialize() {
		selfReference = beanLocator.getInstance(EntityTypeTransformation.class);
	}

	/**
	 * set the converter factory. It provides standard conversion for primitive
	 * types.
	 * 
	 */
	public void setConverterFactory(ConverterFactory converterFactory) {
		this.converterFactory = converterFactory;
	}

	public void setTargetTypeBuilder(EntityTypeBuilder targetTypeBuilder) {
		this.targetTypeBuilder = targetTypeBuilder;
		// this.selfReference.setTypeConverter(createTypeCreator(targetTypeBuilder.getReference()));
		this.selfReference.setEntityTypeB((EntityType<B>) targetTypeBuilder
				.getReference());
	}

	public void setTypeTransfomation(Transformation<A, B> transformation) {
		this.transformation = transformation;
	}

	/**
	 * To transform a single attribute in the source to a single attribute in
	 * the target use this method.
	 * 
	 */
	public OneToOneAttributeTransformationBuilder<A, B, SingleAttributeTransformationBuilder<A, B>> transform() {
		OneToOneAttributeTransformationBuilder<A, B, SingleAttributeTransformationBuilder<A, B>> builder = beanLocator
				.getInstance(SingleAttributeTransformationBuilder.class);
		builder.setSourceType(getSourceType());
		builder.setConverterFactory(converterFactory);
		transformations.add(builder);
		return builder;
	}

	/**
	 * To transform an attribute in the source to a single attribute in the
	 * target use this method.
	 * 
	 */
	public OneToOneAttributeTransformationBuilder<A, B, ?> transform(
			Class<? extends Attribute> attributeClass) {
		if (CollectionAttribute.class.isAssignableFrom(attributeClass)) {
			return transformCollection();
		} else if (MapAttribute.class.isAssignableFrom(attributeClass)) {
			return transformMap();
		} else {
			return transform();
		}
	}

	/**
	 * To transform a collection attribute in the source to a collection
	 * attribute in the target use this method.
	 * 
	 */
	public CollectionAttributeTransformationBuilder<A, B> transformCollection() {
		CollectionAttributeTransformationBuilder<A, B> builder = beanLocator
				.getInstance(CollectionAttributeTransformationBuilder.class);
		builder.setSourceType(getSourceType());
		builder.setConverterFactory(converterFactory);
		transformations.add(builder);
		return builder;
	}

	/**
	 * This method is an extension point to add new custom transformation
	 * builders.
	 * 
	 */
	public <A extends CustomAttributeTransformationBuilder> A transformCustom(
			Class<A> builderClass) {
		A builder = beanLocator.getInstance(builderClass);
		builder.setSourceType(getSourceType());
		builder.setConverterFactory(converterFactory);
		builder.setTargetTypeBuilder(targetTypeBuilder);
		transformations.add(builder);
		return builder;
	}

	/**
	 * To transform a map attribute in the source to a map attribute in the
	 * target use this method.
	 * 
	 */
	public MapAttributeTransformationBuilder<A, B> transformMap() {
		MapAttributeTransformationBuilder builder = beanLocator
				.getInstance(MapAttributeTransformationBuilder.class);
		builder.setSourceType(getSourceType());
		builder.setConverterFactory(converterFactory);
		transformations.add(builder);
		return builder;
	}

	/**
	 * Transform all primitives attributes except the given ones in a one-to-one
	 * manner.
	 * 
	 */
	public void transformPrimitives(String... excludedAttributes) {
		for (Attribute<?, ?> attribute : getSourceType().getAttributes()) {
			if (ArrayUtils.contains(excludedAttributes, attribute.getCode())
					&& attribute.getTargetType() instanceof PrimitiveType<?>) {
				// TODO add conversion from local and global standard
				// transformers
				Converter converter = converterFactory.get(attribute
						.getTargetType());
				OneToOneAttributeTransformationBuilder<A, B, SingleAttributeTransformationBuilder<A, B>> transform = transform();
				transform.from(attribute.getCode()).to(attribute.getCode());// .convert(converter);
				if (converter != null) {
					transform.convert(converter);
				}
			}
		}
	}

	protected abstract EntityType<A> getSourceType() ;

}