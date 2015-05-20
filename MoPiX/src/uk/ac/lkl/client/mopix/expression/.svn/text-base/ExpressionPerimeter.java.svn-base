package uk.ac.lkl.client.mopix.expression;

import com.google.gwt.xml.client.Node;
import uk.ac.lkl.client.mopix.MoPiXObject;

public class ExpressionPerimeter extends ExpressionAttribute {
    protected boolean xCoordinate;
    protected float angle;

    public ExpressionPerimeter(Node xml, MoPiXObject object,
	    String attributeName, Expression timeExpression, int angle,
	    boolean xCoordinate) {
	super(xml, object, attributeName, timeExpression);
	this.xCoordinate = xCoordinate;
	this.angle = angle; // converts from int to float as well
    }

    public double evaluate(MoPiXObject object, int t) {
	int timeValue = (int) Math.round(timeExpression.evaluate(object, t)); 
	// maybe an expression like t-1
	double answer = 0.0;
	if (timeValue >= 0) {
	    // rewritten on 290807 to include defaults etc -- and it caused
	    // infinite recursion
	    // restored on 041007 to get toss ball to work
	    answer = evaluateFunction(attributeName, object, timeValue);
	}
	if (answer == noValue) {
	    // could be optimised to convert from strings to enums earlier
	    answer = defaultAttributeValue(attributeName, object, t);
	}
	return answer
		+ distanceToPoint(angle, evaluateFunction("width", object,
			timeValue), evaluateFunction("height", object,
			timeValue), evaluateFunction("rotation", object,
			timeValue), evaluateStringFunction("appearance",
			object, timeValue), xCoordinate);
    }
}
