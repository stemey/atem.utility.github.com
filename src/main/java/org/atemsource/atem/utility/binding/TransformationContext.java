package org.atemsource.atem.utility.binding;
    
import java.util.Stack;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.view.Visitor;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.visitor.HierachyVisitor;

public class TransformationContext {
	private BindingSession bindingSession;

	private Stack<TypeTransformationBuilder<?, ?>> transformationBuilders = new Stack<TypeTransformationBuilder<?, ?>>();

	public TransformationContext(BindingSession bindingSession,
			TypeTransformationBuilder<?, ?> transformationBuilder) {
		super();
		this.bindingSession = bindingSession;
		this.transformationBuilders.push(transformationBuilder);
	}

	public void addTransformationBuilder(
			TypeTransformationBuilder<?, ?> transformationBuilder) {
		transformationBuilders.add(transformationBuilder);

	}

	public void cascade(EntityType<?> entityType,
			Visitor<TransformationContext> visitor) {

		switch (getState(entityType)) {
		case STARTED:
			break;
		case NOT_STARTED:
			TypeTransformationBuilder<?, ?> transformationBuilder = bindingSession
					.createTransformationBuilder(entityType);
			transformationBuilders.push(transformationBuilder);
			visitor.visit(this);
			TypeTransformationBuilder<?, ?> typeTransformationBuilder = transformationBuilders
					.pop();
			EntityTypeTransformation<?, ?> transformation = typeTransformationBuilder
					.buildTypeTransformation();
			bindingSession.onTypeCreated(transformation);
			break;

		}
	}

	public void finishTransformation(EntityType<?> entityType) {
		TypeTransformationBuilder<?, ?> typeTransformationBuilder = transformationBuilders
				.pop();
		EntityTypeTransformation<?, ?> transformation = typeTransformationBuilder
				.buildTypeTransformation();
	}

	public TypeTransformationBuilder<?, ?> getCurrent() {
		return transformationBuilders.peek();
	}

	public State getState(EntityType<?> sourceType) {
		if (bindingSession.getTransformation(sourceType.getCode()) != null) {
			return State.STARTED;
		} else {
			return State.NOT_STARTED;
		}
	}

	public <A, B> EntityTypeTransformation<A, B> getTransformation(
			EntityType<A> entityType) {
		return (EntityTypeTransformation<A, B>) bindingSession
				.getTransformation(entityType.getCode());
	}

	public void startTransformation(EntityType<?> entityType) {
		TypeTransformationBuilder<?, ?> transformationBuilder = bindingSession
				.createTransformationBuilder(entityType);
		transformationBuilders.push(transformationBuilder);
	}

	public void visitSuperType(EntityType<?> superType,
			TransformationVisitor visitor) {
		switch (getState(superType)) {
		case STARTED:
			break;
		case NOT_STARTED:
			TypeTransformationBuilder<?, ?> transformationBuilder = bindingSession
					.createTransformationBuilder(superType);
			transformationBuilders.push(transformationBuilder);
			HierachyVisitor.visit(superType,visitor, this);
			TypeTransformationBuilder<?, ?> typeTransformationBuilder = transformationBuilders
					.pop();
			EntityTypeTransformation<?, ?> transformation = typeTransformationBuilder
					.buildTypeTransformation();
			bindingSession.onTypeCreated(transformation);
			break;

		}
		getCurrent().includeSuper(getTransformation(superType));
	}

	public void visitSubview(EntityType<?> subType) {
		// TODO Auto-generated method stub

	}
}
