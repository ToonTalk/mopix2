package uk.ac.lkl.client.mopix;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;

import uk.ac.lkl.client.MoPiX;
import uk.ac.lkl.client.Modeller;
import uk.ac.lkl.client.ModellerButton;
import uk.ac.lkl.client.mopix.event.CreateExpressionEvent;
import uk.ac.lkl.client.mopix.expression.Equation;
import uk.ac.lkl.client.mopix.expression.Expression;

class NumberEntry extends Composite {
    private TextBox numberBox = new TextBox();
    protected Button okButton = new ModellerButton(Modeller.constants.ok().toUpperCase());
    protected Button cancelButton = new ModellerButton(Modeller.constants.cancel().toUpperCase());

    public NumberEntry(final String operation, final ExpressionAppearance expression) {
	VerticalPanel panel = new VerticalPanel();
	numberBox.setText("0");
	panel.add(numberBox);
	HorizontalPanel buttonPanel = new HorizontalPanel();
	okButton.setWidth("40px");
	cancelButton.setWidth("80px");
	buttonPanel.add(okButton);
	buttonPanel.add(cancelButton);
	buttonPanel.setSpacing(10);
	panel.add(buttonPanel);
	okButton.addClickHandler(new ClickHandler() {
	    public void onClick(ClickEvent event) {
		acceptNumber(operation, expression, numberBox);
		removeFromParent();
	    }
	});
	numberBox.addKeyPressHandler(new KeyPressHandler() {

	    @Override
	    public void onKeyPress(KeyPressEvent event) {
		if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
		    acceptNumber(operation, expression, numberBox);
		    removeFromParent();
		}
		
	    };
	});
	cancelButton.addClickHandler(new ClickHandler() {
	    public void onClick(ClickEvent event) {
		removeFromParent();
	    }
	});
	// All composites must call initWidget() in their constructors.
	initWidget(panel);
    }

    public void setFocus(boolean flag) {
	numberBox.setFocus(flag);
    }

    protected void acceptNumber(final String operation,
	    final ExpressionAppearance expressionAppearance, TextBox numberBox) {
	String numberXML = "<cn>" + numberBox.getText() + "</cn>";
	Expression numberExpression = new Expression(numberXML, null);
	MoPiXObject object = expressionAppearance.getObject();
	if (operation.equals("eq")) {
	    String MathML = "<apply><eq/>" + expressionAppearance.getXML()
		    + numberXML + "</apply>";
	    Equation equation = new Equation(MathML, object);
	    EquationAppearance appearance = new EquationAppearance(equation,
		    null, expressionAppearance, new ExpressionAppearance(
			    numberExpression));
	    MoPiX.addToExpressionPalette(appearance);
	    new CreateExpressionEvent(equation).addToHistory();
//	    MoPiX2.setStatusLine("Equation is ready");
//	    MoPiX2.addToHistory(Utils.textFontToMatchIcons("Added&nbsp;")
//		    + appearance.HTML()
//		    + Utils.textFontToMatchIcons("&nbsp;to&nbsp;")
//		    + Utils.textFontToMatchIcons(object.getNameHTML()));
	    MoPiX.instance().updateAllObjects(MoPiX.time);
	} else if (operation.equals("plus") || operation.equals("times")
		  || operation.equals("minus") || operation.equals("divide")) {
	    String MathML = "<apply><" + operation + "/>"
		    + expressionAppearance.getXML() + numberXML + "</apply>";
	    Expression equation = new Expression(MathML, object);
	    ExpressionAppearance appearance = new ExpressionAppearance(
		    equation, null, operation, expressionAppearance,
		    new ExpressionAppearance(numberExpression));
	    MoPiX.addToExpressionPalette(appearance);
	    Modeller.setStatusLine(Modeller.constants.expressionIsReady());
	}
    }
};
