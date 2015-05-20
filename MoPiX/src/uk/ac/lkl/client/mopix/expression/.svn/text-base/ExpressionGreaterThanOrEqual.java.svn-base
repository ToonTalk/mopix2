package uk.ac.lkl.client.mopix.expression;

import com.google.gwt.xml.client.Node;
import uk.ac.lkl.client.mopix.MoPiXObject;

public class ExpressionGreaterThanOrEqual extends ExpressionBinaryOperator {
    public ExpressionGreaterThanOrEqual(Node xml, MoPiXObject object,
	    Expression arg1, Expression arg2) {
	super(xml, object, arg1, arg2);
    }

    public double evaluate(MoPiXObject object, int t) {
	if (arg1.evaluate(object, t) >= arg2.evaluate(object, t)) {
	    return 1.0f;
	} else {
	    return 0.0f;
	}
    }
}
