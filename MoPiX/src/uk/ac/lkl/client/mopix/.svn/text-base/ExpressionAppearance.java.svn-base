package uk.ac.lkl.client.mopix;

import uk.ac.lkl.client.MoPiX;
import uk.ac.lkl.client.Modeller;
import uk.ac.lkl.client.RichTextEntry;
import uk.ac.lkl.client.Utils;
import uk.ac.lkl.client.mopix.event.CreateExpressionEvent;
import uk.ac.lkl.client.mopix.graphics.BoundingBox;
import uk.ac.lkl.client.mopix.event.AddEquationToObjectEvent;
import uk.ac.lkl.client.mopix.event.ChangeExpressionAppearanceEvent;
import uk.ac.lkl.client.mopix.expression.Equation;
import uk.ac.lkl.client.mopix.expression.Expression;
import uk.ac.lkl.shared.CommonUtils;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

public class ExpressionAppearance extends HTML {
    protected Expression expression;
    protected String customHTML = null;
    protected ExpressionAppearance subExpressionAppearance1 = null;
    protected ExpressionAppearance subExpressionAppearance2 = null;
    protected String infixOperator = null;
    protected boolean useCustomHTML = true;
    protected boolean generateNaturalLanguageHTML = false;
    protected String elementID = null;
    protected ExpressionAppearance parents[] = null;
    // expressions that contain this one
    protected int liveCount = 0; 
    // number of objects that are currently
    // using this equation -- currently only used by equations
    static ExpressionAppearance expressionWaitingforRHS = null;
    static ExpressionAppearance expressionWaitingforOtherOperand = null;
    static String waitingForOperation = null;

    public ExpressionAppearance(Expression expression) {
	super("");
	this.expression = expression;
	setHTML(getHTML());
	setToolTip(expression);
	setStylePrimaryName("modeller-Equation");
	sinkEvents(Event.ONCLICK | Event.MOUSEEVENTS);
    }

    public ExpressionAppearance(Expression expression, String HTML,
	    String infixOperator, ExpressionAppearance subExpression1,
	    ExpressionAppearance subExpression2, boolean useCustomHTML,
	    boolean generateNaturalLanguageHTML) {
	super("");
	this.expression = expression;
	this.useCustomHTML = useCustomHTML;
	this.generateNaturalLanguageHTML = generateNaturalLanguageHTML;
	this.customHTML = HTML;
	// only set subExpression2 and infixOperator if both sub-expressions are
	// non-null
	if (subExpression1 != null) {
	    this.subExpressionAppearance1 = subExpression1;
	    subExpression1.addParent(this);
	    if (subExpression2 != null) {
		this.subExpressionAppearance2 = subExpression2;
		this.infixOperator = infixOperator;
		subExpression2.addParent(this);
	    }
	}
	setHTML(getHTML());
	setToolTip(expression);
	setStylePrimaryName("modeller-Equation");
    }

    public ExpressionAppearance(Expression expression, String HTML,
	    String infixOperator, ExpressionAppearance subExpression1,
	    ExpressionAppearance subExpression2) {
	this(expression, HTML, infixOperator, subExpression1, subExpression2,
		MoPiX.instance().useCustomHTMLDefault(), 
		MoPiX.instance().generateNaturalLanguageHTMLDefault());
    }

    public ExpressionAppearance(Expression expression, Element element) { 
	super("");
	setElement(element);
	sinkEvents(Event.ONCLICK | Event.MOUSEEVENTS);
	DOM.setEventListener(element, this);
	this.expression = expression;
	setToolTip(expression);
	setStylePrimaryName("modeller-Equation");
    }

    public String getHTML() {
	return getHTML(useCustomHTML, generateNaturalLanguageHTML);
    }

    protected void setToolTip(Expression expression) {
	HTML toolTip = new HTML(expression.HTMLNaturalLanguage());
	setTitle(DOM.getInnerText(toolTip.getElement()));
    }

