package org.uva.sea.ql.evaluate.typecheck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.uva.sea.ql.StatementTest;
import org.uva.sea.ql.ast.expression.Expression;
import org.uva.sea.ql.ast.expression.IdentifierExpression;
import org.uva.sea.ql.ast.expression.binary.arithmetic.AddExpression;
import org.uva.sea.ql.ast.expression.binary.arithmetic.MultiplyExpression;
import org.uva.sea.ql.ast.expression.binary.comparison.EqualExpression;
import org.uva.sea.ql.ast.expression.binary.logical.AndExpression;
import org.uva.sea.ql.ast.expression.literal.BooleanLiteral;
import org.uva.sea.ql.ast.expression.literal.IntegerLiteral;
import org.uva.sea.ql.ast.expression.literal.MoneyLiteral;
import org.uva.sea.ql.ast.expression.literal.StringLiteral;
import org.uva.sea.ql.ast.statement.Assignment;
import org.uva.sea.ql.ast.statement.ComputedQuestion;
import org.uva.sea.ql.ast.statement.FormDeclaration;
import org.uva.sea.ql.ast.statement.IfThen;
import org.uva.sea.ql.ast.statement.IfThenElse;
import org.uva.sea.ql.ast.statement.Statement;
import org.uva.sea.ql.ast.statement.Statements;
import org.uva.sea.ql.ast.statement.VariableDeclaration;
import org.uva.sea.ql.ast.statement.VariableQuestion;
import org.uva.sea.ql.ast.type.BooleanType;
import org.uva.sea.ql.ast.type.IntegerType;
import org.uva.sea.ql.evaluate.Error;
import org.uva.sea.ql.evaluate.ErrorList;
import org.uva.sea.ql.visitor.VisitorTest;

public class StatementTypeCheckerTest extends VisitorTest<Boolean> implements StatementTest {
	private final StatementTypeChecker statementChecker;
	private final ExpressionTypeChecker expressionChecker;
	private final TypeEnvironment environment;

	public StatementTypeCheckerTest() {
		super();

		this.environment = new TypeEnvironment();
		this.expressionChecker = new ExpressionTypeChecker( this.environment );
		this.statementChecker = new StatementTypeChecker( this.environment );
	}

	private Boolean typeCheck( Statement statement ) {
		this.environment.clearErrors();

		if ( !statement.accept( this.statementChecker ) ) {
			return false;
		}

		return true;
	}

	private Boolean typeCheck( Expression expression ) {
		if ( !expression.accept( this.expressionChecker ) ) {
			return false;
		}

		return true;
	}

	private ErrorList getErrorList() {
		return this.environment.getErrorList();
	}

	private Error getFirstError() {
		return this.getErrorList().iterator().next();
	}

	@Test
	@Override
	public void testVarDeclaration() {
		assertTrue( typeCheck( new VariableDeclaration( new IdentifierExpression( "z" ), BooleanType.BOOLEAN ) ) );
		assertFalse( typeCheck( new AddExpression( new IntegerLiteral( 1 ), new IdentifierExpression( "z" ) ) ) );

		assertTrue( typeCheck( new VariableDeclaration( new IdentifierExpression( "x" ), IntegerType.INTEGER ) ) );
		assertTrue( typeCheck( new Assignment( new IdentifierExpression( "x" ), new IntegerLiteral( 23 ) ) ) );

		assertFalse( typeCheck( new Assignment( new IdentifierExpression( "x" ), new IdentifierExpression( "y" ) ) ) );
		assertEquals( TypeError.TYPE_UNDEFINED, this.getFirstError().getCode() );

		assertFalse(
			typeCheck(
				new Assignment(
					new IdentifierExpression( "x" ),
					new AddExpression( new MultiplyExpression( new IntegerLiteral( 24 ), new MoneyLiteral( .5 ) ),
					new IdentifierExpression( "y" ) )
				)
			)
		);
		assertEquals( TypeError.TYPE_UNDEFINED, this.getFirstError().getCode() );

		/*
		 * if ( true ) {
		 * 		x = 24
		 * 		x: boolean
		 * }
		 */
		assertFalse(
			typeCheck(
				new IfThen(
					new BooleanLiteral( true ),
					new Statements(
						new Assignment( new IdentifierExpression( "x" ), new IntegerLiteral( 24 ) ),
						new Statements(
							new VariableDeclaration( new IdentifierExpression( "x" ), BooleanType.BOOLEAN )
						)
					)
				)
			)
		);

		assertEquals( TypeError.TYPE_ERROR, this.getFirstError().getCode() );
	}

