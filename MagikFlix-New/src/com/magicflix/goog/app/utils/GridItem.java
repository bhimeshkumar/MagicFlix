package com.magicflix.goog.app.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.magicflix.goog.R;

public class GridItem extends RelativeLayout implements Checkable {

    private Context mContext;
    private boolean mChecked;
    private ImageView mImgView = null;
    private ImageView mSelcetView = null;

    public GridItem(Context context) {
        this(context, null, 0);
    }

    public GridItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GridItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.video_grid_item, this);
        mImgView = (ImageView) findViewById(R.id.video_grid_iv);
        mSelcetView = (ImageView) findViewById(R.id.video_grid_iv_select);
    }

    @Override
    public void setChecked(boolean checked) {
        mChecked = checked;
        setBackground(checked ? getResources().getDrawable(
                R.drawable.border_stars_local_movie) : null);
        mSelcetView.setVisibility(checked ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    public void setImgResId(Bitmap bitmap) {
        if (mImgView != null) {
            mImgView.setImageBitmap(bitmap);
        }
    }

}
