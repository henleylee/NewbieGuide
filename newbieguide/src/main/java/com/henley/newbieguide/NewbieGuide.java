package com.henley.newbieguide;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.henley.newbieguide.model.GuideTip;
import com.henley.newbieguide.model.HighLight;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * 引导提示辅助类
 *
 * @author Henley
 * @date 2017/9/28 14:56
 */
public class NewbieGuide {

    private static final String TAG = "NewbieGuide";
    private static final String PREFERENCES_NAME = "newbie_guide";
    private Context mContext;
    private List<HighLight> highLights;
    private List<GuideTip> guideTips;
    private String label;
    private boolean alwaysShow;
    private int mBackgroundColor;
    private boolean isAnyWhereCancelable;
    private boolean performHighLightClick;
    private OnGuideChangedListener onGuideChangedListener;
    private SharedPreferences mPreferences;
    private FrameLayout mParentView;
    private GuideLayout mGuideLayout;
    private View.OnClickListener dismissListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };

    public static Builder with(Context context) {
        return new Builder(context);
    }

    private NewbieGuide(Builder builder) {
        this.mContext = builder.mContext;
        this.highLights = builder.highLights;
        this.guideTips = builder.guideTips;
        this.label = builder.label;
        this.alwaysShow = builder.alwaysShow;
        this.mBackgroundColor = builder.mBackgroundColor;
        this.isAnyWhereCancelable = builder.isAnyWhereCancelable;
        this.performHighLightClick = builder.performHighLightClick;
        this.onGuideChangedListener = builder.onGuideChangedListener;
        init();
    }

    private void init() {
        if (!alwaysShow && (TextUtils.isEmpty(label) || TextUtils.isEmpty(label.trim()))) {
            throw new IllegalArgumentException("The label is null or empty.");
        }
        mPreferences = mContext.getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        if (mContext != null && mContext instanceof Activity) {
            mParentView = (FrameLayout) ((Activity) mContext).getWindow().getDecorView();
        }
    }

    /**
     * 重置指定{@code label}对应的引导提示的显示状态
     */
    public void resetLabel(String label) {
        if (!TextUtils.isEmpty(label) && !TextUtils.isEmpty(label.trim())) {
            mPreferences.edit().putBoolean(label, false).apply();
        }
    }

    /**
     * 判断引导提示是否正在显示
     */
    public boolean isShowing() {
        return mGuideLayout != null && mParentView.indexOfChild(mGuideLayout) != -1;
    }

    /**
     * 取消引导提示
     */
    public void dismiss() {
        if (isShowing()) {
            mParentView.removeView(mGuideLayout);
            if (!alwaysShow) {
                mPreferences.edit().putBoolean(label, true).apply();
            }
            if (onGuideChangedListener != null) {
                onGuideChangedListener.onDismiss();
            }
        }
    }

    /**
     * 显示引导提示
     */
    public boolean show() {
        if (!alwaysShow && mPreferences.getBoolean(label, false)) {
            return false;
        }
        if (mGuideLayout == null) {
            mGuideLayout = creatGuideLayout();
            initGuideTip();
            initTouchEvent();
        }
        mParentView.addView(mGuideLayout, new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        if (onGuideChangedListener != null) {
            onGuideChangedListener.onShow();
        }
        return true;
    }

    private GuideLayout creatGuideLayout() {
        GuideLayout guideLayout = new GuideLayout(mContext);
        guideLayout.setHighLights(highLights);
        if (mBackgroundColor != 0) {
            guideLayout.setBackgroundColor(mBackgroundColor);
        }
        return guideLayout;
    }

    private void initGuideTip() {
        if (guideTips != null) {
            for (GuideTip guideTip : guideTips) {
                int[] viewIds = guideTip.viewIds;
                final View guideView = guideTip.guideView;
                if (viewIds != null && viewIds.length > 0) {
                    for (int viewId : viewIds) {
                        View view = guideView.findViewById(viewId);
                        if (view != null) {
                            view.setOnClickListener(dismissListener);
                        }
                    }
                }
                mGuideLayout.addView(guideView, guideTip.offsetX, guideTip.offsetY, guideTip.layoutParams);
            }
        }
    }


    private void initTouchEvent() {
        if (isAnyWhereCancelable || performHighLightClick) {
            mGuideLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_UP:
                            if (highLights != null && !highLights.isEmpty()) {
                                for (HighLight highLight : highLights) {
                                    final View view = highLight.getHightLightView();
                                    // 如果点击事件作用在该View上
                                    if (view != null && inRangeOfView(view, event)) {
                                        dismiss();
                                        if (performHighLightClick) {
                                            view.performClick();
                                            if (onGuideChangedListener != null) {
                                                onGuideChangedListener.onHeightlightViewClick(view);
                                            }
                                        }
                                        break;
                                    } else if (isAnyWhereCancelable) {
                                        dismiss();
                                        break;
                                    }
                                }
                                return false;
                            } else {
                                dismiss();
                                return false;
                            }
                        default:
                            break;
                    }
                    return true;
                }
            });
        }
    }

    /**
     * 点击的位置是否在View中
     */
    private boolean inRangeOfView(View view, MotionEvent ev) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int bottom = top + view.getHeight();
        int right = left + view.getWidth();
        return ev.getX() >= left && ev.getX() <= right && ev.getY() >= top && ev.getY() <= bottom;
    }

    public static class Builder {
        private Context mContext;
        private List<HighLight> highLights;
        private List<GuideTip> guideTips;
        private String label;
        private boolean alwaysShow;
        private int mBackgroundColor;
        private boolean isAnyWhereCancelable = true;
        private boolean performHighLightClick;
        private OnGuideChangedListener onGuideChangedListener;
        private LayoutInflater inflater;

        private Builder(Context context) {
            this.mContext = context;
            this.highLights = new ArrayList<>();
            this.guideTips = new ArrayList<>();
        }

        /**
         * 添加需要高亮的View(高亮类型为矩形)
         */
        public Builder addHighLight(View hightLight) {
            return addHighLight(hightLight, ShapeType.RECTANGLE, 0);
        }

        /**
         * 添加需要高亮的View
         *
         * @param hightLight 需要高亮的View
         * @param shapeType  高亮类型(圆形，椭圆，矩形，圆角矩形)
         */
        public Builder addHighLight(View hightLight, ShapeType shapeType) {
            return addHighLight(hightLight, shapeType, 0);
        }

        /**
         * 添加需要高亮的View
         *
         * @param hightLight   需要高亮的view
         * @param type         高亮类型(圆形、椭圆、矩形、圆角矩形)
         * @param roundCorners 圆角尺寸(单位:px)
         */
        public Builder addHighLight(View hightLight, ShapeType type, int roundCorners) {
            HighLight highLight = new HighLight(hightLight, type);
            if (roundCorners > 0) {
                highLight.setRoundCorners(roundCorners);
            }
            highLights.add(highLight);
            return this;
        }

        /**
         * 添加需要高亮的View集合
         *
         * @param highLights 需要高亮的View集合
         */
        public Builder addHighLights(List<HighLight> highLights) {
            this.highLights.addAll(highLights);
            return this;
        }

        /**
         * 添加引导提示布局资源ID
         *
         * @param guideResID 引导提示布局资源ID
         */
        public Builder addGuideTip(@LayoutRes int guideResID) {
            this.guideTips.add(new GuideTip(inflate(guideResID)));
            return this;
        }

        /**
         * 添加引导提示布局
         *
         * @param guideView 引导提示布局
         */
        public Builder addGuideTip(View guideView) {
            this.guideTips.add(new GuideTip(guideView));
            return this;
        }

        /**
         * 添加引导提示布局资源ID
         *
         * @param guideResID 引导提示布局资源ID
         * @param viewIds    响应点击取消引导提示布局的View资源ID
         */
        public Builder addGuideTip(@LayoutRes int guideResID, @IdRes int... viewIds) {
            this.guideTips.add(new GuideTip(inflate(guideResID), viewIds));
            return this;
        }

        /**
         * 添加引导提示布局
         *
         * @param guideView 引导提示布局
         * @param viewIds   响应点击取消引导提示布局的View资源ID
         */
        public Builder addGuideTip(View guideView, @IdRes int... viewIds) {
            this.guideTips.add(new GuideTip(guideView, viewIds));
            return this;
        }

        /**
         * 添加引导提示布局资源ID
         *
         * @param guideResID   引导提示布局资源ID
         * @param layoutParams 引导提示布局参数
         * @param viewIds      响应点击取消引导提示布局的View资源ID
         */
        public Builder addGuideTip(@LayoutRes int guideResID, RelativeLayout.LayoutParams layoutParams, @IdRes int... viewIds) {
            this.guideTips.add(new GuideTip(inflate(guideResID), viewIds, layoutParams));
            return this;
        }

        /**
         * 添加引导提示布局
         *
         * @param guideView    引导提示布局
         * @param layoutParams 引导提示布局参数
         * @param viewIds      响应点击取消引导提示布局的View资源ID
         */
        public Builder addGuideTip(View guideView, RelativeLayout.LayoutParams layoutParams, @IdRes int... viewIds) {
            this.guideTips.add(new GuideTip(guideView, viewIds, layoutParams));
            return this;
        }

        /**
         * 添加引导提示布局资源ID
         *
         * @param guideResID 引导提示布局资源ID
         * @param offsetX    X轴偏移(正数表示从布局的左侧往右偏移量，负数表示从布局的右侧往左偏移量，{@link GuideTip#CENTER}表示居中)
         * @param offsetY    Y轴偏移(正数表示从布局的上侧往下偏移量，负数表示从布局的下侧往上偏移量，{@link GuideTip#CENTER}表示居中)
         */
        public Builder addGuideTip(@LayoutRes int guideResID, int offsetX, int offsetY) {
            this.guideTips.add(new GuideTip(inflate(guideResID), offsetX, offsetY));
            return this;
        }

        /**
         * 添加引导提示布局
         *
         * @param guideView 引导提示布局
         * @param offsetX   X轴偏移(正数表示从布局的左侧往右偏移量，负数表示从布局的右侧往左偏移量，{@link GuideTip#CENTER}表示居中)
         * @param offsetY   Y轴偏移(正数表示从布局的上侧往下偏移量，负数表示从布局的下侧往上偏移量，{@link GuideTip#CENTER}表示居中)
         */
        public Builder addGuideTip(View guideView, int offsetX, int offsetY) {
            this.guideTips.add(new GuideTip(guideView, offsetX, offsetY));
            return this;
        }

        /**
         * 添加引导提示布局资源ID
         *
         * @param guideResID   引导提示布局资源ID
         * @param offsetX      X轴偏移(正数表示从布局的左侧往右偏移量，负数表示从布局的右侧往左偏移量，{@link GuideTip#CENTER}表示居中)
         * @param offsetY      Y轴偏移(正数表示从布局的上侧往下偏移量，负数表示从布局的下侧往上偏移量，{@link GuideTip#CENTER}表示居中)
         * @param layoutParams 引导提示布局参数
         */
        public Builder addGuideTip(@LayoutRes int guideResID, int offsetX, int offsetY, RelativeLayout.LayoutParams layoutParams) {
            this.guideTips.add(new GuideTip(inflate(guideResID), offsetX, offsetY, layoutParams));
            return this;
        }

        /**
         * 添加引导提示布局
         *
         * @param guideView    引导提示布局
         * @param offsetX      X轴偏移(正数表示从布局的左侧往右偏移量，负数表示从布局的右侧往左偏移量，{@link GuideTip#CENTER}表示居中)
         * @param offsetY      Y轴偏移(正数表示从布局的上侧往下偏移量，负数表示从布局的下侧往上偏移量，{@link GuideTip#CENTER}表示居中)
         * @param layoutParams 引导提示布局参数
         */
        public Builder addGuideTip(View guideView, int offsetX, int offsetY, RelativeLayout.LayoutParams layoutParams) {
            this.guideTips.add(new GuideTip(guideView, offsetX, offsetY, layoutParams));
            return this;
        }

        /**
         * 添加引导提示布局资源ID
         *
         * @param guideResID   引导提示布局资源ID
         * @param offsetX      X轴偏移(正数表示从布局的左侧往右偏移量，负数表示从布局的右侧往左偏移量，{@link GuideTip#CENTER}表示居中)
         * @param offsetY      Y轴偏移(正数表示从布局的上侧往下偏移量，负数表示从布局的下侧往上偏移量，{@link GuideTip#CENTER}表示居中)
         * @param layoutParams 引导提示布局参数
         * @param viewIds      响应点击取消引导提示布局的View资源ID
         */
        public Builder addGuideTip(@LayoutRes int guideResID, int offsetX, int offsetY, RelativeLayout.LayoutParams layoutParams, @IdRes int... viewIds) {
            this.guideTips.add(new GuideTip(inflate(guideResID), offsetX, offsetY, viewIds, layoutParams));
            return this;
        }

        /**
         * 添加引导提示布局
         *
         * @param guideView    引导提示布局
         * @param offsetX      X轴偏移(正数表示从布局的左侧往右偏移量，负数表示从布局的右侧往左偏移量，{@link GuideTip#CENTER}表示居中)
         * @param offsetY      Y轴偏移(正数表示从布局的上侧往下偏移量，负数表示从布局的下侧往上偏移量，{@link GuideTip#CENTER}表示居中)
         * @param layoutParams 引导提示布局参数
         * @param viewIds      响应点击取消引导提示布局的View资源ID
         */
        public Builder addGuideTip(View guideView, int offsetX, int offsetY, RelativeLayout.LayoutParams layoutParams, @IdRes int... viewIds) {
            this.guideTips.add(new GuideTip(guideView, offsetX, offsetY, viewIds, layoutParams));
            return this;
        }

        /**
         * 设置引导层背景颜色
         */
        public Builder setBackgroundColor(@ColorInt int color) {
            this.mBackgroundColor = color;
            return this;
        }

        /**
         * 点击任意区域是否隐藏引导层，默认true
         */
        public Builder setAnyWhereCancelable(boolean cancelable) {
            this.isAnyWhereCancelable = cancelable;
            return this;
        }

        /**
         * 设置高亮控件是否响应点击事件
         */
        public Builder setPerformHighLightClick(boolean performClick) {
            this.performHighLightClick = performClick;
            return this;
        }

        /**
         * 设置引导层隐藏，显示监听
         */
        public Builder setOnGuideChangedListener(OnGuideChangedListener listener) {
            this.onGuideChangedListener = listener;
            return this;
        }

        /**
         * 设置用于识别引导提示的标签(只显示一次时必须设置项)
         */
        public Builder setLabel(String label) {
            this.label = label;
            return this;
        }

        /**
         * 是否总是显示引导层
         */
        public Builder setAlwaysShow(boolean alwaysShow) {
            this.alwaysShow = alwaysShow;
            return this;
        }

        private View inflate(@LayoutRes int layoutResID) {
            if (inflater == null) {
                inflater = LayoutInflater.from(mContext);
            }
            return inflater.inflate(layoutResID, null);
        }

        /**
         * 构建引导层的控制器
         */
        public NewbieGuide build() {
            return new NewbieGuide(this);
        }

        /**
         * 显示引导提示
         */
        public void show() {
            build().show();
        }
    }

    /**
     * 引导提示布局改变监听
     */
    public interface OnGuideChangedListener {

        /**
         * 当引导提示布局显示时调用该方法
         */
        void onShow();

        /**
         * 当引导提示布局消失时调用该方法
         */
        void onDismiss();

        /**
         * 当高亮View被点击时调用该方法
         */
        void onHeightlightViewClick(View view);

    }

    /**
     * 引导提示布局改变监听的空实现
     */
    public class SimpleOnGuideChangedListener implements OnGuideChangedListener {
        @Override
        public void onShow() {

        }

        @Override
        public void onDismiss() {

        }

        @Override
        public void onHeightlightViewClick(View view) {

        }
    }

}