    public String getHTML(boolean useCustomHTML, boolean generateNaturalLanguageHTML) {
	if (useCustomHTML && customHTML != null) {
	    return customHTML;
	} else if (infixOperator == null || !useCustomHTML) { 
	    // leaf node or no custom HTML
	    String html;
	    if (generateNaturalLanguageHTML) {
		html = expression.HTMLNaturalLanguage(); 
		// arg should really be language ID
	    } else {
		html = expression.HTMLAlgebra();
	    }
	    html = Utils.textFontToMatchIcons(html);
	    return html;
	} else {
	    String niceName;
	    if (generateNaturalLanguageHTML) {
		niceName = Utils.niceNameForBinaryOperator(infixOperator);
	    } else {
		niceName = Utils.algebraicNameForMathMLOperator(infixOperator);
	    }
	    if (niceName == null) {
		niceName = " " + infixOperator + " ";
	    }
	    return Utils.combineOperatorAndExpressionsHTML(niceName,
		                                           subExpressionAppearance1.getHTML(), 
		                                           subExpressionAppearance2.getHTML());
	}
    }

    public void customiseAppearance(final String attributeName,
	    Expression attribute, MoPiXObject object) {
	if (customHTML != null) {
	    return; // already have some
	}
	if (MoPiX.configureForPondTiling) {
	    // this should be driven off a config file
	    String reverseColouredBorder = null;
	    if (attributeName != null) {
		if (attributeName.equals("width")) {
		    reverseColouredBorder = "border-top";
		} else if (attributeName.equals("height")) {
		    reverseColouredBorder = "border-left";
		}
	    }
	    if (reverseColouredBorder != null) {
		int red = object.getRed();
		int green = object.getGreen();
		int blue = object.getBlue();
		String colour = CommonUtils.toHexString(red)
			+ CommonUtils.toHexString(green) + CommonUtils.toHexString(blue);
		String reverseColour = CommonUtils.toHexString(255 - red)
			+ CommonUtils.toHexString(255 - green)
			+ CommonUtils.toHexString(255 - blue);
		String html = "<div style='float:left;padding:2px'><div style='padding:2px;width:44px;height:14px;border-style:dotted;border-width:thin'><div style='border-style:solid;border-width:thick;width:40px;height:10px;border-color:"
			+ colour
			+ ";"
			+ reverseColouredBorder
			+ "-color:"
			+ reverseColour + ";'></div></div></div>";
		if (attribute instanceof Equation) {
		    html += Utils.textFontToMatchIcons("&nbsp;=&nbsp;")
			    + ((Equation) attribute).HTMLOfRightHandSide();
		}
		setCustomHTML(html);
		// "<img src='Images/" + attributeName + red + green + blue +
		// ".png' alt='width of " + objectName + "'>"
	    } else {
		// attributeAppearance.setGenerateNaturalLanguageHTML(true);
		// attributeAppearance.updateHTML();
		String html = Utils.textFontToMatchIcons(attribute.HTMLNaturalLanguage());
		setCustomHTML(html);
	    }
	}
    }

