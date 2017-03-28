package me.kaelaela.verticalviewpager;

/**
 * Copyright (C) 2015 Kaelaela
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import me.kaelaela.verticalviewpager.transforms.DefaultTransformer;

public class VerticalViewPager extends ViewPager {
    double downX = 0,downY=0;
    double lastX = 0, lastY = 0;

    public VerticalViewPager(Context context) {
        this(context, null);
    }

    public VerticalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPageTransformer(false, new DefaultTransformer());
    }

    private MotionEvent swapTouchEvent(MotionEvent event) {
        float width = getWidth();
        float height = getHeight();

        float swappedX = (event.getY() / height) * width;
        float swappedY = (event.getX() / width) * height;

        event.setLocation(swappedX, swappedY);

        return event;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean intercept = super.onInterceptTouchEvent(swapTouchEvent(event));
        downX = event.getRawX();
        downY = event.getRawY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                lastX = downX;
                lastY = downY;
            }

            case MotionEvent.ACTION_MOVE:{

                double moveX = downX - lastX;
                double absmoveX = Math.abs(downX - lastX);
                double absmoveY = Math.abs(downY - lastY);

                if(absmoveX != 0.000 && absmoveY != 0.000) {
                    Log.i("ljwtest:", "X轴滑动的距离" + absmoveX);
                    Log.i("ljwtest:", "Y轴滑动的距离" + absmoveY);
                }

                if(absmoveX > absmoveY && (absmoveX - absmoveY >= 1.00)) {
                    Log.i("ljwtest:", "左右滑时位移差是" + (absmoveX - absmoveY));
//                    return super.onInterceptTouchEvent(event);
                    return false;
                } else if(absmoveY > absmoveX) {
                    Log.i("ljwtest:", "上下滑时位移差是" + (absmoveX - absmoveY));
                    return true;
                }

            }
        }
        //If not intercept, touch event should not be swapped.
        swapTouchEvent(event);
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(swapTouchEvent(ev));
    }

}
