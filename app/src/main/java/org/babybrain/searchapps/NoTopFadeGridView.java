package org.babybrain.searchapps;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class NoTopFadeGridView extends GridView {
/*    @Override
    protected float getTopFadingEdgeStrength() {
        return 0.0f;
    }
*/
    public NoTopFadeGridView(Context context) {
        super(context);
    }

    public NoTopFadeGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoTopFadeGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}