package uk.ac.lkl.client.mopix.expression;

import com.google.gwt.xml.client.Node;
import uk.ac.lkl.client.mopix.MoPiXObject;
import uk.ac.lkl.client.Modeller;

public class ExpressionKeyDown extends Expression {
    protected String keyName;

    public ExpressionKeyDown(Node xml, MoPiXObject object, String keyName) {
	super();
	setXML(xml, object);
	this.keyName = keyName;
    }

    public double evaluate(MoPiXObject object, int t) {
	return Modeller.wholePanel.getKeyDown(keyName);
	// could be further optimised at load time
    }
}
