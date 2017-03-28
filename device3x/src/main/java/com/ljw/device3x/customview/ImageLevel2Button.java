package com.ljw.device3x.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ljw.device3x.R;

/**
 * Created by Administrator on 2016/5/26 0026.
 */
public class ImageLevel2Button extends LinearLayout{
    public ImageView imageView;
    public TextView textView;
    private Context mContext;

    public ImageLevel2Button(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        inflater.inflate(R.layout.level2_button_item, this);
        imageView = (ImageView) findViewById(R.id.imageView3);
        textView = (TextView) findViewById(R.id.imageView4);
    }

    /**
     * 设置图片
     */
    public void setImageResource(int imageId, int textId) {
        imageView.setImageResource(imageId);
        textView.setText(mContext.getResources().getText(textId).toString());
    }
}
