package uk.ac.lkl.client.mopix.expression;

/**
 * @author Ken Kahn
 * 
 * copyright etc
 */


import java.util.ArrayList;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.XMLParser;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.DOM;

import com.google.gwt.user.client.ui.HTML;
import uk.ac.lkl.client.mopix.ExpressionAppearance;
import uk.ac.lkl.client.mopix.EquationAppearance;
import uk.ac.lkl.client.mopix.IncrementalProcessXMLCommand;
import uk.ac.lkl.client.mopix.MoPiXObject;
import uk.ac.lkl.client.Modeller;

import uk.ac.lkl.client.mopix.MoPiXGlobals;
import uk.ac.lkl.client.HTMLToPointException;
import uk.ac.lkl.client.MoPiX;
import uk.ac.lkl.client.Utils;
import uk.ac.lkl.client.mopix.event.AddObjectEvent;
import uk.ac.lkl.client.mopix.event.AddEquationToObjectEvent;
import uk.ac.lkl.client.event.ModellerEvent;

public class Expression {
    public static final double noValue = Double.MIN_VALUE;
    public static final double computingThisValue = Double.MAX_VALUE;
    public static final int plus = 0;
    public static final int times = 1;
    public static final int or = 2;
    public static final int minus = 3;
    public static final int divide = 4;
    public static final int power = 5;
    public static final int rem = 6;
    public static final int toPointX = 7;
    public static final int toPointY = 8;
    public static final int keyDown = 9;
    public static final int mouseX = 10;
    public static final int mouseY = 11;
    public static final int mo = 12;
    public static final int sin = 13;
    public static final int cos = 14;
    public static final int tan = 15;
    public static final int asin = 16;
    public static final int acos = 17;
    public static final int atan = 18;
    public static final int abs = 19;
    public static final int not = 20;
    public static final int lt = 21;
    public static final int gt = 22;
    public static final int leq = 23;
    public static final int geq = 24;
    public static final int eq = 25;
    public static final int neq = 26;
    public static final int cn = 27;
    public static final int ci = 28;
    // static final public int width = 0;
    // static public int height = 1;
    // static public int x = 2;
    // static public int y = 3;
    // static public int rotation = 5;
    // static public int redColour = 6;
    // static public int greenColour = 7;
    // static public int blueColour = 8;
    // static public int transparency = 9;
    // static public int appearance = 10;
    // static public int penDown = 11;
    // static public int thicknessPen = 12;
    // static public int redColourPen = 13;
    // static public int greenColourPen = 14;
    // static public int blueColourPen = 15;
    // static public int transparencyPen = 16;
    // String attributes[] =
    // {"width","height","x","y","rotation","redColour","greenColour","blueColour","transparency","appearance",
    // "penDown", "thicknessPen", "redColourPen", "greenColourPen",
    // "blueColourPen", "transparencyPen"};
    protected static MoPiXObject lastObject = null;
    private static boolean firstOBJECT1 = true; // so can refer to it as
						// "another" the first time and
						// "the other" the second time
    public static final double radiansPerDegree = Math.PI / 180.0;
    public static double unitSize = 40.0;

    // public static double atan2(double y, double x) { // atan2 missing from
    // GWT 1.4
    // if (x == 0.0) {
    // if (y >= 0.0) {
    // return Math.PI/2.0;
    // } else {
    // return Math.PI/-2.0;
    // }
    // } else {
    // return Math.atan(y/x);
    // }
    // }
    static public int attributeSubscript(String attribute) {
	if (attribute.length() > 1
		&& (attribute.charAt(0) == 'x' || attribute.charAt(0) == 'y')
		&& (attribute.charAt(1) >= '0' && attribute.charAt(1) <= '9')) { // digit
	    int angle = 0;
	    int index = 1;
	    while (attribute.charAt(index) >= '0'
		    && attribute.charAt(index) <= '9') {
		angle = angle * 10 + (attribute.charAt(index) - '0');
		index++;
		if (index == attribute.length()) {
		    break;
		}
	    }
	    return angle;
	} else {
	    return -1; // no subscript
	}
    }

