package net.jeffreymjohnson.tilegame;

public class TilePosition {
    int column;
    int row;
    
    public TilePosition(int aColumn, int aRow){
	column = aColumn;
	row = aRow;
    }
    
    @Override
    public String toString(){
	String result = "TilePosition[column:" + 
			column +
			" row: " +
			row +
			"]";
	return result;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + column;
	result = prime * result + row;
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (!(obj instanceof TilePosition))
	    return false;
	TilePosition other = (TilePosition) obj;
	if (column != other.column)
	    return false;
	if (row != other.row)
	    return false;
	return true;
    }
    
}
