package net.jeffreymjohnson.tilegame;

import android.graphics.Bitmap;

public class Tile {
    TilePosition currentPosition, correctPosition;
    Bitmap image;
    boolean isBlankTile;
    
    public Tile(Bitmap image, TilePosition currentPosition, TilePosition correctPosition, boolean isBlank){
	this.currentPosition = currentPosition;
	this.correctPosition = correctPosition;
	this.image = image;
	this.isBlankTile = isBlank;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result
		+ ((correctPosition == null) ? 0 : correctPosition.hashCode());
	result = prime * result
		+ ((currentPosition == null) ? 0 : currentPosition.hashCode());
	result = prime * result + (isBlankTile ? 1231 : 1237);
	return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (!(obj instanceof Tile))
	    return false;
	Tile other = (Tile) obj;
	if (correctPosition == null) {
	    if (other.correctPosition != null)
		return false;
	} else if (!correctPosition.equals(other.correctPosition))
	    return false;
	if (currentPosition == null) {
	    if (other.currentPosition != null)
		return false;
	} else if (!currentPosition.equals(other.currentPosition))
	    return false;
	if (isBlankTile != other.isBlankTile)
	    return false;
	if (image == null){
	    if (other.image != null)
		return false;
	} else if (!isBitmapSame(other))
	    return false;
	return true;
    }
    
    private boolean isBitmapSame(Tile testTile){
	if (this == testTile)
	    return true;
	if (testTile == null)
	    return false;
	if (this.image == null){
	    if (testTile.image != null)
		return false;
	} else if (testTile.image == null)
	    return false;
	else if (this.image.getHeight() != testTile.image.getHeight())
	    return false;
	else if (this.image.getWidth() != testTile.image.getWidth())
	    return false;
	if (this.image.getConfig() == null){
	    if (testTile.image.getConfig() != null)
		return false;
	} else if (!testTile.image.getConfig().equals(testTile.image.getConfig()))
		return false;
	for (int row = 0; row > this.image.getHeight(); row++){
	    for (int col = 0; col < this.image.getWidth(); col++){
		if (this.image.getPixel(row, col) != testTile.image.getPixel(row, col))
		    return false;
	    }
	}
	return true;
//	return this.image.sameAs(testTile.image);
    }

    @Override
    public String toString(){
	String result = "Tile:[current position: ";
	if (currentPosition == null)
	    result += "null";
	else
	    result += currentPosition.toString();
	result += ", correct position: ";
	if (correctPosition == null)
	    result += "null";
	else
	 result += correctPosition.toString();
	result += ", image: ";
	if (image == null)
	    result += "null";
	else
	    result += image.toString();
	result += "]";
	return result;
    }
}
