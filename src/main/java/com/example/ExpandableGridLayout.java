package com.example;

import com.vaadin.shared.ui.gridlayout.GridLayoutState.ChildComponentData;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;

/**
 * A component with buttons for adding/removing.
 * The component can be layout out horizontally (COL) or vertically (ROW).
 * The component will notify a listener with its name, orientation, index, and +/-. 
 * <p>
 * Care must be taken when specifying row and columns.
 * Externally, rows/cols start at 0 based on externally provided components.
 * Internally, a row/col at 0 contains the expand/contract controls, and
 * the external components begin at row/col 1.
 */
@SuppressWarnings("serial")
public class ExpandableGridLayout extends GridLayout 
    implements Component.Listener {
	
	protected ExpandableGridLayout ( ) {	
	}
	
	protected ExpandableGridLayout ( int rows, int columns ) {
		createGrid( rows, columns );
	}
	
	/** Creates a grid of the given count of user components. 
	 * Note, no component event is fired to tell containers to add components. */
    protected void createGrid( int rows, int cols) {
    	if (( rows < 1 ) || ( cols < 1 ))
            throw new IllegalArgumentException( "CreateGrid row,col count \"" + rows + "," + cols + "\" is out of range (1+,1+)" );
        this.removeAllComponents();
        this.setRows(rows + 1);
        this.setColumns(cols + 1);
        // Add expand and contract controls
        for (int row = 0; row < rows; row++) {
        	Component comp = new ExpandContractControls( "r" + row,
          		ExpandContractControls.Orientation.ROW, row, ExpandContractControls.Position.CURRENT);
            comp.setStyleName( "expandcontractstyle" );
    		comp.addListener(this);

        	this.addComponent( comp, 0, row + 1); 
        	this.setComponentAlignment(comp, Alignment.BOTTOM_RIGHT);
            // this.setRowExpandRatio( row + 1, 1.0f );
        }
        // Add expand and contract columns
        for (int col = 0; col < cols; col++) {
        	Component comp = new ExpandContractControls( "c" + col,
           		ExpandContractControls.Orientation.COL, col, ExpandContractControls.Position.CURRENT ); 
    		comp.addListener(this);
            comp.setStyleName( "expandcontractstyle" );
        	this.addComponent( comp, col + 1, 0 );
        	this.setComponentAlignment(comp, Alignment.BOTTOM_RIGHT);
            // this.setColumnExpandRatio( col + 1, 1.0f );
        }
        // Set row/col expand ratios.
        for ( int i = 0; i < this.getRows(); i++ ) {
            this.setRowExpandRatio( i, i == 0 ? 0 : 1 );
        }
        for ( int i = 0; i < this.getColumns(); i++ ) {
            this.setColumnExpandRatio( i, i == 0 ? 0 : 1 );
        }
    }
    
    /** Adjust even and odd row names. Ignore control row/cols.*/
    public void adjustStyleNames() {
        for (int row = 1; row < this.getRows(); row++) {
        	// Mutliple styles delimited by spaces.
        	String rowstyle = ((row-1) % 2 == 0) ? "contentstyle evenrow " : "contentstyle oddrow ";
            for (int col = 1; col < this.getColumns(); col++) {
            	String colstyle = ((col-1) % 2 == 0) ? "evencol" : "oddcol";
                Component child = this.getComponent(col, row);
                if ( null != child)
                   child.setStyleName( rowstyle + colstyle );
            }
        }
    }
    
    /** Adds a row before/after the given index.  Takes into account the implicit ExpandContractControl row 0. */
    protected void addRow( String originator, int row, boolean before ) {
    	if (( row < 0 ) || ( row >= this.getRows() - 1))
            throw new IllegalArgumentException( "Add row \"" + row + "\" is out of range (0," + (this.getRows() - 1) + ").");
    	
        // Add expand and contract columns
    	ExpandContractControls ecCurr = getRowControl( row + 1 );
    	int workingRow = before ? row + 1 : row + 2;
   		this.insertRow(workingRow);
    	
       	Component comp = new ExpandContractControls( ecCurr.getName() + "-" + row,
           ExpandContractControls.Orientation.ROW, workingRow - 1, ExpandContractControls.Position.CURRENT ); 
   		comp.addListener(this);
       	this.addComponent( comp, 0, workingRow );
       	this.setComponentAlignment(comp, Alignment.BOTTOM_RIGHT);
       	
       	// Adjust ecComponent indexes after the working row.
        for( int i = workingRow; i < this.getRows(); i++ ) {
  		   ExpandContractControls ecComp = getRowControl(i);
		   ecComp.setIndex(i - 1); // technically not needed for first working row.
    	   this.setRowExpandRatio( i, 1 );
  	    }

    	System.out.println( "ExpandableGrideLayout added row " + (workingRow - 1) );    	
    }
    /** Adds a col before/after the given index. Takes into account the implicit ExpandContractButton col 0. */
    protected void addCol( String originator, int col, boolean before ) {
    	if (( col < 0 ) || ( col >= this.getColumns() - 1))
            throw new IllegalArgumentException( "Add col \"" + col + "\" is out of range (0," + (this.getColumns() - 1) + ").");
    	
        // Add expand and contract row
    	ExpandContractControls ecCurr = getColumnControl( col + 1 );
    	int workingCol = before ? col + 1 : col + 2;
   		this.insertCol(workingCol);
    	
       	Component comp = new ExpandContractControls( ecCurr.getName() + "-" + col,
           ExpandContractControls.Orientation.COL, workingCol - 1, ExpandContractControls.Position.CURRENT ); 
   		comp.addListener(this);
       	this.addComponent( comp, workingCol, 0 );
       	this.setComponentAlignment(comp, Alignment.BOTTOM_RIGHT);
       	
       	// Adjust ecComponent indexes after the working row.
        for( int i = workingCol; i < this.getColumns(); i++ ) {
  		   ExpandContractControls ecComp = getColumnControl(i);
		   ecComp.setIndex(i - 1); // technically not needed for first working col.
    	   this.setColumnExpandRatio( i, 1 );
  	    }

    	System.out.println( "ExpandableGrideLayout added col " + (workingCol - 1) );    	
    }
    /** Deletes the row at the given index. Takes into account the implicit ExpandContractButton row 0. */
    protected void deleteRow( int row ) {
    	if (( row < 0 ) || ( row >= this.getRows() - 1))
            throw new IllegalArgumentException( "Delete row \"" + row + "\" is out of range (0," + (this.getRows() - 1) + ").");
    	// Must avoid deleting row 0 of expand/contract buttons and also last row.
    	if ( this.getRows() > 2 ) {
     	   // Super does not forget examples of last row
    	   this.setRowExpandRatio( this.getRows() - 1, 0 );
     	   this.removeRow(row + 1); // super
     	   // Need to adjust row numbers on remaining expand/contract buttons
     	   for( int i = row + 1; i < this.getRows(); i++ ) {
      		   ExpandContractControls ecComp = getRowControl(i);
 			   ecComp.setIndex(i - 1);
     	   }
           this.adjustStyleNames();
     	   System.out.println( "ExpandableGrideLayout deleted row " + row );
    	}    	
    }
    /** Deletes the col at the given index. Takes into account the implicit ExpandContractButton col 0. */
    protected void deleteCol( int col ) {
    	if (( col < 0 ) || ( col >= this.getColumns() - 1))
           throw new IllegalArgumentException( "Delete column \"" + col + "\" is out of range (0," + (this.getColumns() - 1) + ").");
    	// Must avoid deleting row 0 of expand/contract buttons and also last row.
    	if ( this.getColumns() > 2 ) {
      	   // Super does not forget examples of last col
     	   this.setColumnExpandRatio( this.getColumns() - 1, 0 );
    	   this.removeCol( col + 1); // does not exist in super!
      	   // Need to adjust col numbers on remaining expand/contract buttons
      	   for( int i = col + 1; i < this.getColumns(); i++ ) {
      		   ExpandContractControls ecComp = getColumnControl(i);
 			   ecComp.setIndex(i - 1);
      	   }
           this.adjustStyleNames();
     	   System.out.println( "ExpandableGrideLayout deleted col " + col );
    	}
    }
    /** Missing in super class */
    public void removeCol(int col) {
        if (col >= getColumns()) {
            throw new IllegalArgumentException("Cannot delete column " + col
                    + " from a gridlayout with width " + getColumns());
        }

        // Remove all components in col
        for (int row = 0; row < getRows(); row++) {
            removeComponent(col, row);
        }

        // Shrink or remove areas in the selected row
        for (ChildComponentData existingArea : getState().childData.values()) {
            if (existingArea.column2 >= col) {
                existingArea.column2--;

                if (existingArea.column1 > col) {
                    existingArea.column1--;
                }
            }
        }

        if (getColumns() == 1) {
            /*
             * Removing the last col means that the dimensions of the Grid
             * layout will be truncated to 1 empty row and the cursor is moved
             * to the first cell
             */
            // cursorX = 0;  // private
            // cursorY = 0;
        } else {
            setColumns(getColumns() - 1);
            // if (cursorY > row) {
            //     cursorY--;
            // }
        }
        markAsDirty();
    }
    /** Missing in super class */
    public void insertCol(int col) {
        if (col > getColumns()) {
            throw new IllegalArgumentException("Cannot insert col at " + col
                    + " in a gridlayout with width " + getColumns());
        }

        for (ChildComponentData existingArea : getState().childData.values()) {
            // Areas ending below the row needs to be moved down or stretched
            if (existingArea.column2 >= col) {
                existingArea.column2++;

                // Stretch areas that span over the selected row
                if (existingArea.column1 >= col) {
                    existingArea.column1++;
                }

            }
        }

        // if (cursorX >= col) {
        //     cursorX++;
        // }

        setColumns(getColumns() + 1);
        markAsDirty();
    }

    /** Gets the col control of the internally numbered col. */
    public ExpandContractControls getColumnControl( int col ) {
    	if (( col < 1 ) || ( col >= this.getColumns() ))
            throw new IllegalArgumentException( "Column \"" + col + "\" is out of range (1," + (this.getColumns() - 1) + ").");
    	
	   Component comp = this.getComponent(col, 0);
 	   if (( comp == null ) || !ExpandContractControls.class.isAssignableFrom( comp.getClass() )) 
		   throw new IllegalArgumentException( "Expected expand/contract control at row 0, column \"" + col + "\".");
 	   
	   return (ExpandContractControls) comp;
    }

    /** Gets the row control of the internally numbered row. */
    public ExpandContractControls getRowControl( int row ) {
    	if (( row < 1 ) || ( row >= this.getRows() ))
            throw new IllegalArgumentException( "Row \"" + row + "\" is out of range (1," + (this.getRows() - 1) + ").");
    	
	   Component comp = this.getComponent(0, row);
 	   if (( comp == null ) || !ExpandContractControls.class.isAssignableFrom( comp.getClass() )) 
		   throw new IllegalArgumentException( "Expected expand/contract control at row \"" + row + "\", column 0.");
 	   
	   return (ExpandContractControls) comp;
    }
    
    /** Handles a request to add or delete rows or columns. */
    public void handleEvent( ExpandContractControls.ExpandContractEvent ecEvent ) {
		// System.out.println( "DOB: expGL handleEvent=" + ecEvent.toString() );
    	
    	if ( ExpandContractControls.EXPAND.equals( ecEvent.childName ) ) {
    		if ( ExpandContractControls.Orientation.COL.equals( ecEvent.orientation )) {
               	addCol( ecEvent.name, ecEvent.index, ExpandContractControls.Position.BEFORE.equals( ecEvent.position ) );   			
    		} else if ( ExpandContractControls.Orientation.ROW.equals( ecEvent.orientation )) {
               	addRow( ecEvent.name, ecEvent.index, ExpandContractControls.Position.BEFORE.equals( ecEvent.position ) );
    		} else {
    			System.out.println( "Unknown orientation event=" + ecEvent );
    		}

    	} else if ( ExpandContractControls.CONTRACT.equals( ecEvent.childName ) ) {
    		if ( ExpandContractControls.Orientation.COL.equals( ecEvent.orientation )) {
    			deleteCol( ecEvent.index );   			    			
    		} else if ( ExpandContractControls.Orientation.ROW.equals( ecEvent.orientation )) {
    			deleteRow( ecEvent.index );   			    			
    		} else {
    			System.out.println( "Unknown orientation event=" + ecEvent );
    		}  		    		
    	} else {
    		System.out.println( "Unknown child control event=" + ecEvent );    		
    	}
      	
    }

    /* This method uses external (0-based) component numbering. Internally,
     * the given coordinates make room for the expand/contract controls. */
    public void addContent( Component comp, final int row, final int col) {
        // Add contents
        this.addComponent( comp, col + 1, row + 1 );
       	this.setComponentAlignment( comp, Alignment.MIDDLE_CENTER);
       	// Would be nice to aggregate this.
        this.adjustStyleNames();
    }
    
    /* Returns external (0 based) numbering of this component.
     * Returns null if component not found. */
    public RowCol getContentRowCol( final Component comp ) {
        // Search content components
    	for ( int row = 1; row < this.getRows(); row++ ) {
        	for ( int col = 1; col < this.getColumns(); col++ ) {
        		Component content = this.getComponent( row, col);
        		if ( comp.equals( content ) )
        			return new RowCol( row - 1, col - 1 );
    		}
    	}
        return null;
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
			// Adjust and rebroadcast event data for consumers.
			if (ExpandContractControls.Position.AFTER.equals( ecEvent.position )) {
				ecEvent.index += 1;
			}
			ecEvent.position = ExpandContractControls.Position.CURRENT;
			fireEvent( ecEvent );
		}		
	}    
}