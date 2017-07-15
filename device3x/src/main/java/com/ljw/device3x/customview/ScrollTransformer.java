package com.ljw.device3x.customview;

import android.view.View;

/**
 * ljw：Administrator on 2017/6/21 0021 11:56
 */
public class ScrollTransformer extends ABaseTransformer {
/*
	@Override
	protected void onTransform(View view, float position) {
		if (position <=0f){
			view.setVisibility(View.VISIBLE);
			view.setPivotX(position < 0f ? view.getWidth() : 0f);
			view.setPivotY(view.getHeight() * 0.5f);
			view.setRotationY(90f * position);
			if(position == -1){
				view.setVisibility(View.GONE);
			}
		}else if (position <=1f){
			view.setVisibility(View.VISIBLE);
			view.setPivotX(position < 0f ? view.getWidth() : 0f);
			view.setPivotY(view.getHeight() * 0.5f);
			view.setRotationY(90f * position);
			if(position == 1){
				view.setVisibility(View.GONE);
			}
		}

	}*/



    /**
     * 滚筒动画
     * @param view
     * @param position
     *            Position of page relative to the current front-and-center position of the pager. 0 is front and
     */
    @Override
    protected void onTransform(View view, float position) {
        if (position <= 0f) {
            view.setTranslationX(view.getWidth() * -position);
            view.setPivotX(view.getWidth()*0.5f);
            view.setPivotY(view.getHeight()*0.5f);
            view.setRotationY(90.0f * position);
            view.setAlpha(1 + position);
        } else if (position <= 1f) {
            view.setTranslationX(view.getWidth() * -position);
            view.setPivotX(view.getWidth() * 0.5f);
            view.setPivotY(view.getHeight() * 0.5f);
            view.setRotationY(90.0f * position);
            view.setAlpha(1 - position);
        }
    }


    private static final float MIN_SCALE = 0.75f;
/*
	@Override
	protected void onTransform(View view, float position) {
		if (position <= 0f) {
			view.setTranslationX(0f);
			view.setScaleX(1f);
			view.setScaleY(1f);
		} else if (position <= 1f) {
		//	view.setVisibility(View.VISIBLE);
			final float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
			view.setAlpha(1 - position);
			view.setPivotY(0.5f * view.getHeight());
			view.setTranslationX(view.getWidth() * -position);
			view.setScaleX(scaleFactor);
			view.setScaleY(scaleFactor);
			//修复一个bug，viewPage后一个界面会与当前页重叠，当淡出时候，需要去使能
			if(position == 1){
		//		view.setVisibility(View.GONE);
			}
		}
	}*/

    @Override
    protected boolean isPagingEnabled() {
        return true;
    }


}
