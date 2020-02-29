package com.regent.thomas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;


/**
 * Author: Thomas.<br/>
 * Date: 2020/2/28 20:54<br/>
 * GitHub: https://github.com/TanZhiL<br/>
 * CSDN: https://blog.csdn.net/weixin_42703445<br/>
 * Email: 1071931588@qq.com<br/>
 * Description:销售计划完成率看板
 */
public class PlanCompleteBarView extends LinearLayout {
    private List<DataWrapper> mDataWrappers;
    private Context mContext;
    //底部文字占用高度
    private int marginBottom;
    //顶部文字占用高度
    private int marginTop;

    //y轴视图的宽度
    private int leftWidth = 50;
    //y轴字体大小
    private int yTextSize = 14;
    //y轴字体颜色
    private int yTextColor = 0xff747A7E;
    //y轴右侧内边距
    private int yPaddingRight = 10;

    //x轴字体大小
    private int xTextSize = 14;
    //x轴字体颜色
    private int xTextColor = 0xff747A7E;

    //柱形宽度
    private int barWidth = 30;
    //间隔宽度
    private int spaceWidth = 30;
    //计划颜色
    private int planColor = 0xFF34A6FF;
    //已完成颜色
    private int completeColor = 0xFFFF587A;

    //顶部文字大小
    private int topTextSize = 14;
    //顶部百分比颜色
    private int percentColor = 0xff1F2529;


    private Paint yPaint;
    private Paint xPaint;
    //顶部文字
    private Paint topPaint;
    //线条
    private Paint linePaint;
    private int lineWidth = 1;
    private int lineColor =0xffE6E6E6;

    private Paint planPaint;
    private Paint completePaint;

    //y轴最大值
    private float maxValue = 500;
    //y轴阶数:100,200,3000,400,500
    private int step = 5;
    //y轴文本根据maxValue和step计算
    private List<String> yLabels;
    //柱状图可绘制区域
    private float drawHeight;
    //每个值对应像素点=drawHeight/maxValue
    private float percentHeight;


    public PlanCompleteBarView(Context context) {
        this(context, null);
    }

