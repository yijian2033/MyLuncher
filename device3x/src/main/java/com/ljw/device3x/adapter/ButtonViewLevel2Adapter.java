package com.ljw.device3x.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ljw.device3x.R;
import com.ljw.device3x.customview.ImageButton;
import com.ljw.device3x.customview.ImageLevel2Button;

/**
 * Created by Administrator on 2016/5/26 0026.
 */
public class ButtonViewLevel2Adapter extends BaseAdapter {
    private Context mContext;
    private int[] resourceArray;
    private int[] textViewArray;
    private LayoutInflater mLayoutInflater;
    public static final int APP_PAGE_SIZE = 12;//装载的数据大小

    private final class ViewHolder {
        public ImageLevel2Button ib;
    }

    public ButtonViewLevel2Adapter(Context mContext, int[] resource, int[] text) {
        this.mContext = mContext;
        this.resourceArray = resource;
        this.textViewArray = text;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return APP_PAGE_SIZE;
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
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.from(mContext).inflate(R.layout.activity_level2_item, parent, false);
            viewHolder.ib = (ImageLevel2Button) convertView.findViewById(R.id.define_imagebutton);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();

        /**
         * 先计算正确的position
         */
        viewHolder.ib.setImageResource(resourceArray[position], textViewArray[position]);


        return convertView;
    }
}
