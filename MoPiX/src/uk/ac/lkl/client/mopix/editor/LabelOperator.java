package uk.ac.lkl.client.mopix.editor;

import uk.ac.lkl.client.Modeller;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;

public class LabelOperator extends Label {
    static TPanel parentPanel = null;

    public LabelOperator() {

	super();

	parentPanel = ((TPanel) this.getParent());

	this.sinkEvents(Event.ONDBLCLICK);

    }

    public void onBrowserEvent(Event event) {
	super.onBrowserEvent(event);
	int type = DOM.eventGetType(event);

	switch (type) {
	case Event.ONDBLCLICK: {

	    // createPopUp ();
	    break;
	}

	}

    }

    protected void createPopUp() {
	final PopupPanel popupMenu = new PopupPanel(true);
	popupMenu.setAnimationEnabled(true);
	{

	    final MenuBar menuBar = new MenuBar(true);
	    menuBar.setAnimationEnabled(true);
	    popupMenu.setWidget(menuBar);
	    // RootPanel.get().add(menuBar,
	    // this.getAbsoluteLeft(),this.getAbsoluteTop());

	    menuBar.addItem(Modeller.constants.delete(), new Command() {
		public void execute() {
		    popupMenu.hide();
		    menuBar.setVisible(false);
		}
	    });

	    menuBar.addItem("  ", new Command() {
		public void execute() {
		    popupMenu.hide();
		    menuBar.setVisible(false);

		};
	    });
	    popupMenu.show();
	    popupMenu.setPopupPosition(this.getAbsoluteLeft(), this.getAbsoluteTop());

	}
    }
}