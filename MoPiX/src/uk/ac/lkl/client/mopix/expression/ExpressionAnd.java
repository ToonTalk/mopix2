package uk.ac.lkl.client.mopix.expression;

import com.google.gwt.xml.client.Node;
import uk.ac.lkl.client.mopix.MoPiXObject;

public class ExpressionAnd extends ExpressionBinaryOperator {
    public ExpressionAnd(Node xml, MoPiXObject object, Expression arg1,
	    Expression arg2) {
	super(xml, object, arg1, arg2);
    }

    public double evaluate(MoPiXObject object, int t) {
	if (arg1.evaluate(object, t) == 0.0) {
	    return 0.0f;
	}
	if (arg2.evaluate(object, t) == 0.0) {
	    return 0.0f;
	}
	return 1.0f;
    }
}
