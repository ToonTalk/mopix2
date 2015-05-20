package uk.ac.lkl.client.mopix.expression;

import com.google.gwt.xml.client.Node;
import uk.ac.lkl.client.mopix.MoPiXObject;

public class ExpressionPower extends ExpressionBinaryOperator {
    public ExpressionPower(Node xml, MoPiXObject object, Expression arg1,
	    Expression arg2) {
	super(xml, object, arg1, arg2);
    }

    public double evaluate(MoPiXObject object, int t) {
	return (float) Math.pow(arg1.evaluate(object, t), arg2.evaluate(object, t));
    }
}
