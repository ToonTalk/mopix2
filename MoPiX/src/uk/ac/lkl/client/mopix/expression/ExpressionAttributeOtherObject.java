package uk.ac.lkl.client.mopix.expression;

import com.google.gwt.xml.client.Node;
import uk.ac.lkl.client.mopix.MoPiXObject;
import uk.ac.lkl.client.mopix.MoPiXGlobals;

public class ExpressionAttributeOtherObject extends ExpressionAttribute {
    protected String attributeName;
    protected MoPiXObject otherObject;
    protected Expression timeExpression;
    protected String objectName;

    public ExpressionAttributeOtherObject(Node xml, MoPiXObject object,
	    String attributeName, String objectName, Expression timeExpression) {
	super(xml, object, attributeName, timeExpression);
	this.objectName = objectName;
    }

    public double evaluate(MoPiXObject object, int t) {
	if (otherObject == null) {
	    otherObject = MoPiXGlobals.objectNamed(objectName);
	    if (otherObject == null) {
		System.out.println("Can't find an object named " + objectName + " in " + node.toString());
		return 0.0;
	    }
	}
	return super.evaluate(otherObject, t);
    }
}
