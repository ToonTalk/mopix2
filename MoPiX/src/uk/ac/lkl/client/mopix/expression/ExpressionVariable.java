package uk.ac.lkl.client.mopix.expression;

import com.google.gwt.xml.client.Node;
import uk.ac.lkl.client.mopix.MoPiXObject;
import uk.ac.lkl.client.mopix.MoPiXGlobals;

//implements <ci>variable</ci>
// where variable is not t

public class ExpressionVariable extends Expression {
    protected String variable;

    public ExpressionVariable(Node xml, MoPiXObject object, String variable) {
	super();
	setXML(xml, object);
	this.variable = variable;
    }

    public double evaluate(MoPiXObject object, int t) {
	return MoPiXGlobals.valueOfConstant(variable);
    }
}
