/**
 * 
 */
package uk.ac.lkl.client;

import java.util.ArrayList;
import java.util.Date;

import uk.ac.lkl.client.mopix.AnimationTimer;
import uk.ac.lkl.client.mopix.ConstantNumberEntry;
import uk.ac.lkl.client.mopix.EquationAppearance;
import uk.ac.lkl.client.mopix.MoPiXGlobals;
import uk.ac.lkl.client.mopix.MoPiXObject;
import uk.ac.lkl.client.mopix.ModellerAbsolutePanel;
import uk.ac.lkl.client.mopix.event.AddEquationToObjectEvent;
import uk.ac.lkl.client.mopix.event.AddObjectEvent;
import uk.ac.lkl.client.mopix.event.ChangeExpressionAppearanceEvent;
import uk.ac.lkl.client.event.CompoundEvent;
import uk.ac.lkl.client.event.ModellerEvent;
import uk.ac.lkl.client.event.ReconstructEventsContinutation;
import uk.ac.lkl.client.mopix.event.RemoveObjectEvent;
import uk.ac.lkl.client.mopix.expression.Equation;
import uk.ac.lkl.client.mopix.graphics.Group;
import uk.ac.lkl.client.mopix.graphics.Shape;
import uk.ac.lkl.client.rpc.CreateMoPiXResourcePageService;
import uk.ac.lkl.client.rpc.MathDiLSServiceAsync;
import uk.ac.lkl.shared.CommonUtils;

import uk.ac.lkl.client.mopix.MoPiXImages;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;

/**
 * MoPiX-specific GWT entry module
 * 
 * 
 * @author Ken Kahn
 *
 */
public class MoPiX extends Modeller {
    private static final String LOAD_MATH_ML = "loadMathML";
    public static String defaultLibraryPage = "old_library.html";
    public static String defaultSampleModelsPage = "sample_models.html";
    public static boolean configureForPondTiling = false;
    public static boolean configureForNewtonSessions = false;
    public static int time = 0;
    public static int timeIncrement = 0;
    public static AnimationTimer animationTimer = null;
    public static int frameDuration = 93; // 93 milliseconds or 12 fps
    public static int objectNameCounter = 1;
    final public static HorizontalPanel temporalNavigationPanel = new HorizontalPanel();
    final public static VerticalPanel innerStagePanel = new VerticalPanel();
    public static SimplePanel stagePanel = null;
    final public Image backToTimeZeroButton = new Image(getMoPiXImages().zero());
    final public Image runBackwardsButton = new Image(getMoPiXImages().back_n());
    final public Image stepBackwardsButton = new Image(getMoPiXImages().back_1());
    final public Image pauseButton = new Image(getMoPiXImages().pause());
    final public Image stepForwardButton = new Image(getMoPiXImages().fd_1());
    final public Image runForwardsButton = new Image(getMoPiXImages().play());
    final public static TextBox timeTextBox = new TextBox(); // "Freeze time"
    final public static CheckBox timeLockedCheckBox = new CheckBox(constants.time());
    final public static TextBox gridSizeTextBox = new TextBox();
    final static int minimumExpressionPanelWidth = 300;
    // following should be an option
    public static boolean adjustStageToWindowSize = true;
    public static boolean hideEquations = false;
    public static boolean displayTemporalNavigation = true;
    public static boolean displayEquationEditor = true;
    public static boolean displayMathMLButton = true;
    public static boolean displayRotateButton = true;
    public static boolean displayCopyMyEquationsButton = true;
    public static boolean displaySaveObjectButton = true;
    public final static int DONT_UPADATE = 0;
    public final static int UPDATE_AS_MUCH_AS_POSSIBLE = 1;
    public final static int UPDATE_ON_MOUSE_UP = 2;
    public static int updateWhileChangingViaUI = UPDATE_ON_MOUSE_UP;
    public static boolean dontChangeViaUIUnlessConstant = false;
    public static boolean clearCacheWhenChangingViaUI = false;
    public static String pondAttributes[] = { "width", "height", "x", "y", 
	    "redColour", "greenColour", "blueColour", "appearance" }; 
    public static String pondAttributeLabels[] = { constants.width(), constants.height(), constants.x(), constants.y(), 
	    constants.percentOfRed(), constants.percentOfGreen(), constants.percentOfBlue(), constants.costume() }; 
    public static boolean directManipulationIsForCurrentTime = true;
    public static boolean indicateEquationsNotCurrentlyUsed = false;
    public static Equation equationToAddToObject = null;
    public static String waitingToInstantiateOTHERXML = null;
    public static int waitingToInstantiateOTHER = 0;
    public static MoPiXObject waitingToInstantiateOTHERRecipient = null;
    public static int changeCodeOfNextObject = Shape.NOT_CHANGING;
    public static MathDiLSServiceAsync MathDiLSService = null;
    public static boolean useCachedValues = true;
    final public static HorizontalSplitPanel runAreaWithExpressions = new HorizontalSplitPanel();
    public static ModellerAbsolutePanel runArea = new ModellerAbsolutePanel();
    final public static VerticalPanel expressionStagePanel = new VerticalPanel();
    final public static ScrollPanel expressionStagePanelScrollable = new ScrollPanel();
    public static ScrollPanel editorPanel = null;
    final public static VerticalPanel editorPanelContents = new VerticalPanel();
    final public static VerticalPanel loadTab = new VerticalPanel();
    final public static VerticalPanel saveTab = new VerticalPanel();
    final public static HorizontalPanel inspectorPanel = new HorizontalPanel();
    public static BrowsePanel sampleModelsPanel = null;
    public static Group stageGroup = null; // group of all shapes on the stage
    // default description is several blank lines
    public static String descriptionOfLastLoadedModel = "\r\n\r\n\r\n\r\n\r\n";
    public static int gridSizeX = 1;
    public static int gridSizeY = 1;
    final public static int stageWidthDefault = 800;
    final public static int stageHeightDefault = 600;
    public static int stageWidthDefaultStageCoordinates = stageWidthDefault;
    public static int stageHeightDefaultStageCoordinates = stageHeightDefault;
    public static int stageWidth = stageWidthDefault;
    public static int stageHeight = stageHeightDefault;
    public static double stageScale = 1.0;
    public static String userName = null;
    public static String objectNamePrefix = "object_";
    final public static String builtInTags[] = { "CreatedBy: ", "CreatedOn: ", "LastModifiedOn: ", "ModifiedBy: ", "Type: " }; 
    public static final int SAVING_MODEL = 0;
    public static final int SAVING_OBJECT = 1;
    private ScheduledCommand updateAllCommand = new ScheduledCommand() {

	@Override
	public void execute() {
	    if (MoPiXGlobals.allObjects != null) {
		updateAllObjects(time, null);
//		redisplayAllObjects();
	    }
	}
	
    };
    
    public MoPiX() {
	super();
    }
    
    public MoPiXImages getMoPiXImages() {
	return (MoPiXImages) GWT.create(MoPiXImages.class);
    }
    
