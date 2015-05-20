package uk.ac.lkl.client.mopix;

import java.util.ArrayList;
import java.util.HashMap;

import uk.ac.lkl.client.MoPiX;
import uk.ac.lkl.client.Modeller;
import uk.ac.lkl.client.mopix.event.AddEquationToObjectEvent;
import uk.ac.lkl.client.event.ModellerEvent;
import uk.ac.lkl.client.event.CompoundEvent;
import uk.ac.lkl.client.mopix.expression.Equation;
import uk.ac.lkl.client.mopix.expression.Expression;
import uk.ac.lkl.client.mopix.graphics.Stroke;
import uk.ac.lkl.client.mopix.graphics.Shape;
import uk.ac.lkl.client.mopix.graphics.Line;
import uk.ac.lkl.client.mopix.graphics.BoundingBox;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FocusPanel;

public class MoPiXObject extends FocusPanel {
    // Constructs an empty HashMap with the default initial capacity (16) and
    // the default load factor (0.75).
    // HashMap<String,Equation> constantsTable = new HashMap<String,Equation>();
    // HashMap<String,Equation[]> equationTable = new
    // HashMap<String,Equation[]>();
//    protected HashMap constantsTable = new HashMap();
    protected HashMap<String, Equation[]> equationTable = new HashMap<String, Equation[]>();
    protected String name = "";
    protected Shape shape;
    protected int xOffset, yOffset;
    protected double x, y, rotation, width, height;
    protected int red, green, blue;
    protected double alpha = 1.0; // opaque
    protected double penTransparency = 1.0;
    protected int penRedColour, penGreenColour, penBlueColour, penDown, penThickness;
    // for drawing trails
    protected double previousX, previousY; 
    protected Line trailHistory[] = null;
    protected int previousTime = -1;
    protected FlipSide inspectorPanel = null;

    public MoPiXObject(String name) {
	super();
	this.name = name;
	initialiseShape();
	ClickHandler clickHandler = new ClickHandler() {

	    @Override
	    public void onClick(ClickEvent event) {
		shape.mouseDown(event);		
	    }
	    
	};
	this.addClickHandler(clickHandler);
    }

    public void initialiseShape() {
	shape = new Shape(this);
	setWidget(shape.getCanvas());
    }

    public MoPiXObject copy() {
	MoPiXObject copy = new MoPiXObject(Modeller.constants.copyOf() + name);
	giveCopyOfMyEquationsTo(copy);
	return copy;
    }

