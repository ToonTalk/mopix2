package uk.ac.lkl.client.mopix.graphics;

import java.util.ArrayList;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.Command;
import uk.ac.lkl.client.Modeller;
import uk.ac.lkl.client.mopix.ExpressionAppearance;
import uk.ac.lkl.client.mopix.MoPiXGlobals;
import uk.ac.lkl.client.mopix.MoPiXObject;
import uk.ac.lkl.client.mopix.EquationAppearance;
import uk.ac.lkl.client.mopix.FlipSide;
import uk.ac.lkl.client.MoPiX;
import uk.ac.lkl.client.Utils;
import uk.ac.lkl.client.mopix.event.AddEquationToObjectEvent;
import uk.ac.lkl.client.mopix.event.AddObjectEvent;
import uk.ac.lkl.client.event.ModellerEvent;
import uk.ac.lkl.client.event.CompoundEvent;
import uk.ac.lkl.client.mopix.event.RemoveObjectEvent;
import uk.ac.lkl.client.mopix.expression.Equation;
import uk.ac.lkl.client.mopix.expression.Expression;


public class Shape {
    // shape code constants
    // TODO: replace with enum
    public final static int SHAPE_NOT_INITIALIZED = -1;
    public final static int RECTANGLE = 0;
    public final static int ELLIPSE = 1;
    public final static int LINE = 2;
    public final static int TEXT = 3;
    // now constants for user changing the shape
    // final static int WAITING_FOR_CLICK = -1;
    public final static int NOT_CHANGING = 0;
    public final static int MOVE = 1;
    public final static int ROTATE = 2;
    public final static int CHANGE_WIDTH = 3;
    public final static int CHANGE_HEIGHT = 4;
    public final static int CHANGE_RED = 5;
    public final static int CHANGE_GREEN = 6;
    public final static int CHANGE_BLUE = 7;
    public final static int CHANGE_ALPHA = 8;
//    private static final double SQUARE_ROOT_OF_2 = 1.1415; // slightly more
    public static String attributeNames[] = { "", "position", "rotation",
	    "width", "height", "redColour", "greenColour", "blueColour",
	    "transparency" };
    // probably don't need both lists of names -- rationalise this
    public static String shapeAttributeMenuItems[] = { "width", "height", "x",
	    "y", "rotation", "redColour", "greenColour", "blueColour",
	    "transparency", "appearance" };
    public static String shapeAttributeMenuAttributeLabels[] = new String[] { Modeller.constants.width(),
	    Modeller.constants.height(), Modeller.constants.x(), Modeller.constants.y(), Modeller.constants.rotation(), Modeller.constants.percentOfRed(),
	    Modeller.constants.percentOfGreen(), Modeller.constants.percentOfBlue(), Modeller.constants.transparency(), Modeller.constants.costume() };
    // should add pen, visibility etc too??
    public static boolean mouseMovedSinceChangeStarted = false;
    public static MoPiXObject copyMyEquations = null;
    protected int shapeCode = SHAPE_NOT_INITIALIZED;
    protected int userChangeCode = NOT_CHANGING;
    protected boolean beingChanged = false;
    protected boolean draggable = true;
    protected MoPiXObject object;
//    protected Context2d context = null;
    protected int previousMouseX;
    protected int previousMouseY;
    protected Stroke stroke;
    protected Canvas canvas;
    private String appearance;
    static protected Command findNearestTimeCommand = null;
    static protected Command updateAllObjectsCommand = null;

    public Shape() {
	super();
//	this.setStylePrimaryName("modeller-Shape");
    }

