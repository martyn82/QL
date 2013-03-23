package org.uva.sea.ql.evaluate.render;

import org.uva.sea.ql.ast.statement.Statement;
import org.uva.sea.ql.ui.ButtonControlEventListener;
import org.uva.sea.ql.ui.ControlFactory;
import org.uva.sea.ql.ui.control.ButtonControl;
import org.uva.sea.ql.ui.control.PanelControl;

public class Form {
	private final ControlFactory factory;
	private final StatementRenderer renderer;

	public Form( Statement root, ControlFactory factory ) {
		this.factory = factory;
		this.renderer = new StatementRenderer( factory, new RuntimeEnvironment() );
		root.accept( this.renderer );
	}

	public void addButton( String text ) {
		ButtonControl button = this.factory.createButton( text );
		this.getPanel().addControl( button );
	}

	public void addButton( String text, ButtonControlEventListener clickListener ) {
		ButtonControl button = this.factory.createButton( text );
		button.addClickListener( clickListener );
		this.getPanel().addControl( button );
	}

	public RuntimeValueMap getValues() {
		return this.renderer.getValues();
	}

	public String getName() {
		return this.renderer.getFormName();
	}

	public PanelControl getPanel() {
		return this.renderer.getFormPanel();
	}
}