    static public Expression createExpression(Node node, MoPiXObject object) {
	if (object == null) {
	    return null; // is a generic equation (with ME, etc.)
	}
	String tag = node.getNodeName();
	NodeList argNodes = node.getChildNodes();
	Expression subExpression1 = null;
	Expression subExpression2 = null;
	Expression subExpression3 = null;
	Expression subExpression4 = null; // generalise this later
	if (tag.equals("apply")) {
	    String functionName = argNodes.item(0).getNodeName();
	    int arity = argNodes.getLength() - 1; 
	    // don't count the functionName
	    if (arity >= 1) {
		subExpression1 = createExpression(argNodes.item(1), object);
		if (subExpression1 == null) {
		    arity = 0;
		}
		if (arity >= 2) {
		    subExpression2 = createExpression(argNodes.item(2), object);
		    if (subExpression2 == null) {
			arity = 1;
		    }
		    if (arity >= 3) { // toPointX and toPointY now
			subExpression3 = createExpression(argNodes.item(3),
				object);
			if (subExpression3 == null) {
			    arity = 2;
			}
			if (arity >= 4) {
			    subExpression4 = createExpression(argNodes.item(4),
				    object);
			    if (subExpression4 == null) {
				arity = 3;
			    }
			    // if (arity > 4) {
			    // MoPiX2.setStatusLine("Can't yet handle more than
			    // 4 arguments to a function. Ignoring them in " +
			    // node.toString());
			    // }
			}
		    }
		}
	    }
	    if (functionName.equals("times")) {
		if (arity == 0) {
		    return new ExpressionConstant(node, object, 1.0f);
		} else if (arity == 1) {
		    return subExpression1;
		} else {
		    if (arity > 2) {
			System.out.println("Ignoring extra arguments to "
				+ functionName + " (for now)");
		    }
		    return new ExpressionTimes(node, object, subExpression1,
			    subExpression2);
		}
	    } else if (functionName.equals("plus")) {
		if (arity == 0) {
		    return new ExpressionConstant(node, object, 0.0f);
		} else if (arity == 1) {
		    return subExpression1;
		} else {
		    if (arity > 2) {
			System.out.println("Ignoring extra arguments to "
				+ functionName + " (for now)");
		    }
		    return new ExpressionPlus(node, object, subExpression1,
			    subExpression2);
		}
	    } else if (functionName.equals("minus")) {
		if (arity == 0) {
		    return new ExpressionConstant(node, object, 0.0f);
		} else if (arity == 1) {
		    return subExpression1;
		} else {
		    if (arity > 2) {
			System.out.println("Ignoring extra arguments to "
				+ functionName + " (for now)");
		    }
		    return new ExpressionMinus(node, object, subExpression1,
			    subExpression2);
		}
	    } else if (functionName.equals("divide")) {
		if (arity == 0) {
		    return new ExpressionConstant(node, object, 1.0f);
		} else if (arity == 1) {
		    return subExpression1;
		} else {
		    if (arity > 2) {
			System.out.println("Ignoring extra arguments to "
				+ functionName + " (for now)");
		    }
		    return new ExpressionDivide(node, object, subExpression1,
			    subExpression2);
		}
	    } else if (functionName.equals("rem")) {
		if (arity == 0) {
		    return new ExpressionConstant(node, object, 1.0f);
		} else if (arity == 1) {
		    return subExpression1;
		} else {
		    if (arity > 2) {
			System.out.println("Ignoring extra arguments to " + functionName + " (for now)");
		    }
		    return new ExpressionRemainder(node, object, subExpression1, subExpression2);
		}
	    } else if (functionName.equals("power")) {
		if (arity == 0) {
		    return new ExpressionConstant(node, object, 1.0f);
		} else if (arity == 1) {
		    return subExpression1;
		} else {
		    if (arity > 2) {
			System.out.println("Ignoring extra arguments to " + functionName + " (for now)");
		    }
		    return new ExpressionPower(node, object, subExpression1, subExpression2);
		}
	    } else if (functionName.equals("or")) { 
		// the same as addition modulo 2 with 0 or 1 values
		if (arity == 0) {
		    return new ExpressionConstant(node, object, 0.0f);
		} else if (arity == 1) {
		    return subExpression1;
		} else {
		    if (arity > 2) {
			System.out.println("Ignoring extra arguments to " + functionName + " (for now)");
		    }
		    return new ExpressionOr(node, object, subExpression1, subExpression2);
		}
	    } else if (functionName.equals("and")) {
		if (arity == 0) {
		    return new ExpressionConstant(node, object, 1.0f);
		} else if (arity == 1) {
		    return subExpression1;
		} else {
		    if (arity > 2) {
			System.out.println("Ignoring extra arguments to " + functionName + " (for now)");
		    }
		    return new ExpressionAnd(node, object, subExpression1, subExpression2);
		}
	    } else if (functionName.equals("ToPointX")) { 
		// used to deal with attributes such as x45 and y30
		// generated internally so don't need to check -- could be move
		// clever now
		return new ExpressionToPoint(node, object, subExpression1, subExpression2, subExpression3, subExpression4, true);
	    } else if (functionName.equals("ToPointY")) {
		return new ExpressionToPoint(node, object, subExpression1, subExpression2, subExpression3, subExpression4, false);
	    } else if (functionName.equals("mo")) {
		String attributeName = argNodes.item(0).getFirstChild().getNodeValue();
		String objectName = argNodes.item(1).getFirstChild().getNodeValue();
		if (attributeName.equals("keyDown")) {
		    return new ExpressionKeyDown(node, object, objectName);
		} else if (objectName.equals(object.getName())) {
		    objectName = null; // is the same so don't consider it
		} else {
		    if (objectName.equals("mouse")) { // object == null && 
			if (attributeName.equals("x")) {
			    return new ExpressionMouseX(node, object);
			} else if (attributeName.equals("y")) {
			    return new ExpressionMouseY(node, object);
			} else {
			    Modeller.setStatusLine(Modeller.constants.onlyXAndYAreAttributesOfMouseNot() + " " + attributeName);
			    return null;
			}
		    }
		}
		int subAttribute = attributeSubscript(attributeName);
		if (subAttribute >= 0) {
		    if (objectName == null) {
			return new ExpressionPerimeter(node, object,
				attributeName.substring(0, 1), subExpression2,
				subAttribute, (attributeName.charAt(0) == 'x'));
		    } else {
			return new ExpressionPerimeterOtherObject(node, object,
				attributeName.substring(0, 1), objectName,
				subExpression2, subAttribute, (attributeName.charAt(0) == 'x'));
		    }
		}
		// should also compute the index to attributeName for
		// defaultAttributeValue
		if (objectName == null) {
		    return new ExpressionAttribute(node, object, attributeName,
			    subExpression2);
		} else {
		    return new ExpressionAttributeOtherObject(node, object,
			    attributeName, objectName, subExpression2);
		}
	    } else if (functionName.equals("sin")) {
		if (arity != 1) {
		    System.out.println("Wrong number of arguments to "
			    + functionName + " in " + node.toString());
		}
		return new ExpressionSin(node, object, subExpression1);
	    } else if (functionName.equals("cos")) {
		if (arity != 1) {
		    System.out.println("Wrong number of arguments to "
			    + functionName + " in " + node.toString());
		}
		return new ExpressionCos(node, object, subExpression1);
	    } else if (functionName.equals("tan")) {
		if (arity != 1) {
		    System.out.println("Wrong number of arguments to "
			    + functionName + " in " + node.toString());
		}
		return new ExpressionTan(node, object, subExpression1);
	    } else if (functionName.equals("asin")) {
		if (arity != 1) {
		    System.out.println("Wrong number of arguments to "
			    + functionName + " in " + node.toString());
		}
		return new ExpressionASin(node, object, subExpression1);
	    } else if (functionName.equals("acos")) {
		if (arity != 1) {
		    System.out.println("Wrong number of arguments to "
			    + functionName + " in " + node.toString());
		}
		return new ExpressionACos(node, object, subExpression1);
	    } else if (functionName.equals("atan")) {
		if (arity != 1) {
		    System.out.println("Wrong number of arguments to "
			    + functionName + " in " + node.toString());
		}
		return new ExpressionATan(node, object, subExpression1);
	    } else if (functionName.equals("abs")) {
		if (arity != 1) {
		    System.out.println("Wrong number of arguments to "
			    + functionName + " in " + node.toString());
		}
		return new ExpressionAbs(node, object, subExpression1);
	    } else if (functionName.equals("not")) {
		if (arity != 1) {
		    System.out.println("Wrong number of arguments to "
			    + functionName + " in " + node.toString());
		}
		return new ExpressionNot(node, object, subExpression1);
	    } else if (functionName.equals("lt")) {
		if (arity != 2) {
		    System.out.println("Wrong number of extra arguments to "
			    + functionName + " in " + node.toString());
		}
		return new ExpressionLessThan(node, object, subExpression1,
			subExpression2);
	    } else if (functionName.equals("leq")) {
		if (arity != 2) {
		    System.out.println("Wrong number of extra arguments to "
			    + functionName + " in " + node.toString());
		}
		return new ExpressionLessThanOrEqual(node, object,
			subExpression1, subExpression2);
	    } else if (functionName.equals("gt")) {
		if (arity != 2) {
		    System.out.println("Wrong number of extra arguments to "
			    + functionName + " in " + node.toString());
		}
		return new ExpressionGreaterThan(node, object, subExpression1,
			subExpression2);
	    } else if (functionName.equals("geq")) {
		if (arity != 2) {
		    System.out.println("Wrong number of extra arguments to "
			    + functionName + " in " + node.toString());
		}
		return new ExpressionGreaterThanOrEqual(node, object,
			subExpression1, subExpression2);
	    } else if (functionName.equals("eq")) {
		if (arity != 2) {
		    System.out.println("Wrong number of extra arguments to "
			    + functionName + " in " + node.toString());
		}
		return new ExpressionEqual(node, object, subExpression1,
			subExpression2);
	    } else if (functionName.equals("neq")) {
		if (arity != 2) {
		    System.out.println("Wrong number of extra arguments to "
			    + functionName + " in " + node.toString());
		}
		return new ExpressionNotEqual(node, object, subExpression1,
			subExpression2);
	    } else {
		Modeller.setStatusLine(functionName
			+ " not a recognised function name");
		System.out.println(functionName
			+ " not a recognised function name");
		return null;
	    }
	} else if (tag.equals("cn")) {
	    String answerAsString = argNodes.item(0).getNodeValue();
	    try {
		float constantValue = Float.parseFloat(answerAsString);
		return new ExpressionConstant(node, object, constantValue);
	    } catch (Exception e) {
		Modeller.setStatusLine("<cn>"
				+ answerAsString
				+ "</cn> is not a number. Maybe you should use <ci> instead.");
		return null;
	    }
	} else if (tag.equals("ci")) {
	    // special case t for speed
	    String constant = argNodes.item(0).getNodeValue();
	    if (constant.equals("t")) {
		return new ExpressionTime(node, object);
	    } else {
		return new ExpressionVariable(node, object, constant);
	    }
	}
	System.out.println("Problem creating an expression based upon " + node.toString());
	return null;
    }