    public Shape(MoPiXObject object) {
	this();
	this.object = object;
    }
    
//    private void handleShapeBrowserEvent(Event event) {
//	// the following isn't needed anymore
//	// DOM.eventPreventDefault(event);
//	// disableContextMenu(getElement());
//	// DOM.eventCancelBubble(event,true);
//	if (!isDraggable()) {
//	    return; // e.g. a trail element
//	}
//	int eventType = DOM.eventGetType(event);
//	if (eventType == Event.ONMOUSEMOVE) {
//
//	} else if (eventType == Event.ONMOUSEDOWN) {
//	    mouseDown();
//	    return;
//	} else if (eventType == Event.ONMOUSEUP	&& userChangeCode != NOT_CHANGING) {
//	    userStopsChanging();
//	} else if (eventType == Event.ONMOUSEOVER) {
//	    // this doesn't have any effect in FireFox ...
//	    DOM.setStyleAttribute(MoPiX.innerStagePanel.getElement(), "cursor", "hand");
//	} else if (eventType == Event.ONMOUSEOUT) {
//	    DOM.setStyleAttribute(MoPiX.innerStagePanel.getElement(), "cursor", "default");
//	} else if (eventType == Event.ONDBLCLICK) {
//	    userChangeCode = NOT_CHANGING;
//	    createPopupMenu(event);
//	}
//    }

    public void mouseDown(ClickEvent event) {
	// new scheme is that any click produces the menu
	// if (DOM.eventGetButton(event) == Event.BUTTON_RIGHT) {
	if (copyMyEquations != null) {
	    copyMyEquations.giveCopyOfMyEquationsTo(getObject());
	    copyMyEquations = null;
	    Equation.clearHistoryAllEquations();
	    MoPiX.instance().updateAll(false);
	} else if (MoPiX.waitingToInstantiateOTHERXML != null) {
	    String instantiatedXML = 
		MoPiX.waitingToInstantiateOTHERXML.replaceAll("OTHER" + MoPiX.waitingToInstantiateOTHER, 
			                                      getObject().getName());
	    int otherIndex = MoPiX.waitingToInstantiateOTHER + 1;
	    String nextOther = "OTHER" + otherIndex;
	    int containsOTHER = instantiatedXML.indexOf(nextOther);
	    if (containsOTHER < 0) {
		EquationAppearance appearance =
		    MoPiX.equationToAddToObject.getEquationAppearance(true);
		appearance.incrementLiveCount();
		// if (!appearance.isEquationalAppearance()) {
		// appearance.setAlternativeHTML(instantiatedXML);
		// };
		Equation copy = 
		    new Equation(instantiatedXML, MoPiX.waitingToInstantiateOTHERRecipient, appearance);
		Equation.clearHistoryAllEquations();
		MoPiX.instance().updateAll(false);
		//		    MoPiX2.addToHistory(Utils
		//				    .textFontToMatchIcons("Added&nbsp;")
		//				    + appearance.getHTML()
		//				    + Utils.textFontToMatchIcons("&nbsp;to&nbsp;")
		//				    + Utils.textFontToMatchIcons(MoPiX2.waitingToInstantiateOTHERRecipient
		//						    .getNameHTML()));
		//		    MoPiX2.setStatusLineHTML("Equation added to "
		//			    + MoPiX2.waitingToInstantiateOTHERRecipient
		//				    .getNameHTML());
		new AddEquationToObjectEvent(MoPiX.waitingToInstantiateOTHERRecipient, copy).addToHistory();
		MoPiX.instance().notwaitingForClickOnOTHER();
	    } else {
		MoPiX.instance().waitingForClickOnOTHER(instantiatedXML, otherIndex);
		Modeller.setStatusLineHTML(Modeller.constants.clickOnTheObjectThatIs() + " "
			+ Equation.subscriptFinalNumber(nextOther)
			+ " " + Modeller.constants.in() + " "
			+ new Equation(instantiatedXML, null).HTMLAlgebra());
	    }
	} else if (MoPiX.equationToAddToObject != null) {
	    String instantiatedXML = 
		MoPiX.equationToAddToObject.getXMLInstanceFor(getObject().getName());
	    int containsOTHER = instantiatedXML.indexOf("OTHER1");
	    if (containsOTHER < 0) {
		EquationAppearance originalAppearance = 
		    MoPiX.equationToAddToObject.getEquationAppearance(true);
		originalAppearance.incrementLiveCount();
		EquationAppearance appearance = originalAppearance.copy(); 
		// because it often is for another object
		// if (!appearance.isEquationalAppearance()) {
		// appearance.setAlternativeHTML(instantiatedXML);
		// };
		MoPiXObject object = getObject();
		if (instantiatedXML.indexOf("ME") < 0) {
		    String objectName = object.getName();
		    int equationObjectNameStart = 
			instantiatedXML.indexOf("<ci>");
		    if (equationObjectNameStart >= 0) {
			equationObjectNameStart += 4; 
			// 4 is length of <ci>
			int equationObjectNameEnd = 
			    instantiatedXML.indexOf("</ci>");
			String equationObjectName = 
			    instantiatedXML.substring(equationObjectNameStart, equationObjectNameEnd);
			if (!objectName.equals(equationObjectName)) {
			    instantiatedXML = instantiatedXML.replaceAll(equationObjectName, objectName);
			}
		    }
		}
		Equation copy = new Equation(instantiatedXML, object, appearance);
		if (originalAppearance.getParent() == MoPiX.expressionStagePanel) {
		    copy.setOriginalAppearance(originalAppearance);
		}
		appearance.setEquation(copy);
		new AddEquationToObjectEvent(getObject(), copy).addToHistory();
		Equation.clearHistoryAllEquations();
		MoPiX.instance().updateAll(false); 
		// "Re-computing the state of the world.");
		MoPiX.equationToAddToObject = null;
	    } else {
		MoPiX.instance().waitingForClickOnOTHER(instantiatedXML, 1, getObject());
		Modeller.setStatusLineHTML(Modeller.constants.clickOnTheObjectThatIs() + " "
			+ Equation.subscriptFinalNumber("OTHER1")
			+ " " + Modeller.constants.in() + " "
			+ new Equation(instantiatedXML, null).HTMLAlgebra());
	    }
	} else if (MoPiX.changeCodeOfNextObject != NOT_CHANGING) {
	    userToMakeChanges(MoPiX.changeCodeOfNextObject);
	    MoPiX.changeCodeOfNextObject = NOT_CHANGING;
	    Modeller.setStatusLine(Modeller.constants.whileHoldingTheMouseButtonDownMoveTheMouseToChangeTheObject()); 
	    // no longer waiting for click
	} else {
	    if (!isBeingChanged()) {
		userToMakeChanges(MOVE);
	    }
	    // this undoes the drag start -- not sure what the best scheme is for 
	    // pop up menu and draggabe -- is draggable important?
	    createPopupMenu(event);
	}
    }

