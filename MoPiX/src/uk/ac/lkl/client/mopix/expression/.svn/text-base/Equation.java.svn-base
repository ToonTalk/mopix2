package uk.ac.lkl.client.mopix.expression;

import uk.ac.lkl.client.mopix.EquationAppearance;
import uk.ac.lkl.client.mopix.MoPiXObject;
import uk.ac.lkl.client.MoPiX;
import uk.ac.lkl.client.Modeller;
import uk.ac.lkl.client.HTMLToPointException;
import uk.ac.lkl.client.mopix.MoPiXGlobals;
import uk.ac.lkl.client.Utils;

import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

public class Equation extends Expression {
    protected String attribute;
    protected int specifiedTime = -1;
    // protected String specifiedTimeExpressionMathML = "";
    protected Expression specifiedTimeExpression = null;
    protected boolean computingForward = false;
    protected Expression rightHandSide;
    protected String objectName; // e.g. "me" or "object_3"
    protected boolean equationIncomplete = false;
    protected EquationAppearance equationAppearance = null;
    protected EquationAppearance originalAppearance = null; 
    protected Equation replacedEquation = null;
    protected static int equationsLoadedSingly = 0;
    public static final int historyMaximumSize = 12 * 60 * 60;
    public static final int historyMaximumHalfSize = historyMaximumSize / 2;

    public Equation(String xmlString, MoPiXObject object) {
	// super(xmlString, object);
	// above should work but after initialising (via SetXML) the default
	// values for attributes such as
	// specifiedTime is overridden
	super();
	// initialise();
	replacedEquation = setXML(xmlString, object);
    }

    public Equation(String xmlString, MoPiXObject object, EquationAppearance appearance) {
	// super(xmlString, object, appearance);
	this(xmlString, object);
	if (appearance != null) {
	    appearance.setEquation(this);
	    setAppearance(appearance);
	}
    }

    public Equation(Node xml, MoPiXObject object) {
	// super(xml, object);
	// initialise();
	// in case we want to restore it to a template -- could be on demand or
	// when XML updated
	setXML(xml, object);
    }

    public Equation(Node xml, MoPiXObject object, EquationAppearance appearance) {
	this(xml, object);
	setAppearance(appearance);
    }

    public String evaluateString(MoPiXObject object, int t) {
	// history is of wrong type so not used here
	return XMLNodeValueIfCI(rightHandSide.getNode());
    }

    public boolean isConstant() {
	return rightHandSide instanceof ExpressionConstant;
    }

    public static void clearHistoryAllEquations() {
	if (MoPiXGlobals.allObjects == null || !MoPiX.useCachedValues)
	    return;
	for (int i = 0; i < MoPiXGlobals.allObjects.length; i++) {
	    MoPiXGlobals.allObjects[i].clearHistoryAllEquations();
	}
    }

    public String HTMLAlgebra() {
	// returns HTML of the equation in algebraic notation
	String MathMLString = "";
	if (node == null) {
	    MathMLString = "<ci>?</ci>";
	    equationIncomplete = true;
	} else {
	    try {
		MathMLString = HTMLOfXMLNode(node);
	    } catch (Exception e) {
		MathMLString = "<ci>?</ci>";
		equationIncomplete = true;
		Modeller.setStatusLine(Modeller.constants.errorOccurredWhileGeneratingTheHtmlFor() + ": " 
			+ e.toString() + " in " + node.toString());
	    }
	}
	// MathMLString = "<math>" + MathMLString + "</math>"; // does this make
	// sense? -- experimenting without it
	if (equationIncomplete) {
	    MathMLString = "<font color='#FF0000'>" + MathMLString + "</font>";
	}
	// trace(MathMLString);
	return MathMLString;
    }

    public String getCustomHTML() {
	if (MoPiX.instance().useCustomHTMLDefault() || MoPiX.instance().generateNaturalLanguageHTMLDefault()) {
	    if (appearance == null) {
		setAppearance(new EquationAppearance(this));
		appearance.customiseAppearance(null, this, object);
	    }
	    return appearance.getHTML();
	} else {
	    return null;
	}
    }

    public String HTML() {
	if (appearance != null) {
	    return appearance.getHTML();
	} else {
	    return Utils.textFontToMatchIcons(HTMLAlgebra());
	}
    }

