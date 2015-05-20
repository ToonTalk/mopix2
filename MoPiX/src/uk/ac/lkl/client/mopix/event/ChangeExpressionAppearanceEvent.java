package uk.ac.lkl.client.mopix.event;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.xml.client.Element;

import uk.ac.lkl.client.event.ModellerEvent;
import uk.ac.lkl.client.event.ReconstructEventsContinutation;
import uk.ac.lkl.client.mopix.ExpressionAppearance;
import uk.ac.lkl.client.Utils;
import uk.ac.lkl.client.Modeller;
import uk.ac.lkl.shared.CommonUtils;

public class ChangeExpressionAppearanceEvent extends ModellerEvent {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    protected String oldHTML;
    protected String newHTML;

    public ChangeExpressionAppearanceEvent(ExpressionAppearance expressionAppearance, String oldHTML) {
	super(expressionAppearance);
	this.oldHTML = oldHTML;
	newHTML = expressionAppearance.getCustomHTML();
    }
    
    public ChangeExpressionAppearanceEvent(String newHTML, String oldHTML) {
	super(newHTML);
	this.oldHTML = oldHTML;
	this.newHTML = newHTML;
    }

    @Override
    public boolean dirtyEvent() {
	return false;
    }

    public String toHTMLString(boolean brief) {
	return Utils.textFontToMatchIcons(Modeller.constants.changedAppearanceFrom() + Modeller.NON_BREAKING_SPACE)
		+ Utils.textFontToMatchIcons(oldHTML)
		+ Utils.textFontToMatchIcons(Modeller.NON_BREAKING_SPACE + Modeller.constants.to() + Modeller.NON_BREAKING_SPACE)
		+ Utils.textFontToMatchIcons(newHTML);
    }
    
    public String getXML() {
	return "<ChangeExpressionAppearanceEvent" + getDateAttribute() + 
	       " old='" + CommonUtils.encode(oldHTML) + "'" +
	       " new='" + CommonUtils.encode(newHTML) + "'" +
	       "/>"; 
    }
    
    public void recordInDatabase(AsyncCallback<String[]> recordSubsequentEventCallback, boolean notifyOthers) {
	// do this someday	
    }
    
    public static void reconstruct(String macroBehaviourName, Element eventElement, 
                                   boolean restoringHistory, int version, 
                                   ReconstructEventsContinutation continuation) {
	String oldHTMLEncoded = eventElement.getAttribute("old");
	String newHTMLEncoded = eventElement.getAttribute("new");
	if (oldHTMLEncoded != null && newHTMLEncoded != null) {
	    continuation.reconstructSubsequentEvents(new ChangeExpressionAppearanceEvent(CommonUtils.decode(newHTMLEncoded),
		                                                                         CommonUtils.decode(oldHTMLEncoded)));
	} else {
	    Modeller.addToErrorLog("Reconstructing a change expression appearance event and expected to find old and new attributes in " + 
		                   eventElement.toString());
	}
}

//    public ExpressionAppearance getExpressionAppearance() {
//	return (ExpressionAppearance) getSource();
//    }

}
