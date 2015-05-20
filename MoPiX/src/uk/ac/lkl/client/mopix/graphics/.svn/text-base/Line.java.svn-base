package uk.ac.lkl.client.mopix.graphics;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;

import uk.ac.lkl.client.mopix.ModellerAbsolutePanel;
import uk.ac.lkl.client.mopix.graphics.Shape;

public class Line extends Shape {

    private double left;
    private double bottom;
    private double right;
    private double top;

    public Line(double x1, double y1, double x2, double y2) {
	super();
	shapeCode = LINE;
	if (x1 < x2) {
	    left = x1;
	    right = x2;
	} else {
	    left = x2;
	    right = x1;
	}
	if (y1 < y2) {
	    bottom = y1;
	    top = y2;
	} else {
	    bottom = y2;
	    top = y1;
	}
    }

    public void addTo(ModellerAbsolutePanel area) {
	canvas = Canvas.createIfSupported();
	int strokeWidth = getStroke().getWidth();
	int width = (int) getWidth()+strokeWidth;
	int height = (int) getHeight()+strokeWidth;
	canvas.setPixelSize(width, height);
	canvas.setCoordinateSpaceWidth(width);
	canvas.setCoordinateSpaceHeight(height);
	area.add(canvas, (int) left-area.getAbsoluteLeft(), (int) bottom+strokeWidth-area.getAbsoluteTop());
	display();
    }
    
    public void remove() {
	getCanvas().removeFromParent();
    }

    public double getWidth() {
	return right-left;
    }
    
    public double getHeight() {
	return top-bottom;
    }
    
    @Override
    public void display() {
	// consider optimising based on the fact that this should never change
	// so only need to do this once
	Context2d context2d = canvas.getContext2d();
	context2d.save();
	Stroke stroke = getStroke();
	if (stroke != null) {
	    context2d.setStrokeStyle(stroke.getColor());
	    context2d.setLineWidth(stroke.getWidth());
	} else {
	    // don't think this can happen
	    // not clear if stroke is still generally useful or should be folded into this class
	}
	context2d.moveTo(0, 0);
	context2d.lineTo(right-left, top-bottom);
	context2d.stroke();
	// for debugging
//	context2d.setFillStyle(Utils.createColor(0, 0, 255, 0.4));
//	context2d.fillRect(0, 0, canvas.getOffsetWidth(), canvas.getOffsetHeight());
	context2d.restore();
    }

}
