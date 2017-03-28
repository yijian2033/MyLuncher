package com.ljw.device3x.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

/**
 * Created by Administrator on 2016/8/23 0023.
 */
public class ReversedSeekBar extends SeekBar {

    public ReversedSeekBar(Context context) {
        super(context);
    }

    public ReversedSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float px = this.getWidth() / 2.0f;
        float py = this.getHeight() / 2.0f;
        canvas.save();
        canvas.rotate(180, px, py);
        super.onDraw(canvas);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            setPressed(false);
        }
        event.setLocation(this.getWidth() - event.getX(), event.getY());
        return super.onTouchEvent(event);
    }
}
