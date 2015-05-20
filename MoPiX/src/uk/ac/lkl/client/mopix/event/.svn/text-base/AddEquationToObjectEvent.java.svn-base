package uk.ac.lkl.client.mopix.event;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.xml.client.Element;

import uk.ac.lkl.client.Modeller;
import uk.ac.lkl.client.event.ModellerEvent;
import uk.ac.lkl.client.event.ReconstructEventsContinutation;
import uk.ac.lkl.client.mopix.MoPiXObject;
import uk.ac.lkl.client.mopix.MoPiXGlobals;
import uk.ac.lkl.client.Utils;
import uk.ac.lkl.shared.CommonUtils;
import uk.ac.lkl.client.mopix.expression.Equation;
import uk.ac.lkl.client.mopix.EquationAppearance;

public class AddEquationToObjectEvent extends ModellerEvent {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    protected Equation equation;

    public AddEquationToObjectEvent(MoPiXObject object, Equation equation) {
	super(object);
	this.equation = equation;
    }
    
    @Override
    public void undo(boolean record, boolean justRecord, ReconstructEventsContinutation continuation) {
	super.undo(record, justRecord, continuation);
	getObject().removeEquation(getEquation());
	Equation replacement = getEquation().getReplacedEquation();
	if (replacement != null) {
	    getObject().addEquation(replacement);
	}
	continuation.reconstructSubsequentEvents(null);
    }
    
    @Override
    public void redo(boolean record, boolean justRecord, ReconstructEventsContinutation continuation) {
	super.redo(record, justRecord, continuation);
	Equation replacement = getEquation().getReplacedEquation();
	if (replacement != null) {
	    getObject().removeEquation(replacement);
	}
	getObject().addEquation(getEquation());
	continuation.reconstructSubsequentEvents(null);
    }

    public String toHTMLString(boolean brief) {
	return Utils.textFontToMatchIcons(Modeller.constants.added() + Modeller.NON_BREAKING_SPACE)
		+ Utils.textFontToMatchIcons(getEquation().HTML())
		+ Utils.textFontToMatchIcons(Modeller.NON_BREAKING_SPACE + Modeller.constants.to() + Modeller.NON_BREAKING_SPACE)
		+ Utils.textFontToMatchIcons(getObject().getNameHTML());
    }

    public String getXML() {
	EquationAppearance customAppearance = getEquation().getEquationAppearance(false);
	StringBuilder xml = new StringBuilder();
	xml.append("<AddEquationToObjectEvent" + getDateAttribute() + 
		   " name='" + getObject().getName() + "'" +
	           " equation='" + CommonUtils.encode(getEquation().getXML()) + "'");
	if (customAppearance != null) {
	    String customHTML = customAppearance.getCustomHTML();
	    if (customHTML != null) {
		xml.append(" appearance='" + CommonUtils.encode(customHTML) + "'");
	    }
	}
	xml.append("/>"); 
	return xml.toString();
    }
    
    public void recordInDatabase(AsyncCallback<String[]> recordSubsequentEventCallback, boolean notifyOthers) {
	// to do someday
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
	String equationXMLEncoded = eventElement.getAttribute("equation");
	if (equationXMLEncoded == null) {
	    Modeller.addToErrorLog("No equation attribute in " + eventElement.toString());
	    return;
	}
	String appearanceEncoded = eventElement.getAttribute("appearance");
	Equation equation = new Equation(CommonUtils.decode(equationXMLEncoded), object);
	if (appearanceEncoded != null) {
	    equation.setAppearance(new EquationAppearance(equation, CommonUtils.decode(appearanceEncoded)));
	}
	continuation.reconstructSubsequentEvents(new AddEquationToObjectEvent(object, equation));
    }

    public MoPiXObject getObject() {
	return (MoPiXObject) getSource();
    }

    public Equation getEquation() {
	return equation;
    }

}
