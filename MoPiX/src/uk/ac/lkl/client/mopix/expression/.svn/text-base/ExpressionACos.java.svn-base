package uk.ac.lkl.client.mopix.expression;

import uk.ac.lkl.client.mopix.MoPiXObject;

import com.google.gwt.xml.client.Node;

public class ExpressionACos extends ExpressionUnaryOperator {
    public ExpressionACos(Node xml, MoPiXObject object, Expression arg1) {
	super(xml, object, arg1);
    }

    public double evaluate(MoPiXObject object, int t) {
	return (float) Math.acos(arg1.evaluate(object, t) / radiansPerDegree);
    }
}