    @Override
    protected void configure() {
	applicationTitle = "MoPiX 2.0";
	configureForPondTiling = Utils.urlAttributeNotZero("hideEquations", configureForPondTiling);
	configureForNewtonSessions = Utils.urlAttributeNotZero("Newton", configureForNewtonSessions);
	defaultManualPage = "Manual/MoPiX2manual.html";
	resourceFolderName = "/MoPiX/";
	if (configureForPondTiling) {
	    setGridSize(40, 40);
	    directManipulationIsForCurrentTime = false;
	    displayTemporalNavigation = false;
	    displayEquationEditor = false;
	    displayRotateButton = false;
	    displayCopyMyEquationsButton = false;
	    displaySaveObjectButton = false;
	    updateWhileChangingViaUI = UPDATE_AS_MUCH_AS_POSSIBLE;
	    clearCacheWhenChangingViaUI = true; // at least until dependencies are implemented
	    dontChangeViaUIUnlessConstant = true; // for now
	    indicateEquationsNotCurrentlyUsed = true;
	    Shape.shapeAttributeMenuItems = pondAttributes;
	    Shape.shapeAttributeMenuAttributeLabels = pondAttributeLabels;
	    defaultExplorerPage = "pond_tiling.html";
	    hideEquations = true;
	    objectNamePrefix = "rectangle_";    
	} else {
	    if (configureForNewtonSessions) {
		defaultExplorerPage = "Sessions.htm";
	    }
	    setGridSize(1, 1);
	}
	if (displayTemporalNavigation) {
	    temporalNavigationPanel.add(backToTimeZeroButton);
	    backToTimeZeroButton.setTitle(constants.setsTimeTo0());
	    temporalNavigationPanel.add(runBackwardsButton);
	    runBackwardsButton.setTitle(constants.runBackwardsUntilTime0());
	    temporalNavigationPanel.add(stepBackwardsButton);
	    stepBackwardsButton.setTitle(constants.runBackwardsOneStepUnlessTimeIs0());
	    temporalNavigationPanel.add(pauseButton);
	    pauseButton.setTitle(constants.stopRunning());
	    temporalNavigationPanel.add(stepForwardButton);
	    stepForwardButton.setTitle(constants.runForwardOneStep());
	    temporalNavigationPanel.add(runForwardsButton);
	    runForwardsButton.setTitle(constants.runForwardUntilStopped());
	    timeTextBox.setTitle(constants.currentTimeYouMustUnlockThisToChangeIt());
	    timeTextBox.setVisibleLength(6);
	    VerticalPanel timePanel = new VerticalPanel();
	    timePanel.add(timeTextBox);
	    timePanel.add(timeLockedCheckBox);
	    temporalNavigationPanel.add(timePanel);
	    gridSizeTextBox.setTitle(constants.currentGridSizeYouCanEditThis());
	    gridSizeTextBox.setVisibleLength(4);
	    VerticalPanel gridSizePanel = new VerticalPanel();
	    gridSizePanel.add(gridSizeTextBox);
	    gridSizePanel.add(new Label(constants.gridSize()));
	    temporalNavigationPanel.add(gridSizePanel);
	}
	Window.setTitle(applicationTitle);
	hideEquations = 
	    Utils.urlAttributeNotZero("hideEquations", hideEquations);
	directManipulationIsForCurrentTime = 
	    Utils.urlAttributeNotZero("manipulationIsForCurrentTime", directManipulationIsForCurrentTime);
	indicateEquationsNotCurrentlyUsed = 
	    Utils.urlAttributeNotZero("indicateEquationsNotCurrentlyUsed", indicateEquationsNotCurrentlyUsed);
	computeScaleAndDimensions();
//	stageSurface = new Surface(runArea.getElement(), stageWidth, stageHeight);
	stageGroup = new Group();
	stageGroup.setScale(stageScale, stageScale);
	updateStageDimensions();
    }

    @Override
    public void onModuleLoad() {
	Canvas canvas = Canvas.createIfSupported();
	if (canvas == null) {
	    RootPanel.get().add(new HTML("This browser does not support HTML 5 which is needed by this version of MoPiX."));
	    return;
	}
//	runArea.setCanvas(canvas);
//	context2d = canvas.getContext2d();
	super.onModuleLoad();
	backToTimeZeroButton.addClickHandler(new ClickHandler() {
	    @Override
	    public void onClick(ClickEvent event) {
		Modeller.setStatusLine(constants.timeBeingResetTo() + " 0.");
		runOrStep(0, 0, frameDuration);
		if (MoPiXGlobals.allObjects != null) {
		    for (int i = 0; i < MoPiXGlobals.allObjects.length; i++) {
			MoPiXGlobals.allObjects[i].clearTrailHistory();
		    }
		}
	    };
	});
	runBackwardsButton.addClickHandler(new ClickHandler() {
	    @Override
	    public void onClick(ClickEvent event) {
		Modeller.setStatusLine(constants.runningBackwards());
		runOrStep(time, -1, frameDuration);
	    };
	});
	stepBackwardsButton.addClickHandler(new ClickHandler() {
	    @Override
	    public void onClick(ClickEvent event) {
		if (time > 0) {
		    int newTime = time - 1;
		    Modeller.setStatusLine(constants.timeBeingResetTo() + " " + newTime + ".");
		    runOrStep(newTime, 0, frameDuration);
		    for (int i = 0; i < MoPiXGlobals.allObjects.length; i++) {
			MoPiXGlobals.allObjects[i].removeTrailAt(time);
		    }
		}
	    };
	});
	pauseButton.addClickHandler(new ClickHandler() {
	    @Override
	    public void onClick(ClickEvent event) {
		if (animationTimer != null) {
		    setStatusLine(constants.paused());
		    animationTimer.cancel();
		    animationTimer = null;
		}
	    };
	});
	stepForwardButton.addClickHandler(new ClickHandler() {
	    @Override
	    public void onClick(ClickEvent event) {
		int newTime = time + 1;
		setStatusLine(constants.timeBeingResetTo() + " " + newTime + ".");
		runOrStep(newTime, 0, frameDuration);
	    };
	});
	runForwardsButton.addClickHandler(new ClickHandler() {
	    @Override
	    public void onClick(ClickEvent event) {
		setStatusLine(constants.runningForwards());
		runOrStep(time, 1, frameDuration);
	    };
	});
	timeTextBox.addKeyUpHandler(new KeyUpHandler() {
	    @Override
	    public void onKeyUp(KeyUpEvent event) {
		stringToNewTIme(timeTextBox.getText());
		
	    };
	});
	timeLockedCheckBox.setValue(true); // initially checked
	if (displayTemporalNavigation) {
	    setTimeLock(true);
	    setStatusLine(" "); // don't talk about locking time on
				// initialisation
	    timeLockedCheckBox.addClickHandler(new ClickHandler() {
		@Override
		    public void onClick(ClickEvent event) {
		    setTimeLock(timeLockedCheckBox.getValue());
		};
	    });
	}
//	splitScreenCheckBox.addClickHandler(new ClickHandler() {
//	    @Override
//	    public void onClick(ClickEvent event) {
//		if (Modeller.configureForBehaviourComposer) {
//		    splitComposerFromRest(splitScreenCheckBox.getValue());
//		} else {
//		    splitStageAndTabPanel(splitScreenCheckBox.getValue());
//		}
//	    };
//	});
	gridSizeTextBox.addKeyUpHandler(new KeyUpHandler() {
	    @Override
	    public void onKeyUp(KeyUpEvent event) {
		String gridSizeString = gridSizeTextBox.getText();
		if (gridSizeString.length() > 0) {
		    try {
			int gridSize = Math.abs(Integer.parseInt(gridSizeString));
			setGridSize(gridSize, gridSize);
			updateAllObjects(time);
		    } catch (NumberFormatException e) {
			setStatusLine(Modeller.constants.ignoring() + " " + gridSizeString + " " + Modeller.constants.sinceItIsntAnInteger());
			gridSizeTextBox.setText(Integer.toString(gridSizeX));
		    }
		}
	    };
	});
	gridSizeTextBox.setText(Integer.toString(gridSizeX));
	JavaScript.setConfirmBeforeExitMessage(constants.youWillLoseYourWorkUnlessYouSaveFirst());
	final String modelGuid = Utils.getLocationParameter(LOAD_MATH_ML);
	if (modelGuid != null && !modelGuid.isEmpty()) {
	    AsyncCallback<String> callback = new  AsyncCallback<String>() {

		@Override
		public void onFailure(Throwable caught) {
		    Modeller.reportException(caught, "In fetching a model from the server.");
		}

		@Override
		public void onSuccess(String result) {
		    if (result != null) {
			if (CommonUtils.isErrorResponse(result)) {
			    addToErrorLog(result);
			    return;
			}
			loadModelString(result, false, constants.model() + " " + modelGuid);
		    } else {
			Modeller.addToErrorLog("Could not find a model with the ID: " + modelID);
		    }
		}
		
	    };
	    fetchModel(modelGuid, callback);
	}
    }
    
