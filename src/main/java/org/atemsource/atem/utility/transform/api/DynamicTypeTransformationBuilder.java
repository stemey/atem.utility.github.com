package org.atemsource.atem.utility.transform.api;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class DynamicTypeTransformationBuilder<A,B> extends AbstractTypeTransformationBuilder<A, B>{

	
	@Override
	public EntityTypeTransformation<A, B> buildTypeTransformation() {
		sourceTypeBuilder.createEntityType();
		return super.buildTypeTransformation();
	}
	private EntityTypeBuilder sourceTypeBuilder;
	public EntityTypeBuilder getSourceTypeBuilder() {
		return sourceTypeBuilder;
	}
	public void setSourceTypeBuilder(EntityTypeBuilder sourceTypeBuilder) {
		this.sourceTypeBuilder = sourceTypeBuilder;
		getReference().setEntityTypeA((EntityType) sourceTypeBuilder.getReference());
	}
	@Override
	protected EntityType<A> getSourceType() {
		return (EntityType<A>) sourceTypeBuilder.getReference();
	}

}
