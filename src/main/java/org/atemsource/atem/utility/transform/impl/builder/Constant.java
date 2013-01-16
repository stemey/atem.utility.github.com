/**
* Thie builder creates a single target attribute with a constant value.
*/
@Component
@Scope("prototype")
public class Constant<A, B> extends AbstractAttributeTransformationBuilder<A, B, GenericTransformationBuilder<A, B>>
{
	private Object constantValue;
	private Class<?> targetClass;
	private String targetAttribute;

	@Override
	public void build(EntityTypeBuilder entityTypeBuilder)
	{
		Type<?> attributeTargetType;
		Type[] validTypes;
		attributeTargetType = entityTypeRepository.getType(targetClass);
		SingleAttribute<?> attribute = entityTypeBuilder.addSingleAttribute(targetAttribute, targetClass);
	}

	@Override
	public AttributeTransformation<A, B> create(EntityType<B> targetType)
	{
		GenericAttributeTransformation<A, B> instance = beanLocator.getInstance(GenericAttributeTransformation.class);
		Set<AttributePath> attributeAs =  Collections.EMPTY_SET;
		instance.setAttributeAs(attributeAs);
		Set<AttributePath> attributeBs =new HashSet<AttributePath>();
		final AttributePath targetPath = attributePathBuilderFactory.createAttributePath(targetAttribute, targetType);
		attributeBs.add(targetPath);
		instance.setAttributeBs(attributeBs);
		instance.setTransformation(new JavaTransformation<A,B>() {

			@Override
			public void mergeAB(A a, B b, TransformationContext ctx) {
				targetPath.setValue(b, constantValue);
			}

			@Override
			public void mergeBA(B b, A a, TransformationContext ctx) {
			}
		});
		return instance;
		
	}

	@Override
	public GenericTransformationBuilder<A, B> from(String attributePath) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public A fromMethod() {
		// TODO Auto-generated method stub
		return null;
	}
	
/**
* define  the type and value of the target attribute.
*/
	public <T> Constant<A,B> value(Class<T> targetClass,final T constantValue) {
		this.constantValue=constantValue;
		this.targetClass=targetClass;
		return this;
	}
/**
* define the name of the target attribute.
*/	
	public Constant<A,B> to(String targetAttribute) {
		this.targetAttribute=targetAttribute;
		return this;
	}


}