    public void updateState(int time) {
	// first compute appearance so is initialised for other computations
	double appearanceAsNumber = Equation.evaluateFunction("appearance", this, time);
	int widthMultiplier = 1; 
	// only text objects of more than one character are bigger
	// is this still a good idea?
	if (appearanceAsNumber != Expression.noValue) {
	    String appearanceAsNumberAsString;
	    if (appearanceAsNumber == Math.floor(appearanceAsNumber)) { 
		// is really an integer
		appearanceAsNumberAsString = String.valueOf((int) Math.floor(appearanceAsNumber));
	    } else {
		appearanceAsNumberAsString = String.valueOf(appearanceAsNumber);
	    }
	    setStringAppearance(appearanceAsNumberAsString);
	    widthMultiplier = appearanceAsNumberAsString.length();
	} else {
	    String appearance = Equation.evaluateStringFunction("appearance", this, time);
	    setAppearance(appearance);
	}
	setWidth(Equation.evaluateFunction("width", this, time));
	setHeight(Equation.evaluateFunction("height", this, time));
	if ((width > 0.0 && height > 0.0) || time == 0) { 
	    // no point if zero (and ignore negative sizes)
	    // if time is zero may need to initialise things
//	    resetTransform();
	    double previousX = getX(); // removed (int) Math.round(
	    double previousY = getY();
	    // if (!isBeingChanged()) { // experimenting with running while
	    // changing
	    setX(Equation.evaluateFunction("x", this, time));
	    // System.out.println("x of " + getName() + " " + getX());
	    setY(Equation.evaluateFunction("y", this, time));
	    // };
	    double currentX = getX(); 
	    // used to include (int) Math.round()
	    // but then grid position was too restricted
	    double currentY = getY();
	    setPenDown((int) Equation.evaluateFunction("penDown", this, time));
	    if (getPenDown() != 0 && time >= 1 && !isBeingChanged()
		    && previousTime + 1 == time) { // && !isBeingChanged() OK???
		setPenThickness((int) Math.round(Equation.evaluateFunction("thicknessPen", this, time)));
		if (getPenThickness() < 1)
		    setPenThickness(1); // temporary hack until models are updated
		setPenRedColour((int) Math.round(2.55 * Equation.evaluateFunction("redColourPen", this, time)));
		setPenGreenColour((int) Math.round(2.55 * Equation.evaluateFunction("greenColourPen", this, time)));
		setPenBlueColour((int) Math.round(2.55 * Equation.evaluateFunction("blueColourPen", this, time)));
		setPenTransparency(0.01 * Equation.evaluateFunction("transparencyPen", this, time)); // value between 0 and 1
		if (getPenTransparency() == 0.0) {
		    setPenTransparency(1.0); 
		    // a hack since everything defaults to zero
		}
		Stroke stroke = new Stroke(getPenRedColour(),
			                   getPenGreenColour(), 
			                   getPenBlueColour(),
			                   getPenTransparency(), 
			                   getPenThickness());
		Line line = 
		    new Line(
			(previousX * MoPiX.gridSizeX) % MoPiX.stageWidthDefault,
			MoPiX.stageHeightDefault - ((previousY * MoPiX.gridSizeY) % MoPiX.stageHeightDefault),
			(currentX * MoPiX.gridSizeX) % MoPiX.stageWidthDefault,
			MoPiX.stageHeightDefault - ((currentY * MoPiX.gridSizeY) % MoPiX.stageHeightDefault));
		line.setStroke(stroke);
		if (trailHistory == null) {
		    trailHistory = new Line[Equation.historyMaximumSize];
		}
		trailHistory[time] = line;
		line.addTo(MoPiX.runArea);
	    }
	    setRed((int) Math.round(2.55 * Equation.evaluateFunction("redColour", this, time)));
	    setGreen((int) Math.round(2.55 * Equation.evaluateFunction("greenColour", this, time)));
	    setBlue((int) Math.round(2.55 * Equation.evaluateFunction("blueColour", this, time)));
	    setAlpha(0.01 * Equation.evaluateFunction("transparency", this, time));
	    setRotation(Equation.evaluateFunction("rotation", this, time));
	    if (appearanceAsNumber != Equation.noValue) {
		setXOffset(10 * widthMultiplier);
		setYOffset(-20);
	    } else {
		if (getShapeCode() == Shape.RECTANGLE) { 
		    setXOffset(20); 
		    // half unscaled size (prior to transformations)
		    setYOffset(20);
		} else { // circle
		    setXOffset(0); // already centered
		    setYOffset(0);
		}
	    }
	    shape.display();
	    updatePosition(currentX, currentY, widthMultiplier);
	    previousTime = time;
	}
    }

