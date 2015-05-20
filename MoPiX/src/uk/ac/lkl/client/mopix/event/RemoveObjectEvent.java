package uk.ac.lkl.client.mopix.event;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.xml.client.Element;

import uk.ac.lkl.client.event.ModellerEvent;
import uk.ac.lkl.client.event.ReconstructEventsContinutation;
import uk.ac.lkl.client.MoPiX;
import uk.ac.lkl.client.Modeller;
import uk.ac.lkl.client.mopix.MoPiXGlobals;
import uk.ac.lkl.client.mopix.MoPiXObject;
import uk.ac.lkl.client.Utils;

@SuppressWarnings("serial")
public class RemoveObjectEvent extends ModellerEvent {

    public RemoveObjectEvent(MoPiXObject object) {
	super(object);
    }

    public String toHTMLString(boolean brief) {
	return Utils.textFontToMatchIcons(Modeller.constants.removed() + Modeller.NON_BREAKING_SPACE)
		+ Utils.textFontToMatchIcons(getMoPiXObject().getNameHTML());
    }
    
    public String getXML() {
	return "<RemoveObjectEvent" + getDateAttribute() + 
	       " name='" + getMoPiXObject().getName() + "'" +
	       "/>"; 
    }

    public void recordInDatabase(AsyncCallback<String[]> recordSubsequentEventCallback, boolean notifyOthers) {
	// do some time	
    }
    
    public static void reconstruct(String macroBehaviourName, Element eventElement, 
                                   boolean restoringHistory, int version, 
                                   ReconstructEventsContinutation continuation) {
	String objectName = eventElement.getAttribute("name");
	if (objectName == null) {
	    Modeller.addToErrorLog("No name attribute in " + eventElement.toString());
	    return;
	}
	MoPiXObject object = MoPiXGlobals.objectNamed(objectName);
	if (object == null) {
	    Modeller.addToErrorLog("No object named " + objectName + " from " + eventElement.toString());
	    return;
	}
	continuation.reconstructSubsequentEvents(new RemoveObjectEvent(object));
    }

    @Override
    public void undo(boolean record, boolean justRecord, ReconstructEventsContinutation continuation) {
	super.undo(record, justRecord, continuation);
	getMoPiXObject().initialiseShape();
	getMoPiXObject().updateState(MoPiX.time);
	MoPiXGlobals.addToAllObjects(getMoPiXObject());
	continuation.reconstructSubsequentEvents(null);
    }
    
    @Override
    public void redo(boolean record, boolean justRecord, ReconstructEventsContinutation continuation) {
	super.redo(record, justRecord, continuation);
	getMoPiXObject().removeFromModel(true);
	continuation.reconstructSubsequentEvents(null);
    }

    public MoPiXObject getMoPiXObject() {
	return (MoPiXObject) getSource();
    }

}
