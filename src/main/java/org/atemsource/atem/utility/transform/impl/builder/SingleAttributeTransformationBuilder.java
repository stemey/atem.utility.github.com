/**
* this builder creates a transformation from a single soure attribute to a single target attribute.
*
*/
@Component
@Scope("prototype")
public class SingleAttributeTransformationBuilder<A, B> extends
	OneToOneAttributeTransformationBuilder<A, B, SingleAttributeTransformationBuilder<A, B>>
{
	@Override
	public void build(EntityTypeBuilder entityTypeBuilder)
	{
		AttributePath sourcePath = attributePathBuilderFactory.createAttributePath(getSourceAttribute(), sourceType);
		Type<?> attributeTargetType;
		Converter<?, ?> converter = getConverter(sourcePath.getTargetType().getType());
		Type[] validTypes;
		attributeTargetType = getTargetType(sourcePath, converter);
		validTypes = getValidTargetTypes(sourcePath, converter);
		SingleAttribute<?> attribute = entityTypeBuilder.addSingleAttribute(getTargetAttribute(), attributeTargetType,validTypes);
		if (converter != null && converter instanceof Constraining)
		{
			Constraining constraining = ((Constraining) converter);
			for (String name : constraining.getConstraintNamesAB())
			{
				Attribute metaAttribute = entityTypeRepository.getEntityType(Attribute.class).getMetaAttribute(name);
				if (metaAttribute != null)
				{
					metaAttribute.setValue(attribute, constraining.getConstraintAB(name));
				}
			}
		}
	}

	@Override
	public AttributeTransformation<A, B> create(EntityType<B> targetType)
	{
		AttributePath sourcePath = attributePathBuilderFactory.createAttributePath(getSourceAttribute(), sourceType);
		AttributePath targetPath = attributePathBuilderFactory.createAttributePath(getTargetAttribute(), targetType);
		SingleAttributeTransformation<A, B> primitiveAttributeTransformation =
			beanLocator.getInstance(SingleAttributeTransformation.class);
		primitiveAttributeTransformation.setAttributeA(sourcePath);
		primitiveAttributeTransformation.setAttributeB(targetPath);
		primitiveAttributeTransformation.setTransformation(getTransformation(sourcePath.getTargetType().getType()));
		primitiveAttributeTransformation.setTypeA(sourceType);
		primitiveAttributeTransformation.setTypeB(targetType);
		primitiveAttributeTransformation.setMeta(meta);
		addDerivation(primitiveAttributeTransformation, targetPath.getAttribute(), sourcePath.getAttribute());
		return primitiveAttributeTransformation;
	}

}
