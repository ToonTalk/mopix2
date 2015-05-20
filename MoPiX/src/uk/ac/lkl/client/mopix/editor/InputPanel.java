package uk.ac.lkl.client.mopix.editor;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.XMLParser;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Timer;
import uk.ac.lkl.client.mopix.editor.InputBox;
import uk.ac.lkl.client.mopix.editor.InputPanel;
import uk.ac.lkl.client.mopix.editor.LabelOperator;
import uk.ac.lkl.client.Modeller;
import uk.ac.lkl.client.mopix.expression.Equation;
import uk.ac.lkl.client.mopix.editor.TPanel;
import uk.ac.lkl.client.mopix.EquationAppearance;
import uk.ac.lkl.client.mopix.ExpressionAppearance;
import uk.ac.lkl.client.MoPiX;
import uk.ac.lkl.client.ModellerButton;
import uk.ac.lkl.client.Utils;

@SuppressWarnings("deprecation")
public class InputPanel extends Composite implements FocusListener,
	ClickListener, ChangeListener {

    static Node leftSideOfEquation = null;
    static NodeList childrenNodes = null;
    static boolean rightField = false;
    static boolean begin = true;
    static String text = "";
    static ArrayList<Node> stack = new ArrayList<Node>();// stack of right hand side nodes
    static ArrayList<TPanel> stackOfPanels = new ArrayList<TPanel>();// corresponding panels
    static ArrayList<Node> stackOfParents = new ArrayList<Node>();// corresponding parent nodes
    static ArrayList<TPanel> stackOfPanelsTemp = new ArrayList<TPanel>();

    static String[] unaryOperators = { "sin", "cos", "tan", "abs", "not" };

    private boolean remove = false;
    private boolean remove1 = false;
    private TPanel oldPanel = null;
    private String myText = null;
    static int textindex = 0;
    private boolean inFunction = false;
    static boolean startOpen = true;
    static boolean open = false;
    static boolean done = false;
    static Node equation = null;

    static boolean uno = false;
    static Document EquationMathML = XMLParser.parse("<apply></apply>");
    static Node emptyNode = EquationMathML;

    static String operator = "=";
    static String op = "eq";
    static boolean start = true;
    static boolean buttonClicked = false;
    static boolean infocus = false;
    static boolean onclick = false;
    static boolean unop = false;
    private TPanel panel;

    protected String functionName = "";
    protected String leftSide = "";
    protected String rightSide = "";

    static Node myNewParent = null;

    static boolean isFocused = false;
    static boolean focus = false;
    static boolean debug = true;
    static Node newRoot = EquationMathML.getFirstChild();
    private Node myParent = EquationMathML.getFirstChild();

    static Equation equationToBeDisplayed = new Equation(EquationMathML, null);
    static ExpressionAppearance equationCarrier = new ExpressionAppearance(
	    equationToBeDisplayed);

    static Timer timer = null;

    static String box_name = "";

    public InputPanel() {

	GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
	    public void onUncaughtException(Throwable ex) {
		ex.printStackTrace();
		if (debug) {
		    close();
		    close();
		    Window.alert(Modeller.constants.notImplemented());
		}
	    };
	});

	/**
	 * timer = new Timer() { public void run() { Display(); } };
	 */

//	System.out.println(EquationMathML);
	// Display();

	panel = new TPanel();
	initWidget(panel);
	setStyleName("panel");