    public void onBrowserEvent(Event event) {
	super.onBrowserEvent(event);
	DOM.eventPreventDefault(event); // still needed? good idea??
	DOM.eventCancelBubble(event, true); // still needed? good idea??
	int eventType = DOM.eventGetType(event);
	// Panel currentPanel = MoPiX2.tabPanel.getSelectedPanel();
	if (eventType == Event.ONMOUSEDOWN) { // && currentPanel !=
	    // MoPiX2.constructPanel) {
	    DOM.setStyleAttribute(RootPanel.get().getElement(), "cursor", "default");
	    // System.out.println(getEquation().getXML()); // for debugging
	    if (expressionWaitingforOtherOperand != null) {
		// following should be rewritten to implement generalising
		// equations with object_1, etc.
		// String tempTemplateName1 = "__SHOULD_NOT_OCCUR_ELSEWHERE_1";
		// String tempTemplateName2 = "__SHOULD_NOT_OCCUR_ELSEWHERE_2";
		// String mathML1 =
		// expressionWaitingforOtherOperand.getXMLTemplate(tempTemplateName1);
		// String mathML2 = getXMLTemplate(tempTemplateName2);
		// int OTHERIndex1 = Utils.firstUnusedTemplateName(mathML1 +
		// mathML2,1);
		// if (mathML1.indexOf(tempTemplateName1) >= 0) {
		// mathML1 = mathML1.replaceAll(tempTemplateName1, "OTHER" +
		// OTHERIndex1);
		// } else {
		// OTHERIndex1--; // not used
		// };
		// int OTHERIndex2 =
		// Utils.firstUnusedTemplateName(mathML2,OTHERIndex1+1);
		// mathML2 = mathML2.replaceAll(tempTemplateName2, "OTHER" +
		// OTHERIndex2);
		String mathML1 = expressionWaitingforOtherOperand.getXML();
		String mathML2 = getXML();
		String mathML = "<apply><" + waitingForOperation + "/>"
			+ mathML1 + mathML2 + "</apply>";
		Expression equation = 
		    new Expression(mathML, expressionWaitingforOtherOperand.getObject());
		ExpressionAppearance appearance = 
		    new ExpressionAppearance(equation, null, waitingForOperation, expressionWaitingforOtherOperand, this);
		MoPiX.addToExpressionPalette(appearance);
		// MoPiX2.addToHistory(Utils.textFontToMatchIcons("Created&nbsp;")
		// + appearance.HTML());
		new CreateExpressionEvent(equation).addToHistory();
		expressionWaitingforOtherOperand = null;
	    } else if (expressionWaitingforRHS != null) {
		String mathML = "<apply><eq/>"
			+ expressionWaitingforRHS.getXML() + getXML()
			+ "</apply>";
		MoPiXObject object = expressionWaitingforRHS.getObject();
		Equation equation = new Equation(mathML, object);
		EquationAppearance appearance = 
		    new EquationAppearance(equation, null, expressionWaitingforRHS, this);
		equation.setAppearance(appearance);
		MoPiX.addToExpressionPalette(appearance);
		if (object != null) {
		    new AddEquationToObjectEvent(object, equation);
		    // MoPiX2.addToHistory(Utils
		    // .textFontToMatchIcons("Added&nbsp;")
		    // + appearance.HTML()
		    // + Utils.textFontToMatchIcons("&nbsp;to&nbsp;")
		    // + Utils.textFontToMatchIcons(object.getNameHTML()));
		    // MoPiX2.setStatusLineHTML("Equation added to "
		    // + object.getNameHTML());
		} else {
		    new CreateExpressionEvent(equation).addToHistory();
		    // MoPiX2.addToHistory(Utils
		    // .textFontToMatchIcons("Created&nbsp;")
		    // + appearance.HTML());
		}
		expressionWaitingforRHS = null;
		Equation.clearHistoryAllEquations();
		MoPiX.instance().updateAll(false);
	    } else {
		createPopupMenu(event);
	    }
	} else if (eventType == Event.ONMOUSEOVER) {
	    DOM.setStyleAttribute(RootPanel.get().getElement(), "cursor", "hand");
	} else if (eventType == Event.ONMOUSEOUT) {
	    DOM.setStyleAttribute(RootPanel.get().getElement(), "cursor", "default");
	    // } else if (eventType == Event.ONMOUSEMOVE) {
	    // if (isBeingDragged()) {
	    // if (!isDragStarted()) {
	    // MoPiX2.setStatusLine("Move the mouse to drag the equation and
	    // click to release.");
	    // setDragStarted(true);
	    // }
	    // mouseMoved(event);
	    // };
	    // } else if (eventType == Event.ONMOUSEUP && isDragStarted()) {
	    // dragEnded();
	}
    }

    protected void createPopupMenu(final Event event) {
	final PopupPanel popupMenu = new PopupPanel(true);
	popupMenu.setAnimationEnabled(true);
	final ExpressionAppearance thisExpression = this;
	MenuBar menu = new MenuBar(true);
	menu.setAnimationEnabled(true);
	popupMenu.setWidget(menu);
	MenuItem firstMenuItem = addStandardExpressionMenuItems(popupMenu, thisExpression, menu);
	MenuItem lastMenuItem = new MenuItem(Modeller.constants.remove(), new Command() {
	    public void execute() {
		popupMenu.hide();
		removeFromParent();
	    };
	});
	menu.addItem(lastMenuItem);
	popupMenu.show();
	if (firstMenuItem == null) { // just one menu item
	    firstMenuItem = lastMenuItem;
	}
	Utils.positionPopupMenu(event.getClientX(), event.getClientY(), popupMenu);
    }