    static public Expression createExpression(Node node, MoPiXObject object, ExpressionAppearance appearance) {
	Expression expression = createExpression(node, object);
	expression.setAppearance(appearance);
	return expression;
    }

    public static double currentAttributeValue(String attributeName, MoPiXObject object) {
	// replace names with enum-like constants !!!!
	if (attributeName.equals("x")) {
	    return object.getX();
	} else if (attributeName.equals("y")) {
	    // so y increases in the upward direction
	    return object.getY(); // MoPiX2.maxY-object.getY();
	    // the following uses _xscale and _yscale rather than _width and
	    // _height since the latter change as the object rotates
	} else if (attributeName.equals("width")) {
	    return (object.getWidth());
	} else if (attributeName.equals("height")) {
	    return (object.getHeight());
	} else if (attributeName.equals("rotation")) {
	    return object.getRotation();
	} else if (attributeName.equals("transparency")) {
	    // multiply by 100 to display as a percentage
	    return 100*object.getAlpha(); // Math.round(object.getAlpha()/ 2.55);
	} else if (attributeName.equals("appearance")) {
	    return noValue; // revisit this -- was object.getShapeCode();
	} else if (attributeName.equals("redColour")) {
	    // divide by 2.55 to display as a percentage
	    return Math.round(object.getRed()/2.55);
	} else if (attributeName.equals("greenColour")) {
	    return Math.round(object.getGreen()/2.55);
	} else if (attributeName.equals("blueColour")) {
	    return Math.round(object.getBlue()/2.55);
	} else if (attributeName.equals("transparencyPen")) {
	    return 100*object.getPenTransparency(); // Math.round(object.getPenTransparency()  / 2.55); // opaque
	} else {
	    return 0.0f;
	}
    }

    public static double defaultAttributeValue(String attributeName, MoPiXObject object, int t) {
	if (t > 0) {
	    return currentAttributeValue(attributeName, object); 
	    // new policy as of 271107
	}
	if (attributeName.equals("x")) {
	    return MoPiX.stageWidthDefault / (2 * MoPiX.gridSizeX);
	}
	if (attributeName.equals("y")) {
	    return MoPiX.stageHeightDefault / (2 * MoPiX.gridSizeY);
	}
	if (attributeName.equals("width")) {
	    return Equation.unitSize; // default size of initial square
	}
	if (attributeName.equals("height")) {
	    return Equation.unitSize;
	}
	if (attributeName.equals("transparency")) {
	    return 100.0;
	}
	if (attributeName.equals("appearance")) { 
	    // can be a number or a  string -- noValue if a string
	    return noValue;
	}
	if (attributeName.equals("transparencyPen")) {
	    return 100.0; // fully opaque
	}
	return 0.0;
    }

    public static double evaluateFunction(String attribute, MoPiXObject object, int t) {
	Equation equation = findEquation(attribute, object, t);
	if (equation == null) {
	    // prior to 020407 was currentAttributeValue
	    return defaultAttributeValue(attribute, object, t); 
	}
	double answer = equation.evaluate(object, t);
	// MoPiX2.addHTMLToDebugMessages(answer + " is the evaluation of " +
	// equation.HTML());
	return answer;
    }

    public static String evaluateStringFunction(String functionName,
	    MoPiXObject object, int t) {
	Equation equation = findEquation(functionName, object, t);
	if (equation == null) {
	    // trace("Couldn't find an equation for " + functionName + "(" +
	    // objectName + "," + t
	    // + ") using default value of " + defaultValue);
	    return "square"; 
	    // only one now
	    // defaultAttributeValue(functionName,object);
	    // prior to 020407 was currentAttributeValue
	}
	return equation.evaluateString(object, t);
    }

    public static Equation findEquation(String attribute, MoPiXObject object,
	    int t) {
	// returns a matching equation or null
	Equation equations[] = object.getEquations(attribute);
	if (equations == null) {
	    return null;
	}
	for (int i = 0; i < equations.length; i++) {
	    if (equations[i].matches(t)) {
		return equations[i];
	    }
	}
	// trace("No equation found for " + functionName + " of " + objectName);
	return null;
    }