//	panel.setWidth("100%");

	if (op.equals("funct")) {

	    TextBox tbfname = new TextBox();
	    tbfname.setTitle(Modeller.constants.functionName());
	    Label label0 = new Label("(");
	    TextBox tbleft = new TextBox();
	    tbleft.setWidth("60px");
	    tbleft.setTitle(Modeller.constants.objectName());
	    Label label1 = new Label(",");
	    box_name = "time";
	    TextBox tbright = new InputBox();
	    tbright.setWidth("60px");
	    tbright.setTitle(Modeller.constants.timeTitle());

	    Label label2 = new Label(")");
	    tbleft.addFocusListener(this);
	    tbright.addFocusListener(this);
	    panel.add(tbfname);
	    panel.add(label0);
	    panel.add(tbleft);
	    panel.add(label1);
	    panel.add(tbright);
	    panel.add(label2);

	    myParent = newRoot;
	    Node node0 = EquationMathML.createElement("mo");
	    myParent.appendChild(node0);
	    Node node1 = EquationMathML.createElement("ci");
	    myParent.appendChild(node1);
	    Node node2 = EquationMathML.createElement("ci");
	    myParent.appendChild(node2);

	    tbfname.addChangeListener(this);
	    tbleft.addChangeListener(this);
	    tbright.addChangeListener(this);
	    tbleft.addKeyboardListener(new KeyboardListenerAdapter() {
		public void onKeyUp(Widget sender, char keyCode, int modifiers) {
		    onChange(sender);
		};
	    });
	    tbright.addKeyboardListener(new KeyboardListenerAdapter() {
		public void onKeyUp(Widget sender, char keyCode, int modifiers) {
		    onChange(sender);
		};
	    });

	    tbfname.addKeyboardListener(new KeyboardListenerAdapter() {
		public void onKeyUp(Widget sender, char keyCode, int modifiers) {
		    onChange(sender);
		};
	    });

	} else if (unop) {
	    unop = false;
	    Label tbfname = new Label(op);
	    // tbfname.setTitle(MoPiX2.constants.labelTitle());
	    Label label0 = new Label("(");
	    box_name = "unleft";
	    TextBox unleft = new InputBox();
	    unleft.setWidth("60px");
	    unleft.setTitle(Modeller.constants.boxTitle());
	    Label label2 = new Label(")");
	    unleft.addFocusListener(this);
	    panel.add(tbfname);
	    panel.add(label0);
	    panel.add(unleft);
	    panel.add(label2);
	    unleft.addChangeListener(this);
	    unleft.addKeyboardListener(new KeyboardListenerAdapter() {
		public void onKeyUp(Widget sender, char keyCode, int modifiers) {
		    onChange(sender);
		};
	    });
	    myParent = newRoot;
	    Node node0 = EquationMathML.createElement(op);
	    myParent.appendChild(node0);
	    Node node1 = EquationMathML.createElement("apply");
	    myParent.appendChild(node1);
	} else {
	    box_name = "left";
	    TextBox tbleft = new InputBox();
	    tbleft.setWidth("80");
	    if (op.equals("eq")) {
		tbleft.setTitle(Modeller.constants.leftOfTheEqualSign());
	    } else {
		tbleft.setTitle(Modeller.constants.boxTitle());
	    }
	    LabelOperator label = new LabelOperator();
	    label.setText(operator);
	    label.setTitle(Modeller.constants.labelTitle());
	    label.addClickListener(this);
	    box_name = "right";
	    TextBox tbright = new InputBox();
	    tbright.setWidth("80");
	    tbright.setTitle(Modeller.constants.boxTitle());
	    tbleft.addFocusListener(this);
	    tbright.addFocusListener(this);
	    panel.add(tbleft);
	    panel.add(label);
	    panel.add(tbright);

	    myParent = newRoot;
	    Node node0 = EquationMathML.createElement(op);
	    myParent.appendChild(node0);
	    Node node1 = EquationMathML.createElement("apply");
	    myParent.appendChild(node1);
	    Node node2 = EquationMathML.createElement("apply");
	    myParent.appendChild(node2);

	    tbleft.addChangeListener(this);
	    tbright.addChangeListener(this);
	    tbright.addKeyboardListener(new KeyboardListenerAdapter() {

		public void onKeyUp(Widget sender, char keyCode, int modifiers) {
		    onChange(sender);
		};
	    });
	    tbleft.addKeyboardListener(new KeyboardListenerAdapter() {

		public void onKeyUp(Widget sender, char keyCode, int modifiers) {
		    onChange(sender);
		};
	    });

	}

