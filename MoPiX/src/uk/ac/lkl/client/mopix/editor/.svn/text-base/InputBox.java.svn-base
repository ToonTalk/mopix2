package uk.ac.lkl.client.mopix.editor;



import uk.ac.lkl.client.Modeller;
import uk.ac.lkl.client.Utils;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import uk.ac.lkl.client.mopix.editor.InputPanel;


public class InputBox extends TextBox {
private String name="";
    public InputBox() {

        super();
        name=InputPanel.box_name;
        this.setWidth("80");
        this.sinkEvents(Event.ONDBLCLICK);

    }


  /** Override */
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        int type = DOM.eventGetType(event);
        switch (type) {
        case Event.ONDBLCLICK: {
            createPopUp(event);
            break;
        }

        }

    };

    public String Name (){ 
    	
	     return name; 
	    }
    public void click() {
        this.click();
    }


    protected void createPopUp(Event event) {
    	final PopupPanel popupMenu = new PopupPanel(true);
    	popupMenu.setAnimationEnabled(true);
    	
        {
            final MenuBar menuBar = new MenuBar(true);
            menuBar.setAnimationEnabled(true);
            
            popupMenu.setWidget(menuBar);
           
            MenuItem firstMenuItem = new MenuItem ("+", new Command() {
           
                public void execute() {
                	popupMenu.hide();
                    menuBar.setVisible(false);
                    InputPanel.operator = "+";
                    InputPanel.op = "plus";
                    InputPanel.buttonClicked = true;
                    setFocus(true);
                }
            });
            menuBar.addItem(firstMenuItem);
            menuBar.addItem("-", new Command() {

                public void execute() {
                	popupMenu.hide();
                    menuBar.setVisible(false);
                    InputPanel.operator = "-";
                    InputPanel.op = "minus";
                    InputPanel.buttonClicked = true;
                    setFocus(true);
                };
            });

            menuBar.addItem("*", new Command() {

                public void execute() {
                	popupMenu.hide();
                    menuBar.setVisible(false);
                    InputPanel.operator = "*";
                    InputPanel.op = "times";
                    InputPanel.buttonClicked = true;
                    setFocus(true);
                }
            });

            menuBar.addItem("/", new Command() {

                public void execute() {
                	popupMenu.hide();
                    menuBar.setVisible(false);
                    InputPanel.operator = "/";
                    InputPanel.op = "divide";
                    InputPanel.buttonClicked = true;
                    setFocus(true);
                }
            });
            menuBar.addItem(Modeller.constants.function(), new Command() {

                public void execute() {
                	popupMenu.hide();
                    menuBar.setVisible(false);
                    InputPanel.operator = "function";
                    InputPanel.op = "funct";
                    InputPanel.buttonClicked = true;
                    setFocus(true);
                }
            });
            menuBar.addItem("<hr>", true, (Command) null);

            final MenuBar ari = new MenuBar(true);
            ari.addItem(Modeller.constants.mod(), new Command() {

                public void execute() {
                	popupMenu.hide();
                    menuBar.setVisible(false);
                    InputPanel.operator = Modeller.constants.mod();
                    InputPanel.op = "rem";
                    InputPanel.buttonClicked = true;
                    setFocus(true);

                };
            });

            ari.addItem(Modeller.constants.exp(), new Command() {

                public void execute() {
                	popupMenu.hide();
                    menuBar.setVisible(false);
                    InputPanel.operator = "^";
                    InputPanel.op = "power";
                    InputPanel.buttonClicked = true;
                    setFocus(true);

                };
            });

            ari.addItem(Modeller.constants.abs(), new Command() {

                public void execute() {
                	popupMenu.hide();
                    menuBar.setVisible(false);
                    InputPanel.operator = Modeller.constants.abs();
                    InputPanel.op = "abs";
                    InputPanel.buttonClicked = true;
                    InputPanel.unop = true;
                    setFocus(true);

                };
            });

            ari.addItem(Modeller.constants.max(), new Command() {

                public void execute() {
                	popupMenu.hide();
                    menuBar.setVisible(false);
                    InputPanel.operator = Modeller.constants.max();
                    InputPanel.op = "max";
                    InputPanel.buttonClicked = true;
                    setFocus(true);

                };
            });
            ari.addItem(Modeller.constants.min(), new Command() {

                public void execute() {
                	popupMenu.hide();
                    menuBar.setVisible(false);
                    InputPanel.operator = Modeller.constants.min();
                    InputPanel.op = "min";
                    InputPanel.buttonClicked = true;
                    setFocus(true);

                };
            });
            menuBar.addItem(Modeller.constants.arithmetics(), ari);

            final MenuBar tri = new MenuBar(true);
            tri.addItem(Modeller.constants.sin(), new Command() {

                public void execute() {
                	popupMenu.hide();
                    menuBar.setVisible(false);
                    InputPanel.operator = Modeller.constants.sin();
                    InputPanel.op = "sin";
                    InputPanel.buttonClicked = true;
                    InputPanel.unop = true;
                    setFocus(true);
                };
            });
            tri.addItem(Modeller.constants.cos(), new Command() {

                public void execute() {
                	popupMenu.hide();
                    menuBar.setVisible(false);
                    InputPanel.operator = Modeller.constants.cos();
                    InputPanel.op = "cos";
                    InputPanel.buttonClicked = true;
                    InputPanel.unop = true;
                    setFocus(true);

                };
            });
            tri.addItem(Modeller.constants.tan(), new Command() {

                public void execute() {
                	popupMenu.hide();
                    menuBar.setVisible(false);
                    InputPanel.operator = Modeller.constants.tan();
                    InputPanel.op = "tan";
                    InputPanel.buttonClicked = true;
                    InputPanel.unop = true;
                    setFocus(true);

                };
            });

            menuBar.addItem(Modeller.constants.trigonometry(), tri);

            final MenuBar rel = new MenuBar(true);

            rel.addItem("<", new Command() {

                public void execute() {
                	popupMenu.hide();
                    menuBar.setVisible(false);
                    InputPanel.operator = "<";
                    InputPanel.op = "lt";
                    InputPanel.buttonClicked = true;
                    setFocus(true);
                };
            });

            rel.addItem(">", new Command() {

                public void execute() {
                	popupMenu.hide();
                    menuBar.setVisible(false);
                    InputPanel.operator = ">";
                    InputPanel.op = "gt";
                    InputPanel.buttonClicked = true;
                    setFocus(true);
                };
            });

            rel.addItem(">=", new Command() {

                public void execute() {
                	popupMenu.hide();
                    menuBar.setVisible(false);
                    InputPanel.operator = ">=";
                    InputPanel.op = "geq";
                    InputPanel.buttonClicked = true;
                    setFocus(true);
                };
            });

            rel.addItem("<=", new Command() {

                public void execute() {
                	popupMenu.hide();
                    menuBar.setVisible(false);
                    InputPanel.operator = "<=";
                    InputPanel.op = "leq";
                    InputPanel.buttonClicked = true;
                    setFocus(true);
                };
            });
            rel.addItem("!=", new Command() {

                public void execute() {
                	popupMenu.hide();
                    menuBar.setVisible(false);
                    InputPanel.operator = "!=";
                    InputPanel.op = "neq";
                    InputPanel.buttonClicked = true;
                    setFocus(true);
                };
            });

            menuBar.addItem(Modeller.constants.relations(), rel);

            final MenuBar log = new MenuBar(true);
            log.addItem(Modeller.constants.not(), new Command() {

                public void execute() {
                	popupMenu.hide();
                    menuBar.setVisible(false);
                    InputPanel.operator = Modeller.constants.not();
                    InputPanel.op = "not";
                    InputPanel.buttonClicked = true;
                    InputPanel.unop = true;
                    setFocus(true);
                };
            });

            log.addItem(Modeller.constants.or(), new Command() {

                public void execute() {
                	popupMenu.hide();
                    menuBar.setVisible(false);
                    InputPanel.operator = Modeller.constants.or();
                    InputPanel.op = "or";
                    InputPanel.buttonClicked = true;
                    setFocus(true);
                };
            });

            log.addItem(Modeller.constants.and(), new Command() {

                public void execute() {
                	popupMenu.hide();
                    menuBar.setVisible(false);
                    InputPanel.operator = Modeller.constants.and();
                    InputPanel.op = "and";
                    InputPanel.buttonClicked = true;
                    setFocus(true);
                };
            });
            menuBar.addItem(Modeller.constants.logic(), log);
            popupMenu.show();
            Utils.positionPopupMenu(event.getClientX(), event.getClientY(), popupMenu);
//            popupMenu.setPopupPosition(this.getAbsoluteLeft(), this.getAbsoluteTop());
        }

    }

}