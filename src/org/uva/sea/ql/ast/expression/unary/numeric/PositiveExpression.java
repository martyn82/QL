package org.uva.sea.ql.ast.expression.unary.numeric;

import org.uva.sea.ql.ast.expression.Expression;
import org.uva.sea.ql.visitor.ExpressionVisitor;

public class PositiveExpression extends UnaryNumericExpression {
	public PositiveExpression( Expression expression ) {
		super( expression );
	}

	@Override
	public <T> T accept( ExpressionVisitor<T> visitor ) {
		return visitor.visit( this );
	}
}
