package com.henley.newbieguide.model;

import android.graphics.RectF;
import android.view.View;

import com.henley.newbieguide.ShapeType;

/**
 * 高亮信息
 *
 * @author Henley
 * @date 2017/9/28 14:33
 */
public class HighLight {

    private View mHightLightView;
    private ShapeType mShapeType;
    private int mRoundCorners;

    public HighLight(View hightLight, ShapeType type) {
        this.mHightLightView = hightLight;
        this.mShapeType = type;
    }

    public View getHightLightView() {
        return mHightLightView;
    }

    public ShapeType getShapeType() {
        return mShapeType;
    }

    public int getRoundCorners() {
        return mRoundCorners;
    }

    public void setRoundCorners(int roundCorners) {
        this.mRoundCorners = roundCorners;
    }

    public int getRadius() {
        return mHightLightView != null ? Math.max(mHightLightView.getWidth() / 2, mHightLightView.getHeight() / 2) : 0;
    }

    public RectF getRectF() {
        RectF rectF = new RectF();
        if (mHightLightView != null) {
            int[] location = new int[2];
            mHightLightView.getLocationOnScreen(location);
            rectF.left = location[0];
            rectF.top = location[1];
            rectF.right = location[0] + mHightLightView.getWidth();
            rectF.bottom = location[1] + mHightLightView.getHeight();
        }
        return rectF;
    }

}