//	System.out.println(EquationMathML);
	stackOfPanels.add(panel);

	if (open) {
//	    System.out.println(equation);

	    if (startOpen) {
		textindex = 2;
		startOpen = false;
	    } else {
		textindex = 0;
	    }
	    ;
	    // start with the right hand side of the Equation

	    if (equation.hasChildNodes()) {

		childrenNodes = equation.getChildNodes();
		if (done) {
		    if (childrenNodes.item(2).hasChildNodes()) {
			stack.add(childrenNodes.item(2));
		    }

		    equation = childrenNodes.item(1);//
		    childrenNodes = equation.getChildNodes();
		    done = false;
		}

		if (childrenNodes.getLength() > 1) {
		    if (childrenNodes.getLength() > 2) {
			stack.add(childrenNodes.item(2));
			stackOfParents.add(myParent);
		    } else {
			stack.add(emptyNode);
			stackOfParents.add(myParent);
		    }// unary functions do not have second child

		    op = childrenNodes.item(0).getNodeName();
		    operator = getOperator(op);

		    equation = childrenNodes.item(1);//

		    // --------------------------------------------------------------------------------------
		    if (op.equals("funct")) {
			if ((panel.getWidget(textindex)) instanceof Label) {
			    panel.remove(2);
			    uno = true;
			} else {
			    panel.remove(textindex);
			}

			Node item = EquationMathML.createElement("apply");
			if (textindex == 0) {
			    myParent.replaceChild(item, myParent
				    .getFirstChild().getNextSibling());
			    newRoot = myParent.getFirstChild().getNextSibling();
			    if (uno) {
				uno = false;
				panel.insert(new InputPanel(), 2);
			    } else {
				panel.insert(new InputPanel(), 0);
			    }
			} else {
			    myParent.replaceChild(item, myParent
				    .getFirstChild().getNextSibling()
				    .getNextSibling());
			    newRoot = myParent.getFirstChild().getNextSibling()
				    .getNextSibling();
			    panel.insert(new InputPanel(), 2);
			}
		    } else if (isUnaryOperator(op)) {
			unop = true;
			panel.remove(textindex);
			Node item = EquationMathML.createElement("apply");
			if (textindex == 0) {
			    myParent.replaceChild(item, myParent
				    .getFirstChild().getNextSibling());
			    newRoot = myParent.getFirstChild().getNextSibling();
			    panel.insert(new InputPanel(), 0);
			} else {
			    myParent.replaceChild(item, myParent
				    .getFirstChild().getNextSibling()
				    .getNextSibling());
			    newRoot = myParent.getFirstChild().getNextSibling()
				    .getNextSibling();
			    panel.insert(new InputPanel(), 2);
			}
		    } else {
			if ((panel.getWidget(textindex)) instanceof Label) {
			    panel.remove(2);
			    uno = true;
			} else {
			    panel.remove(textindex);
			}
			Node item = EquationMathML.createElement("apply");
			if (textindex == 0) {
			    myParent.replaceChild(item, myParent
				    .getFirstChild().getNextSibling());
			    newRoot = myParent.getFirstChild().getNextSibling();
			    if (uno) {
				uno = false;
				panel.insert(new InputPanel(), 2);
			    } else {
				panel.insert(new InputPanel(), 0);
			    }
			} else {

			    myParent.replaceChild(item, myParent
				    .getFirstChild().getNextSibling()
				    .getNextSibling());
			    newRoot = myParent.getFirstChild().getNextSibling()
				    .getNextSibling();
			    panel.insert(new InputPanel(), 2);
			}

		    }
		    // --------------------------------------------------------------------------------------

		}
		// it is a text node
		else {
		    if (equation.getNodeName().equals("ci")
			    || equation.getNodeName().equals("cn")) {
			text = equation.getFirstChild().getNodeValue();

			if (op.equals("funct")) {

			    ((TextBox) panel.getWidget(2)).setText(text);

			    Node item = EquationMathML.createElement("ci");
			    myParent.replaceChild(item, myParent
				    .getFirstChild().getNextSibling());
			    myParent
				    .getFirstChild()
				    .getNextSibling()
				    .appendChild(
					    EquationMathML.createTextNode(text));

			    String text1 = equation.getParentNode()
				    .getFirstChild().getFirstChild()
				    .getNodeValue();

			    ((TextBox) panel.getWidget(0)).setText(text1);
			    onChange(((TextBox) panel.getWidget(0)));
			    rightField = true;
			} else if (isUnaryOperator(op)) {
			    ((InputBox) panel.getWidget(2)).setText(text);
			    onChange(((InputBox) panel.getWidget(2)));
			}// rightField=true;}

			else {
			    if (stackOfPanels.size() == 1) {// case of right
							    // side equals
							    // variable or a
							    // number
				((InputBox) panel.getWidget(2)).setText(text);
				onChange(((InputBox) panel.getWidget(2)));
			    } else {
				((InputBox) panel.getWidget(0)).setText(text);
				onChange(((InputBox) panel.getWidget(0)));

				rightField = true;
			    }
			}

		    }
		}
		;
	    }

	    if (rightField) {
		rightField = false;

		if ((!childrenNodes.item(0).getParentNode().getParentNode()
			.getLastChild().getNodeName().equals("ci"))
			&& !childrenNodes.item(0).getParentNode()
				.getParentNode().getLastChild().getNodeName()
				.equals("cn")) {
		    equation = childrenNodes.item(0).getParentNode()
			    .getParentNode().getLastChild();//
		    if (equation.getPreviousSibling().getPreviousSibling()
			    .getNodeName().equals("mo")) {
			inFunction = true;
		    }
		    op = childrenNodes.item(0).getParentNode().getParentNode()
			    .getLastChild().getFirstChild().getNodeName();

		    operator = getOperator(op);
		    textindex = 0;
		    // panel.remove(2);

		    Node item = EquationMathML.createElement("apply");
		    myParent.replaceChild(item, myParent.getFirstChild()
			    .getNextSibling().getNextSibling());
		    newRoot = myParent.getFirstChild().getNextSibling()
			    .getNextSibling();
		    stack.remove(stack.size() - 1);
		    stackOfParents.remove(stackOfParents.size() - 1);

		    done = true;
		    stackOfParents.add(myParent);
		    if (inFunction) {
			panel.remove(4);
			panel.insert(new InputPanel(), 4);
			inFunction = false;
		    } else {
			panel.remove(2);
			panel.insert(new InputPanel(), 2);
		    }
		} else {
		    // write text on the right leaf node
		    text = childrenNodes.item(0).getParentNode()
			    .getParentNode().getLastChild().getFirstChild()
			    .getNodeValue();

		    if (op.equals("funct")) {
			((InputBox) panel.getWidget(4)).setText(text);
			onChange(((InputBox) panel.getWidget(4)));
			if (stack.size() == 0) {
			    open = false;
			}
		    } //
		    else {

			((InputBox) panel.getWidget(2)).setText(text);
			onChange(((InputBox) panel.getWidget(2)));
			if (stack.size() == 0) {
			    open = false;
			}//
		    }
		    stack.remove(stack.size() - 1);
		    stackOfParents.remove(stackOfParents.size() - 1);

		    // here pick up the stack value until the stack is empty
		    while (((stack.size()) > 0)) {

			open = true;

			equation = (Node) stack.get(stack.size() - 1);

			int h = stack.size();

			if (!equation.toString().equals(emptyNode.toString())) {

			    oldPanel = ((TPanel) stackOfPanels.get(h));

			    Node item = EquationMathML.createElement("apply");

			    if (h == 1) {
				TPanel tempPanel = (TPanel) stackOfPanels
					.get(0);
				stackOfPanels = new ArrayList<TPanel>();
				stackOfPanels.add(tempPanel);
			    }
			    ;

			    if (equation
				    .getParentNode()
				    .getParentNode()
				    .getLastChild()
				    .toString()
				    .equals(equation.getParentNode().toString())) {
				myNewParent = ((Node) stackOfParents
					.get(stackOfParents.size() - 1))
					.getLastChild();
				((Node) stackOfParents.get(stackOfParents
					.size() - 1)).getLastChild()
					.replaceChild(
						item,
						((Node) stackOfParents
							.get(stackOfParents
								.size() - 1))
							.getLastChild()
							.getFirstChild()
							.getNextSibling()
							.getNextSibling());
				newRoot = (((Node) stackOfParents
					.get(stackOfParents.size() - 1))
					.getLastChild()).getFirstChild()
					.getNextSibling().getNextSibling();
			    } else {
				myNewParent = ((Node) stackOfParents
					.get(stackOfParents.size() - 1))
					.getFirstChild().getNextSibling();
				((Node) stackOfParents.get(stackOfParents
					.size() - 1)).getFirstChild()
					.getNextSibling().replaceChild(
						item,
						((Node) stackOfParents
							.get(stackOfParents
								.size() - 1))
							.getFirstChild()
							.getNextSibling()
							.getFirstChild()
							.getNextSibling()
							.getNextSibling());
				newRoot = (((Node) stackOfParents
					.get(stackOfParents.size() - 1))
					.getFirstChild().getNextSibling())
					.getFirstChild().getNextSibling()
					.getNextSibling();
			    }

			    if ((!equation.getNodeName().equals("ci") && !equation
				    .getNodeName().equals("cn"))) {

				for (int y = 0; y <= stackOfPanels.size() - 1; y++) {
				    stackOfPanelsTemp.add(y, stackOfPanels.get(y));
				}
				stackOfPanels = new ArrayList<TPanel>();
				for (int x = 0; x <= h - 1; x++) {
				    stackOfPanels.add(x, stackOfPanelsTemp.get(x));
				}
				op = equation.getFirstChild().getNodeName();
				stack.remove(stack.size() - 1);
				stackOfParents.remove(stackOfParents.size() - 1);

				done = true;
				operator = getOperator(op);
				remove1 = true; // panel.remove(2)

				stackOfParents.add(myNewParent);

				oldPanel.insert(new InputPanel(), 2);
			    } else {
				// write text

				myText = equation.getFirstChild()
					.getNodeValue();
				((InputBox) oldPanel.getWidget(1))
					.setText(myText);
				open = false;

				if (equation.getParentNode().getParentNode()
					.getLastChild().toString().equals(
						equation.getParentNode()
							.toString())) {
				    Node c2 = EquationMathML
					    .createTextNode(myText);
				    if (isNumeric(myText)) {
					Node item3 = EquationMathML
						.createElement("cn");
					((Node) stackOfParents
						.get(stackOfParents.size() - 1))
						.getLastChild()
						.replaceChild(
							item3,
							((Node) stackOfParents
								.get(stackOfParents
									.size() - 1))
								.getLastChild()
								.getFirstChild()
								.getNextSibling()
								.getNextSibling());
				    } else {
					Node item3 = EquationMathML
						.createElement("ci");
					((Node) stackOfParents
						.get(stackOfParents.size() - 1))
						.getLastChild()
						.replaceChild(
							item3,
							((Node) stackOfParents
								.get(stackOfParents
									.size() - 1))
								.getLastChild()
								.getFirstChild()
								.getNextSibling()
								.getNextSibling());
				    }
				    ((Node) stackOfParents.get(stackOfParents
					    .size() - 1)).getLastChild()
					    .getFirstChild().getNextSibling()
					    .getNextSibling().appendChild(c2);

				} else {
				    Node c2 = EquationMathML
					    .createTextNode(myText);
				    if (isNumeric(myText)) {
					Node item3 = EquationMathML
						.createElement("cn");
					((Node) stackOfParents
						.get(stackOfParents.size() - 1))
						.getFirstChild()
						.getNextSibling()
						.replaceChild(
							item3,
							((Node) stackOfParents
								.get(stackOfParents
									.size() - 1))
								.getFirstChild()
								.getNextSibling()
								.getFirstChild()
								.getNextSibling()
								.getNextSibling());
				    } else {
					Node item3 = EquationMathML
						.createElement("ci");
					((Node) stackOfParents
						.get(stackOfParents.size() - 1))
						.getFirstChild()
						.getNextSibling()
						.replaceChild(
							item3,
							((Node) stackOfParents
								.get(stackOfParents
									.size() - 1))
								.getFirstChild()
								.getNextSibling()
								.getFirstChild()
								.getNextSibling()
								.getNextSibling());
				    }
				    ((Node) stackOfParents.get(stackOfParents
					    .size() - 1)).getFirstChild()
					    .getNextSibling().getFirstChild()
					    .getNextSibling().getNextSibling()
					    .appendChild(c2);

				}

				stack.remove(stack.size() - 1);
				stackOfParents
					.remove(stackOfParents.size() - 1);

			    }

			}// end if equation not empty
			else {
			    stack.remove(stack.size() - 1);
			    stackOfParents.remove(stackOfParents.size() - 1);
			}

		    }// end while
		}

	    }// end if rightField

//	    System.out.println(EquationMathML);

	    if (remove) {
		panel.remove(1);
		remove = false;
	    }
	    if (remove1) {
		oldPanel.remove(1);
		remove1 = false;
	    }

	}// end if open

    }

    public void onChange(Widget sender) {

	if (sender.getTitle().equals(Modeller.constants.objectName())) {
	    leftSide = ((TextBox) sender).getText();
	    Node c1 = EquationMathML.createTextNode(leftSide);
	    if (isNumeric(leftSide)) {
		Node item = EquationMathML.createElement("cn");
		myParent.replaceChild(item, myParent.getFirstChild()
			.getNextSibling());
	    } else {
		Node item = EquationMathML.createElement("ci");
		myParent.replaceChild(item, myParent.getFirstChild()
			.getNextSibling());
	    }
	    myParent.getFirstChild().getNextSibling().appendChild(c1);

	    Display();

//	    System.out.println(EquationMathML);

	} else if (sender instanceof InputBox) {
	    if ((((InputBox) sender).Name().equals("left"))) {
		leftSide = ((TextBox) sender).getText();
		Node c1 = EquationMathML.createTextNode(leftSide);
		if (isNumeric(leftSide)) {
		    Node item = EquationMathML.createElement("cn");
		    myParent.replaceChild(item, myParent.getFirstChild()
			    .getNextSibling());
		} else {
		    Node item = EquationMathML.createElement("ci");
		    myParent.replaceChild(item, myParent.getFirstChild()
			    .getNextSibling());
		}
		myParent.getFirstChild().getNextSibling().appendChild(c1);

		Display();

//		System.out.println(EquationMathML);

	    }
	    if ((((InputBox) sender).Name().equals("right"))
		    || (((InputBox) sender).Name().equals("time"))) {
		rightSide = ((TextBox) sender).getText();
		Node c2 = EquationMathML.createTextNode(rightSide);
		if (isNumeric(rightSide)) {
		    Node item = EquationMathML.createElement("cn");
		    myParent.replaceChild(item, myParent.getFirstChild()
			    .getNextSibling().getNextSibling());
		} else {
		    Node item = EquationMathML.createElement("ci");
		    myParent.replaceChild(item, myParent.getFirstChild()
			    .getNextSibling().getNextSibling());
		}
		myParent.getFirstChild().getNextSibling().getNextSibling()
			.appendChild(c2);

		Display();

//		System.out.println(EquationMathML);

	    }
	    if ((((InputBox) sender).Name().equals("unleft"))) {
		leftSide = ((TextBox) sender).getText();
		Node c1 = EquationMathML.createTextNode(leftSide);
		if (isNumeric(leftSide)) {
		    Node item = EquationMathML.createElement("cn");
		    myParent.replaceChild(item, myParent.getFirstChild()
			    .getNextSibling());
		} else {
		    Node item = EquationMathML.createElement("ci");
		    myParent.replaceChild(item, myParent.getFirstChild()
			    .getNextSibling());
		}
		myParent.getFirstChild().getNextSibling().appendChild(c1);
		Display();

	    }
	    ;
	}
	if (sender.getTitle().equals(Modeller.constants.functionName())) {
	    functionName = ((TextBox) sender).getText();
	    Node item = EquationMathML.createElement("mo");
	    myParent.replaceChild(item, myParent.getFirstChild());
	    Node c1 = EquationMathML.createTextNode(functionName);
	    myParent.getFirstChild().appendChild(c1);

//	    System.out.println(EquationMathML);

	    Display();

	}
	// Display();
	// timer.cancel();
    }

    /** Override */
    static String labelText = null;
    static TPanel r = null;
    static int t = 0;
    static Widget me = null;
    static int myIndex = 0;
    static InputBox tbr = null;
    static InputBox tbl = null;
    static boolean isUnary = false;
    static int y = 0;
    static int x = 0;

    public void onClick(Widget sender) {

	labelText = ((LabelOperator) sender).getText();
	x = ((LabelOperator) sender).getAbsoluteLeft();
	y = ((LabelOperator) sender).getAbsoluteTop();
	me = (this);
	if (!labelText.equals("=")) {
	    myIndex = ((TPanel) this.getParent()).getWidgetIndex(this);
	    t = ((TPanel) this.getParent()).getWidgetCount() - 1;
	    r = ((TPanel) this.getParent());
	}
	if (myIndex == 2) {
	    box_name = "right";
	    tbr = new InputBox();
	    tbr.setTitle(Modeller.constants.boxTitle());
	} else {
	    box_name = "time";
	    tbr = new InputBox();
	    tbr.setTitle(Modeller.constants.timeTitle());
	}
	tbr.addFocusListener(this);
	tbr.addChangeListener(this);
	tbr.addKeyboardListener(new KeyboardListenerAdapter() {
	    public void onKeyUp(Widget sender, char keyCode, int modifiers) {
		onChange(sender);
	    };
	});
	isUnary = isUnaryOperator(myParent.getParentNode().getFirstChild()
		.getNodeName());
	if (isUnary) {
	    box_name = "unleft";
	    tbl = new InputBox();
	    tbl.setTitle(Modeller.constants.boxTitle());
	} else {
	    box_name = "left";
	    tbl = new InputBox();
	    tbl.setTitle(Modeller.constants.boxTitle());
	}
	tbl.addFocusListener(this);
	tbl.addChangeListener(this);
	tbl.addKeyboardListener(new KeyboardListenerAdapter() {
	    public void onKeyUp(Widget sender, char keyCode, int modifiers) {
		onChange(sender);
	    };
	});

	{

	    final PopupPanel popupMenu = new PopupPanel(true);
	    popupMenu.setAnimationEnabled(true);
	    final MenuBar menuBar = new MenuBar(true);
	    menuBar.setAnimationEnabled(true);
	    popupMenu.setWidget(menuBar);
	    // RootPanel.get().add(menuBar, x,y);

	    menuBar.addItem(Modeller.constants.delete(), new Command() {
		public void execute() {
		    popupMenu.hide();
		    menuBar.setVisible(false);
		    if (labelText.equals("=")) {
			InputPanel.close();
			close();
			onclick = false;
		    } else {

			Node myTempParent = myParent.getParentNode();

			myParent.getParentNode()
				.replaceChild(
					EquationMathML.createElement("apply"),
					myParent);

			myParent = myTempParent;

//			System.out.println("EquationMathML    "  + EquationMathML);

			if (myIndex == 2) {
			    if (isUnary) {
				r.insert(tbl, 2);
				panel = r;
				r.remove(3);
				isUnary = false;
			    } else {
				r.insert(tbr, 2);
				panel = r;
				r.remove(3);
			    }
			} else if (myIndex == 0) {
			    r.insert(tbl, 0);
			    panel = r;
			    r.remove(1);
			} else if (myIndex == 4) {
			    r.insert(tbr, 4);
			    panel = r;
			    r.remove(5);
			}
			
			Display();

		    }

		}
	    });

	    popupMenu.show();
	    popupMenu.setPopupPosition(x, y);

	}

    }

    int j = 0;
    int index = 0;
    Widget panelParent = null;

    /** Override */
    public void onFocus(Widget sender1) {
	infocus = true;
	debug = false;

	if (((sender1.getTitle().equals(Modeller.constants.objectName())) || (((InputBox) sender1)
		.Name().equals("left")))
		&& buttonClicked && infocus) {
	    index = 0;
	    sender1.removeFromParent();
	    Node item = EquationMathML.createElement("apply");
	    myParent.replaceChild(item, myParent.getFirstChild()
		    .getNextSibling());
	    newRoot = myParent.getFirstChild().getNextSibling();

	    panel.insert(new InputPanel(), 0);
	    buttonClicked = false;
	    infocus = false;

	}
	;
	if ((((InputBox) sender1).Name().equals("right")) && buttonClicked
		&& infocus) {
	    index = 2;
	    sender1.removeFromParent();
	    Node item = EquationMathML.createElement("apply");
	    myParent.replaceChild(item, myParent.getFirstChild()
		    .getNextSibling().getNextSibling());
	    newRoot = myParent.getFirstChild().getNextSibling()
		    .getNextSibling();
	    panel.insert(new InputPanel(), index);
	    buttonClicked = false;
	    infocus = false;
	}
	;
	if ((((InputBox) sender1).Name().equals("unleft")) && buttonClicked
		&& infocus) {
	    index = 2;
	    sender1.removeFromParent();
	    Node item = EquationMathML.createElement("apply");
	    myParent.replaceChild(item, myParent.getFirstChild()
		    .getNextSibling());
	    newRoot = myParent.getFirstChild().getNextSibling();
	    panel.insert(new InputPanel(), 2);
	    buttonClicked = false;
	    infocus = false;
	}
	;
	if ((((InputBox) sender1).Name().equals("time")) && buttonClicked
		&& infocus) {
	    index = 4;
	    sender1.removeFromParent();
	    Node item = EquationMathML.createElement("apply");
	    myParent.replaceChild(item, myParent.getFirstChild()
		    .getNextSibling().getNextSibling());
	    newRoot = myParent.getFirstChild().getNextSibling()
		    .getNextSibling();
	    panel.insert(new InputPanel(), index);
	    buttonClicked = false;
	    infocus = false;
	}
	;

	debug = true;
    }

    /** } */
    public void onLostFocus(final Widget sender1) {

    }

    static public void Init() {

	// opening existing equation

	op = "eq";
	operator = "=";
	EquationMathML = XMLParser.parse("<apply></apply>");
	start = true;
	buttonClicked = false;
	infocus = false;
	newRoot = EquationMathML.getFirstChild();
	stack = new ArrayList<Node>();
	stackOfPanels = new ArrayList<TPanel>();
	stackOfParents = new ArrayList<Node>();
	TPanel.hpindex = 0;
	// start creating panels
	InputPanel panel1 = new InputPanel();
	MoPiX.editorPanelContents.add(panel1);// add(panel1, 0, 400);
	// RootPanel.get().add(panel1, 0, 400); //attach the panel at the end

	if (leftSideOfEquation.getNodeName().equals("ci")) {// left side is a
							    // name

	    ((TextBox) ((TPanel) stackOfPanels.get(0)).getWidget(0))
		    .setText(leftSideOfEquation.getFirstChild().getNodeValue());
	    Node item = EquationMathML.createElement("ci");
	    EquationMathML.getFirstChild().replaceChild(
		    item,
		    EquationMathML.getFirstChild().getFirstChild()
			    .getNextSibling());
	    Node c1 = EquationMathML.createTextNode(leftSideOfEquation
		    .getFirstChild().getNodeValue());
	    EquationMathML.getFirstChild().getFirstChild().getNextSibling()
		    .appendChild(c1);

	} else {
	    // initial values for the left side
	    op = "funct";
	    operator = getOperator(op);
	    newRoot = EquationMathML.getFirstChild().getFirstChild()
		    .getNextSibling();
	    open = false;

	    // add left hand side of the EquationMathML
	    ((InputBox) ((TPanel) stackOfPanels.get(0)).getWidget(0))
		    .removeFromParent();
	    ((TPanel) stackOfPanels.get(0)).insert(new InputPanel(), 0);

	    // attach left side of equation
	    TPanel panel2 = ((TPanel) stackOfPanels
		    .get((stackOfPanels.size() - 1)));
	    ((TextBox) panel2.getWidget(0)).setText(leftSideOfEquation
		    .getFirstChild().getFirstChild().getNodeValue());
	    ((TextBox) panel2.getWidget(2)).setText(leftSideOfEquation
		    .getFirstChild().getNextSibling().getFirstChild()
		    .getNodeValue());
	    ((TextBox) panel2.getWidget(4)).setText(leftSideOfEquation
		    .getLastChild().getFirstChild().getNodeValue());

	    Node item = EquationMathML.createElement("mo");
	    EquationMathML.getFirstChild().getFirstChild().getNextSibling()
		    .replaceChild(
			    item,
			    EquationMathML.getFirstChild().getFirstChild()
				    .getNextSibling().getFirstChild());
	    Node c1 = EquationMathML.createTextNode(leftSideOfEquation
		    .getFirstChild().getFirstChild().getNodeValue());
	    EquationMathML.getFirstChild().getFirstChild().getNextSibling()
		    .getFirstChild().appendChild(c1);

	    Node item2 = EquationMathML.createElement("ci");
	    EquationMathML.getFirstChild().getFirstChild().getNextSibling()
		    .replaceChild(
			    item2,
			    EquationMathML.getFirstChild().getFirstChild()
				    .getNextSibling().getFirstChild()
				    .getNextSibling());
	    Node c2 = EquationMathML.createTextNode(leftSideOfEquation
		    .getFirstChild().getNextSibling().getFirstChild()
		    .getNodeValue());
	    EquationMathML.getFirstChild().getFirstChild().getNextSibling()
		    .getFirstChild().getNextSibling().appendChild(c2);

	    if (isNumeric(leftSideOfEquation.getLastChild().getFirstChild()
		    .getNodeValue())) {
		Node item4 = EquationMathML.createElement("cn");
		EquationMathML.getFirstChild().getFirstChild().getNextSibling()
			.replaceChild(
				item4,
				EquationMathML.getFirstChild().getFirstChild()
					.getNextSibling().getLastChild());
	    }

	    else {
		Node item3 = EquationMathML.createElement("ci");
		EquationMathML.getFirstChild().getFirstChild().getNextSibling()
			.replaceChild(
				item3,
				EquationMathML.getFirstChild().getFirstChild()
					.getNextSibling().getLastChild());
	    }

	    Node c3 = EquationMathML.createTextNode(leftSideOfEquation
		    .getLastChild().getFirstChild().getNodeValue());
	    EquationMathML.getFirstChild().getFirstChild().getNextSibling()
		    .getLastChild().appendChild(c3);

	}
	Display();

	open = false;
//	System.out.println(EquationMathML);
	Button closeButton = new ModellerButton(Modeller.constants.close());
	closeButton.addClickListener(new ClickListener() {
	    public void onClick(Widget sender) {
		close();
		sender.removeFromParent();
		close();
	    }
	});
	for (int k = 0; k <= MoPiX.editorPanelContents.getWidgetCount() - 1; k++) {
	    Widget w = MoPiX.editorPanelContents.getWidget(k);
	    if (w instanceof Button) {
		if (((Button) w).getText().equals(Modeller.constants.close())) {
		    w.removeFromParent();
		}
	    }
	}

	MoPiX.editorPanelContents.add(Utils.wrapForGoodSize(closeButton));

    }

    static public void Display() {
	if (equationCarrier.isAttached()) {
	    equationCarrier.removeFromParent();
	}
	equationToBeDisplayed = new Equation(EquationMathML, null);
	equationCarrier = new EquationAppearance(equationToBeDisplayed);
	MoPiX.editorPanelContents.add(equationCarrier); // , 0, 300);
    }

    static private final String REAL_NUMBER = "^[-+]?\\d+(\\.\\d+)?$";

    static public boolean isNumeric(String string) {
	return string.matches(REAL_NUMBER);
    }

    static public boolean isUnaryOperator(String s) {
	for (int j = 0; j < unaryOperators.length; j++) {
	    if (s.equals(unaryOperators[j])) {
		return true;
	    }
	}
	return false;
    }

    static public void createEquation() {

	// called when user clicks the New Equation button
	InputPanel panel = new InputPanel();
	MoPiX.editorPanelContents.add(panel); // , 0, 180);

	final HTML message = new HTML(Modeller.constants
		.enterNumbersOrVariablesInTheBoxesOrDoubleClickForMoreOptions());
	if (!message.isAttached()) {
	    MoPiX.editorPanelContents.add(message);
	}

	Button closeButton = new ModellerButton(Modeller.constants.close());
	closeButton.addClickListener(new ClickListener() {
	    public void onClick(Widget sender) {
		close();
		sender.removeFromParent();
		close();
	    }
	});
	for (int k = 0; k <= MoPiX.editorPanelContents.getWidgetCount() - 1; k++) {
	    Widget w = MoPiX.editorPanelContents.getWidget(k);
	    if (w instanceof Button) {
		if (((Button) w).getText().equals(Modeller.constants.close())) {
		    w.removeFromParent();
		}
	    }
	}
	// if (!closeButton.isAttached())
	MoPiX.editorPanelContents.add(closeButton);

    };

    static public String getOperator(String tag) {
	String oper = null;
	if (tag.equals("plus")) {
	    oper = "+";
	} else if (tag.equals("minus")) {
	    oper = "-";
	} else if (tag.equals("times")) {
	    oper = "*";
	} else if (tag.equals("divide")) {
	    oper = "/";
	} else if (tag.equals("rem")) {
	    oper = Modeller.constants.mod();
	} else if (tag.equals("power")) {
	    oper = "^";
	} else if (tag.equals("max")) {
	    oper = Modeller.constants.max();
	} else if (tag.equals("min")) {
	    oper = Modeller.constants.min();
	    ;
	} else if (tag.equals("leq")) {
	    oper = "<=";
	} else if (tag.equals("geq")) {
	    oper = ">=";
	} else if (tag.equals("gt")) {
	    oper = ">";
	} else if (tag.equals("lt")) {
	    oper = "<";
	} else if (tag.equals("neq")) {
	    oper = "!=";
	}

	else if (tag.equals("and")) {
	    oper = Modeller.constants.and();
	} else if (tag.equals("sin")) {
	    oper = Modeller.constants.sin();
	} else if (tag.equals("cos")) {
	    oper = Modeller.constants.cos();
	} else if (tag.equals("tan")) {
	    oper = Modeller.constants.tan();
	} else if (tag.equals("abs")) {
	    oper = Modeller.constants.abs();
	} else if (tag.equals("not")) {
	    oper = Modeller.constants.not();
	} else if (tag.equals("or")) {
	    oper = Modeller.constants.or();
	} else if (tag.equals("mo")) {
	    oper = "function";
	    op = "funct";
	}
	return oper;
    }

    static public void openEquation(Node eq) {
//	String equationMathMLString = EquationMathML.toString();
	String equationMathMLTag = EquationMathML.getFirstChild().getNodeName();
	if (equationMathMLTag.equals("apply")) {
	    // Ken edited this on 10 June 2008 since the MathML was <apply></apply>
	    // but I don't understand why it does this at all
//	if (equationMathMLString.equals("<apply/>")) {
	    open = true;
	    startOpen = true;
	    equation = eq.getFirstChild().getNextSibling().getNextSibling();
	    // .getFirstChild() after eq.
	    leftSideOfEquation = eq.getFirstChild().getNextSibling();
	    // .getFirstChild() after eq.
	    Init();
	} else {
	    Window.alert(Modeller.constants.pleaseCloseTheCurrentEquation());
	}
    }

    static public void close() {
	InputPanel.op = "eq";
	InputPanel.operator = "=";
	InputPanel.EquationMathML = XMLParser.parse("<apply></apply>");
	InputPanel.start = true;
	InputPanel.startOpen = true;
	InputPanel.buttonClicked = false;
	InputPanel.infocus = false;
	InputPanel.open = false;
	done = false;
	debug = true;
	InputPanel.newRoot = InputPanel.EquationMathML.getFirstChild();
	InputPanel.stackOfPanels = new ArrayList<TPanel>();
	InputPanel.stackOfParents = new ArrayList<Node>();
	InputPanel.stack = new ArrayList<Node>();
	equationToBeDisplayed = new Equation(EquationMathML, null);
	equationCarrier = new EquationAppearance(equationToBeDisplayed);
	for (int k = 0; k <= MoPiX.editorPanelContents.getWidgetCount() - 1; k++) {
	    Widget w = MoPiX.editorPanelContents.getWidget(k);
	    if (w instanceof InputPanel) {
		MoPiX.editorPanelContents.remove(k);
	    }
	    if (w instanceof Button) {
		if (((Button) w).getText().equals(Modeller.constants.close())) {
		    w.removeFromParent();
		}
	    }
	    if (w instanceof HTML && !(w instanceof EquationAppearance)) {
		w.removeFromParent();

	    }

	}

    }

}