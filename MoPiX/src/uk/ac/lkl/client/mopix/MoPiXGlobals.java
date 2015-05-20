package uk.ac.lkl.client.mopix;

import java.util.HashMap;

import uk.ac.lkl.client.MoPiX;
import uk.ac.lkl.client.mopix.expression.Equation;

public class MoPiXGlobals {
    // static int time = 0;
    // static int oldTime = 0;
    // Constructs an empty HashMap with the default initial capacity (16) and
    // the default load factor (0.75).
    static HashMap<String, Double> constantsValueTable = new HashMap<String, Double>();
    static HashMap<String, Equation> constantsEquationTable = new HashMap<String, Equation>();
    // the following
    // following might be better as a hash map
    // if not some other kind of collection
    static public MoPiXObject allObjects[] = null;

    static public MoPiXObject objectNamed(String name) {
	if (allObjects == null) {
	    return null;
	}
	for (int i = 0; i < allObjects.length; i++) {
	    if (name.equals(allObjects[i].getName())) {
		return allObjects[i];
	    }
	}
	return null;
    }

    static public void addConstant(String constant, double value, Equation equation) {
	constantsValueTable.put(constant, new Double(value));
	constantsEquationTable.put(constant, equation);
    }

    static public double valueOfConstant(String constant) {
	Double value = constantsValueTable.get(constant);
	if (value == null) {
	    return Double.MIN_VALUE;
	} else {
	    return value.doubleValue(); 
	}
    }
    
    static public String xmlOfContants() {
	StringBuilder buffer = new StringBuilder("");
	for (Equation equation : constantsEquationTable.values()) {
	    buffer.append(equation.getXML() + "\n");
	}
	return buffer.toString();	
    }

    static public void addToAllObjects(MoPiXObject object) {
	int newLength = 1;
	if (allObjects != null) {
	    newLength = allObjects.length + 1;
	}
	MoPiXObject newAllObjects[] = new MoPiXObject[newLength];
	for (int i = 0; i < newLength - 1; i++) {
	    newAllObjects[i] = allObjects[i];
	}
	newAllObjects[newLength - 1] = object;
	allObjects = newAllObjects;
	MoPiX.runArea.add(object, object.getXInt(), object.getYInt());
    }

    static public void removeFromAllObjects(MoPiXObject object) {
	if (allObjects == null)
	    return; // warn?
	int newLength = allObjects.length - 1;
	if (newLength == 0) {
	    allObjects = null;
	    return;
	}
	MoPiXObject newAllObjects[] = new MoPiXObject[newLength];
	boolean shift = false;
	for (int i = 0; i < allObjects.length; i++) {
	    if (shift) {
		newAllObjects[i - 1] = allObjects[i];
	    } else if (object == allObjects[i]) {
		shift = true;
	    } else {
		newAllObjects[i] = allObjects[i];
	    }
	}
	if (!shift) {
	    System.out.println("Warning: Didn't remove object in removeFromAllObjects");
	} else {
	    allObjects = newAllObjects;
	}
    }

}
