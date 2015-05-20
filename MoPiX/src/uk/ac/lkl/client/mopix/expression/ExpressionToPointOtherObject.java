package uk.ac.lkl.client.mopix.expression;

import com.google.gwt.xml.client.Node;
import uk.ac.lkl.client.mopix.MoPiXObject;
import uk.ac.lkl.client.mopix.MoPiXGlobals;

public class ExpressionToPointOtherObject extends Expression {
    protected Expression arg1;
    protected Expression arg2;
    protected Expression arg3;
    protected Expression arg4;
    protected boolean xCoordinate;
    protected String objectName;
    protected MoPiXObject otherObject = null;

    public ExpressionToPointOtherObject(Node xml, MoPiXObject object,
	    String objectName, Expression arg1, Expression arg2,
	    Expression arg3, Expression arg4, boolean xCoordinate) {
	super();
	setXML(xml, object);
	this.arg1 = arg1;
	this.arg2 = arg2;
	this.arg3 = arg3;
	this.arg4 = arg4;
	this.xCoordinate = xCoordinate;
	this.objectName = objectName;
    }

    public double evaluate(MoPiXObject object, int t) {
	if (otherObject == null) {
	    otherObject = MoPiXGlobals.objectNamed(objectName);
	    if (otherObject == null) {
		System.out.println("Can't find an object named " + objectName
			+ " in " + node.toString());
		return 0.0f;
	    }
	}
	return distanceToPoint(arg1.evaluate(otherObject, t), 
		arg2.evaluate(otherObject, t), 
		arg3.evaluate(otherObject, t), 
		arg4.evaluate(otherObject, t), 
		Equation.evaluateStringFunction("appearance", otherObject, t), // revisit this
		xCoordinate);
    }
}
