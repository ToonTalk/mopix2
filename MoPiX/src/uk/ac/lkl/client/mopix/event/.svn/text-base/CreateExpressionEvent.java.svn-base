package uk.ac.lkl.client.mopix.event;

import com.google.gwt.user.client.rpc.AsyncCallback;

import uk.ac.lkl.client.Utils;
import uk.ac.lkl.client.Modeller;
import uk.ac.lkl.client.event.ModellerEvent;
import uk.ac.lkl.client.mopix.expression.Expression;

@SuppressWarnings("serial")
public class CreateExpressionEvent extends ModellerEvent {

    /**
     * Not fully implemented event (undo, redo, and reconstruct missing)
     */

    public CreateExpressionEvent(Expression expression) {
	super(expression);
    }

    @Override
    public boolean dirtyEvent() {
	return false; // I guess
    }

    public String toHTMLString(boolean brief) {
	return Utils.textFontToMatchIcons(Modeller.constants.created() + Modeller.NON_BREAKING_SPACE)
		+ Utils.textFontToMatchIcons(getExpression().getHTML());
    }

    public void undo() {
//	to do?
    }
    
    public void redo() {
//	to do?
    }
    
    public String getXML() {
	return "<CreateExpressionEvent" + getDateAttribute() + "/>"; // needs more work
    }
    
    
    public void recordInDatabase(AsyncCallback<String[]> recordSubsequentEventCallback, boolean notifyOthers) {
	// do this someday	
    }

    public Expression getExpression() {
	return (Expression) getSource();
    }

}
