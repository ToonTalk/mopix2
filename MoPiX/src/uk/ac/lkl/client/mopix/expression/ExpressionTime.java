package uk.ac.lkl.client.mopix.expression;

import com.google.gwt.xml.client.Node;
import uk.ac.lkl.client.mopix.MoPiXObject;

// implements <ci>t</ci>

public class ExpressionTime extends Expression {
    public ExpressionTime(Node xml, MoPiXObject object) {
	super();
	setXML(xml, object);
    }

    public double evaluate(MoPiXObject object, int t) {
	return t;
    }
}