    static public void processXML(Node node, boolean topLevel, ArrayList<ModellerEvent> events, String description) {
	// starts with apply "and" to a list of equations (where ME has already
	// been replaced with a name)
	// topLevel is so that if the file contains a single equation it is
	// displayed
	String tag = node.getNodeName();
	NodeList argNodes = node.getChildNodes();
	if (argNodes.getLength() == 0)
	    return; // was also "?";
	String functionName;
	Node firstArg = argNodes.item(0);
	if (tag.equals("apply")) {
	    functionName = firstArg.getNodeName();
	} else { // should really check there is only one child
	    processXML(firstArg, topLevel, events, description);
	    return;
	}
	if (functionName.equals("and")) {
	    if (topLevel) {
		// this is the same as the following loop but prevents the
		// browser from hanging or
		// putting up annoying warnings		
		Scheduler.get().scheduleIncremental(new IncrementalProcessXMLCommand(argNodes, events, description));
	    } else {
		for (int i = argNodes.getLength() - 1; i > 0; i--) {
		    // ordered reversed on 110207 to match the order they were
		    // originally added
		    // note that i = 0 is skipped since that is the "and"
		    processXML(argNodes.item(i), false, events, description);
		}
	    }
	} else if (functionName.equals("eq")) {
	    // System.out.println(argNodes.item(1).toString());
	    // System.out.println(argNodes.item(1).getChildNodes().item(1).toString());
	    // System.out.println(argNodes.item(1).getChildNodes().item(1).getFirstChild().toString());
	    String objectName = null;
	    NodeList children = argNodes.item(1).getChildNodes();
	    if (argNodes.item(1).getNodeName().equals("apply")) {
		objectName = children.item(1).getFirstChild().getNodeValue();
		// } else {
		// System.out.println(argNodes.item(1).toString()); // constant
	    }
	    // trace("eq: " + objectName + " " + node.toString());
	    // if (objectName == null) {
	    // MoPiX2.setStatusLine("ignoring equation of an null object");
	    // // new on 270306 since was saved without a name
	    // };
	    MoPiXObject object = null;
	    if (!topLevel) {
		if (objectName == null) { // new on 140507
		    object = lastObject;
		} else {
		    object = MoPiXGlobals.objectNamed(objectName); 
		}
		// should enhance so when adding a model it renames conflicting names
		if (object == null) {
		    object = new MoPiXObject(objectName);
		    if (objectName != null) {
			MoPiXGlobals.addToAllObjects(object);
			events.add(new AddObjectEvent(object));
		    }
		}
		lastObject = object;
	    }
	    Equation equation = new Equation(node, object);
	    // following needed to redo an undo
	    events.add(new AddEquationToObjectEvent(object, equation));
	    NamedNodeMap attributes = node.getAttributes();
	    if (attributes != null) {
		Node appearanceAttribute = attributes.getNamedItem("HTMLAppearance");
		if (appearanceAttribute != null) {
		    String appearanceHTML = appearanceAttribute.getNodeValue();
		    if (appearanceHTML != null) {
			appearanceHTML = Utils.decode(appearanceHTML);
			if (appearanceHTML != null) {
			    Expression expression1 = equation.getSubExpression1();
			    ExpressionAppearance appearance1 = null;
			    if (expression1 != null) {
				appearance1 = expression1.getAppearance(true);
			    }
			    Expression expression2 = equation.getSubExpression2();
			    ExpressionAppearance appearance2 = null;
			    if (expression2 != null) {
				appearance2 = expression1.getAppearance(true);
			    }
			    equation.setAppearance(new EquationAppearance(
				    equation, appearanceHTML, appearance1, appearance2));
			}
		    }
		}
	    }
	} else {
	    Modeller.setStatusLine(functionName
		    + " " + Modeller.constants.notHandledByProcessxmlWhileLoadingAModel());
	}
    }

    public static String subscriptFinalNumber(String attributeName) {
	if (attributeName.indexOf('_') < 0)
	    return attributeName; // so constants like 1.2 are ok
	int split = -1; // -1 means no split
	for (int i = attributeName.length() - 1; i > 0; i--) {
	    if (attributeName.charAt(i) >= '0' && attributeName.charAt(i) <= '9') {
		split = i;
	    } else {
		int numberStart;
		if (attributeName.charAt(i) == '_') {
		    split--; // don't include the final _
		    numberStart = i + 1;
		} else {
		    numberStart = split;
		}
		if (split < 0) {
		    return attributeName; // no subscript
		}
		return attributeName.substring(0, split) + "<sub>" +
		       attributeName.substring(numberStart) + "</sub>";
	    }
	}
	return attributeName;
    }

    protected double history[] = null; // what about non-numeric values?
    protected int lastHistoryEntry = 0;
    protected boolean historyValid = false;
    protected Node node;
    protected MoPiXObject object;
    protected Node originalXML;;
    protected ExpressionAppearance appearance = null;
    protected Expression subExpression1 = null;;
    protected Expression subExpression2 = null;;
    protected Expression subExpression3 = null; // just for toPointX
    protected Expression subExpression4 = null;
    protected int operation = -1; // not yet computed;
    protected int subAttribute = -1;;
    // protected String constant; // for references to constants
    // protected int argCount; // now is always 2
    // protected NodeList argNodes;
    protected String referencedAttributeName;;
    protected String referencedObjectName;;

    // the following code assumes (incorrectly?) that tags are lower case. (In
    // Java they are always upper case.)
    public Expression() {
	// nothing special
    };

    public Expression(String xmlString) {
	this(xmlString, null);
    };

    public Expression(String xmlString, MoPiXObject object) {
	// initialise();
	setXML(xmlString, object);
    };

    public Expression(String xmlString, MoPiXObject object,
	    ExpressionAppearance appearance) {
	this(xmlString, object);
	setAppearance(appearance);
    }

    public double distanceToPoint(double angle, double w, double h,
	    double rotation, String shape, boolean xWanted) {
	// computes the point on the perimeter of the shape at the angle
	// the shape has been rotated and scaled in x and y
	double pointOnUnitShape[] = pointOnShape(shape, angle);
	double x = pointOnUnitShape[0] * w / 2; // half the width is the
						// "radius"
	double y = pointOnUnitShape[1] * h / 2;
	double rotatedPoint[] = rotatePoint(x, y, rotation);
	// trace("distanceToPoint " + angle + " " + xScale + " " + yScale + " "
	// + rotation + " " + shape + " result: " + rotatedPoint);
	if (xWanted) {
	    return rotatedPoint[0];
	} else {
	    return rotatedPoint[1];
	}
    }

    public String NaturalLanguageHTMLOfXMLNode(Node node) throws HTMLToPointException {
	firstOBJECT1 = true;
	try {
	    return (NaturalLanguageHTMLOfXMLNode(node, "", ""));
	} catch (HTMLToPointException e) {
	    throw e; // need to worry about this...
	}
    }