    public void createPopupMenu(ClickEvent event) {
	userChangeCode = NOT_CHANGING;
	final Shape shape = this;
	final PopupPanel popupMenu = new PopupPanel(true);
	popupMenu.setAnimationEnabled(true);
	MenuBar menu = new MenuBar(true);
	menu.setAnimationEnabled(true);
	popupMenu.setWidget(menu);
	MenuBar getAttributeMenu = new MenuBar(true);
	menu.addItem(Modeller.constants.getAttribute(), getAttributeMenu);
	// add pen stuff later
//	MenuItem firstMenuItem = null;
	for (int i = 0; i < shapeAttributeMenuItems.length; i++) {
	    addAttributeMenuItem(
		    shapeAttributeMenuItems[i],
		    shapeAttributeMenuAttributeLabels[i], popupMenu,
		    getAttributeMenu);
	    if (i == 0) {
//		firstMenuItem = menuItem;
	    }
	}
	// need something like positionPopupMenu for getAttributeMenu
	menu.addItem(Modeller.constants.copy(), new Command() {
	    public void execute() {
		// could animate this flying in from off screen since will end
		// up on top of the original need something to see what is
		// happening
		popupMenu.hide();
		MoPiXObject copy = shape.getObject().copy();
		new AddObjectEvent(copy).addToHistory();
		MoPiXGlobals.addToAllObjects(copy);
		MoPiX.instance().updateAll(true);
//		MoPiX2.setStatusLineHTML("Added " + copy.getNameHTML());
//		MoPiX2.addToHistory(Utils.textFontToMatchIcons("Added&nbsp;")
//			+ Utils.textFontToMatchIcons(copy.getNameHTML()));
	    };
	});
	menu.addItem(new MenuItem(Modeller.constants.inspect(), new Command() {
	    public void execute() {
		popupMenu.hide();
		MoPiXObject object = shape.getObject();
		if (object.getInspectorPanel() == null) {
		    MoPiX.inspectorPanel.add(new FlipSide(object));
		} else {
		    Modeller.setStatusLineHTML(Modeller.constants.anInspectorIsAlreadyOpenFor() + object.getNameHTML());
		}
	    };
	}));
	if (MoPiX.displayCopyMyEquationsButton) {
	    menu.addItem(Modeller.constants.copyMyEquationsTo() + "...", new Command() {
		public void execute() {
		    // could animate this flying in from off screen since will
		    // end up on top of the original need something to see what
		    // is happening
		    popupMenu.hide();
		    copyMyEquations = shape.getObject();
		    Modeller.setStatusLineHTML(Modeller.constants.clickOnTheObjectThatShouldReceiveTheEquationsOf()
				    + copyMyEquations.getNameHTML());
		};
	    });
	}
	if (MoPiX.displaySaveObjectButton) {
	    menu.addItem(Modeller.constants.save(), new Command() {
		public void execute() {
		    popupMenu.hide();
		    MoPiX.instance().saveDialog(this, MoPiX.SAVING_OBJECT, shape.getObject().getXML(true));
		}
	    });
	}
	menu.addItem(Modeller.constants.delete(), new Command() {
	    public void execute() {
		popupMenu.hide();
		shape.getObject().removeFromModel(true);
		new RemoveObjectEvent(shape.getObject()).addToHistory();
	    }
	});
	popupMenu.show();
	Utils.positionPopupMenu(event.getClientX(), event.getClientY(), popupMenu);
	// popupMenu.setPopupPosition(DOM.eventGetClientX(event)-menu.getOffsetWidth()/2+Window.getScrollLeft(),
	// DOM.eventGetClientY(event)-firstMenuItem.getOffsetHeight()/2+Window.getScrollTop());
    }