    public String HTMLOfRightHandSide() {
	if (rightHandSide == null) {
	    System.out.println("No right hand side");
	    return Modeller.constants.missingRightHandSide();
	}
	return rightHandSide.getHTML();
    }

    public Equation setXML(Node node, MoPiXObject object) {
	super.setXML(node, object);
	NodeList children = node.getChildNodes();
	if (children.getLength() == 0
		|| !children.item(0).getNodeName().equals("eq")) {
	    return null;
	}
	Node leftHandSide = children.item(1);
	if (leftHandSide == null)
	    return null;
	Node rightHandSideNode = children.item(2);
	if (rightHandSideNode == null) {
	    return null;
	}
	if (leftHandSide.getNodeName().equals("ci")) { 
	    // removed && object != null
	    // just setting a variable to a value
	    if (rightHandSideNode.getNodeName().equals("cn")) {
		// System.out.println("double: " + leftHandSide.toString());
		// System.out.println(leftHandSide.getFirstChild().getNodeValue());
		double nodeValue = Double.parseDouble(rightHandSideNode.getFirstChild().getNodeValue());
		String nameOfConstant = leftHandSide.getFirstChild().getNodeValue();
		MoPiXGlobals.addConstant(nameOfConstant, nodeValue, this);
		// record which object has constant equations for the interface
		// (e.g. when flipped)
		// following not used to evaluate expressions just saving and
		// flipping
		// if (object.getConstants() == null) {
		// alternates between constant identifier and the equation
		// object.setConstants(new
		// Array(leftHandSide.getFirstChild().getNodeValue(),this));
		// } else
		// following is a temporary hack to enable saving in such
		// equations
//		if (object != null) {
//		    object.setConstant(nameOfConstant, this);
//		};
	    }
	    // } else {
	    // MoPiX2.setStatusLine("Currently can't handle equations of a
	    // variable to a non-number. Sorry. " + xmlString);
	    return null;
	}
	rightHandSide = Expression.createExpression(rightHandSideNode, object);
	NodeList leftHandSideChildren = leftHandSide.getChildNodes();
	if (leftHandSideChildren.getLength() == 0) {
	    return null;
	}
	Node firstChild = leftHandSideChildren.item(0);
	if (firstChild.getChildNodes().getLength() == 0) {
	    return null;
	}
	attribute = firstChild.getFirstChild().getNodeValue();
	if (leftHandSideChildren.getLength() < 1) {
	    return null;
	}
	node = leftHandSideChildren.item(1).getFirstChild();
	if (node == null) {
	    return null;
	}
	objectName = node.getNodeValue();
	if (!objectName.equals("ME") && object != null) { 
	    // not used to evaluate
	    int subAttribute = attributeSubscript(attribute); 
	    // currently is angle for x<sub> and y<sub>
	    if (subAttribute >= 0) {
		String parts[] = leftHandSide.toString().split(attribute);
		String newLeftHandSideXML = parts[0]
			+ attribute.substring(0, 1) + parts[1];
		for (int i = 2; i < parts.length; i++) {
		    newLeftHandSideXML = newLeftHandSideXML + attribute
			    + parts[i];
		}
		String timeXML = leftHandSideChildren.item(2).toString();
		String newRightHandSideXML = "<apply><minus/>"
			+ rightHandSideNode.toString() + "<apply><ToPoint"
			+ attribute.substring(0, 1).toUpperCase() + "/>"
			+ "<cn>" + subAttribute + "</cn>"
			+ "<apply><mo>width</mo><ci>" + object.getName()
			+ "</ci>" + timeXML + "</apply>"
			+ "<apply><mo>height</mo><ci>" + object.getName()
			+ "</ci>" + timeXML + "</apply>"
			+ "<apply><mo>rotation</mo><ci>" + object.getName()
			+ "</ci>" + timeXML + "</apply>"
			+ "<apply><mo>appearance</mo><ci>" + object.getName()
			+ "</ci>" + timeXML + "</apply>" + "</apply></apply>";
		String newMathML = "<apply";
		if (appearance != null) {
		    newMathML += " HTMLAppearance='"
			    + Utils.encode(appearance.getHTML()) + "'";
		}
		setXML(newMathML + "><eq/>" + newLeftHandSideXML
			+ newRightHandSideXML + "</apply>", object);
	    }
	    // object.addAttribute(attribute);
	    // allAttributes[objectName] = object["_allAttributes"]; // why? -
	    // can go thru allObjects instead
	    if (leftHandSideChildren.getLength() < 2) {
		return null;
	    }
	    String timeTag = leftHandSideChildren.item(2).getNodeName();
	    Equation replacedEquation = null;
	    if (timeTag.equals("ci")
		    && leftHandSideChildren.item(2).getFirstChild().getNodeValue().equals("t")) {
		// arg is "<ci>t</ci>"
		replacedEquation = replaceEquation(this, attribute, object, -1);
		if (replacedEquation == null) {
		    object.appendAttribute(attribute, this);
		}
	    } else if (timeTag.equals("cn")) { // constant time
		try {
		    specifiedTime = 
			Integer.parseInt(leftHandSideChildren.item(2).getFirstChild().getNodeValue());
		} catch (NumberFormatException e) {
		    // can't use setStatusLineHTMLError here since XML won't display well
		    Modeller.setStatusLine(e.toString() + "; " + Modeller.constants.cnTagExpectsANumberIn() + " " + leftHandSide.toString());
		    return null;
		}
		replacedEquation = replaceEquation(this, attribute, object, specifiedTime);
		if (replacedEquation == null) {
		    object.prependAttribute(attribute, this);
		}
	    } else {
		specifiedTimeExpression = 
		    Expression.createExpression(leftHandSideChildren.item(2), object);
		String specifiedTimeExpressionMathML = 
		    leftHandSideChildren.item(2).toString();
		replacedEquation = 
		    replaceEquationWithTimeExpression(this, attribute, object, specifiedTimeExpressionMathML);
		if (replacedEquation == null) {
		    object.prependAttribute(attribute, this);
		}
	    }
	    return replacedEquation;
	}
	return null;
    }

