package uk.ac.lkl.client.mopix;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbsolutePanel;
import uk.ac.lkl.client.mopix.graphics.Shape;

public class ModellerAbsolutePanel extends AbsolutePanel {

    protected Shape shapeBeingChanged;
    protected int kindOfChange;
//    protected Canvas canvas;

    public ModellerAbsolutePanel() {
	super();
	sinkEvents(Event.ONCLICK | Event.MOUSEEVENTS);
    }

    public void onBrowserEvent(Event event) {
	super.onBrowserEvent(event);
	int eventType = DOM.eventGetType(event);
	if (shapeBeingChanged == null)
	    return;
	if (eventType == Event.ONMOUSEUP) {
	    shapeBeingChanged.userStopsChanging();
	    shapeBeingChanged = null;
	} else if (eventType == Event.ONMOUSEMOVE) {
	    shapeBeingChanged.mouseMoved(event);
	}
    }

    public void userToMakeChanges(Shape shapeBeingChanged, int kindOfChange) {
	this.shapeBeingChanged = shapeBeingChanged;
	this.kindOfChange = kindOfChange;
    }
    
//    @Override
//    public void setPixelSize(int width, int height) {
//	super.setPixelSize(width, height);
//	if (canvas != null) {
//	    canvas.setPixelSize(width, height);
//	    canvas.setCoordinateSpaceWidth(width);
//	    canvas.setCoordinateSpaceHeight(height);
//	}
//    }

//    public Canvas getCanvas() {
//        return canvas;
//    }
//
//    public void setCanvas(Canvas canvas) {
//        this.canvas = canvas;
//        add(canvas);
//    }

}
