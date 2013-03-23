package org.uva.sea.ql.evaluate.render;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.uva.sea.ql.StatementTest;
import org.uva.sea.ql.ast.expression.IdentifierExpression;
import org.uva.sea.ql.ast.expression.binary.arithmetic.AddExpression;
import org.uva.sea.ql.ast.expression.binary.comparison.EqualExpression;
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
import org.uva.sea.ql.ast.type.MoneyType;
import org.uva.sea.ql.ast.type.StringType;
import org.uva.sea.ql.ui.swing.SwingControlFactory;

public class StatementRendererTest extends EvaluatorTest implements StatementTest {
	private final FormBuilder builder;

	public StatementRendererTest() {
		super( new RuntimeEnvironment() );
		this.builder = new FormBuilder( new SwingControlFactory(), this.environment );
	}

	private void eval( Statement statement ) {
		StatementRenderer.renderPart( statement, this.builder, this.environment );
	}

	@Test
	@Override
	public void testIfThen() {
		/*
		 * if ( true ) {
		 * 		x = 1
		 * }
		 */
		eval(
			new IfThen(
				new BooleanLiteral( true ),
				new Statements(
					new Assignment(
						new IdentifierExpression( "x" ),
						new IntegerLiteral( 1 )
					)
				)
			)
		);
		assertEquals( 1, eval( new IdentifierExpression( "x" ) ) ); // x is assigned to be 1
	}

	@Test
	@Override
	public void testIfThenElse() {
		/*
		 * if ( false ) {
		 * 		x = 1
		 * }
		 * else if ( true ) {
		 * 		x = 2
		 * }
		 */
		eval(
			new IfThenElse(
				new BooleanLiteral( false ),
				new Statements(
					new Assignment(
						new IdentifierExpression( "x" ),
						new IntegerLiteral( 1 )
					)
				),
				new Statements(
					new IfThen(
						new BooleanLiteral( true ),
						new Statements(
							new Assignment(
								new IdentifierExpression( "x" ),
								new IntegerLiteral( 2 )
							)
						)
					)
				)
			)
		);
		assertEquals( 2, eval( new IdentifierExpression( "x" ) ) ); // x is assigned to be 2

		/*
		 * if ( false ) {
		 * 		x = 1
		 * }
		 * else if ( true == false ) {
		 * 		x = 2
		 * }
		 * else {
		 * 		x = 10
		 * }
		 */
		eval(
			new IfThenElse(
				new BooleanLiteral( false ),
				new Statements(
					new Assignment(
						new IdentifierExpression( "x" ),
						new IntegerLiteral( 3 )
					)
				),
				new Statements(
					new IfThenElse(
						new EqualExpression( new BooleanLiteral( true ), new BooleanLiteral( false ) ),
						new Statements(
							new Assignment(
								new IdentifierExpression( "x" ),
								new IntegerLiteral( 4 )
							)
						),
						new Statements(
							new Assignment(
								new IdentifierExpression( "x" ),
								new IntegerLiteral( 10 )
							)
						)
					)
				)
			)
		);
		assertEquals( 10, eval( new IdentifierExpression( "x" ) ) ); // x is assigned to be 10
	}

	@Test
	@Override
	public void testFormDeclaration() {
		eval( new FormDeclaration( "form1", new Statements() ) );
	}

	@Test
	@Override
	public void testVarDeclaration() {
		eval( new VariableDeclaration( new IdentifierExpression( "x" ), BooleanType.BOOLEAN ) );
		assertEquals( false, eval( new IdentifierExpression( "x" ) ) );

		eval( new VariableDeclaration( new IdentifierExpression( "y" ), IntegerType.INTEGER ) );
		assertEquals( 0, eval( new IdentifierExpression( "y" ) ) );

		eval( new VariableDeclaration( new IdentifierExpression( "z" ), StringType.STRING ) );
		assertEquals( "", eval( new IdentifierExpression( "z" ) ) );

		eval( new VariableDeclaration( new IdentifierExpression( "u" ), MoneyType.MONEY ) );
		assertEquals( 0d, eval( new IdentifierExpression( "u" ) ) );
	}

	@Test
	@Override
	public void testAssignment() {
		eval( new Assignment( new IdentifierExpression( "x" ), new StringLiteral( "hello world!" ) ) );
		assertEquals( "hello world!", eval( new IdentifierExpression( "x" ) ) );
	}

	@Test
	@Override
	public void testQuestionVariable() {
		eval(
			new VariableQuestion(
				new StringLiteral( "Foo bar?" ),
				new VariableDeclaration( new IdentifierExpression( "x" ), BooleanType.BOOLEAN )
			)
		);
		assertEquals( false, eval( new IdentifierExpression( "x" ) ) );

		eval(
			new VariableQuestion(
				new StringLiteral( "Foo bar?" ),
				new VariableDeclaration( new IdentifierExpression( "x" ), IntegerType.INTEGER ) )
		);
		assertEquals( 0, eval( new IdentifierExpression( "x" ) ) );
	}

	@Test
	@Override
	public void testQuestionComputed() {
		eval(
			new ComputedQuestion(
				new StringLiteral( "Foo Bar?" ),
				new Assignment(
					new IdentifierExpression( "y" ),
					new EqualExpression( new BooleanLiteral( true ), new BooleanLiteral( true ) ) )
				)
		);
		assertEquals( true, eval( new IdentifierExpression( "y" ) ) );

		eval(
			new ComputedQuestion(
				new StringLiteral( "Foo Bar?" ),
				new Assignment(
					new IdentifierExpression( "y" ),
					new AddExpression( new IntegerLiteral( 12 ), new MoneyLiteral( 23.4 ) ) )
				)
		);
		assertEquals( 35.4, eval( new IdentifierExpression( "y" ) ) );
	}
}
