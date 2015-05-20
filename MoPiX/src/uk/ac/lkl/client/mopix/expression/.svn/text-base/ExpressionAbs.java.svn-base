package uk.ac.lkl.client.mopix.expression;

import com.google.gwt.xml.client.Node;
import uk.ac.lkl.client.mopix.MoPiXObject;

public class ExpressionAbs extends ExpressionUnaryOperator {
    public ExpressionAbs(Node xml, MoPiXObject object, Expression arg1) {
	super(xml, object, arg1);
    }

    public double evaluate(MoPiXObject object, int t) {
	return Math.abs(arg1.evaluate(object, t));
    }
}
