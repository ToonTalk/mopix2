package uk.ac.lkl.client.mopix;

import uk.ac.lkl.client.mopix.editor.InputPanel;
import uk.ac.lkl.client.BrowsePanel;
import uk.ac.lkl.client.MoPiX;
import uk.ac.lkl.client.Utils;
import uk.ac.lkl.client.Modeller;
import uk.ac.lkl.client.mopix.event.RemoveEquationFromObjectEvent;
import uk.ac.lkl.client.mopix.expression.Equation;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.NoSuchElementException;

public class EquationAppearance extends ExpressionAppearance {
    public EquationAppearance(Equation equation) {
	super(equation);
    }

    public EquationAppearance(Equation equation, String HTML,
	    ExpressionAppearance subExpression1,
	    ExpressionAppearance subExpression2, boolean useCustomHTML,
	    boolean algebraDefaultHTML) {
	super(equation, HTML, "eq", subExpression1, subExpression2,
		useCustomHTML, algebraDefaultHTML);
	sinkEvents(Event.ONCLICK | Event.MOUSEEVENTS);
    }

    public EquationAppearance(Equation equation, String HTML,
	    ExpressionAppearance subExpression1,
	    ExpressionAppearance subExpression2) {
	super(equation, HTML, "eq", subExpression1, subExpression2, 
		MoPiX.instance().useCustomHTMLDefault(), 
		MoPiX.instance().generateNaturalLanguageHTMLDefault());
	sinkEvents(Event.ONCLICK | Event.MOUSEEVENTS);
    }

    public EquationAppearance(Equation equation, String HTML) {
	this(equation, HTML, null, null, 
		MoPiX.instance().useCustomHTMLDefault(), 
		MoPiX.instance().generateNaturalLanguageHTMLDefault());
    }

    public EquationAppearance(Equation equation, Element element) {
	super(equation, element);
    }

    public EquationAppearance copy() {
	EquationAppearance copy = new EquationAppearance((Equation) expression,
		customHTML, subExpressionAppearance1, subExpressionAppearance2,
		useCustomHTML, generateNaturalLanguageHTML);
	return copy;
    }

    protected void createPopupMenu(final Event event) {
	final PopupPanel popupMenu = new PopupPanel(true);
	popupMenu.setAnimationEnabled(true);
	final EquationAppearance thisEquationAppearance = this;
	MenuBar menu = new MenuBar(true);
	menu.setAnimationEnabled(true);
	popupMenu.setWidget(menu);
	MenuItem firstMenuItem = new MenuItem(Modeller.constants.addToObject(), new Command() {
	    public void execute() {
		ScheduledCommand command = new ScheduledCommand() {

		    @Override
		    public void execute() {
			popupMenu.hide();
			MoPiX.equationToAddToObject = thisEquationAppearance.getEquation();
			Modeller.INSTANCE.switchToConstructionArea();
			Modeller.setStatusLineHTML(Modeller.constants.clickOnTheObjectYouWantToAddThisEquationTo()); 
			// :  + thisEquationAppearance.getEquation().HTMLAlgebra());
		    }
		    
		};
		Scheduler.get().scheduleDeferred(command);
	    };
	});
	menu.addItem(firstMenuItem);
	if (getParent() != MoPiX.expressionStagePanel) {
	    menu.addItem(Modeller.constants.sendToStage(), new Command() {
		public void execute() {
		    popupMenu.hide();
		    EquationAppearance copy = thisEquationAppearance.copy();
		    MoPiX.addToExpressionPalette(copy); // just keep adding new elements
		    Modeller.setStatusLine(Modeller.constants.equationAddedToStage());
		};
	    });
	}
	if (MoPiX.displayEquationEditor) {
	    menu.addItem(Modeller.constants.sendToEquationEditor(), new Command() {
		public void execute() {
		    popupMenu.hide();
		    EquationAppearance copy1 = thisEquationAppearance.copy();
		    Modeller.tabPanel.switchTo(MoPiX.editorPanel);
		    MoPiX.editorPanelContents.add(copy1);
		    InputPanel.openEquation(thisEquationAppearance.getEquation().getNode());
		};
	    });
	}
	// if (!generatedAppearanceFromExpression && getParent() !=
	// MoPiX2.browsePanelHTML) {
	// // this triggers a JavaScript exception when run in the browse panel
	// menu.addItem("Reveal underlying equation",
	// new Command() {
	// public void execute() {
	// popupMenu.hide();
	// alternativeHTML = thisEquationAppearance.getHTML();
	// // if (alternativeHTML == null) {
	// // alternativeHTML = thisEquationAppearance.getEquationHTML();
	// // };
	// generatedAppearanceFromExpression = true;
	// setHTML(expression.HTML());
	// };});
	// } else if (generatedAppearanceFromExpression && alternativeHTML !=
	// null) {
	// menu.addItem("Restore original appearance",
	// new Command() {
	// public void execute() {
	// popupMenu.hide();
	// generatedAppearanceFromExpression = false;
	// setHTML(alternativeHTML);
	// };});
	// };
	if (!(getParent().getParent() instanceof BrowsePanel)) {
	    menu.addItem(Modeller.constants.remove(), new Command() {
		public void execute() {
		    popupMenu.hide();
		    // MoPiX2.runPanel.remove(thisEquation);
		    Widget panel = getParent();
		    if (panel instanceof FlipSide) {
			// then remove equation from object as well
			MoPiXObject object = getEquation().getObject();
			if (object != null) {
			    object.removeEquation(getEquation());
			    new RemoveEquationFromObjectEvent(object, getEquation()).addToHistory();
			    MoPiX.instance().updateAll(true);
			};
		    }
		    thisEquationAppearance.removeFromParent();
		};
	    });
	}
	addViewsToMenu(menu, popupMenu);
	popupMenu.show();
	Utils.positionPopupMenu(event.getClientX(), event.getClientY(), popupMenu);
    }

    public void setHTML(String html) {
	Element element = getElement();
	String id = getElementID();
	if (id != null) {
	    Widget parent = getParent();
	    if (parent instanceof HTMLPanel) {
		EquationAppearance copy = copy();
//		removeFromParent();
		copy.setHTML(html);
		try {
		    Utils.replaceElementWithWidget(id, element, copy, ((HTMLPanel) parent));
		} catch (NoSuchElementException e) {
		    e.printStackTrace();
		    Modeller.addToErrorLog(e.toString());
		    return;
		}
	    }
	} else {
	    super.setHTML(html);
	    DOM.setEventListener((com.google.gwt.user.client.Element) element, this);
	}
    }

    public String getHTML() {
	String html = super.getHTML();
	if (isActive() || !MoPiX.indicateEquationsNotCurrentlyUsed) {
	    return html;
	} else {
	    return "<strike>" + html + "</strike>" + Modeller.NON_BREAKING_SPACE + Modeller.constants.notUsed();
	}
    }

    protected Equation getEquation() {
	return (Equation) expression; // should always be an equation
    }

    public void decrementLiveCount() {
	super.decrementLiveCount();
	if (liveCount == 0) { // && getParent() != null) {
	    updateHTML();
	}
    }

    public void incrementLiveCount() {
	super.incrementLiveCount();
	if (liveCount == 1) { // && getParent() != null) {
	    updateHTML();
	}
    }
}