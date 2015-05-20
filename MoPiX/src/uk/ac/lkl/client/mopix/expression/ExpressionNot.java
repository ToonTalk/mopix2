package uk.ac.lkl.client.mopix.expression;

import com.google.gwt.xml.client.Node;
import uk.ac.lkl.client.mopix.MoPiXObject;

public class ExpressionNot extends ExpressionUnaryOperator {
    public ExpressionNot(Node xml, MoPiXObject object, Expression arg1) {
	super(xml, object, arg1);
    }

    public double evaluate(MoPiXObject object, int t) {
	double answer = arg1.evaluate(object, t);
	answer = (answer + 1) % 2;
	return answer;
    }
}
