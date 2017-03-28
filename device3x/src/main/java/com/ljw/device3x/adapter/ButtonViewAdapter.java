package com.ljw.device3x.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ljw.device3x.R;
import com.ljw.device3x.customview.ImageButton;


/**
 * Created by Administrator on 2016/4/25 0025.
 */
public class ButtonViewAdapter extends BaseAdapter {
    private Context mContext;
    private int[] resourceArray;
    private int[] textViewArray;
    private LayoutInflater mLayoutInflater;
    public static final int APP_PAGE1_SIZE = 3;//第一页装载的数据大小
    public static final int APP_PAGE2_SIZE = 5;//第二页装载的数据大小
    private int mIndex;//页数下标，从0开始

    private final class ViewHolder {
        public ImageButton ib;
    }

    public ButtonViewAdapter(Context mContext, int[] resource, int[] text, int page) {
        this.mContext = mContext;
        this.resourceArray = resource;
        this.textViewArray = text;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.mIndex = page;
    }

    @Override
    public int getCount() {
        return mIndex > 0 ? APP_PAGE2_SIZE : APP_PAGE1_SIZE;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.from(mContext).inflate(R.layout.activity_item, parent, false);
            viewHolder.ib = (ImageButton)convertView.findViewById(R.id.define_iamgebutton);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder)convertView.getTag();

        /**
         * 先计算正确的position
         */
        viewHolder.ib.setImageResource(resourceArray[position], textViewArray[position]);

        viewHolder.ib.setTag(viewHolder);
        return convertView;
    }
}