    private MenuItem addAttributeMenuItem(final String attributeName,
	    String attributeLabel, final PopupPanel popupMenu,
	    MenuBar getAttributeMenu) {
	MenuItem menuItem = new MenuItem(attributeLabel, new Command() {
	    public void execute() {
		popupMenu.hide();
		String objectName = object.getName();
		String mathML = constructMathMLAttribute(attributeName,
			objectName);
		Expression attribute = new Expression(mathML, object);
		ExpressionAppearance attributeAppearance = new ExpressionAppearance(attribute);
		attributeAppearance.customiseAppearance(attributeName, attribute, object);
		MoPiX.addToExpressionPalette(attributeAppearance);
		Modeller.setStatusLine(Modeller.constants.newExpressionCreatedClickOnItForOptions());
		// MoPiX2.setStatusLineHTML("<p>Click on " +
		// attributeAppearance.getHTML() + " for a menu of what you can
		// do with this." );
	    };
	});
	getAttributeMenu.addItem(menuItem);
	return menuItem;
    };

    public void userToMakeChanges(int kindOfChange) {
	mouseMovedSinceChangeStarted = false;
	MoPiX.runArea.userToMakeChanges(this, kindOfChange);
	userChangeCode = kindOfChange; 
    }

    // public void userStartsChanging(int kindOfChange, Event event) {
    // setBeingChanged(true);
    // userChangeCode = kindOfChange;
    // startChangeMouseX = DOM.eventGetClientX(event);
    // startChangeMouseY = DOM.eventGetClientY(event);
    // previousMouseX = startChangeMouseX;
    // previousMouseY = startChangeMouseY;
    // };
    
    public void mouseMoved(Event event) {
	int mouseX = event.getClientX();
	int mouseY = event.getClientY();
	mouseMoved(mouseX, mouseY);
    }
	