    protected MenuItem addStandardExpressionMenuItems(final PopupPanel popupMenu,
	                                              final ExpressionAppearance thisExpression, 
	                                              MenuBar menu) {
	MenuItem firstMenuItem = new MenuItem(" = " + Modeller.constants.another() + "...", new Command() {
	    public void execute() {
		ScheduledCommand command = new ScheduledCommand() {

		    @Override
		    public void execute() {
			popupMenu.hide();
			expressionWaitingforRHS = thisExpression;
			Modeller.setStatusLine(Modeller.constants.clickOnTheExpressionThisShouldBeEqualTo());
		    }
		    
		};
		Scheduler.get().scheduleDeferred(command);
	    };
	});
	menu.addItem(firstMenuItem);
	// menu.addItem(new MenuItem("= a number...",
	// new Command() {
	// public void execute() {
	// DeferredCommand.addCommand(new Command() {
	// public void execute() {
	// popupMenu.hide();
	// NumberEntry numberEntry = new NumberEntry("eq", thisExpression);
	// MoPiX2.addToExpressionPalette(numberEntry);
	// numberEntry.setFocus(true);
	// MoPiX2.setStatusLine("Enter the number this should be equal to.");
	// };
	// });
	// };
	// }));
	addArithmeticMenuOptions(popupMenu, thisExpression, menu);
	addViewsToMenu(menu, popupMenu);
	menu.addItem(Modeller.constants.editAppearance(), new Command() {
	    public void execute() {
		popupMenu.hide();
		final RichTextEntry richText = new RichTextEntry(thisExpression);
		richText.addSaveButtonClickHandler(new ClickHandler() {
		    @Override
		    public void onClick(ClickEvent event) {
			Window.alert("Edits are not yet saved and will be lost when you close the browser.");
			thisExpression.acceptHTML(richText.getRichTextArea());
			richText.getParent().removeFromParent();
			Modeller.INSTANCE.switchToConstructionArea();

		    }
		});
		richText.addCancelButtonClickHandler(new ClickHandler() {
		    @Override
		    public void onClick(ClickEvent event) {
			richText.getParent().removeFromParent();
			Modeller.INSTANCE.switchToConstructionArea();
		    }
		    
		});
		SimplePanel panel = new SimplePanel();
		panel.setWidget(richText);
		Modeller.tabPanel.add(panel, Modeller.constants.appearanceEditor());
		Modeller.tabPanel.switchTo(panel);
		// MoPiX2.addToExpressionPalette(richText);
		Modeller.setStatusLine(Modeller.constants.editTheAppearanceThenClickOnOk());
	    }
	});
	return firstMenuItem;
    }
    
    protected void acceptHTML(RichTextArea richText) {
	String richTextString = richText.getHTML();
	String oldHTML = getHTML();
	setCustomHTML(richTextString);
	new ChangeExpressionAppearanceEvent(this, oldHTML).addToHistory();
    }

    protected MenuItem addArithmeticMenuOptions(final PopupPanel popupMenu,
	    final ExpressionAppearance thisExpression, MenuBar menu) {
	String operations[] = { "plus", "minus", "times", "divide" }; 
//	String operations[] = { MoPiX2.constants.plus(), MoPiX2.constants.minus(), MoPiX2.constants.times(), MoPiX2.constants.divide() }; 
	// add mod, etc?
	String operationSymbols[] = { "+", "-", "*", "/" };
	String operationPastTenses[] = { Modeller.constants.addedTo(), Modeller.constants.subtractedFrom(), Modeller.constants.multipliedBy(), Modeller.constants.dividedBy() };
	MenuItem firstMenuItem = null;
	for (int i = 0; i < operations.length; i++) {
	    final String operation = operations[i];
	    final String operationSymbol = operationSymbols[i];
	    final String operationPastTense = operationPastTenses[i];
	    MenuItem menuItem = new MenuItem(operationSymbol + " " + Modeller.constants.another() + "...",
		    new Command() {
			public void execute() {
			    ScheduledCommand command = new ScheduledCommand() {

				@Override
				public void execute() {
				    popupMenu.hide();
				    expressionWaitingforOtherOperand = thisExpression;
				    waitingForOperation = operation;
				    Modeller.setStatusLine(
					    Modeller.constants.clickOnTheExpressionThisShouldBe() + " " + operationPastTense + ".");				    
				}

			    };
			    Scheduler.get().scheduleDeferred(command);
			};
		    });
	    menu.addItem(menuItem);
	    if (firstMenuItem == null) {
		firstMenuItem = menuItem;
	    }
	    // menu.addItem(new MenuItem(operationSymbol + " a number...",
	    // new Command() {
	    // public void execute() {
	    // DeferredCommand.addCommand(new Command() {
	    // public void execute() {
	    // popupMenu.hide();
	    // NumberEntry numberEntry = new NumberEntry(operation,
	    // thisExpression);
	    // MoPiX2.addToExpressionPalette(numberEntry);
	    // numberEntry.setFocus(true);
	    // // generalise "added"
	    // MoPiX2.setStatusLine("Enter the number this should be " +
	    // operationPastTense + ".");
	    // };
	    // });
	    // };
	    // }));
	}
	return firstMenuItem;
    }

