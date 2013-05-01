package org.atemsource.atem.utility.visitor;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.atemsource.atem.api.view.AttributeView;
import org.atemsource.atem.api.view.View;
import org.atemsource.atem.api.view.ViewVisitor;
import org.atemsource.atem.api.view.Visitor;

public class HierachyVisitor<C> implements Visitor<C> {

	private final ViewVisitor<C> visitor;
	private Set<View> visitedTypes;
	private View view;

	protected void visitAttributes(ViewVisitor<C> visitor, C context) {
		Iterator<AttributeView> attributes = view.attributes();
		while (attributes.hasNext()) {
			AttributeView attribute = attributes.next();

			if (attribute.getTargetView() != null) {
				visitor.visit(context, attribute.getAttribute(),
						new AttributeVisitor<C>(visitor, attribute.getTargetView()));
			} else {
				visitor.visit(context, attribute.getAttribute());
			}
		}
	}


	public static <C> void visit(View view, ViewVisitor<C> visitor, C context) {
		new HierachyVisitor<C>(view, visitor).visit(context);
	}

	public void visit(View view) {
		this.view = view;
		this.visitedTypes = new HashSet<View>();
	}

	private HierachyVisitor(View view, ViewVisitor<C> visitor, Set<View> visitedTypes) {
		this.visitor = visitor;
		this.view = view;
		this.visitedTypes = visitedTypes;
	}

	private HierachyVisitor(View view, ViewVisitor<C> visitor) {
		this.visitor = visitor;
		this.view = view;
		this.visitedTypes = new HashSet<View>();
	}

	public void visit(C context) {
		visitedTypes.add(view);
		this.visitAttributes(visitor, context);
		visitSubTypes(context);
		View superView = view.getSuperView();
		if (superView!= null && !visitedTypes.contains(superView)) {
			visitor.visitSuperView(context, superView, new HierachyVisitor<C>(superView, visitor, visitedTypes));
		}
	}

	public void visitSubTypes(C context) {
		Iterator<? extends View> subviews = view.subviews();
		while (subviews.hasNext()) {
			View subView = subviews.next();
			if (!visitedTypes.contains(subView)) {
				visitor.visitSubView(context, subView, new HierachyVisitor<C>(subView, visitor, visitedTypes));
			}
		}
	}
}