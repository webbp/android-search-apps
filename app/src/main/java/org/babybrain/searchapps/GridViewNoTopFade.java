package org.babybrain.searchapps;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class GridViewNoTopFade extends GridView {
/*    @Override
    protected float getTopFadingEdgeStrength() {
        return 0.0f;
    }
*/
    public GridViewNoTopFade(Context context) {
        super(context);
    }

    public GridViewNoTopFade(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridViewNoTopFade(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}