package uk.ac.lkl.client.mopix.expression;

import com.google.gwt.xml.client.Node;
import uk.ac.lkl.client.mopix.MoPiXObject;

//implements <cn>constantValue</cn>

public class ExpressionConstant extends Expression {
    protected float constantValue;

    public ExpressionConstant(Node xml, MoPiXObject object, float constantValue) {
	super();
	setXML(xml, object);
	this.constantValue = constantValue;
    }

    public double evaluate(MoPiXObject object, int t) {
	return constantValue;
    }
}
