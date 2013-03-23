package org.uva.sea.ql.ui.swing;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;

import org.uva.sea.ql.ui.ControlEvent;
import org.uva.sea.ql.ui.InputControlEventListener;
import org.uva.sea.ql.ui.control.CheckBoxControl;
import org.uva.sea.ql.value.BooleanValue;
import org.uva.sea.ql.value.Value;

public class JCheckBoxControl extends CheckBoxControl {
	private final JCheckBox control;

	public JCheckBoxControl() {
		this.control = new JCheckBox();
	}

	@Override
	public JCheckBox getInnerControl() {
		return this.control;
	}

	@Override
	public void setValue( Value value ) {
		if ( value.isDefined() ) {
			this.control.setSelected( ( (BooleanValue) value ).getValue() );
		}
	}

	@Override
	public BooleanValue getValue() {
		return new BooleanValue( this.control.isSelected() );
	}

	@Override
	public void setEnabled( boolean enabled ) {
		this.control.setEnabled( enabled );
	}

	@Override
	public void setVisible( boolean visible ) {
		this.control.setVisible( visible );
	}

	@Override
	public void addChangeListener( final InputControlEventListener listener ) {
		this.control.addItemListener(
			new ItemListener() {
				@Override
				public void itemStateChanged( ItemEvent itemEvent ) {
					listener.valueChanged( new ControlEvent( JCheckBoxControl.this ) );
				}
			}
		);
	}
}
