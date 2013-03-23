package org.uva.sea.ql.ui.control;

import org.uva.sea.ql.ui.InputControlEventListener;
import org.uva.sea.ql.value.Value;

abstract public class InputControl extends Control {
	abstract public Value getValue();
	abstract public void setValue( Value value );
	abstract public void addChangeListener( final InputControlEventListener listener );
}
