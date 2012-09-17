package org.atemsource.atem.utility.path.parser;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.atemsource.atem.utility.path.AttributePathBuilder;
import org.junit.Test;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.DefaultValueStack;
import org.parboiled.support.ParsingResult;
import org.parboiled.support.ValueStack;

public class AtemPathBuilderTest {

	@Test
	public void test() {
		builder = attributePathBuilderfACTORY:CREATE89,
		AtemPathParser parser = Parboiled.createParser(AtemPathParser.class);
		DefaultValueStack<ParboiledAttributePathBuilder> valueStack= new DefaultValueStack<ParboiledAttributePathBuilder>();
		valueStack.push(new ParboiledAttributePathBuilder(builder))
		ParsingResult<?> result = new ReportingParseRunner(parser.Expression()).withValueStack(valueStack)
				.run("gg.ff");
		Assert.assertEquals(0,result.parseErrors.size());
	}

}