    protected void addViewsToMenu(final MenuBar menu, final PopupPanel popupMenu) {
	final ExpressionAppearance thisAppearance = this;
	MenuBar viewMenu = new MenuBar(true);
	viewMenu.addItem(Modeller.constants.showIconsAndAlgebra(), new Command() {
	    public void execute() {
		popupMenu.hide();
		thisAppearance.setUseCustomHTML(true);
		thisAppearance.setGenerateNaturalLanguageHTML(false);
		updateHTML();
	    };
	});
	viewMenu.addItem(Modeller.constants.showOnlyAlgebra(), new Command() {
	    public void execute() {
		popupMenu.hide();
		thisAppearance.setUseCustomHTML(false);
		thisAppearance.setGenerateNaturalLanguageHTML(false);
		updateHTML();
	    };
	});
	viewMenu.addItem(Modeller.constants.showIconsAndEnglish(), new Command() {
	    public void execute() {
		popupMenu.hide();
		thisAppearance.setUseCustomHTML(true);
		thisAppearance.setGenerateNaturalLanguageHTML(true);
		updateHTML();
	    }
	});
	viewMenu.addItem(Modeller.constants.showOnlyEnglish(), new Command() {
	    public void execute() {
		popupMenu.hide();
		thisAppearance.setUseCustomHTML(false);
		thisAppearance.setGenerateNaturalLanguageHTML(true);
		updateHTML();
	    };
	});
	menu.addItem(Modeller.constants.view(), viewMenu);
    }

    protected AbsolutePanel getAbsoluteParentPanel() {
	if (getParent() instanceof AbsolutePanel) {
	    return (AbsolutePanel) getParent();
	} else {
	    // System.out.println("Parent expected to be an AbsolutePanel.");
	    return null;
	}
    }

    public BoundingBox getBoundingBox() {
	AbsolutePanel panel = getAbsoluteParentPanel();
	if (panel == null)
	    return null;
	int left = panel.getWidgetLeft(this);
	int right = left + getOffsetWidth();
	int top = panel.getWidgetTop(this);
	int bottom = top + getOffsetHeight();
	return new BoundingBox(left, right, top, bottom);
    }

    protected void addParent(ExpressionAppearance parent) {
	int newLength = 1;
	if (parents != null) {
	    newLength = parents.length + 1;
	}
	ExpressionAppearance newParents[] = new ExpressionAppearance[newLength];
	for (int i = 0; i < newLength - 1; i++) {
	    newParents[i] = parents[i];
	}
	newParents[newLength - 1] = parent;
	parents = newParents;
    }

    protected void updateAllParents() {
	if (parents == null)
	    return;
	for (int i = 0; i < parents.length; i++) {
	    parents[i].updateHTML(); // since subExpression has changed
	}
    }

    public void updateHTML() {
	String html = getHTML();
	setHTML(html);
	updateAllParents();
    }

    public String getXMLTemplate(String templateVariable) { // e.g. ME or OTHER1
	return expression.getXMLTemplate(templateVariable);
    }

    public String getXML() {
	return expression.getXML();
    }

    public Expression getExpression() {
	return expression;
    }

    public void setEquation(Expression expression) {
	this.expression = expression;
    }

