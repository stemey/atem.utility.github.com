package org.atemsource.atem.utility.transform.impl.builder;

import java.util.HashSet;
import java.util.Set;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.path.AttributePath;
import org.atemsource.atem.utility.transform.api.AttributeTransformation;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.transform.impl.transformation.CollectionAssociationAttributeTransformation;
import org.atemsource.atem.utility.transform.impl.transformation.CollectionToMapAttributeTransformation;
import org.atemsource.atem.utility.transform.impl.transformation.EmbedAttributeTransformation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class Mixin 
extends AbstractAttributeTransformationBuilder<Object, Object, Mixin> {

private EntityTypeTransformation<?,?> transformation;
private String from;


public Mixin transform(EntityTypeTransformation<?,?> transformation) {
	this.transformation=transformation;
	return this;
}

public Mixin from(String from) {
	this.from=from;
	return this;
}
	@Override
	public void build(EntityTypeBuilder entityTypeBuilder) {
		AttributePath sourcePath = attributePathBuilderFactory
				.createAttributePath(from, sourceType);
		
		if (transformation!=null) {
			entityTypeBuilder.mixin(transformation.getEntityTypeB());
		}else{
			throw new IllegalArgumentException("you need to provide a transformation")		;
		}
	}

	@Override
	public AttributeTransformation<Object, Object> create(
			EntityType<Object> targetType) {
		
		
		EmbedAttributeTransformation embedAttributeTransformation = beanLocator
				.getInstance(EmbedAttributeTransformation.class);
		AttributePath sourcePath = attributePathBuilderFactory
				.createAttributePath(from, sourceType);
		embedAttributeTransformation.setAttributeA(sourcePath);
		Set<AttributePath> targetPaths= new HashSet<AttributePath>();
		// TODO add the target attributes
		embedAttributeTransformation.setAttributeBs(targetPaths);
		embedAttributeTransformation.setTransformation(transformation);
		embedAttributeTransformation.setTypeA(sourceType);
		embedAttributeTransformation.setTypeB(targetType);
		embedAttributeTransformation.setMeta(meta);
		return embedAttributeTransformation;
	}

	@Override
	public Object fromMethod() {
		throw new UnsupportedOperationException("not implemented yet");
	}



}