    public int searchForNearestTime(int changeCode, int howMuchToSearch) {
	int currentTime = MoPiX.time;
	int bestTime = currentTime;
	double bestError = Double.MAX_VALUE;
	int earliestTime = Math.max(0, currentTime - howMuchToSearch);
	int latestTime = currentTime + howMuchToSearch;
	if (changeCode == Shape.MOVE) { // handle specially
	    double currentX = 
		getX() % MoPiX.stageWidthDefaultStageCoordinates;
	    double currentY = 
		getY() % MoPiX.stageHeightDefaultStageCoordinates;
	    double currentPositionSquared = 
		currentX * currentX + currentY * currentY;
	    double currentTimeX = 
		Equation.evaluateFunction("x", this, bestTime)
		    % MoPiX.stageWidthDefaultStageCoordinates;
	    double currentTimeY = 
		Equation.evaluateFunction("y", this, bestTime)
		    % MoPiX.stageHeightDefaultStageCoordinates;
	    bestError = Math.abs(currentTimeX * currentTimeX + currentTimeY
		    * currentTimeY - currentPositionSquared); 
	    // initialise at current time
	    for (int i = earliestTime; i <= latestTime; i++) {
		double newX = Equation.evaluateFunction("x", this, i)
			% MoPiX.stageWidthDefaultStageCoordinates;
		double newY = Equation.evaluateFunction("y", this, i)
			% MoPiX.stageHeightDefaultStageCoordinates;
		double newError = 
		    Math.abs(newX * newX + newY * newY - currentPositionSquared);
		if (newError < bestError) {
		    bestTime = i;
		    bestError = newError;
		}
	    }
	} else {
	    String attributeName = Shape.attributeNames[changeCode];
	    double currentValue = 0.0;
	    switch (changeCode) {
	    case Shape.ROTATE:
		currentValue = getRotation();
		break;
	    case Shape.CHANGE_WIDTH:
		currentValue = getWidth();
		break;
	    case Shape.CHANGE_HEIGHT:
		currentValue = getHeight();
		break;
	    case Shape.CHANGE_RED:
		currentValue = getRed();
		break;
	    case Shape.CHANGE_GREEN:
		currentValue = getGreen();
		break;
	    case Shape.CHANGE_BLUE:
		currentValue = getBlue();
		break;
	    case Shape.CHANGE_ALPHA:
		currentValue = getAlpha();
		break;
	    }
	 // initialise at current time
	    bestError = Math.abs(Equation.evaluateFunction(attributeName, this, bestTime) - currentValue); 
	    if (changeCode == Shape.ROTATE) {
		bestError = cannonicaliseAngleDifference(bestError);
	    }
	    // could have a preference for closest time rather than earliest
	    // time in case of ties
	    for (int i = earliestTime; i <= latestTime; i++) {
		double newValue = Equation.evaluateFunction(attributeName, this, i);
		double newError = Math.abs(newValue - currentValue);
		if (changeCode == Shape.ROTATE) {
		    newError = cannonicaliseAngleDifference(newError);
		}
		if (newError < bestError) {
		    bestTime = i;
		    bestError = newError;
		}
	    }
	}
	return bestTime;
    }
    
    static public double cannonicaliseAngleDifference(double angle) {
	angle = angle%360.0;
	if (angle > 180.0) {
	    return 360.0 - angle;
	} else if (angle < -180.0) {
	    return 360.0 + angle;
	} else {
	    return angle;
	}
    }

//    public void transform() {
//	resetTransform();
//	updatePosition(getX(), getY(), 1);
//    }

    public void updatePosition(double centerX, double centerY, int widthMultiplier) {
	int runAreaWidth = MoPiX.runArea.getOffsetWidth();
	int runAreaHeight = MoPiX.runArea.getOffsetHeight();
	int centerXInteger = (int) Math.round((centerX*MoPiX.gridSizeX))%runAreaWidth;
	int centerYInteger = (int) Math.round((centerY*MoPiX.gridSizeY))%runAreaHeight;
	// since y increases downward in Canvas
	centerYInteger = MoPiX.stageHeightDefault-centerYInteger; 
	int runAreaLeft = MoPiX.runArea.getAbsoluteLeft();
	int runAreaTop = MoPiX.runArea.getAbsoluteTop();
	Canvas canvas = shape.getCanvas();
	int halfWidth = canvas.getOffsetWidth()/2;
	int dx = centerXInteger-runAreaLeft-halfWidth;
	int halfHeight = canvas.getOffsetHeight()/2;
	int dy = centerYInteger-runAreaTop-halfHeight;
	MoPiX.runArea.setWidgetPosition(this, dx, dy);
	// following is now accomplished by shape.display()
//	rotategAt(getRotation(), getXOffset(), getYOffset());
//	scaleAt(getWidth()/Equation.unitSize*widthMultiplier, 
//		getHeight()/Equation.unitSize, 
//		getXOffset(), 
//		getYOffset());
//	setFill(Utils.createColor(getRed(), getGreen(), getBlue(), getAlpha()));
    }

    public void removeFromModel(boolean removeFromGlobals) {
	removeFromParent();
	shape.removeFromModel();
	clearTrailHistory(); 
	if (removeFromGlobals) {
	    MoPiXGlobals.removeFromAllObjects(this);
	}
    }

    public void setAppearance(String appearanceName) {
	if (!shape.setAppearance(appearanceName)) {
	    // failed so create a new shape
	    replaceShape();
	    shape.setAppearance(appearanceName);
	}
    }

