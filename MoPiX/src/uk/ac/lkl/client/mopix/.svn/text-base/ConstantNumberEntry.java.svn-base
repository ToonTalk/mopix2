package uk.ac.lkl.client.mopix;

import com.google.gwt.user.client.ui.TextBox;

import uk.ac.lkl.client.MoPiX;
import uk.ac.lkl.client.mopix.ConstantExpressionAppearance;

import uk.ac.lkl.client.mopix.event.CreateExpressionEvent;
import uk.ac.lkl.client.mopix.expression.Expression;

public class ConstantNumberEntry extends NumberEntry {
    public ConstantNumberEntry() {
	super(null, null);
    }

    protected void acceptNumber(final String operation,
	                        final ExpressionAppearance expressionAppearance, TextBox numberBox) {
	// relaxing this so it accepts any constant not just numbers
	String boxContents = numberBox.getText();
	String numberXML;
	try {
	    Double.parseDouble(boxContents);
	    numberXML = "<cn>" + boxContents + "</cn>";
	} catch (NumberFormatException e) {
	    numberXML = "<ci>" + boxContents + "</ci>";
	}
	Expression numberExpression = new Expression(numberXML, null);
	ExpressionAppearance appearance = new ConstantExpressionAppearance(
		numberExpression);
	MoPiX.addToExpressionPalette(appearance);
	new CreateExpressionEvent(numberExpression).addToHistory();
//	MoPiX2.setStatusLine("Expression is ready");
    }
}
