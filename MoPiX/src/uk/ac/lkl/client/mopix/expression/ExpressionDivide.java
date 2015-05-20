package uk.ac.lkl.client.mopix.expression;

import com.google.gwt.xml.client.Node;
import uk.ac.lkl.client.Modeller;
import uk.ac.lkl.client.mopix.MoPiXObject;

public class ExpressionDivide extends ExpressionBinaryOperator {
    public ExpressionDivide(Node xml, MoPiXObject object, Expression arg1, Expression arg2) {
	super(xml, object, arg1, arg2);
    }

    public double evaluate(MoPiXObject object, int t) {
	double dividend = arg1.evaluate(object, t);
	if (dividend == 0.0)
	    return 0.0;
	double divisor = arg2.evaluate(object, t);
	if (divisor == 0.0) {
	    Modeller.setStatusLineHTML(Modeller.constants.divisionByZeroWhileComputing() + 
		                     Modeller.NON_BREAKING_SPACE + arg1.getHTML() + 
		                     "&nbsp;&divide;&nbsp;" + arg2.getHTML());
	    return 0.0f; // what else?
	}
	return dividend / divisor;
    }
}