    @Override
    protected void startNewSession() {
	super.startNewSession();
	createInitialContents(false); // for now
	// start a new session -- on success will reload the URL with the session ID
//	new StartEvent(null, initialReadOnlySessionID, initialModelID).addToHistory();
    }
    
    @Override
    protected void createInitialContents(boolean restoringHistory) {
	super.createInitialContents(restoringHistory);
	stagePanel = new SimplePanel();
	stagePanel.add(innerStagePanel);
	int tabIndex = 0;
	RootPanel.get().add(tabPanel);
	tabPanel.insert(stagePanel, constants.stage(), tabIndex); 
	MenuBar stageMenu = new MenuBar(false);
	String label;
	if (configureForPondTiling) {
	    label = constants.addNewRectangle();
	} else {
	    label = constants.addNewObject();
	}
	stageMenu.addItem(label, new Command() {
	    public void execute() {
		String objectName;
		objectName = newObjectName();
		MoPiXObject object = new MoPiXObject(objectName);
		object.setAppearance("Square");
		int x = runArea.getOffsetWidth()/(2*gridSizeX);
		Shape.addMathML(Shape.constructMathMLEquation("x", x, objectName, false), object, false);
		int y = runArea.getOffsetHeight()/(2*gridSizeY);
		Shape.addMathML(Shape.constructMathMLEquation("y", y, objectName, false), object, false);
		Shape.addMathML(Shape.constructMathMLEquation("width", Equation.unitSize, objectName, false), object, false);
		Shape.addMathML(Shape.constructMathMLEquation("height", Equation.unitSize, objectName, false), object, false);
		if (!configureForPondTiling) {
		    // should work more generally
		    Shape.addMathML(Shape.constructMathMLEquation("transparency", 100.0, objectName, false), object, false);
		    Shape.addMathML(Shape.constructMathMLEquation("penTransparency", 100.0, objectName, false), object, false);
		}
		// should really set thicknessPen to 1
		// object.setX(stageWidthDefault/(2*gridSizeX));
		// object.setY(stageHeightDefault/(2*gridSizeY));
		// object.setWidth(Equation.unitSize);
		// object.setHeight(Equation.unitSize);
		// object.setAlpha(1.0);
		// addToHistory(Utils.textFontToMatchIcons("Added&nbsp;")
		// + Utils.textFontToMatchIcons(object.getNameHTML()));
		MoPiXGlobals.addToAllObjects(object);
		new AddObjectEvent(object).addToHistory();
		updateAll(true);
	    };
	});
	// if (MoPiX2.configureForPondTiling) {
	stageMenu.addItem(constants.addANumber(), new Command() {
	    public void execute() {
		ConstantNumberEntry entry = new ConstantNumberEntry();
		entry.setFocus(true);
		addToExpressionPalette(entry);
	    };
	});
	// } 
	MenuBar changeObjectMenu = new MenuBar(true);
	// make these into Labels and setTitle for tool tips
	changeObjectMenu.addItem(constants.changePosition(), new Command() {
	    public void execute() {
		changeCodeOfNextObject = Shape.MOVE;
		setStatusLine(constants.clickOnTheObjectYouWishToMove());
	    };
	});
	MenuBar changeSizeMenu = new MenuBar(true);
	changeSizeMenu.addItem(constants.changeWidth(), new Command() {
	    public void execute() {
		changeCodeOfNextObject = Shape.CHANGE_WIDTH;
		setStatusLine(constants.clickOnTheObjectYouWishToMakeWiderOrThinner());
	    };
	});
	changeSizeMenu.addItem(constants.changeHeight(), new Command() {
	    public void execute() {
		changeCodeOfNextObject = Shape.CHANGE_HEIGHT;
		setStatusLine(constants.clickOnTheObjectYouWishToMakeTallerOrShorter());
	    };
	});
	changeObjectMenu.addItem(constants.changeSize(), changeSizeMenu);
	if (displayRotateButton) {
	    changeObjectMenu.addItem(constants.rotate(), new Command() {
		public void execute() {
		    changeCodeOfNextObject = Shape.ROTATE;
		    setStatusLine(constants.clickOnTheObjectYouWishToRotate());
		};
	    });
	}
	MenuBar changeColourMenu = new MenuBar(true);
	changeColourMenu.addItem(constants.changeRedColour(), new Command() {
	    public void execute() {
		changeCodeOfNextObject = Shape.CHANGE_RED;
		setStatusLine(constants.clickOnTheObjectYouWishToMakeMoreOrLessRed());
	    };
	});
	changeColourMenu.addItem(constants.changeGreenColour(), new Command() {
	    public void execute() {
		changeCodeOfNextObject = Shape.CHANGE_GREEN;
		setStatusLine(constants.clickOnTheObjectYouWishToMakeMoreOrLessGreen());
	    };
	});
	changeColourMenu.addItem(constants.changeBlueColour(), new Command() {
	    public void execute() {
		changeCodeOfNextObject = Shape.CHANGE_BLUE;
		setStatusLine(constants.clickOnTheObjectYouWishToMakeMoreOrLessBlue());
	    };
	});
	changeColourMenu.addItem(constants.changeTransparency(), new Command() {
	    public void execute() {
		changeCodeOfNextObject = Shape.CHANGE_ALPHA;
		setStatusLine(constants.clickOnTheObjectYouWishToMakeMoreOrLessTransparent());
	    };
	});
	changeObjectMenu.addItem(constants.changeColour(), changeColourMenu);
	stageMenu.addItem(constants.changeObject(), changeObjectMenu);
	// when there are sliders this can move back to
	// double click of the object
//	Command loadCommand = new Command() {
//	    public void execute() {
//		loadTab.clear();
//		if (!loadTab.isAttached()) {
//		    tabPanel.add(loadTab, constants.searchForModels());
//		}
//		tabPanel.switchTo(loadTab);
//		if (getUserName() == null) {
//		    askUserName(this, loadTab);
//		    return;
//		}
//		final HTML instructions = new HTML(
//			constants.enterFileDescriptionTags());
//		loadTab.add(instructions);
//		final TextArea description = new TextArea();
//		description.setText("\r\n\r\n\r\n\r\n\r\n\r\nCreatedBy: "
//			+ getUserName());
//		description.setPixelSize(stageWidthDefault, 200);
//		loadTab.add(description);
//		final Button searchButton = new ModellerButton(
//			constants.searchForMatchingModels());
//		searchButton.setWidth("200px"); // a hack until I figure out
//		// how
//		// to make it wide as needed
//		loadTab.add(searchButton);
//		searchButton.addClickHandler(new ClickHandler() {
//		    @Override
//		    public void onClick(ClickEvent event) {
//			Modeller.setStatusLine(constants.pleaseWait());
//			getMathDiLSService().listDDAFiles(new AsyncCallback<String[]>() {
//
//			    public void onFailure(Throwable caught) {
//				Modeller.reportException(caught, "In listDDAFiles.");
//			    };
//
//			    public void onSuccess(String resultStrings[]) {
//				if (resultStrings != null) {
//				    if (resultStrings.length == 1) {
//					setStatusLineHTML(resultStrings[0]);
//					return;
//				    }
//				    // shouldn't do the following if nothing
//				    // matches
//				    loadTab.remove(instructions);
//				    loadTab.remove(description);
//				    loadTab.remove(searchButton);
//				    loadMatchingModel(resultStrings, description.getText(), loadTab);
//				} else {
//				    setStatusLine(constants.couldNotFindAnySavedModels());
//				}
//			    }
//			});
//		    }
//		});
//	    }
//	};
//	stageMenu.addItem(constants.load(), loadCommand);
	Command saveCommand = new Command() {
	    public void execute() {
		setStatusLine(constants.pleaseWait());
		saveDialog(this, SAVING_MODEL, getAllXML(true));
	    }
	};
	stageMenu.addItem(constants.save(), saveCommand);
	if (displayMathMLButton) {
	    stageMenu.addItem(constants.mathml(), new Command() {
		public void execute() {
		    final VerticalPanel panel = new VerticalPanel();
		    panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		    final TextArea area = new TextArea();
		    area.setPixelSize(stageWidth, stageHeight);
		    area.setText(getAllXML(true));
		    panel.add(area);
		    Button cancelButton = new ModellerButton(constants.cancel().toUpperCase());
		    cancelButton.addClickHandler(new ClickHandler() {
			@Override
			    public void onClick(ClickEvent event) {
			    panel.removeFromParent();
			}
		    });
		    Button addButton = new ModellerButton(constants.addThisToModel());
		    addButton.addClickHandler(new ClickHandler() {
			@Override
			    public void onClick(ClickEvent event) {
			    loadModelString(area.getText(), true, constants.modelInMathmlPanel());
			    panel.removeFromParent();
			}
		    });
		    Button replaceButton = new ModellerButton(constants.replaceModelWithThis());
		    replaceButton.addClickHandler(new ClickHandler() {
			@Override
			    public void onClick(ClickEvent event) {
			    loadModelString(area.getText(), false, constants.modelInMathmlPanel());
			    panel.removeFromParent();
			}
		    });
		    HorizontalPanel buttonPanel = new HorizontalPanel();
		    buttonPanel.setSpacing(12);
		    buttonPanel.add(addButton);
		    buttonPanel.add(replaceButton);
		    buttonPanel.add(cancelButton);
		    panel.add(buttonPanel);
		    innerStagePanel.insert(panel, 1);
		};
	    });
	}
	innerStagePanel.add(stageMenu);
	runAreaWithExpressions.setLeftWidget(runArea);
	expressionStagePanelScrollable.setWidget(expressionStagePanel);
	expressionStagePanelScrollable.setPixelSize(minimumExpressionPanelWidth, stageHeight);
	expressionStagePanelScrollable.setTitle(constants.yourPaletteOfExpressions());
	runAreaWithExpressions.setRightWidget(expressionStagePanelScrollable);
	innerStagePanel.add(runAreaWithExpressions);
	if (displayTemporalNavigation) {
	    innerStagePanel.add(temporalNavigationPanel);
	}
//	if (displaySplitScreenCheckBox) {
//	    splitScreenCheckBox.setText(constants.splitScreen());
//	    innerStagePanel.add(splitScreenCheckBox); 
//	    // should really be in an Options panel?
//	}
	inspectorPanel.setSpacing(10); // 10 pixels between flip sides
	innerStagePanel.add(inspectorPanel); // empty unless one is inspecting
	if (!configureForPondTiling) {
	    String libraryPage = Utils.getLocationParameter("library");
	    if (libraryPage == null) {
		libraryPage = defaultLibraryPage;
	    }
	    if (!libraryPage.isEmpty()) {
		String defaultResourceURL = defaultResourceURL(libraryPage);
		libraryPanel = new BrowsePanel();
		tabPanel.add(libraryPanel, constants.library());
		libraryPanel.browseTo(defaultResourceURL);
	    }
	    String sampleModelsPage = Utils.getLocationParameter("SampleModels");
	    if (sampleModelsPage == null) {
		sampleModelsPage = defaultSampleModelsPage;
	    }
	    if (!defaultSampleModelsPage.isEmpty()) { 
		String defaultResourceURL = defaultResourceURL(sampleModelsPage);
		sampleModelsPanel = new BrowsePanel();
		tabPanel.add(sampleModelsPanel, constants.sampleModels());
		sampleModelsPanel.browseTo(defaultResourceURL);
	    }
	}
	if (displayEquationEditor) {
	    editorPanel = new ScrollPanel(editorPanelContents);
	    editorPanel.setPixelSize(Window.getClientWidth() - windowWidthMargins, Window.getClientHeight() - windowHeightMargins);
	    tabPanel.add(editorPanel, constants.equationEditor());
	}
	switchToResourcesPanel();
	if (manualPanel != null) {
	    tabPanel.add(manualPanel, constants.help());
	    tabPanel.switchTo(manualPanel);
	}
    }
    