    public void mouseMoved(int mouseX, int mouseY) {
	if (userChangeCode == NOT_CHANGING) {
	    return;
	}
	if (!isDraggable()) {
	    return; // e.g. a trail element
	}
	if (mouseX == previousMouseX && previousMouseY == mouseY) {
System.out.println("No coordinate change");
	    return;
	}
	if (!isBeingChanged()) {
	    previousMouseX = mouseX;
	    previousMouseY = mouseY;
	    setBeingChanged(true);
//System.out.println("Not being changed");
	    return;
	}
	mouseMovedSinceChangeStarted = true;
//System.out.println("user change code: " + userChangeCode);	
	switch (userChangeCode) {
	case MOVE:
	    double scale = MoPiX.getTrueStageScale();
	    draggedBy((mouseX - previousMouseX) / (scale * MoPiX.gridSizeX),
		      (previousMouseY - mouseY) / (scale * MoPiX.gridSizeY));
	    break;
	case ROTATE:
	    // this doesn't work right when rotating more than 180 (or 90?)
	    // degrees
	    // double previousAngle =
	    // Equation.atan2(previousMouseY-object.getY(),
	    // previousMouseX-object.getX());
	    // Element containingPanel = DOM.getParent(getElement());
	    // int containerTop = DOM.getAbsoluteTop(containingPanel);
	    // int containerLeft = DOM.getAbsoluteLeft(containingPanel);
	    // need to use same coordinate system for this shape and the mouse
	    double myAbsoluteCenterY = object.getY(); // DOM.getAbsoluteTop(getElement())-object.getHeight()/2;
	    double myAbsoluteCenterX = object.getX(); // DOM.getAbsoluteLeft(getElement())-object.getWidth()/2;
	    double currentAngle = Math.atan2(mouseY - myAbsoluteCenterY, mouseX - myAbsoluteCenterX);
	    // MoPiX2.addToDebugMessages("currentAngle: " + currentAngle + "
	    // ;degrees: " + currentAngle/Equation.radiansPerDegree + " ;y: " +
	    // (mouseY-myAbsoluteCenterY) + " ;x: " +
	    // (mouseX-myAbsoluteCenterX));
	    // MoPiX2.addToDebugMessages("centerY: " + myAbsoluteCenterY + "
	    // centerX: " + myAbsoluteCenterX);
	    rotatedToAngle(currentAngle / Equation.radiansPerDegree); // -previousAngle);
	    break;
	case CHANGE_WIDTH:
	    changeWidthBy(mouseX - previousMouseX);
	    break;
	case CHANGE_HEIGHT:
	    changeHeightBy(previousMouseY - mouseY);
	    break;
	case CHANGE_RED:
	    changeRedBy(mouseX - previousMouseX);
	    break;
	case CHANGE_GREEN:
	    changeGreenBy(mouseX - previousMouseX);
	    break;
	case CHANGE_BLUE:
	    changeBlueBy(mouseX - previousMouseX);
	    break;
	case CHANGE_ALPHA:
	    changeAlphaBy(mouseX - previousMouseX);
	    break;
	default:
	    System.out.println("Change not yet implemented for code: "
		    + userChangeCode);
	}
	previousMouseX = mouseX;
	previousMouseY = mouseY;
	if (MoPiX.instance().isTimeLocked()) {
	    if (MoPiX.instance().updateAsMuchAsPossible()
		    && updateAllObjectsCommand == null) {
		updateAllObjectsCommand = new Command() {
		    public void execute() {
			// long start = new Date().getTime();
			// MoPiX2.addHTMLToDebugMessages("start " + start);
			// Equation.clearHistoryAllEquations(); // do it now
			// because does nothing when !MoPiX2.useCachedValues --
			// above should have triggered it
			MoPiX.useCachedValues = false;
			addEquationforChange(false);
			MoPiX.instance().updateAllObjects(MoPiX.time); 
			// in case some other object's state depends upon this
			MoPiX.useCachedValues = true;
			updateAllObjectsCommand = null;
			// MoPiX2.addHTMLToDebugMessages("end " + (new
			// Date().getTime()-start) + " started " + start);
		    }
		};
		// MoPiX2.addHTMLToDebugMessages("scheduled " + new
		// Date().getTime());
		Scheduler.get().scheduleDeferred(updateAllObjectsCommand);
	    }
	} else {
	    findNearestTime();
	}
    }