    public double valueInCache(int t) {
	double cachedEntry = getHistory()[t % historyMaximumSize]; 
	// not perfect if run longer than 1/2 of historyMaximumSize
	if (cachedEntry == computingThisValue) {
	    Modeller.addToErrorLog(Modeller.constants.errorWhileTryingToUse() +
		                   " " + HTMLAlgebra() + " "
		                   + Modeller.constants.foundThatThereIsACircularDependency());
	    getHistory()[t % historyMaximumSize] = noValue;
	    return 0;
	}
	return cachedEntry;
    }

    @Override
    public double evaluate(MoPiXObject object, int t) {
	// if (MoPiX2.DEBUG) {
	// System.out.println("Evaluating " + getAlgebra() + " at time " + t);
	// }
	double answer = noValue;
	if (t >= 0 && MoPiX.useCachedValues) {
	    answer = valueInCache(t);
	    if (answer != noValue) {
		// if (MoPiX2.DEBUG) {
		// System.out.println("Found in cache: " + answer);
		// }
		return answer;
	    }
	    // history must exist now
	    history[t % historyMaximumSize] = computingThisValue; 
	    // to detect circularities
	}
	// long start = new Date().getTime();
	// MoPiX2.addHTMLToDebugMessages("eval at " + start + " of " +
	// HTMLNaturalLanguage());
	if (t < MoPiX.time - 2 && t > 0 && !computingForward) {
	    // if the past value wasn't in the cache compute from 0 to t and try
	    // again
	    // but not if already computing forward
	    // removed && MoPiXGlobals.oldTime != t on 261107 since wasn't being
	    // updated
	    Modeller.setStatusLine(Modeller.constants.pleaseWaitWhileWeComputeThePastThisMayTakeAWhile());
	    computingForward = true;
	    for (int oldTime = 0; oldTime < t; oldTime++) {
		answer = evaluate(object, oldTime);
	    }
	    computingForward = false;
	    history[t % historyMaximumSize] = noValue; 
	    // since about to try again
	    answer = valueInCache(t);
	    if (answer != noValue) {
		return answer;
	    }
	    // not clear why it is worth continuing here if answer is still undefined
	}
	// answer = evaluateXMLNode(rightHandSide, object, t);
	answer = rightHandSide.evaluate(object, t);
	// if (MoPiX2.DEBUG) {
	// System.out.println("computed " + attribute + ": " + answer);
	// }
	if (t >= 0 && MoPiX.useCachedValues) {
	    getHistory()[t % historyMaximumSize] = answer;
	    // history must exist now
	    history[(t + historyMaximumHalfSize) % historyMaximumSize] = noValue;
	    if (t > lastHistoryEntry) {
		lastHistoryEntry = t;
	    }
	}
	// MoPiX2.addHTMLToDebugMessages("result is " + answer + " after " +
	// (new Date().getTime()-start) + "ms");
	return answer;
    }

