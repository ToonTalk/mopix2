package uk.ac.lkl.client.mopix.event;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.xml.client.Element;

import uk.ac.lkl.shared.CommonUtils;
import uk.ac.lkl.client.Modeller;
import uk.ac.lkl.client.event.ModellerEvent;
import uk.ac.lkl.client.event.ReconstructEventsContinutation;
import uk.ac.lkl.client.mopix.MoPiXGlobals;
import uk.ac.lkl.client.mopix.MoPiXObject;
import uk.ac.lkl.client.Utils;
import uk.ac.lkl.client.mopix.expression.Equation;

@SuppressWarnings("serial")
public class RemoveEquationFromObjectEvent extends ModellerEvent {
    /**
     * 
     */
    protected Equation equation;

    public RemoveEquationFromObjectEvent(MoPiXObject object, Equation equation) {
	super(object);
	this.equation = equation;
    }
    
    @Override
    public void undo(boolean record, boolean justRecord, ReconstructEventsContinutation continuation) {
	super.undo(record, justRecord, continuation);
	getObject().addEquation(getEquation());
	continuation.reconstructSubsequentEvents(null);
    }
    
    @Override
    public void redo(boolean record, boolean justRecord, ReconstructEventsContinutation continuation) {
	super.redo(record, justRecord, continuation);
	getObject().removeEquation(getEquation());
	continuation.reconstructSubsequentEvents(null);
    }

    public String toHTMLString(boolean brief) {
	return Utils.textFontToMatchIcons(Modeller.constants.removed() + Modeller.NON_BREAKING_SPACE)
		+ Utils.textFontToMatchIcons(getEquation().HTML())
		+ Utils.textFontToMatchIcons(Modeller.NON_BREAKING_SPACE + Modeller.constants.from() + Modeller.NON_BREAKING_SPACE)
		+ Utils.textFontToMatchIcons(getObject().getNameHTML());
    }
       
    public String getXML() {
	return "<RemoveEquationFromObjectEvent" + getDateAttribute() + 
		   " name='" + getObject().getName() + "'" +
	           " equation='" + CommonUtils.encode(getEquation().getXML()) + "'/>";
    }
    
    public void recordInDatabase(AsyncCallback<String[]> recordSubsequentEventCallback, boolean notifyOthers) {
	// do someday	
    }
    
    public static ModellerEvent reconstruct(String macroBehaviourName, Element eventElement, 
                                         boolean restoringHistory, int version) {
	String objectName = eventElement.getAttribute("name");
	if (objectName == null) {
	    Modeller.addToErrorLog("No name attribute in " + eventElement.toString());
	    return noEvent;
	}
	MoPiXObject object = MoPiXGlobals.objectNamed(objectName);
	if (object == null) {
	    Modeller.addToErrorLog("No object named " + objectName + " from " + eventElement.toString());
	    return noEvent;
	}
	String equationXMLEncoded = eventElement.getAttribute("equation");
	if (equationXMLEncoded == null) {
	    Modeller.addToErrorLog("No equation attribute in " + eventElement.toString());
	    return noEvent;
	}
	String equationXML = CommonUtils.decode(equationXMLEncoded);
	Equation equation = object.findEquationWithSameXML(equationXML);
	if (equation == null) {
	    Modeller.addToErrorLog("Could not find this XML in any of the equations of object " + objectName + ": " + equationXML);
	    return noEvent;
	}
	return new RemoveEquationFromObjectEvent(object, equation);
    }

    public MoPiXObject getObject() {
	return (MoPiXObject) getSource();
    }

    public Equation getEquation() {
	return equation;
    }

}