    public String NaturalLanguageHTMLOfXMLNode(Node node, String parentFunction,
	    String attributeSuffix) throws HTMLToPointException {
	// not clear if this should be elsewhere since isn't really a method of equations
	// parentFunction is the function that this node is an argument of
	String tag = node.getNodeName();
	NodeList argNodes = node.getChildNodes();
	Node firstArg = argNodes.item(0);
	String functionName = null;
	if (firstArg == null) {
	    return ""; // just something like <apply/>
	}
	if (tag.equals("apply")) {
	    functionName = firstArg.getNodeName();
	} else {
	    functionName = tag;
	}
	String answer = "";
	if (functionName.equals("ci")) {
	    String variableName = firstArg.getNodeValue();
	    if (variableName.equals("t")) {
		answer = "the time";
	    } else {
		answer = subscriptFinalNumber(variableName); 
		// e.g. Object12 becomes Object<sub>12</sub>
	    }
	} else if (functionName.equals("cn")) {
	    answer = firstArg.getNodeValue();
	} else {
	    Node secondArg = argNodes.item(1);
	    if (secondArg == null) {
		return answer + redFont("?");
	    }
	    Node ownerNode = secondArg.getFirstChild();
	    if (functionName.equals("mo")) {
		Node innerFunctionNode = firstArg.getFirstChild();
		if (innerFunctionNode == null) {
		    return answer + redFont("?");
		}
		String innerFunctionName = innerFunctionNode.getNodeValue();
		String niceAttributeName = Utils.niceNameForAttribute(innerFunctionName);
		if (niceAttributeName != null) {
		    if (ownerNode == null) {
			return answer + redFont("?");
		    }
		    String owner = ownerNode.getNodeValue();
		    if (owner.equals("ME")) {
			answer += Modeller.constants.my() + " " + niceAttributeName;
		    } else if (owner.equals("OTHER1")) {
			if (firstOBJECT1) {
			    answer += Modeller.constants.anothers() + " " + niceAttributeName;
			    firstOBJECT1 = false;
			} else {
			    answer += niceAttributeName + " " + Modeller.constants.ofTheOther();
			}
		    } else if (owner.startsWith("OTHER")) {
			answer += niceAttributeName
				+ " "
				+ Modeller.constants.ofThe()
				+ " "
				+ Utils.ordinal(owner.substring("OTHER".length())) + " "
				+ Modeller.constants.other();
		    } else {
			answer += niceAttributeName + " "
				+ Modeller.constants.of() + " "
				+ NaturalLanguageHTMLOfXMLNode(secondArg);
		    }
		} else {
		    answer = "<i>" + innerFunctionName + attributeSuffix
			    + "</i>(";
		    if (argNodes.getLength() > 1) {
			for (int i = 1; i < argNodes.getLength() - 1; i++) {
			    answer = answer
				    + NaturalLanguageHTMLOfXMLNode(argNodes.item(i), innerFunctionName, "")
				    + ", ";
			}
			// last one
			answer = answer
				+ NaturalLanguageHTMLOfXMLNode(argNodes.item(argNodes.getLength() - 1),
					innerFunctionName, attributeSuffix);
		    }
		    answer = answer + ")";
		}
		// } else if (functionName.equals("minus")) { // special case to
		// deal with points on perimeter
		// try {
		// answer = answer + infixHTML(" &minus;
		// ",argNodes,functionName,
		// attributeSuffix,
		// (parentFunction.equals("times") ||
		// parentFunction.equals("divide") ||
		// parentFunction.equals("power")));
		// } catch (HTMLToPointException e) {
		// e.setRightHandSide(NaturalLanguageHTMLOfXMLNode(argNodes.item(1)));
		// throw e;
		// }
	    } else if (functionName.equals("eq")) {
		// need to handle specially to deal with x90 and the like
		try {
		    answer = NaturalLanguageHTMLOfXMLNode(secondArg, "eq", attributeSuffix)
			    + " "
			    + Modeller.constants.isEqualTo()
			    + " "
			    + NaturalLanguageHTMLOfXMLNode(argNodes.item(2), "eq", attributeSuffix);
		} catch (HTMLToPointException e) {
		    answer = NaturalLanguageHTMLOfXMLNode(secondArg, "eq", 
			    e.getAngle())
			    + " "
			    + Modeller.constants.isEqualTo()
			    + " "
			    + e.getRightHandSide();
		}
	    } else {
		String niceBinaryFunctionName = Utils
			.niceNameForBinaryOperator(functionName);
		if (niceBinaryFunctionName != null) {
		    answer = answer + NaturalLanguageHTMLOfXMLNode(secondArg)
			    + " " + niceBinaryFunctionName + " "
			    + NaturalLanguageHTMLOfXMLNode(argNodes.item(2));
		} else {
		    String niceUnaryFunctionName = Utils
			    .niceNameForUnaryOperator(functionName);
		    if (niceUnaryFunctionName != null) {
			answer = answer + niceUnaryFunctionName + " "
				+ NaturalLanguageHTMLOfXMLNode(secondArg);
		    } else if (functionName.equals("ToPointX")
			    || functionName.equals("ToPointY")) {
			// System.out.println(argNodes.item(1).getFirstChild());
			// first arg should be like <cn>90</cn>
			// int angle = Integer.valueOf(argNodes.item(1).getFirstChild().toString()).intValue();
			throw new HTMLToPointException(ownerNode.toString(), "");
		    } else {
			Modeller.setStatusLine(Modeller.constants.functionNotYetHandledByNaturallanguagehtmlofxmlnode()
					+ ": " + functionName);
		    }
		}
	    }
	}
	// System.out.println("NaturalLanguage HTML of " + node.toString() + "
	// is " +
	// answer);
	return answer;
    }

    public double evaluate(MoPiXObject object, int t) {
	return 0.0; // default value
    }

    public String getAlgebra() {
	HTML html = new HTML(HTMLAlgebra());
	return DOM.getInnerText(html.getElement());
    }

    public ExpressionAppearance getAppearance(boolean generateAppearance) {
	if (appearance == null && generateAppearance) {
	    appearance = new ExpressionAppearance(this);
	}
	return appearance;
    }

    public String getCustomHTML() {
	if (MoPiX.instance().useCustomHTMLDefault() || 
	    MoPiX.instance().generateNaturalLanguageHTMLDefault()) {
	    return getHTML(); // could pass down a flag that returns null if
			    // nothing custom encountered
	} else {
	    return null;
	}
    }

    public String getNaturalLanguage() {
	HTML html = new HTML(HTMLNaturalLanguage());
	return DOM.getInnerText(html.getElement());
    }

    public Node getNode() {
	return node;
    }

    public MoPiXObject getObject() {
	return object;
    }

    public int getOperation() {
	return operation;
    }

    public Node getOriginalNode() {
	return node;
    }

    public Expression getSubExpression1() {
	return subExpression1;
    }

    public Expression getSubExpression2() {
	return subExpression2;
    }

    public String getXML() {
	return node.toString();
    }

    public String getXMLInstanceFor(String objectName) {
	if (originalXML != null) {
	    return originalXML.toString().replaceAll("ME", objectName);
	} else if (object != null) { // switch "allegiance"
	    return getXML().replaceAll(object.getName(), objectName);
	} else {
	    return getXML().replaceAll("ME", objectName);
	}
    }

    public String getXMLTemplate(String templateVariable) { // e.g. ME or OTHER1
	if (object == null) {
	    return getXML();
	} else {
	    return getXML().replaceAll(object.getName(), templateVariable);
	}
    }

    public String getXMLWithCustomHTML() {
	String xml = getXML();
	String customHTML = getCustomHTML();
	if (customHTML != null) {
	    xml = xml.replaceFirst("<apply>", "<apply HTMLAppearance='"
		    + Utils.encode(customHTML) + "'>");
	}
	return xml;
    }

