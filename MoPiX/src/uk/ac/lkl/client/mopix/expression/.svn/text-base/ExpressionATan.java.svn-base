package uk.ac.lkl.client.mopix.expression;

import com.google.gwt.xml.client.Node;
import uk.ac.lkl.client.mopix.MoPiXObject;

public class ExpressionATan extends ExpressionUnaryOperator {
    public ExpressionATan(Node xml, MoPiXObject object, Expression arg1) {
	super(xml, object, arg1);
    }

    public double evaluate(MoPiXObject object, int t) {
	return (float) Math.atan(arg1.evaluate(object, t) / radiansPerDegree);
    }
}
