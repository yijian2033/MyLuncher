package com.ljw.device3x.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.GridView;

import com.ljw.device3x.Activity.Level1_Fragment;

import java.util.List;

public class pagerAdapter extends PagerAdapter {
    private List<GridView> array;

    /**
     * ���ⲿ���ã�new���ķ���
     *
     * @param imageViews ��ӵ����ж���
     * @param context    ������
     */
    public pagerAdapter(Level1_Fragment context, List<GridView> array) {
        this.array = array;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return array.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        // TODO Auto-generated method stub
        return arg0 == arg1;
    }

    @Override
    public Object instantiateItem(View arg0, int arg1) {
        ((ViewPager) arg0).addView(array.get(arg1));
        return array.get(arg1);
    }


    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView((View) arg2);
    }

}
