package maciej01.soft.niezbednikkreglarza;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;

/**
 * Created by Maciej on 2017-04-13.
 */

public class ScrollAwareFABBehavior extends FloatingActionButton.Behavior{
    // this fixes the stupid Rect should intersect with child's bounds. error
    // i thought it was something wrong with my code but apparently it's the library's fault
    public ScrollAwareFABBehavior() {
        super();
    }

    public ScrollAwareFABBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public boolean getInsetDodgeRect(@NonNull CoordinatorLayout parent, @NonNull FloatingActionButton child, @NonNull Rect rect) {
        super.getInsetDodgeRect(parent, child, rect);
        return false;
    }
}