    // public String evaluateCode(MoPiXObject object) {
    // // deal with specified time expressions conditionally first
    // // String code = "var answer = cache[t];\r\nif (answer != " + noValue +
    // ") return answer;\r\ncache[t] = computingThisValue + " + ";\r\n";
    // // took out code for jumping in time
    // // code = code + "answer = " + evaluateXMLNodeCode(rightHandSide, object)
    // + "\r\n;cache[t] = answer;\r\nreturn answer;\r\n";
    // // history[(t+historyMaximumHalfSize)%historyMaximumSize] = noValue;
    // // return code;
    // return "return " + evaluateXMLNodeCode(rightHandSide, object);
    // }

    public boolean matches(int otherTime) {
	// negative time is a variable that matches all times
	// trace("otherTime: " + otherTime + " specifiedTime: " +
	// specifiedTime);
	if (specifiedTimeExpression != null) {
	    // if the following is a variable could "cache" its value in
	    // specifiedTime
	    // trace("Evaluating " + specifiedTimeExpression + " -> " +
	    // evaluateXMLNode(specifiedTimeExpressionNode,null,otherTime));
	    return otherTime == specifiedTimeExpression.evaluate(null,
		    otherTime); // why null????
	} else {
	    return specifiedTime < 0 || otherTime == specifiedTime;
	}
    }

    public boolean sameTime(int otherTime) {
	return otherTime == specifiedTime;
    }

    public Equation replaceEquation(Equation newEquation, String attribute,
	    MoPiXObject object, int t) {
	Equation equations[] = object.getEquations(attribute);
	if (equations == null) {
	    return null;
	}
	for (int i = 0; i < equations.length; i++) {
	    // if (equations[i].sameTime(t)) { // prior to 150407 was
	    // equations[i].matches(t) but then particular time equations
	    // replace general ones
	    Equation oldEquation = equations[i];
	    if (oldEquation.specifiedTimeExpression == null && 
		    // not an expression
		    oldEquation.specifiedTime == t) {
		// following message is only sometimes useful
		// String message = "Replaced " + equations[i].getNaturalLanguage() + "
		// with " + newEquation.getNaturalLanguage();
		// MoPiX2.setStatusLineHTML(message);
		// System.out.println(message);
		equations[i] = newEquation;
		oldEquation.removed();
		return oldEquation;
	    }
	}
	return null;
    }

    public Equation replaceEquationWithTimeExpression(Equation newEquation,
	    String attribute, MoPiXObject object, String timeExpression) {
	Equation equations[] = object.getEquations(attribute);
	if (equations == null) {
	    return null;
	}
	for (int i = 0; i < equations.length; i++) {
	    String specifiedTimeExpressionString = equations[i].getSpecifiedTimeExpressionXML();
	    if (specifiedTimeExpressionString == null) {
		// System.out.println("no specified time expression in " +
		// HTML());
	    } else {
		if (timeExpression.equals(specifiedTimeExpressionString)) {
		    Equation oldEquation = equations[i];
		    Modeller.setStatusLineHTML(Modeller.constants.replaced() + " " + oldEquation.HTML()
			    + " " + Modeller.constants.with() + " " + newEquation.HTML());
		    oldEquation.removed();
		    equations[i] = newEquation;
		    return oldEquation;
		}
	    }
	}
	return null;
    }

    protected void removed() {
	if (getOriginalAppearance() != null) {
	    getOriginalAppearance().decrementLiveCount();
	}
	object.equationsChanged();
    }

    protected String getSpecifiedTimeExpressionXML() {
	if (specifiedTimeExpression == null) {
	    return null;
	} else {
	    return specifiedTimeExpression.getXML();
	}
    }
    
    public boolean timeIsT() {
	// should rationalise this and combine these
	return (specifiedTimeExpression == null && specifiedTime < 0);
    }

