package org.uva.sea.ql.parser;

import org.uva.sea.ql.ast.statement.Statement;

public interface Parser {
	Statement parse( String src ) throws ParseError;
}
