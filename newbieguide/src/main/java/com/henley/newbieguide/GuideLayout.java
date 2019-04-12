package com.henley.newbieguide;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.henley.newbieguide.model.GuideTip;
import com.henley.newbieguide.model.HighLight;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * 引导提示控件
 *
 * @author Henley
 * @date 2017/9/28 14:37
 */
public class GuideLayout extends RelativeLayout {

    public static final int DEFAULT_COLOR = 0xb2000000;
    private Paint mPaint;
    private List<HighLight> highLights;
    private int mBackgroundColor = DEFAULT_COLOR;

    public GuideLayout(Context context) {
        this(context, null);
    }

    public GuideLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GuideLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
        setClickable(true); // 设置View可点击
        setWillNotDraw(false); // ViewGroup默认设定为true，会使onDraw方法不执行，如果复写了onDraw(Canvas)方法，需要清除此标记
        setLayerType(LAYER_TYPE_SOFTWARE, null); // 关闭当前view的硬件加速
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        //设置画笔遮罩滤镜,可以传入BlurMaskFilter或EmbossMaskFilter，前者为模糊遮罩滤镜而后者为浮雕遮罩滤镜
        //这个方法已经被标注为过时的方法了，如果你的应用启用了硬件加速，你是看不到任何阴影效果的
        mPaint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.INNER));
    }

    public void setHighLights(List<HighLight> highLights) {
        this.highLights = highLights;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.mBackgroundColor = backgroundColor;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(mBackgroundColor);
        if (highLights != null && !highLights.isEmpty()) {
            for (HighLight highLight : highLights) {
                drawHighLight(canvas, highLight);
            }
        }
    }

    /**
     * 绘制高亮部分
     */
    private void drawHighLight(Canvas canvas, HighLight highLight) {
        RectF rectF = highLight.getRectF();
        switch (highLight.getShapeType()) {
            case CIRCLE:
                canvas.drawCircle(rectF.centerX(), rectF.centerY(), highLight.getRadius(), mPaint);
                break;
            case OVAL:
                canvas.drawOval(rectF, mPaint);
                break;
            case ROUND_RECTANGLE:
                canvas.drawRoundRect(rectF, highLight.getRoundCorners(), highLight.getRoundCorners(), mPaint);
                break;
            case RECTANGLE:
            default:
                canvas.drawRect(rectF, mPaint);
                break;
        }
    }

    /**
     * 添加任意View到引导提示布局中
     *
     * @param view    需要添加到引导提示布局中的View
     * @param offsetX X轴偏移(正数表示从布局的左侧往右偏移量，负数表示从布局的右侧往左偏移量，{@link GuideTip#CENTER}表示居中)
     * @param offsetY Y轴偏移(正数表示从布局的上侧往下偏移量，负数表示从布局的下侧往上偏移量，{@link GuideTip#CENTER}表示居中)
     * @param params  需要添加到引导提示布局中的View的布局参数
     */
    public void addView(View view, int offsetX, int offsetY, LayoutParams params) {
        if (params == null) {
            params = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        }
        if (offsetX == GuideTip.CENTER) {
            params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        } else if (offsetX < 0) {
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            params.rightMargin = -offsetX;
        } else {
            params.leftMargin = offsetX;
        }

        if (offsetY == GuideTip.CENTER) {
            params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        } else if (offsetY < 0) {
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            params.bottomMargin = -offsetY;
        } else {
            params.topMargin = offsetY;
        }
        addView(view, params);
    }

}