    public boolean generateNaturalLanguageHTMLDefault() {
	if (configureForPondTiling)
	    return false;
	return hideEquations;
    }
    public boolean isTimeLocked() {
	return timeLockedCheckBox.getValue();
    }

//    public void loadMatchingModel(String models[], String description,
//	    final Panel loadTab) {
//	int matchingModels[] = new int[models.length / 2];
//	int numberOfMatchingModels = 0;
//	String tags[] = description.split("\n");
//	// in IE the lines end with \r\n while FireFox just \n
//	for (int i = 0; i < tags.length; i++) {
//	    tags[i] = tags[i].trim();
//	}
//	for (int j = 0; j < models.length; j += 2) {
//	    if (tagsMatch(tags, models[j + 1])) {
//		matchingModels[numberOfMatchingModels++] = j;
//	    }
//	}
//	if (numberOfMatchingModels == 0) {
//	    tabPanel.remove(loadTab);
//	    switchToConstructionArea();
//	    setStatusLine(constants.noModelsMatchTheTags());
//	} else if (numberOfMatchingModels == 1) {
//	    tabPanel.remove(loadTab);
//	    try {
//		final int modelID = Integer.parseInt(models[matchingModels[0]]);
//		descriptionOfLastLoadedModel = models[matchingModels[1]];
//		StringBuilder userDescription = new StringBuilder();
//		for (int i = 0; i < tags.length; i++) {
//		    userDescription.append(tags[i] + " ");
//		}
//		loadModelWithID(modelID, false, userDescription.toString());
//	    } catch (Exception e) {
//		e.printStackTrace();
//		addToErrorLog(constants.errorLoadingAModel() + e.toString());
//	    }
//	} else {
//	    setStatusLine(constants.pleaseChooseOneOfTheseModels());
//	    FlexTable tableOfModels = new FlexTable();
//	    tableOfModels.setCellSpacing(12);
//	    loadTab.add(tableOfModels);
//	    for (int i = 0; i < numberOfMatchingModels; i++) {
//		String modelDescription = models[1 + matchingModels[i]];
//		modelDescription = restoreNewLines(modelDescription);
//		// final Label modelDescription = new
//		// Label(models[1+matchingModels[i]]);
//		// loadTab.add(modelDescription);
//		// Button OKButton = new ModellingButton("OK");
//		// loadTab.add(OKButton);
//		// HorizontalPanel modelPanel = new HorizontalPanel();
//		// modelPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
//		VerticalPanel radioButtons = new VerticalPanel();
//		final RadioButton radioButtonReplace = new RadioButton(constants.models());
//		radioButtonReplace.setText(constants.replaceStageWith());
//		final RadioButton radioButtonAdd = new RadioButton(constants.models());
//		radioButtonAdd.setText(constants.addThisToStage());
//		radioButtons.add(radioButtonAdd);
//		radioButtons.add(radioButtonReplace);
//		tableOfModels.setWidget(i, 0, radioButtons);
//		String lines[] = modelDescription.split("\r\n");
//		VerticalPanel descriptionPanel = new VerticalPanel();
//		for (int j = 0; j < lines.length; j++) {
//		    String line = lines[j].trim();
//		    if (!line.isEmpty()) {
//			if (startsWithBuiltInTag(line)) {
//			    descriptionPanel.add(new HTML(
//				    "<font color='#808080'>" + line + "</font><br>"));
//			} else {
//			    descriptionPanel.add(new HTML("<b>" + line + "</b><br>"));
//			}
//		    }
//		}
//		tableOfModels.setWidget(i, 1, descriptionPanel);
//		try {
//		    final int modelID = 
//			Integer.parseInt(models[matchingModels[i]]);
//		    final String descriptionString = models[matchingModels[i] + 1];
//		    ClickHandler clickHandler = new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//			    loadTab.clear();
//			    tabPanel.remove(loadTab);
//			    setStatusLine(constants.loadingPleaseWait());
//			    descriptionOfLastLoadedModel = descriptionString;
//			    loadModelWithID(modelID, event.getSource() == radioButtonAdd, descriptionString);
//			}
//		    };
//		    radioButtonReplace.addClickHandler(clickHandler);
//		    radioButtonAdd.addClickHandler(clickHandler);
//		} catch (Exception e) {
//		    Modeller.addToErrorLog(e.toString());
//		}
//	    }
//	}
//    }