	@Test
	@Override
	public void testIfThen() {
		assertTrue( typeCheck( new IfThen( new BooleanLiteral( true ), new Statements() ) ) );

		assertFalse( typeCheck( new IfThen( new IntegerLiteral( 1 ), new Statements() ) ) );
		assertEquals( TypeError.TYPE_INVALID, this.getFirstError().getCode() );

		/*
		 * if ( true ) {
		 *		"" x: boolean
		 *		x = 23
		 * }
		 */
		assertFalse(
			typeCheck(
				new IfThen(
					new BooleanLiteral( true ),
					new Statements(
						new VariableQuestion(
							new StringLiteral( "" ),
							new VariableDeclaration( new IdentifierExpression( "x" ), BooleanType.BOOLEAN )
						),
						new Statements(
							new Assignment( new IdentifierExpression( "x" ), new IntegerLiteral( 23 ) )
						)
					)
				)
			)
		);
		assertEquals( TypeError.TYPE_MISMATCH, this.getFirstError().getCode() );
	}

	@Test
	@Override
	public void testIfThenElse() {
		assertTrue(
			typeCheck(
				new IfThenElse(
					new BooleanLiteral( true ),
					new Statements(), // empty body
					new Statements(
						new IfThen(
							new BooleanLiteral( false ),
							new Statements() // empty body
						)
					)
				)
			)
		);

		assertFalse( typeCheck( new IfThenElse( new IntegerLiteral( 1 ), new Statements(), new Statements() ) ) );
		assertEquals( TypeError.TYPE_INVALID, this.getFirstError().getCode() );

		/*
		 * if ( true ) {
		 *		"" x: boolean
		 *		x = 23
		 * }
		 */
		assertFalse(
			typeCheck(
				new IfThenElse(
					new BooleanLiteral( true ),
					new Statements(
						new VariableQuestion(
							new StringLiteral( "" ),
							new VariableDeclaration( new IdentifierExpression( "x" ), BooleanType.BOOLEAN )
						),
						new Statements(
							new Assignment( new IdentifierExpression( "x" ), new IntegerLiteral( 23 ) )
						)
					),
					new Statements()
				)
			)
		);
		assertEquals( TypeError.TYPE_MISMATCH, this.getFirstError().getCode() );

	}

	@Test
	@Override
	public void testFormDeclaration() {
		assertTrue( typeCheck( new FormDeclaration( "x", new Statements() ) ) );
		assertTrue(
			typeCheck(
				new FormDeclaration(
					"formVar",
					new Statements(
						new VariableQuestion(
							new StringLiteral( "label" ),
							new VariableDeclaration(
								new IdentifierExpression( "questionVar" ),
								IntegerType.INTEGER
							)
						)
					)
				)
			)
		);
	}

	@Test
	@Override
	public void testQuestionVariable() {
		assertTrue(
			typeCheck(
				new VariableQuestion(
					new StringLiteral( "label" ),
					new VariableDeclaration(
						new IdentifierExpression( "var" ),
						BooleanType.BOOLEAN
					)
				)
			)
		);
	}

	@Test
	@Override
	public void testQuestionComputed() {
		assertTrue(
			typeCheck(
				new ComputedQuestion(
					new StringLiteral( "label" ),
					new Assignment(
						new IdentifierExpression( "x" ),
						new AddExpression(
							new IntegerLiteral( 6 ),
							new MoneyLiteral( 19.99 )
						)
					)
				)
			)
		);

		assertFalse(
			typeCheck(
				new ComputedQuestion(
					new StringLiteral( "label" ),
					new Assignment(
						new IdentifierExpression( "x" ),
						new AddExpression(
							new IdentifierExpression( "y" ), // undefined variable
							new IdentifierExpression( "z" )  // undefined variable
						)
					)
				)
			)
		);
	}

	@Test
	@Override
	public void testAssignment() {
		assertTrue( typeCheck( new Assignment( new IdentifierExpression( "x" ), new IntegerLiteral( 99 ) ) ) );
		assertTrue(
			typeCheck(
				new Assignment(
					new IdentifierExpression( "y" ),
					new AndExpression( new BooleanLiteral( true ), new BooleanLiteral( false ) )
				)
			)
		);
		assertTrue(
			typeCheck(
				new Assignment(
					new IdentifierExpression( "y" ),
					new EqualExpression( new StringLiteral( "" ), new StringLiteral( "" ) )
				)
			)
		);

		// variable 'x' is already declared as type Integer
		assertFalse( typeCheck( new Assignment( new IdentifierExpression( "x" ), new BooleanLiteral( true ) ) ) );
		// variable 'y' is already declared as type Boolean
		assertFalse(
			typeCheck(
				new Assignment(
					new IdentifierExpression( "y" ),
					new MultiplyExpression( new IdentifierExpression( "x" ), new IntegerLiteral( 4 ) )
				)
			)
		);
	}
}
