package com.example;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Layout;

/**
 * A component with buttons for adding/removing.
 * The component can be layout out horizontally (COL) or vertically (ROW).
 * The component will notify a listener with its internal information
 * (name, orientation, index, +/-, etc.). 
 */
@SuppressWarnings("serial")
public class ExpandContractControls extends CustomComponent 
    implements Button.ClickListener {

	public static final String EXPAND = "+";
	public static final String CONTRACT = "-";
	
	public enum Orientation {
		COL, ROW
	}
	
	public enum Position {
		BEFORE, CURRENT, AFTER
	}
	
	protected String name;  // example, col	
	protected int index; // example 0..n	
	protected Orientation orientation; // example ROW or COL
	protected Position position; // example BEFORE, CURRENT, or AFTER, used for adding before or after index

	protected Layout layout;
    protected Button contract = new Button( CONTRACT );
    protected Button expand = new Button( EXPAND );
	
	protected ExpandContractControls () {	
	}
	
	public ExpandContractControls( String name, 
		Orientation orientation, int index, Position position) {
		setName( name );
		setOrientation( orientation );
		setIndex( index );
		setPosition( position );
		
		contract.setStyleName( "v-button-small" );
		contract.addClickListener( this );
		expand.setStyleName( "v-button-small" );
		expand.addClickListener( this );
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
	public Orientation getOrientation() {
		return orientation;
	}

	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
		
		if ( null != layout)
			layout.removeAllComponents();
		if ( orientation == Orientation.COL) {
			GridLayout gLayout = new GridLayout( 3, 1 );
			layout = gLayout;
			// gLayout.addComponent( new Label( "COL" ), 0, 0 ); 
			gLayout.addComponent( contract, 1, 0 ); 
			gLayout.addComponent( expand, 2, 0  );
			gLayout.setColumnExpandRatio( 0,  10 );
			gLayout.setColumnExpandRatio( 1,  0 );
			gLayout.setColumnExpandRatio( 2,  0 );
		} else {
			GridLayout gLayout = new GridLayout( 1, 3 );
			layout = gLayout;
			// gLayout.addComponent( new Label( " " ), 0, 0 ); 
			gLayout.addComponent( contract, 0, 1 ); 
			gLayout.addComponent( expand, 0, 2 );
			gLayout.setRowExpandRatio( 0,  10 );
			gLayout.setRowExpandRatio( 1,  0 );
			gLayout.setRowExpandRatio( 2,  0 );
		}
        layout.setSizeUndefined();
        setSizeUndefined();
        setCompositionRoot( layout );
	}
	
	public static String alignToString( Alignment val ) {
		StringBuffer sb = new StringBuffer();
		if ( val.isTop() ) sb.append( "TOP_" );
		if ( val.isMiddle() ) sb.append( "MIDDLE_" );
		if ( val.isBottom() ) sb.append( "BOTTOM_" );
		if ( val.isLeft() ) sb.append( "LEFT" );
		if ( val.isCenter() ) sb.append( "CENTER" );
		if ( val.isRight() ) sb.append( "RIGHT" );
		return sb.toString();
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition( Position position ) {
		this.position = position;
	}

	public void buttonClick(ClickEvent event) {
		Component comp = event.getButton();
		if ( expand.equals( comp ) || contract.equals( comp ) ) {
			// System.out.println( "ExpandContractButtons clickEvent=" + getName() + " "
			//    + getOrientation().toString() + " " + getIndex() + " " + comp.getCaption());
	        
			// Aggregate and pass on child event to my listeners
			// fireEvent(event);			
			// fireEvent( new Component.Event( this ));
			Position desiredPosition = Position.CURRENT;
			if ( expand.equals( comp ) ) {
				if ( event.isAltKey() || event.isCtrlKey() || event.isMetaKey() || event.isShiftKey() )
					desiredPosition = Position.BEFORE;
				else
					desiredPosition = Position.AFTER;
			}
			fireEvent( new ExpandContractEvent( this, 
				getName(), getOrientation(), getIndex(), desiredPosition, comp.getCaption() ));			
		} else {			
			// comp.getWindow().showNotification( "Unknown click from component=" + comp );;
		}
	}
	

	@Override
	public String toString() {
		return "ExpandContractButtons " +
				"name=" + name + 
				",orientation=" + orientation.toString() + 
				",index=" + index +  
				",position=" + position.toString(); 
	}

	/** This event aggregates information from this component and child controls. */
    public static class ExpandContractEvent extends Component.Event {
		public ExpandContractEvent(Component source, 
			String name, Orientation orientation, int index, Position position, String childName) {
			super(source);
			this.name = name;
			this.orientation = orientation;
			this.index = index;
			this.position = position;
			this.childName = childName;			
		}
		
		@Override
		public String toString() {
			return "ExpandContractEvent " +
				"name=" + name + 
				",orientation=" + orientation.toString() + 
				",index=" + index + 
				",position=" + position.toString() + 
				",childName=" + childName;			
		}
		
		String name;  // example, col	
		Orientation orientation; // example ROW or COL
		int index; // example 0..n	
		Position position;  // example BEFORE, CURRENT, or AFTER. Aids in adding before index.
		String childName; // example + -, button text
    }
}