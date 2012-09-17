package org.atemsource.atem.utility.path.parser;

import org.atemsource.atem.utility.path.AttributeAttributePathElement;
import org.atemsource.atem.utility.path.AttributePathBuilder;
import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.support.Var;

@BuildParseTree
public class AtemPathParser extends BaseParser<ParboiledAttributePathBuilder> {

	/**
	 * 
	 * 
	 * Expression ← Term ((‘+’ / ‘-’) Term) pathElement ← Factor (. Factor /
	 * [attribute/index])// attribute ← String() index Number()
	 * 
	 * 
	 * 
	 */

	Rule Expression() {
		return Sequence(FirstOf(Attribute(),Index(), DotPathElement()),OneOrMore(FirstOf(ParensPathElement(), DotPathElement())),EOI);
	}

	Rule ParensPathElement() {
		return Sequence('[', FirstOf(ComplexAttribute(), Index(),Attribute()), ']');
	}

	Rule DotPathElement() {
		return Sequence('.', FirstOf(Attribute(), Index()));
	}

	Rule Attribute() {
		return Sequence(OneOrMore(FirstOf(CharRange('a', 'z'),CharRange('A', 'Z'))),peek().addElement(match()));
	}
	

	Rule ComplexAttribute() {
		return Sequence(OneOrMore(FirstOf(CharRange('a', 'z'),CharRange('A', 'Z'), '.')),peek().addElement(match()));
	}

	Rule Index() {
		return Sequence(OneOrMore(CharRange('0', '9')),peek().addElement(match()));
	}

}
