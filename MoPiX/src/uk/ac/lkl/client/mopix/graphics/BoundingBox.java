package uk.ac.lkl.client.mopix.graphics;

/**
 * Question: why not use a Rectangle2D.Double?
 * 
 * Answer: GWT doesn't have direct support for it
 * 
 */
public class BoundingBox {

    double left, top, right, bottom;

    public BoundingBox(double left, double right, double top, double bottom) {
	this.left = left;
	this.top = top;
	this.right = right;
	this.bottom = bottom;
    }

    public boolean intersectsWith(BoundingBox other) {
	if (getLeft() > other.getRight())
	    return false;
	if (other.getLeft() > getRight())
	    return false;
	if (getTop() > other.getBottom())
	    return false;
	if (other.getTop() > getBottom())
	    return false;
	return true;
    };

    public String toString() {
	return ("left: " + getLeft() + "; right: " + getRight() + "; top: "
		+ getTop() + "; bottom: " + getBottom());
    }

    public double getLeft() {
	return left;
    }

    public double getTop() {
	return top;
    }

    public double getRight() {
	return right;
    }

    public double getBottom() {
	return bottom;
    }
}