    public void setStringAppearance(String appearanceName) {
	if (!shape.setStringAppearance(appearanceName)) {
	    replaceShape();
	    shape.setStringAppearance(appearanceName);
	}
    }

    public void replaceShape() {
	// shouldn't be necessary but fixes a bug when switching to split screen
	// of the gfx surface
	// TODO: revisit this
	Shape newShape = new Shape(this);
	newShape.setPreviousMouseX(shape.getPreviousMouseX());
	newShape.setPreviousMouseY(shape.getPreviousMouseY());
	removeFromParent();
	shape.removeFromModel();
	// MoPiX2.stageGroup.remove(shape); // above does this
	shape = newShape;
	if (MoPiX.stageGroup != null) {
	    MoPiX.stageGroup.add(shape);
	}
    }

    public String getName() {
	return name;
    }

    public String getNameHTML() {
	return Equation.subscriptFinalNumber(getName());
    }

//    public void setConstant(String constant, Expression equation) {
//	if (constantsTable.get(constant) == null) { 
//	    // check why sets only if not already set!!!
//	    constantsTable.put(constant, equation);
//	}
//    }
//
//    public int constantEquationCount() {
//	return constantsTable.size(); // more sure this is right
//    }

    public void appendAttribute(String attribute, Equation equation) {
	equationTable.put(attribute, 
		          Equation.appendEquation(equation, getEquations(attribute)));
	equationsChanged();
    }

    public void prependAttribute(String attribute, Equation equation) {
	equationTable.put(attribute, 
		          Equation.prependEquation(equation, getEquations(attribute)));
	equationsChanged();
    }

    public Equation[] getEquations(String attribute) {
	return equationTable.get(attribute); 
	// type coercion necessary in 1.4
    }

    public void setEquations(String attribute, Equation equations[]) {
	equationTable.put(attribute, equations);
	equationsChanged();
    }

    public void equationsChanged() {
	if (getInspectorPanel() != null) {
	    getInspectorPanel().updateContents(this);
	}
    }

    public int equationCount() {
	// sum up the lengths of each element of Collection
	int count = 0;
	// Iterator<Equation[]> iterator = equationTable.values().iterator();
	for (Equation equations[] : equationTable.values()) {
	    count += equations.length;
	}
	return count;
    }

    public void addHTMLOfAllEquations(FlipSide inspectorPanel) {
	for (Equation equations[] : equationTable.values()) {
	    if (equations != null) { 
		// can be null if equations have been explicitly removed
		for (int i = 0; i < equations.length; i++) {
		    EquationAppearance appearance = 
			equations[i].getEquationAppearance(true).copy();
		    equations[i].setAppearance(appearance);
		    appearance.customiseAppearance(equations[i].getAttribute(),
			                           equations[i], equations[i].getObject());
		    inspectorPanel.add(appearance);
		}
	    }
	}
	this.inspectorPanel = inspectorPanel;
    }
    
    public Equation findEquationWithSameXML(String XML) {
	for (Equation equations[] : equationTable.values()) {
	    if (equations != null) { 
		// can be null if equations have been explicitly removed
		for (int i = 0; i < equations.length; i++) {
		    String xmlOfEquation = equations[i].getXML();
		    if (XML.equals(xmlOfEquation)) {
			return equations[i];
		    }
		}
	    }
	}
	return null;
    }
    
    public String getXML(boolean includingCustomHTML) {
	StringBuilder buffer = new StringBuilder("<apply><and/>\r\n");
	addXML(buffer, includingCustomHTML);
	buffer.append("</apply>");
	return buffer.toString();
    }

    public void addXML(StringBuilder buffer, boolean includingCustomHTML) {
	for (Equation equations[] : equationTable.values()) {
	    if (equations != null) { 
		// can be null if equations have been explicitly removed
		for (int i = 0; i < equations.length; i++) {
		    if (includingCustomHTML) {
			buffer.append(equations[i].getXMLWithCustomHTML());
		    } else {
			buffer.append(equations[i].getXML());
		    }
		    buffer.append('\r');
		    buffer.append('\n');
		}
	    }
	}
    }

