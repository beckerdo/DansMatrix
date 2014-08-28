package com.example;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

@Theme("mytheme")
@SuppressWarnings("serial")
public class ExpandableGridExample extends UI 
	implements Component.Listener {

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = ExpandableGridExample.class, widgetset = "com.example.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    protected ExpandableGridLayout grid; 
    
    @Override
    protected void init(VaadinRequest request) {
    	int ROWS = 3;
    	int COLS = 3;
		grid = new ExpandableGridLayout(ROWS, COLS);
        grid.addStyleName("expandablegridstyle");
        grid.setSizeFull();
        // Add contents.
        for (int row = 0; row < ROWS; row++) {
        	// Mutliple styles delimited by spaces.
            for (int col = 0; col < COLS; col++) {
                Label child = new Label( "Component " + row + "," + col);
                grid.addContent( child, row, col );
            }
        }
        grid.addListener( this );		
		setContent( grid );
    }
    
	@Override
	/** Handles events from expand/contract controls.
	 * Fires events so containers can add content components to new row/cols.
	 */
	public void componentEvent(Event event) {
		// System.out.println( "ExpandableGridLayout event=" + event.toString() );
		Component comp = event.getComponent();
		if ( ExpandContractControls.class.isAssignableFrom( comp.getClass() )) {
			ExpandContractControls.ExpandContractEvent ecEvent = 
				(ExpandContractControls.ExpandContractEvent) event;
			
			// System.out.println( "ExpandableGridLayout event=" + ecEvent.toString() );
			handleEvent( ecEvent );
		}		
	}    
	
    /** Handles a request to add or delete rows or columns. */
    public void handleEvent( ExpandContractControls.ExpandContractEvent ecEvent ) {
		// System.out.println( "DOB: Example.handleEvent=" + ecEvent.toString() );
    	
    	if ( ExpandContractControls.EXPAND.equals( ecEvent.childName ) ) {
    		if ( ExpandContractControls.Orientation.COL.equals( ecEvent.orientation )) {
    			// System.out.println( "DOB: Example.handleEvent addCol index=" + ecEvent.index );    		
    	        // Add contents (adjust getRows for controls).
    	        for (int row = 0; row < grid.getRows() - 1; row++) {
    	            Label child = new Label( "Component " + row + "," + ecEvent.index );
    	            grid.addContent( child, row, ecEvent.index );
    	        }
    		} else if ( ExpandContractControls.Orientation.ROW.equals( ecEvent.orientation )) {
    			// System.out.println( "DOB: Example.handleEvent addRow index=" + ecEvent.index );
    	        // Add contents (adjust getColumns for controls).
    	        for (int col = 0; col < grid.getColumns() - 1; col++) {
    	            Label child = new Label( "Component " + ecEvent.index + "," + col );
    	            grid.addContent( child, ecEvent.index , col );
    	        }

    		} else {
    			System.out.println( "Unknown orientation event=" + ecEvent );
    		}

    	} else if ( ExpandContractControls.CONTRACT.equals( ecEvent.childName ) ) {
    		if ( ExpandContractControls.Orientation.COL.equals( ecEvent.orientation )) {
    			// System.out.println( "DOB: Example.handleEvent delCol index=" + ecEvent.index );    		
    		} else if ( ExpandContractControls.Orientation.ROW.equals( ecEvent.orientation )) {
    			// System.out.println( "DOB: Example.handleEvent delRow index=" + ecEvent.index );    		
    		} else {
    			System.out.println( "Unknown orientation event=" + ecEvent );
    		}  		    		
    	} else {
    		System.out.println( "Unknown child control event=" + ecEvent );    		
    	}
    }
}