    public String getEquationHTML() {
	return expression.HTMLAlgebra();
    }

    public String getCustomHTML() {
	return customHTML;
    }

    public void setCustomHTML(String alternativeHTML) {
	this.customHTML = alternativeHTML;
	if (useCustomHTML) {
	    updateHTML();
	}
    }

    public MoPiXObject getObject() {
	return expression.getObject();
    }

    // public void setPosition(int left, int top) {
    // this.left = left;
    // this.top = top;
    // };
    // public int getLeft() {
    // if (left == Integer.MAX_VALUE) {
    // left = getAbsoluteLeft();
    // };
    // return left;
    // }
    // public void setLeft(int left) {
    // this.left = left;
    // }
    // public int getTop() {
    // if (top == Integer.MAX_VALUE) {
    // top = getAbsoluteTop();
    // };
    // return top;
    // }
    // public void setTop(int top) {
    // this.top = top;
    // }
    // public boolean isBeingDragged() {
    // return beingDragged;
    // }
    // public void setBeingDragged(boolean beingDragged) {
    // this.beingDragged = beingDragged;
    // }
    // public boolean isDragStarted() {
    // return dragStarted;
    // }
    // public void setDragStarted(boolean dragStarted) {
    // this.dragStarted = dragStarted;
    // }
    // public void mouseMoved(Event event) {
    // AbsolutePanel panel = getAbsoluteParentPanel();
    // // if (MoPiX2.DEBUG) {
    // // System.out.println("MouseX: " + DOM.eventGetClientX(event) + ";
    // MouseY: " + DOM.eventGetClientY(event));
    // // };
    // panel.setWidgetPosition(this,
    // Math.min(MoPiX2.panelWidth,DOM.eventGetClientX(event)), // -
    // getStartDragMouseX(),
    // Math.min(MoPiX2.panelHeight-20,DOM.eventGetClientY(event))); //
    // MoPiX2.panelHeight - (getStartDragMouseY() -
    // DOM.eventGetClientY(event)));
    // };
    // public void dragEnded() {
    // setBeingDragged(false);
    // setDragStarted(false);
    // DOM.releaseCapture(getElement());
    // MoPiX2.setStatusLine(""); // remove any old help
    // BoundingBox equationBoundingBox = getBoundingBox();
    // // System.out.println("Equation bounding box: " + equationBoundingBox);
    // MoPiXObject objectUnderneath = null;
    // for (int i = 0; i < MoPiXGlobals.allObjects.length; i++) {
    // BoundingBox box = MoPiXGlobals.allObjects[i].getBoundingBox();
    // if (box.intersectsWith(equationBoundingBox)) {
    // if (objectUnderneath == null) {
    // objectUnderneath = MoPiXGlobals.allObjects[i];
    // } else {
    // MoPiX2.setStatusLine("There are more than one objects underneath the
    // equation. You may need to move things around to.");
    // objectUnderneath = null;
    // break;
    // };
    // };
    // // System.out.println(i + " bounding box: " + box + "; intersects: " +
    // box.intersectsWith(equationBoundingBox));
    // };
    // if (objectUnderneath != null) {
    // getEquation().setObject(objectUnderneath);
    // removeFromParent();
    // MoPiX2.updateAllObjects(MoPiX2.time);
    // MoPiX2.setStatusLine("<p>Added " + getEquation().HTML() + " to " +
    // equation.getObject().getNameHTML() + ".</p>");
    // };
    // };
    public boolean isUseCustomHTML() {
	return useCustomHTML;
    }

    public void setUseCustomHTML(boolean useCustomHTML) {
	this.useCustomHTML = useCustomHTML;
    }

    public boolean isGenerateNaturalLanguageHTML() {
	return generateNaturalLanguageHTML;
    }

    public void setGenerateNaturalLanguageHTML(boolean generateNaturalLanguageHTML) {
	this.generateNaturalLanguageHTML = generateNaturalLanguageHTML;
    }

    public String getElementID() {
	return elementID;
    }

    public void setElementID(String elementID) {
	this.elementID = elementID;
    }

    public boolean isActive() {
	return liveCount > 0;
    }

    public void decrementLiveCount() {
	liveCount--;
    }

    public void incrementLiveCount() {
	liveCount++;
    }
}