    public String getHTML() {
	if (appearance == null) {
	    appearance = new ExpressionAppearance(this);
	    appearance.customiseAppearance(null, this, object);
	}
	return appearance.getHTML();
    }

    public String HTMLAlgebra() {
	// returns HTML of the equation
	String HTMLString = "";
	boolean problemEncountered = false;
	if (node == null) {
	    HTMLString = "<ci>?</ci>";
	    problemEncountered = true;
	} else {
	    try {
		HTMLString = HTMLOfXMLNode(node);
	    } catch (Exception e) {
		HTMLString = "<ci>?</ci>";
		problemEncountered = true;
		Modeller.setStatusLine(Modeller.constants.errorOccurredWhileGeneratingTheHtmlFor() + ": "
			             + e.toString() + " in " + node.toString());
	    }
	}
	// MathMLString = "<math>" + MathMLString + "</math>"; // does this make
	// sense? -- experimenting without it
	if (problemEncountered) {
	    HTMLString = redFont(HTMLString);
	}
	return HTMLString;
    }
    
    public static String redFont(String innerHTML) {
	return "<font color='#FF0000'>" + innerHTML + "</font>";
    }

    public String HTMLNaturalLanguage() {
	// returns HTML of the equation
	String NaturalLanguageString = "";
	boolean problemEncountered = false;
	if (node == null) {
	    NaturalLanguageString = Modeller.constants.processingErrorSorry();
	    problemEncountered = true;
	} else {
	    try {
		NaturalLanguageString = NaturalLanguageHTMLOfXMLNode(node);
	    } catch (Exception e) {
		Modeller.setStatusLine(Modeller.constants.errorOccurredWhileGeneratingDescriptonOf() + ": "
		             + e.toString() + " in " + node.toString());
		e.printStackTrace();
		return redFont(HTMLAlgebra());
	    }
	}
	if (problemEncountered) {
	    NaturalLanguageString = redFont(NaturalLanguageString);
	}
	return NaturalLanguageString;
    };

    public String HTMLOfXMLNode(Node node) throws HTMLToPointException {
	try {
	    return (HTMLOfXMLNode(node, "", ""));
	} catch (HTMLToPointException e) {
	    throw e;
	}
    }

    public String HTMLOfXMLNode(Node node, String parentFunction,
	    String attributeSuffix) throws HTMLToPointException {
	// not clear if this should be elsewhere since isn't really a method of
	// equations
	// parentFunction is the function that this node is an argument of
	String tag = node.getNodeName();
	NodeList argNodes = node.getChildNodes();
	Node firstArg = argNodes.item(0);
	String functionName = null;
	if (tag.equals("apply")) {
	    functionName = firstArg.getNodeName();
	} else {
	    functionName = tag;
	}
	// MoPiX2.setStatusLine(functionName); // for debugging
	String answer = "";
	if (functionName.equals("ci")) {
	    answer = subscriptFinalNumber(firstArg.getNodeValue()); 
	    // e.g. Arm12 becomes Arm<sub>12</sub>
	} else if (functionName.equals("cn")) {
	    answer = firstArg.getNodeValue();
	} else if (functionName.equals("mo")) {
	    Node innerFunctionNode = firstArg.getFirstChild();
	    // REVISIT THIS !!!!!!!!!!!!!!!!
	    if (innerFunctionNode == null) {
		// equationIncomplete = true;
		return "?";
	    }
	    String innerFunctionName = innerFunctionNode.getNodeValue();
	    answer = "<i>" + innerFunctionName + attributeSuffix + "</i>(";
	    if (argNodes.getLength() > 1) {
		for (int i = 1; i < argNodes.getLength() - 1; i++) {
		    answer = answer
			    + HTMLOfXMLNode(argNodes.item(i),
				    innerFunctionName, "") + ", ";
		}
		// last one
		answer = answer
			+ HTMLOfXMLNode(
				argNodes.item(argNodes.getLength() - 1),
				innerFunctionName, attributeSuffix);
	    }
	    answer = answer + ")";
	} else if (functionName.equals("eq")) {
	    try {
		answer = HTMLOfXMLNode(argNodes.item(1), "eq", attributeSuffix)
			+ " = "
			+ HTMLOfXMLNode(argNodes.item(2), "eq", attributeSuffix);
	    } catch (HTMLToPointException e) {
		answer = HTMLOfXMLNode(argNodes.item(1), "eq", e.getAngle())
			+ " = " + e.getRightHandSide();
	    }
	} else if (functionName.equals("times")) {
	    answer = answer
		    + infixHTML(" &times; ", argNodes, functionName, attributeSuffix, false); 
	} else if (functionName.equals("and")) {
	    answer = answer
		    + infixHTML(" and ", argNodes, functionName, attributeSuffix, false);
	} else if (functionName.equals("plus")) {
	    answer = answer
		    + infixHTML(
			    " + ",
			    argNodes,
			    functionName,
			    attributeSuffix,
			    (parentFunction.equals("times") ||
			     parentFunction.equals("divide") || 
			     parentFunction.equals("power")));
	} else if (functionName.equals("or")) {
	    answer = answer
		    + infixHTML(" or ", argNodes, functionName,
			    attributeSuffix, false);
	} else if (functionName.equals("minus")) {
	    try {
		answer = answer
			+ infixHTML(
				" &minus; ",
				argNodes,
				functionName,
				attributeSuffix,
				(parentFunction.equals("times") ||
				 parentFunction.equals("divide") || 
				 parentFunction.equals("power")));
	    } catch (HTMLToPointException e) {
		e.setRightHandSide(HTMLOfXMLNode(argNodes.item(1)));
		throw e;
	    }
	} else if (functionName.equals("divide")) {
	    answer = answer
		    + infixHTML(" &divide; ", argNodes, functionName,
			    attributeSuffix, false);
	} else if (functionName.equals("power")) {
	    answer = HTMLOfXMLNode(argNodes.item(1)) + "<sup>"
		    + HTMLOfXMLNode(argNodes.item(2)) + "</sup>";
	} else if (functionName.equals("sin") || functionName.equals("cos")
		   || functionName.equals("tan") || functionName.equals("asin")
		   || functionName.equals("acos") || functionName.equals("atan")) {
	    // not italics according to
	    // http://en.wikipedia.org/wiki/Wikipedia:Manual_of_Style_(mathematics)
	    answer = functionName + "(" + HTMLOfXMLNode(argNodes.item(1)) + ")";
	} else if (functionName.equals("abs")) { // new on 131206
	    answer = "<b>|</b>" + HTMLOfXMLNode(argNodes.item(1)) + "<b>|</b>";
	} else if (functionName.equals("rem")) { 
	    // remainder or mod of positive args
	    answer = answer + "mod(" + HTMLOfXMLNode(argNodes.item(1)) + ","
		    + HTMLOfXMLNode(argNodes.item(2)) + ")";
	} else if (functionName.equals("not")) {
	    answer = answer + "not(" + HTMLOfXMLNode(argNodes.item(1)) + ")";
	} else if (functionName.equals("lt")) {
	    answer = answer + "(" + HTMLOfXMLNode(argNodes.item(1)) + " &lt; "
		    + HTMLOfXMLNode(argNodes.item(2)) + ")";
	} else if (functionName.equals("leq")) {
	    answer = answer + "(" + HTMLOfXMLNode(argNodes.item(1)) + " ≤ "
		    + HTMLOfXMLNode(argNodes.item(2)) + ")";
	} else if (functionName.equals("gt")) {
	    answer = answer + "(" + HTMLOfXMLNode(argNodes.item(1)) + " &gt; "
		    + HTMLOfXMLNode(argNodes.item(2)) + ")";
	} else if (functionName.equals("geq")) {
	    answer = answer + "(" + HTMLOfXMLNode(argNodes.item(1)) + " ≥ "
		    + HTMLOfXMLNode(argNodes.item(2)) + ")";
	} else if (functionName.equals("eq")) {
	    answer = answer + "(" + HTMLOfXMLNode(argNodes.item(1)) + " = "
		    + HTMLOfXMLNode(argNodes.item(2)) + ")";
	} else if (functionName.equals("neq")) {
	    answer = answer + "(" + HTMLOfXMLNode(argNodes.item(1)) + " ≠ "
		    + HTMLOfXMLNode(argNodes.item(2)) + ")";
	} else if (functionName.equals("ToPointX")
		|| functionName.equals("ToPointY")) {
	    // System.out.println(argNodes.item(1).getFirstChild());
	    // first arg should be like <cn>90</cn>
	    // int angle =
	    // Integer.valueOf(argNodes.item(1).getFirstChild().toString()).intValue();
	    throw new HTMLToPointException(argNodes.item(1).getFirstChild()
		    .toString(), "");
	} else {
	    Modeller.setStatusLine(Modeller.constants.functionNotYetHandledByHtmlofxmlnode() + ": "
		    + functionName);
	}
	// System.out.println("HTML of " + node.toString() + " is " + answer);
	return answer;
    };

