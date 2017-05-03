/*
 * Copyright 2014 Toxic Bakery
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ljw.device3x.customview;

import android.view.View;

public class CubeOutTransformer extends ABaseTransformer {
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
	@Override
	protected void onTransform(View view, float position) {

		view.setPivotX(position < 0f ? view.getWidth() : 0f);
		if(position <= -0.9){
			view.setPivotX(view.getWidth() * 0.5f);
		}
		if(position >= 0.9){
			view.setPivotX(view.getWidth() * 0.5f);
		}
		view.setPivotY(view.getHeight() * 0.5f);
		view.setRotationY(90f * position);
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
