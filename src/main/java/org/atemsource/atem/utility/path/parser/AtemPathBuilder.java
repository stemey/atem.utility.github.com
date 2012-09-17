package org.atemsource.atem.utility.path.parser;

import org.parboiled.Parboiled;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;

public class AtemPathBuilder {
	public void createAttributePath(String path) {
		AtemPathParser parser = Parboiled.createParser(AtemPathParser.class);
		ParsingResult<?> result = ReportingParseRunner.run(parser.Expression(),
				path);
	}
}