    public String infixHTML(String operator, NodeList argNodes,
	    String parentFunction, String attributeSuffix,
	    boolean needsBracketing) throws HTMLToPointException {
	String answer = "";
	if (needsBracketing) { // was == true) {
	    answer = "(";
	}
	answer = answer
		+ HTMLOfXMLNode(argNodes.item(1), parentFunction, attributeSuffix);
	if (argNodes.getLength() > 1) {
	    answer = answer + operator;
	    for (int i = 2; i < argNodes.getLength() - 1; i++) {
		answer = answer
			+ HTMLOfXMLNode(argNodes.item(i), parentFunction,
				attributeSuffix) + operator;
	    }
	    // last one
	    answer = answer
		    + HTMLOfXMLNode(argNodes.item(argNodes.getLength() - 1),
			    parentFunction, attributeSuffix);
	}
	if (needsBracketing == true) {
	    answer = answer + ")";
	}
	return answer;
    };

    public double[] pointOnShape(String shape, double angle) {
	double[] result = new double[2];
	if (shape.equals("square")) {
	    angle = angle % 360;
	    if (angle >= 315 || angle <= 45) {
		result[0] = (float) Math.sin(angle * radiansPerDegree);
		result[1] = 1.0f;
		return result;
	    }
	    if (angle >= 45 && angle <= 135) {
		result[0] = 1.0f;
		result[1] = (float) Math.sin((90 - angle) * radiansPerDegree);
		return result;
	    }
	    if (angle >= 135 && angle <= 225) {
		result[0] = (float) Math.sin((180 - angle) * radiansPerDegree);
		result[1] = -1.0f;
		return result;
	    }
	    // otherwise is between 225 and 315
	    result[0] = -1.0f;
	    result[1] = (float) Math.sin((270 - angle) * radiansPerDegree);
	    return result;
	} else { // correct for circles
	    double radians = (90 - angle) * radiansPerDegree;
	    // convert from clock-like angles to radians
	    result[0] = (float) Math.cos(radians);
	    result[1] = (float) Math.sin(radians);
	    return result;
	}
    };

    public double[] rotatePoint(double x, double y, double rotation) {
	double result[] = new double[2];
	if (rotation == 0) {
	    result[0] = x;
	    result[1] = y;
	    return result; // no need to do all this work
	}
	float distance = (float) Math.sqrt(x * x + y * y);
	double angle = Math.atan2(y, x);
	double totalAngle = angle - rotation * radiansPerDegree; 
	// angle is already in radians
	result[0] = Math.cos(totalAngle) * distance;
	result[1] = Math.sin(totalAngle) * distance;
	return result;
    }

    public void setAppearance(ExpressionAppearance appearance) {
	this.appearance = appearance;
    }

    public void setObject(MoPiXObject object) {
	setXML(getXMLInstanceFor(object.getName()), object); 
	// a bit of extra work but works
    }

    public void setOperation(int operation) {
	this.operation = operation;
    }

    public void setSubExpression1(Expression subExpression1) {
	this.subExpression1 = subExpression1;
    }

    public void setSubExpression2(Expression subExpression2) {
	this.subExpression2 = subExpression2;
    }

    public Equation setXML(Node node, MoPiXObject object) {
	if (node.getNodeType() == Node.DOCUMENT_NODE) {
	    node = ((Document) node).getDocumentElement();
	}
	this.node = node;
	this.object = object;
	// Equation.setXML does return the replaced equation if there is one
	return null;
    }

    public Equation setXML(String xmlString, MoPiXObject object) {
	// returns expression that this replaced
	if (xmlString.equals(""))
	    return null;
	Document xml;
	try {
	    xml = XMLParser.parse(xmlString);
	    if (originalXML == null) {
		originalXML = xml; 
		// in case we want to restore it to a template
	    }
	} catch (Exception e) {
	    Modeller.setStatusLine(Modeller.constants.mathmlXmlParseError() + ": " + e.toString()
		                 + " " + Modeller.constants.whileLoading()  + ": " + xmlString);
	    e.printStackTrace();
	    return null;
	}
	// XMLParser.removeWhitespace(xml);
	// Window.alert("xml : " + xml.toString() + " count: " +
	// xml.getChildNodes().getLength() + "\n" +
	// "xml first child: " + xml.getFirstChild().toString() + " count: " +
	// xml.getFirstChild().getChildNodes().getLength() + "\n" +
	// "xml first child: " + xml.getChildNodes().item(0).toString() + "
	// count: " + xml.getChildNodes().item(0).getChildNodes().getLength());
	return setXML(xml.getFirstChild(), object);
    }

