package org.atemsource.atem.utility.validation;

import javax.validation.constraints.Max;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.JavaMetaData;
import org.atemsource.atem.api.attribute.primitive.TextType;
import org.atemsource.atem.api.extension.EntityTypePostProcessor;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.spi.EntityTypeCreationContext;

public class ValidationEntityTypePostProcessor implements EntityTypePostProcessor{

	@Override
	public void postProcessEntityType(EntityTypeCreationContext context,
			EntityType<?> entityType) {
		for (Attribute<?,?> attribute:entityType.getDeclaredAttributes()) {
			if (attribute.getTargetType() instanceof TextType) {
				TextType targetType=(TextType) attribute.getTargetType();
				Max max=((JavaMetaData)attribute).getAnnotationAnnotatedBy(Max.class);
				
			}
		}
		
		find all Constraints by the ref api. how to get data ?
	}

}
