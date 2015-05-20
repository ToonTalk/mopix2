package uk.ac.lkl.client.mopix;

import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.xml.client.NodeList;

import uk.ac.lkl.client.MoPiX;
import uk.ac.lkl.client.Modeller;
import uk.ac.lkl.client.Utils;
import uk.ac.lkl.client.event.CompoundEvent;
import uk.ac.lkl.client.event.ModellerEvent;
import uk.ac.lkl.client.mopix.expression.Equation;
import java.util.ArrayList;

public class IncrementalProcessXMLCommand implements RepeatingCommand {
    protected NodeList argNodes;
    protected int index;
    protected ArrayList<ModellerEvent> events;
    protected String description;

    public IncrementalProcessXMLCommand(NodeList argNodes, ArrayList<ModellerEvent> events, String description) {
	this.argNodes = argNodes;
	this.events = events;
	this.description = description;
	index = argNodes.getLength() - 1;
    }

    public boolean execute() {
	Modeller.setStatusLine(Modeller.constants.processingEquationsPleaseWaitEquationsRemaining() + ": " + index);
	Equation.processXML(argNodes.item(index), false, events, description);
	index--;
	if (index == 0) {
	    Modeller.setStatusLine(Modeller.constants.allEquationsLoaded());
	    int count = events.size();
	    if (count > 0) {
		// make a reversed copy so it is in chronological order
		ArrayList<ModellerEvent> reversedEvents = new ArrayList<ModellerEvent>(count);
		for (int i = count-1; i >= 0; i--) {
		    reversedEvents.add(events.get(i));
		}
		CompoundEvent compoundEvent = new CompoundEvent(reversedEvents);
		compoundEvent.setAlternativeHTML(Utils.textFontToMatchIcons(Modeller.constants.loaded() + Modeller.NON_BREAKING_SPACE + description));
		compoundEvent.addToHistory();
	    }
	    MoPiX.instance().topLevelLoop();
	    return false;
	}
	return true;
    }
}
