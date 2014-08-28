package com.example;

public class RowCol {

	public RowCol( ) {
		row = 0;
		col = 0;
	}
	
	public RowCol( int row, int col ) {
		this.row = row;
		this.col = col;
	}
	public int row;
	public int col;

	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getCol() {
		return col;
	}
	public void setCol(int col) {
		this.col = col;
	}

	@Override
	public String toString() {
		return "RowCol row=" + row + ",col=" + col; 
	}
	
	@Override
	public int hashCode() {
		return row + (101 * col);
	}
	
	@Override
    public boolean equals( final Object object ) {
		if ( object == null ) return false;
        return RowCol.class.isAssignableFrom( object.getClass() ) && 
        	equals( (RowCol) object );
    }

    public boolean equals( final RowCol rowcol ) {
       if ( rowcol == null ) return false;

       return (( this.row == rowcol.row) && (this.col == rowcol.col));
   }
}