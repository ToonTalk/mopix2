package uk.ac.lkl.client.mopix.expression;

import com.google.gwt.xml.client.Node;
import uk.ac.lkl.client.mopix.MoPiXObject;

public class ExpressionOr extends ExpressionBinaryOperator {
    public ExpressionOr(Node xml, MoPiXObject object, Expression arg1, Expression arg2) {
	super(xml, object, arg1, arg2);
    }

    public double evaluate(MoPiXObject object, int t) {
	// the same as addition modulo 2 with 0 or 1 values
	// but is optimised since if any arg is 1 then so is answer
	if (arg1.evaluate(object, t) != 0.0) {
	    return 1.0f;
	}
	if (arg2.evaluate(object, t) != 0.0) {
	    return 1.0f;
	}
	return 0.0f;
    }
}
