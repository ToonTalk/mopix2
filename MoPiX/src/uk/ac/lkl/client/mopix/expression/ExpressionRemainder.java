package uk.ac.lkl.client.mopix.expression;

import com.google.gwt.xml.client.Node;
import uk.ac.lkl.client.mopix.MoPiXObject;

public class ExpressionRemainder extends ExpressionBinaryOperator {
    public ExpressionRemainder(Node xml, MoPiXObject object, Expression arg1,
	    Expression arg2) {
	super(xml, object, arg1, arg2);
    }

    public double evaluate(MoPiXObject object, int t) {
	// remainder -- not clear if with negative args is different from %
	// could signal a friendly error if divisor is zero
	double dividend = arg1.evaluate(object, t);
	double divisor = arg2.evaluate(object, t);
	double answer = dividend % divisor;
	if (answer < 0.0) {
	    answer += divisor;
	}
	return answer;
    }
}