    public static String constructMathMLEquation(String attributeName,
	    double attributeValue, String objectName, boolean now) {
	if (now) {
	    return "<apply><eq /><apply><mo>" + attributeName + "</mo><ci>"
		    + objectName + "</ci><cn>" + MoPiX.time
		    + "</cn></apply><cn>" + attributeValue + "</cn></apply>";
	} else {
	    return "<apply><eq /><apply><mo>" + attributeName + "</mo><ci>"
		    + objectName + "</ci><ci>t</ci></apply><cn>"
		    + attributeValue + "</cn></apply>";
	}
    }

    public static String constructMathMLAttribute(String attributeName,
	    String objectName) {
	return "<apply><mo>" + attributeName + "</mo><ci>" + objectName
		+ "</ci><ci>t</ci></apply>";
    }

    public static ModellerEvent addMathML(String xml, MoPiXObject object, boolean record) {
	Equation equation = new Equation(xml, object);
	if (record) {
	    return new AddEquationToObjectEvent(object, equation);
//	    MoPiX2.addToHistory(Utils.textFontToMatchIcons("Added&nbsp;")
//		    + equation.HTML()
//		    + Utils.textFontToMatchIcons("&nbsp;to&nbsp;")
//		    + Utils.textFontToMatchIcons(object.getNameHTML()));
	}
	return null;
    }

    public void userStopsChanging() {
	if (userChangeCode == NOT_CHANGING) {
	    return;
	}
	if (!mouseMovedSinceChangeStarted) {
	    userChangeCode = NOT_CHANGING;
	    setBeingChanged(false);
	    return;
	}
	if (MoPiX.instance().isTimeLocked()) {
	    addEquationforChange(true);
	    userChangeCode = NOT_CHANGING;
	    setBeingChanged(false);
	    if (MoPiX.instance().updateOnMouseUp()) {
		if (MoPiX.clearCacheWhenChangingViaUI) {
		    Equation.clearHistoryAllEquations();
		}
		MoPiX.instance().updateAllObjects(MoPiX.time, null);
		// something goes wrong when deferred below -- probably was a
		// different bug -- revisit this
		// MoPiX2.deferredUpdateAll();
	    }
	} else if (userChangeCode != NOT_CHANGING) {
	    findNearestTime();
	    setBeingChanged(false);
	    userChangeCode = NOT_CHANGING; // do it now so not called twice
	}
    }

    protected ModellerEvent constructAndAddEquation(String attribute, double value, boolean record) {
	if (MoPiX.dontChangeViaUIUnlessConstant) {
	    Equation equations[] = object.getEquations(attribute);
	    if (equations != null) {
		if (equations.length > 1) {
		    return null; // not constant
		} else {
		    if (!equations[0].isConstant()) {
			return null;
		    }
		}
	    }
	}
	return addMathML(
		constructMathMLEquation(
			attribute, 
			value, 
			getObject().getName(), 
			MoPiX.directManipulationIsForCurrentTime),
		getObject(),
		record);
    }

    protected void addEquationforChange(boolean record) {
	ModellerEvent event = null;
	switch (userChangeCode) {
	case MOVE:
	    ArrayList<ModellerEvent> events = new ArrayList<ModellerEvent>(2);
	    events.add(constructAndAddEquation("x", getObject().getX(), record));
	    events.add(constructAndAddEquation("y", getObject().getY(), record));
	    event = new CompoundEvent(events);
	    // addMathML(constructMathMLEquation("x", getObject().getX(),
	    // objectName, MoPiX2.directManipulationIsForCurrentTime),
	    // getObject(), record);
	    // addMathML(constructMathMLEquation("y", getObject().getY(),
	    // objectName, MoPiX2.directManipulationIsForCurrentTime),
	    // getObject(), record);
	    break;
	case ROTATE:
	    event = constructAndAddEquation("rotation", getObject().getRotation(), record);
	    break;
	case CHANGE_WIDTH:
	    event = constructAndAddEquation("width", getObject().getWidth(), record);
	    break;
	case CHANGE_HEIGHT:
	    event = constructAndAddEquation("height", getObject().getHeight(), record);
	    break;
	case CHANGE_RED:
	    event = constructAndAddEquation("redColour", Math.round(getObject().getRed() / 2.55), record);
	    break;
	case CHANGE_GREEN:
	    event = constructAndAddEquation("greenColour", Math.round(getObject().getGreen() / 2.55), record);
	    break;
	case CHANGE_BLUE:
	    event = constructAndAddEquation("blueColour", Math.round(getObject().getBlue() / 2.55), record);
	    break;
	case CHANGE_ALPHA:
	    event = constructAndAddEquation("transparency", Math.round(getObject().getAlpha() * 100.0), record);
	    break;
	case NOT_CHANGING:
	    break; // do nothing
	default:
	    System.out.println("Change not yet implemented for code: "
		    + userChangeCode);
	}
	if (record && event != null) {
	    event.addToHistory();
	}
    }

