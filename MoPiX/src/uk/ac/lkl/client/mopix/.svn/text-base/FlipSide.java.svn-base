package uk.ac.lkl.client.mopix;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Button;

import uk.ac.lkl.client.Modeller;
import uk.ac.lkl.client.ModellerButton;
import uk.ac.lkl.client.Utils;

public class FlipSide extends VerticalPanel {
    protected Button closeButton = null;

    public FlipSide(final MoPiXObject object) {
	super();
	final FlipSide flipSide = this;
	closeButton = new ModellerButton(Modeller.constants.close());
	updateContents(object);
	closeButton.addClickHandler(new ClickHandler() {
	    public void onClick(ClickEvent event) {
		flipSide.removeFromParent();
		// Why did I once think it was a good idea to
		// do the following when closing the inspector panel?
		// Equation.clearHistoryAllEquations(); // member of the right
		// class?
		// MoPiX2.updateAll(false);
		object.setInspectorPanel(null);
	    }
	});
	setStylePrimaryName("modeller-FlipSide");
    }

    public void updateContents(MoPiXObject object) {
	this.clear();
	String objectName = object.getNameHTML();
	add(new HTML(Utils.textFontToMatchIcons("<b>" + Modeller.constants.equationsOf() + objectName + "</b>")));
	object.addHTMLOfAllEquations(this);
	add(closeButton);
    }
}