    public void loadModelString(String model, boolean addToExisting, final String description) {
	// need to implement addToExisting
	if (model == "")
	    return;
	setStatusLine(constants.pleaseWaitWhileTheModelIsLoaded());
	temporalNavigationPanel.removeFromParent();
	CompoundEvent removeObjectsEvent = null;
	if (addToExisting) {
	    model = renameAllObjects(model);
	} else {
	    if (MoPiXGlobals.allObjects != null) {
		ArrayList<ModellerEvent> events = new ArrayList<ModellerEvent>(MoPiXGlobals.allObjects.length);
		for (int i = 0; i < MoPiXGlobals.allObjects.length; i++) {
		    events.add(new RemoveObjectEvent(MoPiXGlobals.allObjects[i]));
		    MoPiXGlobals.allObjects[i].removeFromModel(false);
		}
		MoPiXGlobals.allObjects = null;
		removeObjectsEvent = new CompoundEvent(events);
		removeObjectsEvent.setAlternativeHTML(Modeller.constants.removedAllObjects());
	    }
	}
	final String theModel = model;
	final ModellerEvent theRemoveObjectsEvent = removeObjectsEvent;
	ScheduledCommand command = new ScheduledCommand() {

	    @Override
	    public void execute() {
		Document document = XMLParser.parse(theModel);
		XMLParser.removeWhitespace(document);
		ArrayList<ModellerEvent> events = new ArrayList<ModellerEvent>();
		if (theRemoveObjectsEvent != null) {
		    events.add(theRemoveObjectsEvent);
		}
		Equation.processXML(document, true, events, description);
	    }
	    
	};
	Scheduler.get().scheduleDeferred(command);
    }

//    public void loadModelWithID(final int fileID, final boolean addToExisting, final String description) {
//	getMathDiLSService().DDAFileRead(fileID, new AsyncCallback<String>() {
//	    public void onFailure(Throwable caught) {
//		Modeller.reportException(caught, "In loadModelWithID.");
//	    };
//
//	    public void onSuccess(String result) {
//		if (result != null) {
//		    loadModelString(result, addToExisting, description);
//		} else {
//		    setStatusLine(constants.failedToDownloadModelWhoseInternalIdIs() + fileID);
//		}
//	    }
//	});
//    }

    public void moveTimeTo(final int t, String message) {
	if (t < 0) {
	    setStatusLine(constants.timeCannotBeNegativeIgnoringAttemptToChangeTimeTo() + t);
	    timeTextBox.setText(Integer.toString(time));
	    return;
	}
	if (message == null) {
	    if (t != time) {
		Modeller.setStatusLine(constants.updatingTheTimeTo() + t + ".");
		time = t;
	    }
	} else {
	    Modeller.setStatusLine(message);
	}
	updateAll(false);
    }

    public String newObjectName() {
	String name = objectNamePrefix + objectNameCounter++;
	if (MoPiXGlobals.objectNamed(name) != null) { // already taken
	    return newObjectName(); // counter has been incremented so will try
	    // a higher number
	} else {
	    return name;
	}
    }

    public void notwaitingForClickOnOTHER() {
	waitingToInstantiateOTHERXML = null;
	waitingToInstantiateOTHER = 0;
	waitingToInstantiateOTHERRecipient = null;
	equationToAddToObject = null;
    }
    
    public void removeTrailAt(int t) {
	if (MoPiXGlobals.allObjects == null)
	    return;
	for (int i = 0; i < MoPiXGlobals.allObjects.length; i++) {
	    MoPiXGlobals.allObjects[i].removeTrailAt(t);
	}
    }

    public String renameAllObjects(String mathML) {
	// finds strings starting with object_ and renumbers them if necessary
	String tempObjectPrefix = "OBJECT_______";
	// shouldn't occur elsewhere and shouldn't be
	// super string of the above
	boolean tempObjectPrefixUsed = false;
	int objectNameMathMLStart = mathML.indexOf(objectNamePrefix);
	int offset = objectNamePrefix.length();
	while (objectNameMathMLStart >= 0) {
	    int objectNameMathMLEnd = mathML.indexOf('<', objectNameMathMLStart
		    + offset);
	    if (objectNameMathMLEnd >= 0) {
		String objectName = mathML.substring(objectNameMathMLStart,
			objectNameMathMLEnd);
		if (MoPiXGlobals.objectNamed(objectName) != null) {
		    String newName = newObjectName();
		    newName = newName.replaceFirst(objectNamePrefix,
			    tempObjectPrefix);
		    mathML = mathML.replaceAll(objectName, newName);
		    tempObjectPrefixUsed = true;
		}
	    }
	    objectNameMathMLStart = mathML.indexOf(objectNamePrefix,
		    objectNameMathMLStart + offset);
	}
	if (tempObjectPrefixUsed) {
	    return mathML.replaceAll(tempObjectPrefix, objectNamePrefix);
	} else {
	    return mathML;
	}
    }

    public void replaceAllShapes() {
	if (MoPiXGlobals.allObjects == null)
	    return;
	for (int i = 0; i < MoPiXGlobals.allObjects.length; i++) {
	    MoPiXGlobals.allObjects[i].replaceShape();
	}
    }

    public String restoreNewLines(String modelDescription) {
	String newModelDescription = modelDescription;
	for (int i = 0; i < builtInTags.length; i++) {
	    newModelDescription = newModelDescription.replaceAll(
		    builtInTags[i], "\r\n" + builtInTags[i]);
	}
	// newModelDescription = newModelDescription.replaceAll("ModifiedOn:",
	// "\r\nModifiedOn:"); // legacy tag not used anymore but should look
	// nice
	return newModelDescription;
    }

    public void runOrStep(int t, int increment, int millisecondDelta) {
	if (animationTimer != null) {
	    animationTimer.cancel();
	}
	setTime(t);
	timeIncrement = increment;
	animationTimer = new AnimationTimer();
	animationTimer.scheduleRepeating(millisecondDelta);
    }