    protected void findNearestTime() {
	final int changeCode = userChangeCode;
	if (findNearestTimeCommand == null) {
	    findNearestTimeCommand = new Command() {
		public void execute() {
		    Modeller.setStatusLine(Modeller.constants.pleaseWaitWhileWeSearchForTheBestMatchingTime());
		    int nearestTime = object.searchForNearestTime(changeCode, 200);
		    if (nearestTime != MoPiX.time) {
			MoPiX.instance().updateAllObjects(nearestTime, null);
			MoPiX.instance().setTime(nearestTime);
		    }
		    Modeller.setStatusLine(Modeller.constants.timeSetTo() + " " + nearestTime);
		    findNearestTimeCommand = null;
		};
	    };
	    Scheduler.get().scheduleDeferred(findNearestTimeCommand);
	}
    }

    public boolean setAppearance(String appearanceName) {
	boolean shapeChanged = false;
	if (appearanceName.equalsIgnoreCase("square")) {
	    if (shapeCode != RECTANGLE) {
		shapeChanged = true;
		shapeCode = RECTANGLE;
	    }
	} else if (appearanceName.equalsIgnoreCase("circle")) {
	    if (shapeCode != ELLIPSE) {
		shapeChanged = true;
		shapeCode = ELLIPSE;
	    }
	} else if (appearanceName.equalsIgnoreCase("line")) {
	    // not currently used
	    if (shapeCode != LINE) {
		shapeChanged = true;
		shapeCode = LINE;
	    }
	} else {
	    // best way to deal with this?
	    Window.alert(appearanceName + " " + Modeller.constants.isAnUnrecognisedAppearance());
	    return true;
	}
	if (shapeChanged && MoPiX.stageGroup != null) {
	    MoPiX.stageGroup.add(this);
	}
	return true;
    }

    public boolean setStringAppearance(String appearance) {
	shapeCode = TEXT;
	this.appearance = appearance;
	return true;
    }
    
    public void display() {
	Context2d context2d = getContext2d();
	context2d.save();
	int widthInt = object.getWidthInt();
	int heightInt = object.getHeightInt();
	// expand to larger dimension to accommodate rotation
	// a square rotated 45 degrees has a diagonal that is the square root of 2 
	// larger than the square size -- OK to be slightly larger, e.g. 1.5
	int maximumDimension = 3*Math.max(widthInt, heightInt)/2;
	getCanvas().setPixelSize(maximumDimension, maximumDimension);
	getCanvas().setCoordinateSpaceWidth(maximumDimension);
	getCanvas().setCoordinateSpaceHeight(maximumDimension);
	// keep it centred in the expanded space
	int centerX = (maximumDimension-widthInt)/2;
	int centerY = (maximumDimension-heightInt)/2;
	context2d.translate(centerX, centerY);
	CssColor color = Utils.createColor(object.getRed(), object.getGreen(), object.getBlue(), object.getAlpha());
	context2d.setFillStyle(color);
	context2d.setStrokeStyle(color);
	double width = object.getWidth();
	double height = object.getHeight();
	double rotation = object.getRotation();
	if (rotation != 0.0) {
	    context2d.translate(width*0.5, height*0.5);
	    context2d.rotate(Math.toRadians(rotation));
	    context2d.translate(width*-0.5, height*-0.5);
	}
	switch (getShapeCode()) {
	case Shape.RECTANGLE: 
	    context2d.scale(width, height);
	    context2d.fillRect(0, 0, 1, 1);
	    break;
	case Shape.ELLIPSE:
	    context2d.scale(width, height);
	    // draw unit circle (appropriately transformed)
	    context2d.arc(0.5, 0.5, 0.5, 0, 2*Math.PI);
	    context2d.fill();
	    break;
	case Shape.TEXT:
	    // for debugging
//	    context2d.setFillStyle(Utils.createColor(0, 255, 0, 0.4));
//	    context2d.fillRect(0, 0, width, height);
//	    context2d.setFillStyle(color);
	    context2d.setFont(heightInt + "px Courier");
	    context2d.fillText(appearance, 0, height, width);
	    context2d.strokeText(appearance, 0, height, width);
	    break;
	case Shape.LINE:
	    // not currently used
	    break;
	}
	context2d.restore();
    }

