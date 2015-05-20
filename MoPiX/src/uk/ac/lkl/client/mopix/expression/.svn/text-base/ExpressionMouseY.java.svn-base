package uk.ac.lkl.client.mopix.expression;

import com.google.gwt.xml.client.Node;

import uk.ac.lkl.client.mopix.MoPiXObject;
import uk.ac.lkl.client.MoPiX;
import uk.ac.lkl.client.Modeller;

public class ExpressionMouseY extends Expression {
    public ExpressionMouseY(Node xml, MoPiXObject object) {
	super();
	setXML(xml, object);
    }

    public double evaluate(MoPiXObject object, int t) {
	return MoPiX.stageHeightDefault - Modeller.wholePanel.getMouseCurrentY();
    }
}
