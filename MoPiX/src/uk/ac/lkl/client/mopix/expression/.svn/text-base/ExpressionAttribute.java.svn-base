package uk.ac.lkl.client.mopix.expression;

import com.google.gwt.xml.client.Node;
import uk.ac.lkl.client.mopix.MoPiXObject;

public class ExpressionAttribute extends Expression {
    protected String attributeName;
    protected MoPiXObject otherObject;
    protected Expression timeExpression;

    public ExpressionAttribute(Node xml, MoPiXObject object,
	    String attributeName, Expression timeExpression) {
	super();
	setXML(xml, object);
	this.attributeName = attributeName;
	this.timeExpression = timeExpression;
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
	return answer;
    }
}