    public void removeFromModel() {
	MoPiX.stageGroup.remove(this, false);
    }

    public void draggedBy(double dx, double dy) {
System.out.println("draggedby " + dx + "," + dy);
	object.setX(object.getX() + dx);
	object.setY(object.getY() + dy);
	displayAndUpdatePosition();
    }

    private void displayAndUpdatePosition() {
	display();
	object.updatePosition(object.getX(), object.getY(), 1);
    }

    public void rotatedToAngle(double angle) {
	object.setRotation(angle);
	displayAndUpdatePosition();
    }

    public void changeWidthBy(int dx) {
	object.setWidth(Math.max(1, object.getWidth() + dx));
	displayAndUpdatePosition();
    }

    public void changeHeightBy(int dy) {
	object.setHeight(Math.max(1, object.getHeight() + dy));
	displayAndUpdatePosition();
    }

    public void changeRedBy(int delta) {
	object.setRed(Math.max(0, Math.min(255, object.getRed() + delta)));
	display();
    }

    public void changeGreenBy(int delta) {
	object.setGreen(Math.max(0, Math.min(255, object.getGreen() + delta)));
	display();
    }

    public void changeBlueBy(int delta) {
	object.setBlue(Math.max(0, Math.min(255, object.getBlue() + delta)));
	display();
    }

    public void changeAlphaBy(int delta) {
	object.setAlpha(Math.max(0, Math.min(1.0, object.getAlpha() + delta*0.01)));
	display();
    }

    public int getX() {
	if (object == null)
	    return 0;
	return (int) Math.round(object.getX());
    }

    public int getY() {
	if (object == null)
	    return 0;
	return (int) Math.round(object.getY());
    }
    
    public int getShapeCode() {
	return shapeCode;
    }

    public void setShapeCode(int shapeCode) {
	this.shapeCode = shapeCode;
    }

    public MoPiXObject getObject() {
	return object;
    }

    public void setObject(MoPiXObject object) {
	this.object = object;
    }

    public boolean isBeingChanged() {
	return beingChanged;
    }

    public void setBeingChanged(boolean beingChanged) {
	this.beingChanged = beingChanged;
    }

    public boolean isDraggable() {
	return draggable;
    }

    public void setDraggable(boolean draggable) {
	this.draggable = draggable;
    }

    public int getPreviousMouseX() {
	return previousMouseX;
    }

    public void setPreviousMouseX(int previousMouseX) {
	this.previousMouseX = previousMouseX;
    }

    public int getPreviousMouseY() {
	return previousMouseY;
    }

    public void setPreviousMouseY(int previousMouseY) {
	this.previousMouseY = previousMouseY;
    }

    public Stroke getStroke() {
        return stroke;
    }

    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }
    
    public Canvas getCanvas() {
	if (canvas == null) {
	    canvas = Canvas.createIfSupported();
	    int width = object.getWidthInt();
	    int height = object.getHeightInt();
	    canvas.setPixelSize(width, height);
	    canvas.setCoordinateSpaceWidth(width);
	    canvas.setCoordinateSpaceHeight(height);
	}
	return canvas;
    }
    
    protected Context2d getContext2d() {
	return getCanvas().getContext2d();
    }

}