    public void clearHistory() {
	historyValid = false;
	// if (history != null) {
	// long start = new Date().getTime();
	// MoPiX2.addHTMLToDebugMessages("clear history " + start);
	// };
	// history = null;
	// following now happens upon first reference in getHistory()
	// if (history == null) {
	// history = new double[historyMaximumSize];
	// };
	// for (int i = 0; i < historyMaximumSize; i++) {
	// history[i] = noValue;
	// };
    }

    public double[] getHistory() {
	if (history == null) {
	    history = new double[historyMaximumSize];
	    for (int i = 0; i < historyMaximumSize; i++) {
		history[i] = noValue;
	    }
	    // MoPiX2.addHTMLToDebugMessages("reset history at " + new
	    // Date().getTime());
	    lastHistoryEntry = 0;
	    historyValid = true;
	} else if (!historyValid) { 
	    // invalid up to and including lastHistoryEntry
	    for (int i = 0; i <= lastHistoryEntry; i++) {
		history[i] = noValue;
	    }
	    lastHistoryEntry = 0;
	    historyValid = true;
	}
	return history;
    }

    // public String getSpecifiedTimeExpression() {
    // return specifiedTimeExpressionMathML;
    // }
    // public void setSpecifiedTimeExpression(String specifiedTimeExpression) {
    // this.specifiedTimeExpressionMathML = specifiedTimeExpression;
    // }
    public static Equation[] appendEquation(Equation equation,
	    Equation equations[]) {
	// should probably use Java's List Collection instead
	int newLength = 1;
	if (equations != null) {
	    newLength = equations.length + 1;
	}
	Equation newEquations[] = new Equation[newLength];
	for (int i = 0; i < newLength - 1; i++) {
	    newEquations[i] = equations[i];
	}
	newEquations[newLength - 1] = equation;
	return newEquations;
    }

    public static Equation[] prependEquation(Equation equation,
	    Equation equations[]) {
	int newLength = 1;
	if (equations != null) {
	    newLength = equations.length + 1;
	}
	Equation newEquations[] = new Equation[newLength];
	for (int i = 0; i < newLength - 1; i++) {
	    newEquations[i + 1] = equations[i];
	}
	newEquations[0] = equation;
	return newEquations;
    }

    public static Equation[] removeEquation(Equation equation,
	    Equation equations[]) {
	if (equations == null || equations.length <= 1)
	    return null;
	Equation newEquations[] = new Equation[equations.length - 1];
	int index = 0;
	for (int i = 0; i < equations.length; i++) {
	    if (equation != equations[i]) {
		if (index == equations.length) {
		    Modeller.addToErrorLog("removeEquation failed since not a member");
		    return newEquations;
		}
		newEquations[index++] = equations[i];
	    }
	}
	return newEquations;
    }

    public String HTMLOfXMLNode(Node node, String parentFunction,
	    String attributeSuffix) throws HTMLToPointException {
	// not clear if this should be elsewhere since isn't really a method of
	// equations
	// parentFunction is the function that this node is an argument of
	NodeList argNodes = node.getChildNodes();
	if (argNodes.getLength() == 0) {
	    equationIncomplete = true;
	    return "?";
	}
	return super.HTMLOfXMLNode(node, parentFunction, attributeSuffix);
    };

    public EquationAppearance getEquationAppearance(boolean generateAppearance) {
	if (appearance == null && generateAppearance) {
	    return new EquationAppearance(this);
	}
	return equationAppearance;
    };

    public void setAppearance(EquationAppearance appearance) {
	// same but can't use template variables in 1.4 (gwt 4.60) so temp work
	// around
	this.equationAppearance = appearance;
	this.appearance = appearance;
	setOriginalAppearance(appearance);
    }

    public String getAttribute() {
	return attribute;
    }

    public void setAttribute(String attribute) {
	this.attribute = attribute;
    }

    public Equation virginState() {
	return new Equation(originalXML, null);
    }

    public EquationAppearance getOriginalAppearance() {
	return originalAppearance;
    }

    public void setOriginalAppearance(EquationAppearance originalAppearance) {
	this.originalAppearance = originalAppearance;
	if (originalAppearance != null) {
	    originalAppearance.incrementLiveCount();
	}
    }

    public Equation getReplacedEquation() {
        return replacedEquation;
    }
}
