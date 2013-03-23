package org.uva.sea.ql.ast.expression.binary.comparison;

import org.uva.sea.ql.ast.expression.Expression;
import org.uva.sea.ql.visitor.ExpressionVisitor;

public class LesserThanOrEqualExpression extends ComparisonExpression {
	public LesserThanOrEqualExpression( Expression lhs, Expression rhs ) {
		super( lhs, rhs );
	}

	@Override
	public <T> T accept( ExpressionVisitor<T> visitor ) {
		return visitor.visit( this );
	}
}