    public void setGridSize(int x, int y) {
	gridSizeX = x;
	gridSizeY = y;
	Equation.unitSize = 40.0 / x; // should revisit this if ever want
	// non-square grid size
	stageWidthDefaultStageCoordinates = stageWidthDefault / gridSizeX;
	stageHeightDefaultStageCoordinates = stageHeightDefault / gridSizeY;
    }
    
    public void setTimeLock(boolean locked) {
	if (locked) {
	    Modeller.setStatusLine(constants.timeLocked());
	    timeTextBox.setReadOnly(true);
	    timeTextBox.setTitle(constants.currentTimeYouMustUnlockThisToChangeIt());
	    timeLockedCheckBox.setTitle(constants.clickToUnlockTime());
	} else {
	    setStatusLine(constants.timeUnlocked());
	    timeTextBox.setReadOnly(false);
	    timeTextBox.setTitle(constants.currentTimeYouCanEditThis());
	    timeLockedCheckBox.setTitle(constants.clickToLockTime());
	}
    }

    public void splitStageAndTabPanel(boolean split) {
	if (split) {
// if !adjustStageToWindowSize then should store current values and use them
// when restoring
	    splitPanel = new SplitLayoutPanel();
	    stagePanel.remove(innerStagePanel);
	    int halfHeight = Window.getClientHeight()/2;
	    splitPanel.addNorth(innerStagePanel, halfHeight);
	    previousConstructionPanelIndex = tabPanel.getWidgetIndex(stagePanel);
	    tabPanel.remove(stagePanel);
	    tabPanel.removeFromParent();
	    splitPanel.addSouth(tabPanel, halfHeight);
	    wholePanel.insert(splitPanel, 0); // on top (before status line)
//	    computeSplitStageGeometry();
	    switchToResourcesPanel();
	} else {
	    innerStagePanel.removeFromParent();
	    stagePanel.add(innerStagePanel);
	    splitPanel.removeFromParent();
	    stagePanel.removeFromParent();
	    tabPanel.insert(stagePanel, constants.stage(), previousConstructionPanelIndex);
	    tabPanel.removeFromParent();
	    wholePanel.insert(tabPanel, 0); // on top (before status line
//	    computeUnsplitStageGeometry();
	    splitPanel = null;
	    switchToConstructionArea();
	}
	replaceAllShapes();
	updateAllObjects(time);
	computeScaleAndDimensions();
    }
    
    public void setTime(int t) {
	time = t;
	timeTextBox.setText(Integer.toString(time));
    }

    protected void stringToNewTIme(String timeString) {
	try {
	    time = Integer.valueOf(timeString).intValue();
	    moveTimeTo(time, null);
	} catch (Exception e) {
	    setStatusLine(constants.restoringTimeSincePreviousValueWasnTANumber());
	    timeTextBox.setText(Integer.toString(time));
	}
    }
    
    @Override
    public void switchToConstructionArea() {
	if (inSplitPanelMode()) {
	    return; //  no need to do anything
	}
	tabPanel.switchTo(stagePanel);
    }
    
    public static boolean tagsMatch(String tags[], String description) {
	for (int i = 0; i < tags.length; i++) {
	    if (!tags[i].isEmpty()) {
		if (description.indexOf(tags[i]) < 0) {
		    return false;
		}
	    }
	}
	return true;
    }

    public void topLevelLoop() {
	if (!temporalNavigationPanel.isAttached()) {
	    innerStagePanel.add(temporalNavigationPanel);
	}
	Equation.clearHistoryAllEquations();
	moveTimeTo(0, null);
	switchToConstructionArea();
    }

    public void updateAll(boolean deferred) {
	if (deferred) {
	    Scheduler.get().scheduleDeferred(updateAllCommand);
	} else {
	    updateAllCommand.execute();
	}
    }
       
//    protected void redisplayAllObjects() {
//	context2d.clearRect(stagePanel.getAbsoluteLeft(), stagePanel.getAbsoluteTop(),
//		            stagePanel.getOffsetWidth(), stagePanel.getOffsetHeight());
//	for (MoPiXObject object : MoPiXGlobals.allObjects) {
//	    object.display(context2d);
//	}
//    }

    public void updateAllObjects(final int t) {
	updateAllObjects(t, null);
    }

    public void updateAllObjects(final int t, String message) {
	if (MoPiXGlobals.allObjects == null)
	    return;
	for (int i = 0; i < MoPiXGlobals.allObjects.length; i++) {
	    MoPiXGlobals.allObjects[i].updateState(t);
	}
	for (int i = time; i > t; i--) {
	    removeTrailAt(i);
	}
	setTime(t);
	if (message != null) {
	    setStatusLine(message); 
// MoPiX2.addToHistory(message); // huh?
	}
    }

    public boolean updateAsMuchAsPossible() {
	return (updateWhileChangingViaUI == UPDATE_AS_MUCH_AS_POSSIBLE);
    }

    public String updateDescription(String modelDescription, String type, boolean alreadyEdited) {
	String createdBy = "CreatedBy: " + getUserName();
	if (modelDescription.indexOf("CreatedBy: ") < 0) {
	    if (alreadyEdited || modelDescription.indexOf(createdBy) < 0) {
		modelDescription += "\r\n" + createdBy;
	    } else {
		// created by someone else
		String modifiedBy = "ModifiedBy: " + getUserName();
		if (modelDescription.indexOf(modifiedBy) < 0) {
		    modelDescription += "\r\n" + modifiedBy;
		}
	    }
	}
	String typeTag = "Type: ";
	if (modelDescription.indexOf(typeTag) < 0) {
	    modelDescription += "\r\n" + typeTag + type;
	};
	String createdOn = "CreatedOn: ";
	String dateString = new Date().toString();
	if (modelDescription.indexOf(createdOn) < 0) {
	    modelDescription += "\r\n" + createdOn + dateString;
	}
	modelDescription += "\r\n" + "LastModifiedOn: " + dateString;
	return modelDescription;
    }

    public boolean updateOnMouseUp() {
	return (updateWhileChangingViaUI == UPDATE_ON_MOUSE_UP || 
		updateWhileChangingViaUI == UPDATE_AS_MUCH_AS_POSSIBLE);
    }
    
    @Override
    protected void windowResized() {
	super.windowResized();
	updateStageDimensions();
    }
    
    public void updateStageDimensions() {
	if (adjustStageToWindowSize) {
	    computeScaleAndDimensions();
	    if (stageGroup != null) {
		if (splitPanel == null) {
		    computeUnsplitStageGeometry();
		} else {
		    computeSplitStageGeometry();
		}
	    } else {
		runArea.setPixelSize(stageWidth, stageHeight);
		runAreaWithExpressions.setPixelSize(Window.getClientWidth() - windowWidthMargins, 
			                            stageHeight + 20);
	    }
	    if (editorPanel != null && editorPanel.isAttached()) {
		editorPanel.setPixelSize(Window.getClientWidth()  - windowWidthMargins, Window.getClientHeight() - windowHeightMargins);
	    }
	    // int buttonCount = temporalNavigationPanel.getWidgetCount();
	    // double minScale = Math.min(1.0, Math.max(0.2, stageScale));
	    // for (int i = 0; i < buttonCount; i++) {
	    // Widget widget = temporalNavigationPanel.getWidget(i);
	    // if (widget instanceof Image) {
	    // Image image = (Image) widget;
	    // image.setWidth((image.getWidth() * minScale) + "px");
	    // image.setHeight((image.getHeight() * minScale) + "px");
	    // // image.setPixelSize((int) Math.round(minScale *
	    // image.getWidth()), (int) Math.round(minScale *
	    // image.getHeight()));
	    // // String scaleString = Math.round(minScale * 100) + "%";
	    // // ((Image) widget).setSize(scaleString, scaleString);
	    // };
	    // };
	}
    }
    
