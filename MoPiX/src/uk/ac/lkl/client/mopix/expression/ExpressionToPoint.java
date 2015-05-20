package uk.ac.lkl.client.mopix.expression;

import com.google.gwt.xml.client.Node;
import uk.ac.lkl.client.mopix.MoPiXObject;

public class ExpressionToPoint extends Expression {
    protected Expression arg1;
    protected Expression arg2;
    protected Expression arg3;
    protected Expression arg4;
    protected boolean xCoordinate;

    public ExpressionToPoint(Node xml, MoPiXObject object, Expression arg1,
	    Expression arg2, Expression arg3, Expression arg4,
	    boolean xCoordinate) {
	super();
	setXML(xml, object);
	this.arg1 = arg1;
	this.arg2 = arg2;
	this.arg3 = arg3;
	this.arg4 = arg4;
	this.xCoordinate = xCoordinate;
    }

    public double evaluate(MoPiXObject object, int t) {
	return distanceToPoint(arg1.evaluate(object, t), 
		               arg2.evaluate(object, t), 
		               arg3.evaluate(object, t), 
		               arg4.evaluate(object, t),
		               Equation.evaluateStringFunction("appearance", object, t), // revisit this
		               xCoordinate);
    }
}