    public void clearHistoryAllEquations() {
	// call clearHistory() on each equation
	// Iterator<Equation[]> iterator = equationTable.values().iterator();
	for (Equation equations[] : equationTable.values()) {
	    if (equations != null) {
		for (int i = 0; i < equations.length; i++) {
		    equations[i].clearHistory();
		}
	    }
	}
	clearTrailHistory();
    }
    
    public void addEquation(Equation equation) {
	if (equation.timeIsT()) {
	    appendAttribute(equation.getAttribute(), equation);
	} else {
	    prependAttribute(equation.getAttribute(), equation);
	}
    }
    
    public void removeEquation(Equation equation) {
	String attribute = equation.getAttribute();
	setEquations(attribute, 
		     Equation.removeEquation(equation, getEquations(attribute)));
    }

    public void giveCopyOfMyEquationsTo(MoPiXObject other) {
	int equationCount = equationCount();
	ArrayList<ModellerEvent> events = new ArrayList<ModellerEvent>(equationCount);
	String otherName = other.getName();
	for (Equation equations[] : equationTable.values()) {
	    for (int i = 0; i < equations.length; i++) {
		String xmlString = 
		    equations[i].getXML().replaceAll(getName(),	otherName);
		EquationAppearance appearanceCopy = 
		    equations[i].getEquationAppearance(false);
		if (appearanceCopy != null) {
		    appearanceCopy = appearanceCopy.copy();
		}
		Equation equationCopy = 
		    new Equation(xmlString, other, appearanceCopy);
		if (appearanceCopy != null) {
		    appearanceCopy.setEquation(equationCopy);
		    // if (!appearanceCopy.isEquationalAppearance()) {
		    // appearanceCopy.setAlternativeHTML(equationCopy.HTMLAlgebra());
		    // };
		}
		events.add(new AddEquationToObjectEvent(other, equationCopy));
	    }
	}
	CompoundEvent event = new CompoundEvent(events);
	event.setAlternativeHTML(
		Modeller.constants.added() + Modeller.NON_BREAKING_SPACE + equationCount + Modeller.NON_BREAKING_SPACE +
		Modeller.constants.equationsOf() + Modeller.NON_BREAKING_SPACE +
		getNameHTML() + Modeller.NON_BREAKING_SPACE + Modeller.constants.to() + Modeller.NON_BREAKING_SPACE +
		other.getNameHTML());
	event.addToHistory();
    }

    public void clearTrailHistory() {
	for (int i = 0; i < Equation.historyMaximumSize; i++) {
	    removeTrailAt(i);
	}
    }

    public void removeTrailAt(int time) {
	if (trailHistory != null && trailHistory[time] != null) {
	    trailHistory[time].remove();
	    trailHistory[time] = null;
	}
    }

    public double getValue(double history[], int time, String attributeName, double defaultValue) {
	if (history == null || history[time] == Equation.noValue) {
	    Equation equation = Equation.findEquation(attributeName, this, time); 
	    // look for time specific value
	    if (equation != null) {
		return equation.getHistory()[time];
	    } else {
		// System.out.println("Expected to find an equation for " +
		// attributeName + " at time " + time);
		return defaultValue;
	    }
	} else {
	    return history[time];
	}
    }

    public void translate(int dx, int dy) {
//	shape.translate(dx, dy);
    }

    public void rotategAt(double angle, int xCenter, int yCenter) {
//	if (angle != 0.0) {
//	    shape.rotategAt(angle, xCenter, yCenter);
//	}
    }

    public void rotateg(double angle) {
//	if (angle != 0.0) {
//	    shape.rotateg(angle);
//	}
    }

    public void scale(double xScale, double yScale) {
	scale(xScale, yScale);
    }

    public void scaleAt(double xScale, double yScale, int xCenter, int yCenter) {
//	shape.scaleAt(xScale, yScale, xCenter, yCenter);
    }

//    public void resetTransform() {
//	shape.resetTransform(0, 0); // 0,0 added temporarily
//    }

//    public void setFill(int red, int green, int blue, double alpha) {
//	shape.setFill(red, green, blue, alpha);
//    }
//
//    public void setFill(CssColor color) {
//	shape.setFill(color);
//    }

    public void setSize(int width, int height) {
//	shape.setSize(width, height);
    }

    public boolean isBeingChanged() {
	return shape.isBeingChanged();
    }

