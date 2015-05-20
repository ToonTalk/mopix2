package uk.ac.lkl.client.mopix;

import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;

import uk.ac.lkl.client.mopix.expression.Expression;

public class ConstantExpressionAppearance extends ExpressionAppearance {
    ConstantExpressionAppearance(Expression expression) {
	super(expression);
    }

    protected MenuItem addStandardExpressionMenuItems(
	    final PopupPanel popupMenu,
	    final ExpressionAppearance thisExpression, MenuBar menu) {
	// do nothing since the other menu items don't apply
	return addArithmeticMenuOptions(popupMenu, thisExpression, menu);
    }
}
