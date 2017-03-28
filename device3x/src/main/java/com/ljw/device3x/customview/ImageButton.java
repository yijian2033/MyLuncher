package com.ljw.device3x.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ljw.device3x.R;


public class ImageButton extends LinearLayout {
    public ImageView imageView;
    public TextView textView;
    private Context mContext;

    public ImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.imagebutton_item, this);
        imageView = (ImageView) findViewById(R.id.imageView1);
        textView = (TextView) findViewById(R.id.imageView2);
        mContext = context;
    }

    /**
     * 设置图片
     */
    public void setImageResource(int imageId, int textId) {
        imageView.setImageResource(imageId);
        textView.setText(mContext.getResources().getText(textId).toString());
    }

}