    public double getX() {
	return x;
    }
    
    public int getXInt() {
	return (int) Math.round(x);
    }

    public void setX(double x) {
	this.x = x;
    }

    public double getY() {
	return y;
    }
    
    public int getYInt() {
	return (int) Math.round(y);
    }

    public void setY(double y) {
	this.y = y;
    }

    public double getRotation() {
	return rotation;
    }

    public void setRotation(double rotation) {
	this.rotation = rotation;
    }

    public double getAlpha() {
	return alpha;
    }

    public void setAlpha(double alpha) {
	this.alpha = alpha;
    }

    public int getShapeCode() {
	return shape.getShapeCode();
    }

    // public double getAppearance() {
    // return appearance;
    // }
    //
    // public void setAppearance(double appearance) {
    // this.appearance = appearance;
    // }
    public double getPreviousY() {
	return previousY;
    }

    public void setPreviousY(double previousY) {
	this.previousY = previousY;
    }

    public double getPreviousX() {
	return previousX;
    }

    public void setPreviousX(double previousX) {
	this.previousX = previousX;
    }

    public Shape getShape() {
	return shape;
    }

    public void setShape(Shape shape) {
	this.shape = shape;
    }

    public double getWidth() {
	return width;
    }
    
    public int getWidthInt() {
	return (int) Math.round(width);
    }

    public void setWidth(double width) {
	this.width = width;
    }

    public double getHeight() {
	return height;
    }
    
    public int getHeightInt() {
	return (int) Math.round(height);
    }

    public void setHeight(double height) {
	this.height = height;
    };

    public BoundingBox getBoundingBox() {
	return new BoundingBox(x - width / 2, x + width / 2,
		MoPiX.stageHeightDefault - (y + height / 2),
		MoPiX.stageHeightDefault - (y - height / 2));
    }

    // public int getAbsoluteTop() {
    // return absoluteTop;
    // }
    // public void setAbsoluteTop(int absoluteTop) {
    // this.absoluteTop = absoluteTop;
    // }
    // public int getAbsoluteLeft() {
    // return absoluteLeft;
    // }
    // public void setAbsoluteLeft(int absoluteLeft) {
    // this.absoluteLeft = absoluteLeft;
    // };
    public int getXOffset() {
	return xOffset;
    }

    public void setXOffset(int offset) {
	xOffset = offset;
    }

    public int getYOffset() {
	return yOffset;
    }

    public void setYOffset(int offset) {
	yOffset = offset;
    }

    public int getRed() {
	return red;
    }

    public void setRed(int red) {
	this.red = red;
    }

    public int getGreen() {
	return green;
    }

    public void setGreen(int green) {
	this.green = green;
    }

    public int getBlue() {
	return blue;
    }

    public void setBlue(int blue) {
	this.blue = blue;
    }

    public int getPenRedColour() {
	return penRedColour;
    }

    public void setPenRedColour(int penRedColour) {
	this.penRedColour = penRedColour;
    }

    public int getPenGreenColour() {
	return penGreenColour;
    }

    public void setPenGreenColour(int penGreenColour) {
	this.penGreenColour = penGreenColour;
    }

    public int getPenBlueColour() {
	return penBlueColour;
    }

    public void setPenBlueColour(int penBlueColour) {
	this.penBlueColour = penBlueColour;
    }

    public int getPenDown() {
	return penDown;
    }

    public void setPenDown(int penDown) {
	this.penDown = penDown;
    }

    public int getPenThickness() {
	return penThickness;
    }

    public void setPenThickness(int penThickness) {
	this.penThickness = penThickness;
    }

    public double getPenTransparency() {
	return penTransparency;
    }

    public void setPenTransparency(double penTransparency) {
	this.penTransparency = penTransparency;
    }

    public FlipSide getInspectorPanel() {
	return inspectorPanel;
    }

    public void setInspectorPanel(FlipSide inspectorPanel) {
	this.inspectorPanel = inspectorPanel;
    }

}