    public boolean useCustomHTMLDefault() {
	return configureForPondTiling || hideEquations;
    }

    public void waitingForClickOnOTHER(String instantiatedXML,
	                               int otherNumber) {
	waitingToInstantiateOTHERXML = instantiatedXML;
	waitingToInstantiateOTHER = otherNumber;
    }

    public void waitingForClickOnOTHER(String instantiatedXML,
	                               int otherNumber, MoPiXObject objectToReceiveEquation) {
	waitingToInstantiateOTHERXML = instantiatedXML;
	waitingToInstantiateOTHER = otherNumber;
	waitingToInstantiateOTHERRecipient = objectToReceiveEquation;
    }
    
    public void saveDialog(Command command, int typeCode, final String mathML) {
	// type can be Model or Object
//	final String type = typeCodeToString(typeCode);
	if (!saveTab.isAttached()) {
	    tabPanel.add(saveTab, constants.savingYourModel());
	}
	AsyncCallback<String[]> callback = new AsyncCallback<String[]>() {

	    @Override
	    public void onFailure(Throwable caught) {
		Modeller.reportException(caught, "In saveXML.");
	    }

	    @Override
	    public void onSuccess(String[] result) {
		String error = result[0];
		if (error != null) {
		    Modeller.addToErrorLog(error);
		} else {
		    String guid = result[1];
		    String html = constants.useThisURLInANewTabToLoadYourModelIntoMoPiX();
		    html += " (" + new Date().toString() + ")";
		    HTML description = new HTML(html);
		    VerticalPanel verticalPanel = new VerticalPanel();
		    verticalPanel.add(description);
		    TextBox textBox = new TextBox();
		    String currentURL = CommonUtils.removeBookmark(Window.Location.getHref());
		    String url = CommonUtils.addAttributeToURL(currentURL, LOAD_MATH_ML, guid);
		    textBox.setText(url);
		    textBox.setSelectionRange(0, url.length());
		    verticalPanel.add(textBox);
		    saveTab.insert(verticalPanel, 0);
		    tabPanel.selectTab(tabPanel.getWidgetIndex(saveTab));
		    textBox.setWidth(tabPanel.getOffsetWidth()-10 + "px");
		    JavaScript.confirmUnload(false);
		}
	    }
	    
	};
	CreateMoPiXResourcePageService.createMoPiXResourcePageService().saveXML(mathML, MoPiX.sessionGuid, MoPiX.userGuid, callback );
    }
    
//    public void saveDialog(Command command, int typeCode, final String mathML) {
//	// type can be Model or Object
//	final String type = typeCodeToString(typeCode);
//	saveTab.clear();
//	if (!saveTab.isAttached()) {
//	    tabPanel.add(saveTab, constants.savingYourModel());
//	}
//	switch (typeCode) {
//	case SAVING_MODEL:
//	    setStatusLine(constants.pleaseTagYourModelBeforeSaving());
//	    break;
//	case SAVING_OBJECT:
//	    setStatusLine(constants.pleaseTagYourObjectBeforeSaving());
//	    break;
//	default:
//	    setStatusLine(constants.pleaseTagYour() + type.toLowerCase() + constants.beforeSaving());
//	}
//	tabPanel.switchTo(saveTab);
//	if (getUserName() == null) {
//	    askUserName(command, saveTab);
//	    return;
//	}
//	final HTML instructions = new HTML(constants.enterFileDescriptionTags());
//	saveTab.add(instructions);
//	final TextArea descriptionTextArea = new TextArea();
//	descriptionTextArea.setText(updateDescription(descriptionOfLastLoadedModel, type, false));
//	descriptionTextArea.setPixelSize(stageWidthDefault, 200);
//	saveTab.add(descriptionTextArea);
//	Button OKButton = new ModellerButton(constants.ok());
//	saveTab.add(OKButton);
//	OKButton.addClickHandler(new ClickHandler() {
//	    @Override
//	    public void onClick(ClickEvent event) {
//		setStatusLine(constants.saving() + type.toLowerCase() + constants.pleaseWait());
//		String descriptionString = 
//		    updateDescription(descriptionTextArea.getText(), type, true);
//		// update again in case user removed some of it
//		String firstLine[] = descriptionString.split("\r\n", 2);
//		getMathDiLSService().insertDDAFile2(firstLine[0],
//			descriptionString, mathML,
//			new AsyncCallback<String>() {
//		    
//			    public void onFailure(Throwable caught) {
//				Modeller.reportException(caught, "In insertDDAFile2.");
//			    }
//
//			    public void onSuccess(String result) {
//				if (result != null) {
//				    setStatusLine(result);
//				    JavaScript.confirmUnload(false);
//				    // no need since model is saved
//				} else {
//				    setStatusLine("Abnormal success signal -- perhaps did not save -- try loading it");
//				}
//				switchToConstructionArea();
//			    }
//			});
//	    };
//	});
//    }

//    private static String typeCodeToString(int typeCode) {
//	switch (typeCode) {
//	case SAVING_MODEL:
//	    return constants.model();
//	case SAVING_OBJECT:
//	    return constants.object();
//	}
//	return null;
//    }

//    public static MathDiLSServiceAsync getMathDiLSService() {
//	if (MathDiLSService == null) {
//	    MathDiLSService = CreateMathDiLSService.createMathDiLSService();
//	}
//	return MathDiLSService;
//    }
    
    public static void computeSplitStageGeometry() {
	int newStageHeight = stageHeight/2+20;
//	stageSurface.setDimensions(stageWidth, newStageHeight);
	int newStageWidth = stageWidth/2-20;
	runArea.setPixelSize(newStageWidth , stageHeight/2);
	expressionStagePanelScrollable.setPixelSize(minimumExpressionPanelWidth - 60, newStageHeight);
	int windowWidth = Window.getClientWidth();
	runAreaWithExpressions.setPixelSize(windowWidth-60, newStageHeight);
	runAreaWithExpressions.setSplitPosition(newStageWidth+20 + "px");
	int clientHeight = Window.getClientHeight();
	tabPanel.setPixelSize(windowWidth-70, clientHeight - newStageHeight);
	stageGroup.setScale(0.5*stageScale, 0.5*stageScale);
	expressionStagePanelScrollable.setPixelSize(minimumExpressionPanelWidth, stageHeight / 2);
	// subtract 40 and 20 below to avoid outer scroll bars -- not clear
	// how to compute the overhead of tabs, etc.
	splitPanel.setWidth(windowWidth - 40 + "px"); 
	splitPanel.setHeight(clientHeight - 20 + "px");
//	splitPanel.setSplitPosition(splitScreenCheckBox.getAbsoluteTop() + 16 +
//                                    splitScreenCheckBox.getOffsetHeight() + "px");
    }
    
    public static void computeUnsplitStageGeometry() {
//	stageSurface.setDimensions(stageWidth, stageHeight);
	runArea.setPixelSize(stageWidth, stageHeight);
	int windowWidth = Window.getClientWidth();
	runAreaWithExpressions.setPixelSize(windowWidth-20, stageHeight);
	stageGroup.setScale(stageScale, stageScale);
	runAreaWithExpressions.setSplitPosition(stageWidth+20 + "px");
	int excessWidth = 
	    Math.max(0, windowWidth - stageWidth - minimumExpressionPanelWidth - windowWidthMargins);
	expressionStagePanelScrollable.setPixelSize(minimumExpressionPanelWidth+excessWidth-30,
		                                    stageHeight-20);
	runAreaWithExpressions.setPixelSize(windowWidth-windowWidthMargins, stageHeight+20);
    }
    
