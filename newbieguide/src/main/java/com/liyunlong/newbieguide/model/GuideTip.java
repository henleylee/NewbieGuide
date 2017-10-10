package com.liyunlong.newbieguide.model;

import android.view.View;
import android.widget.RelativeLayout;

/**
 * 引导信息
 *
 * @author liyunlong
 * @date 2017/9/28 14:48
 */
public class GuideTip {

    public final static int CENTER = Integer.MAX_VALUE;
    public View guideView;
    public int offsetX = CENTER;
    public int offsetY = CENTER;
    public int[] viewIds;
    public RelativeLayout.LayoutParams layoutParams;

    public GuideTip(View guideView) {
        this(guideView, 0, 0);
    }

    public GuideTip(View guideView, int offsetX, int offsetY) {
        this(guideView, offsetX, offsetY, null);
    }

    public GuideTip(View guideView, int[] viewIds) {
        this(guideView, 0, 0, viewIds, null);
    }

    public GuideTip(View guideView, int[] viewIds, RelativeLayout.LayoutParams layoutParams) {
        this(guideView, 0, 0, viewIds, layoutParams);
    }

    public GuideTip(View guideView, int offsetX, int offsetY, RelativeLayout.LayoutParams layoutParams) {
        this(guideView, offsetX, offsetY, null, layoutParams);
    }

    public GuideTip(View guideView, int offsetX, int offsetY, int[] viewIds, RelativeLayout.LayoutParams layoutParams) {
        this.guideView = guideView;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.viewIds = viewIds;
        this.layoutParams = layoutParams;
    }
}