    public PlanCompleteBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setOrientation(HORIZONTAL);
        mDataWrappers = new ArrayList<>();
        mDataWrappers.add(new DataWrapper("11日", 300, 320));
        mDataWrappers.add(new DataWrapper("12日", 320, 200));
        mDataWrappers.add(new DataWrapper("13日", 100, 210));
        mDataWrappers.add(new DataWrapper("14日", 200, 200));
        mDataWrappers.add(new DataWrapper("15日", 300, 250));
        mDataWrappers.add(new DataWrapper("16日", 306, 260));
        mDataWrappers.add(new DataWrapper("17日", 320, 280));
        mDataWrappers.add(new DataWrapper("18日", 330, 400));
        post(new Runnable() {
            @Override
            public void run() {
                show();
            }
        });
    }

    private void show() {
        removeAllViews();
        init();
        LayoutParams layoutParams = new LayoutParams(SizeUtils.dp2px(mContext, leftWidth), ViewGroup.LayoutParams.MATCH_PARENT);
        addView(new LeftView(mContext), layoutParams);

        HorizontalScrollView scrollView = new HorizontalScrollView(mContext);
        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.addView(new RightView(mContext), new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(scrollView, new LayoutParams(getMeasuredWidth() - getPaddingLeft() - getPaddingRight()
                - SizeUtils.dp2px(mContext, leftWidth), ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void init() {
        yPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        yPaint.setTextSize(SizeUtils.sp2px(mContext, yTextSize));
        yPaint.setColor(yTextColor);

        xPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        xPaint.setTextSize(SizeUtils.sp2px(mContext, xTextSize));
        xPaint.setColor(xTextColor);

        topPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        topPaint.setTextSize(SizeUtils.sp2px(mContext, topTextSize));


        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStrokeWidth(SizeUtils.sp2px(mContext, lineWidth));
        linePaint.setColor(lineColor);

        planPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        planPaint.setStyle(Paint.Style.FILL);
        planPaint.setStrokeWidth(SizeUtils.sp2px(mContext, barWidth));
        planPaint.setColor(planColor);

        completePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        completePaint.setStyle(Paint.Style.FILL);
        completePaint.setStrokeWidth(SizeUtils.sp2px(mContext, barWidth));
        completePaint.setColor(completeColor);

        Paint.FontMetricsInt xFontMetricsInt = xPaint.getFontMetricsInt();
        marginBottom = xFontMetricsInt.bottom - xFontMetricsInt.top;

        Paint.FontMetricsInt topFontMetricsInt = topPaint.getFontMetricsInt();
        marginTop = topFontMetricsInt.bottom - topFontMetricsInt.top;
        //因为顶部有两行文字
        marginTop *= 2;
        drawHeight = getMeasuredHeight() - getPaddingBottom() - getPaddingTop() - marginBottom - marginTop;
        percentHeight = drawHeight / maxValue;
        yLabels = new ArrayList<>();
        float i = maxValue / step;
        for (int j = 0; j <= step; j++) {
            yLabels.add(String.valueOf((int) (j * i)));
        }
    }

    /**
     * 左边y轴视图
     */
    class LeftView extends View {
        private float mWidth;
        private float mHeight;

        public LeftView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.translate(mWidth, mHeight - marginBottom);

            float i = maxValue / step;

            for (int j = 0; j <= step; j++) {

                float i1 = j * i * percentHeight;
                // canvas.drawLine(0,-i1,-mWidth,-i1,linePaint);

                String s = yLabels.get(j);
                float v1 = yPaint.measureText(s);
                canvas.drawText(s, -v1 - SizeUtils.dp2px(mContext, yPaddingRight), -i1, yPaint);

            }
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            mWidth = getMeasuredWidth();
            mHeight = getMeasuredHeight();
        }

    }

    class RightView extends View {

        private float mWidth;
        private float mHeight;

        public RightView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.translate(0, mHeight);
            float i = maxValue / step;
            //网格
            for (int j = 0; j <= step; j++) {
                float i1 = j * i * percentHeight;
                canvas.drawLine(0, -i1 - marginBottom, mWidth, -i1 - marginBottom, linePaint);
            }
            //x轴文字
            for (int j = 0; j < mDataWrappers.size(); j++) {
                String label = mDataWrappers.get(j).label;
                float v = xPaint.measureText(label);
                canvas.drawText(label, j * (SizeUtils.dp2px(mContext, spaceWidth)
                        + SizeUtils.dp2px(mContext, barWidth))
                        + SizeUtils.dp2px(mContext, barWidth/2)  +
                        SizeUtils.dp2px(mContext, spaceWidth/2) - v / 2, -5, xPaint);
            }
            //bar
            canvas.translate(0, -marginBottom);
            for (int j = 0; j < mDataWrappers.size(); j++) {
                float x = j * (SizeUtils.dp2px(mContext, spaceWidth)
                        + SizeUtils.dp2px(mContext, barWidth))
                        + SizeUtils.dp2px(mContext, barWidth/2)+
                        SizeUtils.dp2px(mContext, spaceWidth/2);

                DataWrapper dataWrapper = mDataWrappers.get(j);
                if (dataWrapper.complete > dataWrapper.plan) {
                    canvas.drawLine(x, 0, x, -dataWrapper.complete * percentHeight, completePaint);
                    canvas.drawLine(x, 0, x, -dataWrapper.plan * percentHeight, planPaint);
                } else {
                    canvas.drawLine(x, 0, x, -dataWrapper.plan * percentHeight, planPaint);
                    canvas.drawLine(x, 0, x, -dataWrapper.complete * percentHeight, completePaint);
                }
                //300/200
                String s = "/";
                float v = topPaint.measureText(s);
                topPaint.setColor(percentColor);
                Paint.FontMetricsInt fontMetricsInt = topPaint.getFontMetricsInt();
                int i1 = fontMetricsInt.bottom - fontMetricsInt.top;

                x = j * (SizeUtils.dp2px(mContext, spaceWidth)
                        + SizeUtils.dp2px(mContext, barWidth))
                        + SizeUtils.dp2px(mContext, barWidth/2) +
                        SizeUtils.dp2px(mContext, spaceWidth/2);

                canvas.drawText(s, x-v/2, -(Math.max(dataWrapper.complete, dataWrapper.plan) * percentHeight)
                        - SizeUtils.dp2px(mContext, 10), topPaint);

                topPaint.setColor(completeColor);
                float v1=topPaint.measureText(String.valueOf(dataWrapper.complete));
                canvas.drawText(String.valueOf(dataWrapper.complete), x-v/2-v1, -(Math.max(dataWrapper.complete, dataWrapper.plan) * percentHeight)
                        - SizeUtils.dp2px(mContext, 10), topPaint);

                topPaint.setColor(planColor);
                canvas.drawText(String.valueOf(dataWrapper.plan), x+v/2, -(Math.max(dataWrapper.complete, dataWrapper.plan) * percentHeight)
                        - SizeUtils.dp2px(mContext, 10), topPaint);

                topPaint.setColor(percentColor);
                float percent = (float) dataWrapper.complete / dataWrapper.plan * 100;
                s = (int)percent + "%";
                v = topPaint.measureText(s);
                canvas.drawText(s, x-v/2, -(Math.max(dataWrapper.complete, dataWrapper.plan) * percentHeight)
                        - SizeUtils.dp2px(mContext, 5)-i1, topPaint);
            }
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            //计算所需宽度
            int i = barWidth * mDataWrappers.size() + spaceWidth * mDataWrappers.size();
            setMeasuredDimension(SizeUtils.dp2px(mContext, i), MeasureSpec.getSize(heightMeasureSpec));

        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            mWidth = getMeasuredWidth();
            mHeight = getMeasuredHeight();
        }
    }

    public static class DataWrapper {
        String label;
        int plan;
        int complete;

        public DataWrapper(String label, int plan, int complete) {
            this.label = label;
            this.plan = plan;
            this.complete = complete;
        }
    }

    public List<DataWrapper> getDataWrappers() {
        return mDataWrappers;
    }

    public void setDataWrappers(List<DataWrapper> dataWrappers) {
        mDataWrappers = dataWrappers;
    }

    public int getLeftWidth() {
        return leftWidth;
    }

    public void setLeftWidth(int leftWidth) {
        this.leftWidth = leftWidth;
    }

    public int getyTextSize() {
        return yTextSize;
    }

    public void setyTextSize(int yTextSize) {
        this.yTextSize = yTextSize;
    }

    public int getyTextColor() {
        return yTextColor;
    }

    public void setyTextColor(int yTextColor) {
        this.yTextColor = yTextColor;
    }

    public int getyPaddingRight() {
        return yPaddingRight;
    }

    public void setyPaddingRight(int yPaddingRight) {
        this.yPaddingRight = yPaddingRight;
    }

    public int getxTextSize() {
        return xTextSize;
    }

    public void setxTextSize(int xTextSize) {
        this.xTextSize = xTextSize;
    }

    public int getxTextColor() {
        return xTextColor;
    }

    public void setxTextColor(int xTextColor) {
        this.xTextColor = xTextColor;
    }

    public int getBarWidth() {
        return barWidth;
    }

    public void setBarWidth(int barWidth) {
        this.barWidth = barWidth;
    }

    public int getSpaceWidth() {
        return spaceWidth;
    }

    public void setSpaceWidth(int spaceWidth) {
        this.spaceWidth = spaceWidth;
    }

    public int getPlanColor() {
        return planColor;
    }

    public void setPlanColor(int planColor) {
        this.planColor = planColor;
    }

    public int getCompleteColor() {
        return completeColor;
    }

    public void setCompleteColor(int completeColor) {
        this.completeColor = completeColor;
    }

    public int getTopTextSize() {
        return topTextSize;
    }

    public void setTopTextSize(int topTextSize) {
        this.topTextSize = topTextSize;
    }

    public int getPercentColor() {
        return percentColor;
    }

    public void setPercentColor(int percentColor) {
        this.percentColor = percentColor;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    @Override
    public void invalidate() {
        show();
    }
}
