package org.uva.sea.ql.parser;

public class ParseError extends Exception {
	private static final long serialVersionUID = 1L;

	public ParseError( String message ) {
		super( message );
	}
}