/*
 * public void drawTrail(JsGraphicsPanel graphicsPanel, int now) { Equation
 * equation = Equation.findEquation("penDown", this, -1); if (equation != null) {
 * equation = Equation.findEquation("penDown", this, now); // specific to this
 * time }; if (equation != null) { double penDownHistory[] =
 * equation.getHistory(); double thicknessPenHistory[] = null; equation =
 * Equation.findEquation("thicknessPen", this, -1); if (equation != null) {
 * thicknessPenHistory = equation.getHistory(); }; double redColourPenHistory[] =
 * null; equation = Equation.findEquation("redColourPen", this, -1); if
 * (equation != null) { redColourPenHistory = equation.getHistory(); }; double
 * greenColourPenHistory[] = null; equation =
 * Equation.findEquation("greenColourPen", this, -1); if (equation != null) {
 * greenColourPenHistory = equation.getHistory(); }; double
 * blueColourPenHistory[] = null; equation =
 * Equation.findEquation("blueColourPen", this, -1); if (equation != null) {
 * blueColourPenHistory = equation.getHistory(); }; double xHistory[] = null;
 * equation = Equation.findEquation("x", this, -1); if (equation != null) {
 * xHistory = equation.getHistory(); }; double yHistory[] = null; equation =
 * Equation.findEquation("y", this, -1); if (equation != null) { yHistory =
 * equation.getHistory(); }; // double transparencyPenHistory[] = null; // not
 * currently supported by JsGraphics int penThickness; int red, green, blue; for
 * (int time = 1; time < now-1; time++) { // start with time = 1 rather than 0
 * to avoid artefacts due to initial values if
 * (getValue(penDownHistory,time,"penDown",0.0) != 0.0) { int xNow = (int)
 * Math.round(getValue(xHistory,time,"x",0.0)); int yNow = (int)
 * Math.round(getValue(yHistory,time,"y",0.0)); int xNext = (int)
 * Math.round(getValue(xHistory,time+1,"x",0.0)); int yNext = (int)
 * Math.round(getValue(yHistory,time+1,"y",0.0)); if ((xNow != xNext || yNow !=
 * yNext) && // no change so nothing to draw // and not wrapping just now
 * (xNow/MoPiX2.panelWidth == xNext/MoPiX2.panelWidth && yNow/MoPiX2.panelHeight ==
 * yNext/MoPiX2.panelHeight)) { xNow = xNow % MoPiX2.panelWidth; xNext = xNext %
 * MoPiX2.panelWidth; yNow = MoPiX2.panelHeight - (yNow % MoPiX2.panelHeight); //
 * since y increases downward in JavaScript library yNext = MoPiX2.panelHeight -
 * (yNext % MoPiX2.panelHeight); if (thicknessPenHistory != null) { penThickness =
 * (int) Math.round(getValue(thicknessPenHistory,time,"thicknessPen",1.0));
 * graphicsPanel.setStrokeWidth(penThickness); }; red = (int)
 * Math.round(2.55*getValue(redColourPenHistory,time,"redColourPen",0.0)); green =
 * (int)
 * Math.round(2.55*getValue(greenColourPenHistory,time,"greenColourPen",0.0));
 * blue = (int)
 * Math.round(2.55*getValue(blueColourPenHistory,time,"blueColourPen",0.0)); //
 * System.out.println("r: " + red + " g: " + green + " b: " + blue + " time: " +
 * time); // graphicsPanel.setColor(new Color(red%256,green%256,blue%256)); //
 * graphicsPanel.drawLine(xNow, yNow, xNext, yNext); } }; }
 *  } } public void debug(int time, int i) { int xOffset = 0, yOffset; if (i ==
 * 0) { if (time == 0) setAppearance("circle"); xOffset = 0; yOffset = 0;
 * setFill(new Color(255,0,0,.4)); } else if (i == 1) { String s =
 * "12345.67890"; if (time == 0) setStringAppearance(s); xOffset =
 * 10*s.length(); yOffset = -20; setFill(new Color(0,255,0,.4)); } else { if
 * (time == 0) setAppearance("square"); xOffset = 20; yOffset = 20; setFill(new
 * Color(0,0,255,.5)); }; if (time%3 == 0) { resetTransform();
 * translate(200-xOffset, 300-yOffset); }; if (time%3 == 1)
 * rotategAt(30,xOffset,yOffset); if (time%3 == 2)
 * scaleAt(.5,2,xOffset,yOffset); };
 */