    public void computeScaleAndDimensions() {
	double xScale = 
	    ((double) Window.getClientWidth() - windowWidthMargins - minimumExpressionPanelWidth)
	    / stageWidthDefault;
	double yScale = 
	    ((double) Window.getClientHeight() - windowHeightMargins)
	    / stageHeightDefault;
	stageScale = Math.min(1.0, Math.min(xScale, yScale));
	stageWidth = (int) Math.round(stageScale * stageWidthDefault);
	stageHeight = (int) Math.round(stageScale * stageHeightDefault);
	// when scale is 1.0 this doesn't work right in the hosted mode browser
	// but since it works when compiled no big deal
	recomputeBrowsePanelHeights();
	computeSplitPanelGeometry();
    }
    
    @Override
    public void undoUpTo(
	    int fromHistoryItemIndex, int toHistoryItemIndex,
            boolean record, 
            boolean justRecord,
            ReconstructEventsContinutation continuation) {
	super.undoUpTo(fromHistoryItemIndex, toHistoryItemIndex, record, justRecord, continuation);
	if (MoPiXGlobals.allObjects != null) {
	    updateAll(true);
	}   
    }
    
    @Override
    protected void addModel(String data, boolean removeOld) {
	loadModelString(data, !removeOld, Modeller.constants.modelFromAResourcePage());
    }
    
    public static MoPiX instance() {
	return (MoPiX) INSTANCE;
    }
    
    @Override
    public void widgetSelected(Widget widgetSelected) {
	if (widgetSelected == stagePanel) {
	    // otherwise FireFox computes it too early and the split is on the far left
	    updateStageDimensions();
	}	
    }
    
    @Override
    public void historyReconstructed()  {
	topLevelLoop();
    }
    
    @Override
    public void reconstructEvent(String tag,
	    Element eventElement,
	    boolean restoringHistory,
	    int whatToIgnore,
	    boolean copyOnUpdate,
	    ReconstructEventsContinutation continuation, 
	    int version,
	    String name) {
	if (tag.equals("AddEquationToObjectEvent")) {
	    AddEquationToObjectEvent.reconstruct(name, eventElement, restoringHistory, version, continuation);
	} else if (tag.equals("AddObjectEvent")) {
	    AddObjectEvent.reconstruct(name, eventElement, restoringHistory, version, continuation); 
	} else if (tag.equals("ChangeExpressionAppearanceEvent")) {
	    ChangeExpressionAppearanceEvent.reconstruct(name, eventElement, restoringHistory, version, continuation);
	} else if (tag.equals("RemoveObjectEvent")) {
	    RemoveObjectEvent.reconstruct(name, eventElement, restoringHistory, version, continuation); 
	    // TODO: move the handling of undo and redo up from BehaviourComposer to share with MoPiX
	} else {
	    if (!tag.equals("StartEvent") && !tag.equals("undo") && !tag.equals("redo")) {
		Modeller.setStatusLine("This configuration of the Modeller does not recognise the following event: " + tag);
	    }
	}
    }
    
    public static String getUserName() {
        return userName;
    }

    public static double getTrueStageScale() {
        if (Modeller.splitPanel != null) {
            return 0.5 * stageScale;
        } else {
            return stageScale;
        }
    }

    public static void addToExpressionPalette(Widget w) {
	expressionStagePanel.insert(w, 0); // most recent on top
    }

    public static void askUserName(final Command command, Panel parentPanel) {
	final VerticalPanel loginPanel = new VerticalPanel();
	final TextBox userNameTextBox = new TextBox();
	loginPanel.add(userNameTextBox);
	loginPanel.add(new Label(constants.pleaseEnterYourUserNameOrEmailAddress()));
	parentPanel.add(loginPanel);
	// tabPanel.switchTo(loginPanel);
	userNameTextBox.addChangeHandler(new ChangeHandler() {
	    @Override
	    public void onChange(ChangeEvent event) {
		userName = userNameTextBox.getText();
		loginPanel.removeFromParent();
		// MoPiX2.tabPanel.switchTo(MoPiX2.loadTab);
		command.execute();
	    }

	});
	userNameTextBox.addKeyDownHandler(new KeyDownHandler() {
	    @Override
	    public void onKeyDown(KeyDownEvent event) {
		if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
		    userName = userNameTextBox.getText();
		    loginPanel.removeFromParent();
		    // MoPiX2.tabPanel.switchTo(MoPiX2.loadTab);
		    command.execute();
		}
		
	    };
	});
    }
    
    public static boolean startsWithBuiltInTag(String line) {
	for (int i = 0; i < builtInTags.length; i++) {
	    if (line.startsWith(builtInTags[i]))
		return true;
	}
	return false;
    }
    
    @Override
    public String textFontToMatchIcons(String html) {
	if (configureForPondTiling) {
	    return "<div style='float:left;height:18px'><b><font size='5'>"
		    + html + "</font></b></div>";
	} else {
	    return super.textFontToMatchIcons(html);
	}
    }
    
    public static String getAllXML(boolean includingCustomHTML) {
	StringBuilder buffer = new StringBuilder("<apply><and/>\r\n");
	if (MoPiXGlobals.allObjects != null) {
	    for (int i = 0; i < MoPiXGlobals.allObjects.length; i++) {
		MoPiXGlobals.allObjects[i].addXML(buffer, includingCustomHTML);
	    }
	}
	buffer.append(MoPiXGlobals.xmlOfContants());
	buffer.append("</apply>");
	return buffer.toString();
    }
    
    @Override
    public boolean recordingHistoryInDatabase() {
	// for now at least
	return false;
    }
    
    @Override
    public boolean processNonGenericCodeElement(
	    com.google.gwt.dom.client.Element codeElement, 
	    String innerHTML, 
	    BrowsePanel browsePanel,
	    String id,
	    Command command) {
	String mathML = codeElement.getAttribute("equationMathML");
	if (mathML != null && !mathML.isEmpty()) {
	    String replaceAppearance = codeElement.getAttribute("replaceAppearance");
	    Equation equation = null;
	    EquationAppearance equationAppearance = null;
	    if (replaceAppearance != null && !replaceAppearance.isEmpty() &&
		    replaceAppearance.equalsIgnoreCase("equation")) {
		equation = new Equation(mathML, null);
		equationAppearance = new EquationAppearance(equation);
	    } else {
		mathML = mathML.replaceFirst("<apply>",
			"<apply HTMLAppearance='" + Utils.encode(innerHTML) + "'>");
		equation = new Equation(mathML, null);
		equationAppearance = new EquationAppearance(equation, innerHTML);
	    }
	    equation.setAppearance(equationAppearance);
	    equationAppearance.setElementID(id);
	    browsePanel.replaceElementWithWidget(id, codeElement, equationAppearance);
	    return true;
	}
	mathML = codeElement.getAttribute("modelMathML");
	if (mathML != null && !mathML.isEmpty()) {
	    ModelLoader modelLoader = new ModelLoader(innerHTML, mathML, ModelLoader.MOPIX_MODEL);
	    modelLoader.setTitle(Modeller.constants.clickToAddThisModel());
	    browsePanel.replaceElementWithWidget(id, codeElement, modelLoader);
	    return true;
	} 
	return false;
    }
    
    @Override
    public ModellerTabPanel getMainTabPanel() {
	return resourcesTabPanel;
    }
    
    @Override
    public boolean needToConfirmUnLoad() {
	return true;
    }
    
    @Override
    public boolean isGetLinksPanelToBeAdded() {
	return false;
    }
    
    @Override
    public boolean isSplitScreenCheckBoxToBeAdded() {
	return false;
    }
    
    @Override
    protected String getLocalizedFolderName() {
	if (locale != null && locale.equals("el")) {
	    return "el";
	} else {
	    return "en";
	}
    }

}
