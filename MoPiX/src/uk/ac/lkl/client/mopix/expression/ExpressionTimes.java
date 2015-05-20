package uk.ac.lkl.client.mopix.expression;

import com.google.gwt.xml.client.Node;
import uk.ac.lkl.client.mopix.MoPiXObject;

public class ExpressionTimes extends ExpressionBinaryOperator {
    public ExpressionTimes(Node xml, MoPiXObject object, Expression arg1,
	    Expression arg2) {
	super(xml, object, arg1, arg2);
    }

    public double evaluate(MoPiXObject object, int t) {
	double value1 = arg1.evaluate(object, t);
	if (value1 == 0.0)
	    return 0.0;
	return value1 * arg2.evaluate(object, t);
    }
}
