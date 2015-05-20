package uk.ac.lkl.client.mopix;


import uk.ac.lkl.client.MoPiX;

import com.google.gwt.user.client.Timer;

public class AnimationTimer extends Timer {
    public void run() {
	MoPiX.instance().updateAllObjects(MoPiX.time, null);
	if (MoPiX.timeIncrement == 0) {
	    cancel();
	    MoPiX.animationTimer = null;
	}
	if (MoPiX.timeIncrement == -1) {
	    MoPiX.instance().removeTrailAt(MoPiX.time);
	}
	MoPiX.instance().setTime(Math.max(-1, MoPiX.time + MoPiX.timeIncrement));
	if (MoPiX.time == -1) {
	    cancel();
	    MoPiX.animationTimer = null;
	}
    }
}
