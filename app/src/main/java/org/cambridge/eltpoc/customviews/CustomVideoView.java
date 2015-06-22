package org.cambridge.eltpoc.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by etorres on 6/18/15.
 */
public class CustomVideoView extends VideoView {

    private int mForceHeight = 0;
    private int mForceWidth = 0;
    public CustomVideoView(Context context) {
        super(context);
    }

    public CustomVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setDimensions(int w, int h) {
        this.mForceHeight = h;
        this.mForceWidth = w;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mForceWidth, mForceHeight);
    }
}