    public String XMLNodeValueIfCI(Node node) {
	String tag = node.getNodeName();
	if (tag.equals("ci")) {
	    return node.getFirstChild().getNodeValue(); 
	    // if not bound then use the name as a string (e.g. ARM)}
	} else {
	    if (Modeller.DEBUG) {
		System.out.println("XMLNodeValueIfCI called on: " + node
			           + " and expected CI tag.");
	    }
	    return null;
	}
    }
};

// public double evaluate(MoPiXObject object, int t) {
// double answer = 0.0; // unless overridden below
// double leftHand;
// double rightHand;
// switch (operation) {
// case mo:
// if (referencedObjectName != null) {
// object = MoPiXGlobals.objectNamed(referencedObjectName);
// };
// int timeValue = (int) Math.round(subExpression2.evaluate(object, t)); //
// maybe an expression like t-1
// double offset = 0.0;
// if (subAttribute >= 0) {
// offset = distanceToPoint((double) subAttribute,
// evaluateFunction("width",object,timeValue),
// evaluateFunction("height",object,timeValue),
// evaluateFunction("rotation",object,timeValue),
// evaluateStringFunction("appearance",object,timeValue),
// (referencedAttributeName.equals("x"))); // could "prepare" this
// };
// if (timeValue >= 0) {
// // rewritten on 290807 to include defaults etc -- and it caused infinite
// recursion
// // restored on 041007 to get toss ball to work
// answer = evaluateFunction(referencedAttributeName,object,timeValue);
// };
// if (answer == noValue) {
// // prior to 020407 was currentAttributeValue
// answer = defaultAttributeValue(referencedAttributeName, object, t);
// // trace("Can't find an equation for " + attributeName + " of " + objectName
// // + " using default " + answer + " was evaluating " + node);
// };
// answer = answer + offset;
// break;
// case times:
// answer = subExpression1.evaluate(object, t) * subExpression2.evaluate(object,
// t);
// break;
// case plus:
// answer = subExpression1.evaluate(object, t) + subExpression2.evaluate(object,
// t);
// break;
// case minus:
// // answer = evaluateXMLNode(argNodes.item(1),object,t) -
// evaluateXMLNode(argNodes.item(2),object,t);
// answer = subExpression1.evaluate(object, t) - subExpression2.evaluate(object,
// t);
// break;
// case divide:
// // answer = evaluateXMLNode(argNodes.item(1),object,t) /
// evaluateXMLNode(argNodes.item(2),object,t);
// answer = subExpression1.evaluate(object, t) / subExpression2.evaluate(object,
// t);
// break;
// case rem: // remainder -- not clear if with negative args is different from %
// // double dividend = evaluateXMLNode(argNodes.item(1),object,t);
// // double divisor = evaluateXMLNode(argNodes.item(2),object,t);
// double dividend = subExpression1.evaluate(object, t);
// double divisor = subExpression2.evaluate(object, t);
// answer = dividend % divisor;
// if (answer < 0.0) {
// answer += divisor;
// };
// break;
// case power:
// answer = Math.pow(subExpression1.evaluate(object, t),
// subExpression2.evaluate(object, t));
// break;
// case toPointX: // used to deal with attributes such as x45 and y30
// answer = distanceToPoint(subExpression1.evaluate(object, t),
// subExpression2.evaluate(object, t),
// subExpression3.evaluate(object, t),
// subExpression4.evaluate(object, t),
// Equation.evaluateStringFunction("appearance", object, t),
// // XMLNodeValueIfCI(argNodes.item(5)),
// true);
// // much room for improvement here
// break;
// case toPointY:
// answer = distanceToPoint(subExpression1.evaluate(object, t),
// subExpression2.evaluate(object, t),
// subExpression3.evaluate(object, t),
// subExpression4.evaluate(object, t),
// Equation.evaluateStringFunction("appearance", object, t),
// // XMLNodeValueIfCI(argNodes.item(5)),
// false);
// break;
// case keyDown:
// answer = MoPiX2.wholePanel.getKeyDown(referencedObjectName);
// break;
// case mouseX:
// answer = MoPiX2.wholePanel.getMouseCurrentX();
// break;
// case mouseY:
// answer = MoPiX2.wholePanel.getMouseCurrentY();
// break;
// case or:
// // the same as addition modulo 2 with 0 or 1 values
// // but can be optimised since if any arg is 1 then so is answer
// if (subExpression1.evaluate(object, t) > 0.0) {
// answer = 1.0;
// break; // no need to do more work
// }
// if (subExpression2.evaluate(object, t) > 0.0) {
// answer = 1.0;
// break; // no need to do more work
// }
// break;
// // 'and' it treated the same as times:
// case sin:
// answer = Math.sin(subExpression1.evaluate(object, t) * radiansPerDegree);
// break;
// case cos:
// answer = Math.cos(subExpression1.evaluate(object, t) * radiansPerDegree);
// break;
// case tan:
// answer = Math.tan(subExpression1.evaluate(object, t) * radiansPerDegree);
// break;
// case asin:
// answer = Math.asin(subExpression1.evaluate(object, t)) / radiansPerDegree;
// break;
// case acos:
// answer = Math.acos(subExpression1.evaluate(object, t)) / radiansPerDegree;
// break;
// case atan:
// answer = Math.atan(subExpression1.evaluate(object, t)) / radiansPerDegree;
// break;
// case abs:
// answer = Math.abs(subExpression1.evaluate(object, t));
// break;
// case not:
// answer = subExpression1.evaluate(object, t);
// answer = (answer+1) % 2;
// break;
// case lt:
// leftHand = subExpression1.evaluate(object, t);
// rightHand = subExpression2.evaluate(object, t);
// if (leftHand < rightHand) {
// answer = 1.0;
// };
// break;
// case leq:
// leftHand = subExpression1.evaluate(object, t);
// rightHand = subExpression2.evaluate(object, t);
// if (leftHand <= rightHand) {
// answer = 1.0;
// };
// break;
// case gt:
// leftHand = subExpression1.evaluate(object, t);
// rightHand = subExpression2.evaluate(object, t);
// if (leftHand > rightHand) {
// answer = 1.0;
// };
// break;
// case geq:
// leftHand = subExpression1.evaluate(object, t);
// rightHand = subExpression2.evaluate(object, t);
// if (leftHand >= rightHand) {
// answer = 1.0;
// };
// break;
// case eq:
// leftHand = subExpression1.evaluate(object, t);
// rightHand = subExpression2.evaluate(object, t);
// if (leftHand == rightHand) {
// answer = 1.0;
// };
// case neq:
// leftHand = subExpression1.evaluate(object, t);
// rightHand = subExpression2.evaluate(object, t);
// if (leftHand != rightHand) {
// answer = 1.0;
// };
// break;
// }
// // System.out.println(answer + " = " + node + " at time " + t);
// return answer;
// }
