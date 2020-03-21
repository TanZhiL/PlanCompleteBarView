![效果图](https://github.com/TanZhiL/PlanCompleteBarView/blob/master/screenshot.png)
![效果图](https://github.com/TanZhiL/PlanCompleteBarView/blob/master/screenshot1.png)
### 要点
1. 支持滚动查看
2. 支持两种显示模式切换
### 思路
根据效果图可整理思路
1.因为要实现左侧x轴固定,右侧可滑动,所以可将整个View看成左右两部分,分别为左侧自定义LeftView,及右侧的水平滚动视图ScrollView.然后再ScrollView中加入自定义RightView.这样即可实现左侧固定,右侧水平滑动的效果.
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200321105902456.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MjcwMzQ0NQ==,size_16,color_FFFFFF,t_70#pic_center)
2.所以我们的View应该继承LinearLayout,用于放置左侧和右侧两个视图
3.由效果图可知,切换显示模式只需改变bar的宽度,及绘制的起始坐标.
### 代码实现
1.继承自LinearLayout并设置为水平方向排列.
```java
public class PlanCompleteBarView extends LinearLayout {

   public PlanCompleteBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setOrientation(HORIZONTAL);     
}
```
2.添加基本结构
```java
    private void show() {
        removeAllViews();
        init();
        LayoutParams layoutParams = new LayoutParams(dp2px(leftWidth), ViewGroup.LayoutParams.MATCH_PARENT);
        addView(new LeftView(mContext), layoutParams);

        HorizontalScrollView scrollView = new HorizontalScrollView(mContext);
        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.addView(new RightView(mContext), new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(scrollView, new LayoutParams(getMeasuredWidth() - dp2px(leftWidth), ViewGroup.LayoutParams.MATCH_PARENT));
    }

```
3.初始化必要参数
```java
    private void init() {
        yPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        yPaint.setTextSize(sp2px(yTextSize));
        yPaint.setColor(yTextColor);

        xPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        xPaint.setTextSize(sp2px(xTextSize));
        xPaint.setColor(xTextColor);

        topPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        topPaint.setTextSize(sp2px(topTextSize));


        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStrokeWidth(sp2px(lineWidth));
        linePaint.setColor(lineColor);

        planPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        planPaint.setStyle(Paint.Style.FILL);
        planPaint.setStrokeWidth(sp2px(barWidth));
        planPaint.setColor(planColor);

        completePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        completePaint.setStyle(Paint.Style.FILL);
        completePaint.setStrokeWidth(sp2px(barWidth));
        completePaint.setColor(completeColor);

        Paint.FontMetricsInt xFontMetricsInt = xPaint.getFontMetricsInt();
        marginBottom = xFontMetricsInt.bottom - xFontMetricsInt.top;

        Paint.FontMetricsInt topFontMetricsInt = topPaint.getFontMetricsInt();
        marginTop = topFontMetricsInt.bottom - topFontMetricsInt.top + topMarginBottom;
        //因为顶部有两行文字
        marginTop *= 2;
        drawHeight = getMeasuredHeight() - getPaddingBottom() - getPaddingTop() - marginBottom - marginTop;
        percentHeight = drawHeight / maxValue;
        yLabels = new ArrayList<>();
        int i = maxValue / step;
        for (int j = 0; j <= step; j++) {
            yLabels.add(String.valueOf(j * i));
        }
    }

```
4.自定义左侧LeftView
```java
         @Override
        protected void onDraw(Canvas canvas) {
            //将坐标系移动至右下角
            canvas.translate(mWidth, mHeight - marginBottom);
            //计算每一段的长度
            float i = maxValue / step;

            for (int j = 0; j <= step; j++) {
                float i1 = j * i * percentHeight;
                // canvas.drawLine(0,-i1,-mWidth,-i1,linePaint);
                String s = yLabels.get(j);
                float v1 = yPaint.measureText(s);
                //绘制y轴文字
                canvas.drawText(s, -v1 - dp2px(yPaddingRight), -i1, yPaint);
            }
        }
```
5.自定义右侧RightView
	测量自身所需宽度
```java
      @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            //计算所需宽度
            int i = barWidth * mDataWrappers.size() + spaceWidth * mDataWrappers.size();
            setMeasuredDimension(dp2px(i), MeasureSpec.getSize(heightMeasureSpec));

        }
```
开始绘制
```java
	    @Override
        protected void onDraw(Canvas canvas) {
            //将坐标移动至左下角
            canvas.translate(0, mHeight);
            float i = maxValue / step;
            //绘制网格线
            for (int j = 0; j <= step; j++) {
                float i1 = j * i * percentHeight;
                canvas.drawLine(0, -i1 - marginBottom, mWidth, -i1 - marginBottom, linePaint);
            }
            for (int j = 0; j < mDataWrappers.size(); j++) {
                DataWrapper dataWrapper = mDataWrappers.get(j);
                String label = dataWrapper.label;
                if (j == 0) {
                    canvas.translate(0, -marginBottom);
                }
                //计算绘制的x中心
                float x = j * (dp2px(spaceWidth)
                        + dp2px(barWidth))
                        + dp2px(barWidth / 2)
                        + dp2px(spaceWidth / 2);
                //绘制x轴文字
                float textWidth = xPaint.measureText(label);
                Paint.FontMetrics fontMetrics = xPaint.getFontMetrics();
                canvas.drawText(label, x - textWidth / 2, marginBottom - fontMetrics.bottom, xPaint);
                //绘制bar
                if (BAR_STYLE_OVERLAY == barstyle) {
                    //堆叠样式,先绘制大的数值再绘制小的数值,形成覆盖的效果
                    if (dataWrapper.complete > dataWrapper.plan) {
                        canvas.drawLine(x, 0, x, -dataWrapper.complete * percentHeight, completePaint);
                        canvas.drawLine(x, 0, x, -dataWrapper.plan * percentHeight, planPaint);
                    } else {
                        canvas.drawLine(x, 0, x, -dataWrapper.plan * percentHeight, planPaint);
                        canvas.drawLine(x, 0, x, -dataWrapper.complete * percentHeight, completePaint);
                    }
                } else if (BAR_STYLE_TIE == barstyle) {
                    //排列样式
                    planPaint.setStrokeWidth(dp2px(barWidth / 2));
                    completePaint.setStrokeWidth(dp2px(barWidth / 2));
                    canvas.drawLine(x - dp2px(barWidth / 4), 0, x - dp2px(barWidth / 4),
                            -dataWrapper.plan * percentHeight, planPaint);
                    canvas.drawLine(x + dp2px(barWidth / 4), 0,
                            x + dp2px(barWidth / 4), -dataWrapper.complete * percentHeight, completePaint);
                }
                //绘制顶部文字   200%
                //              200/100
                //应为需要中心对齐,所以要分别绘制左边的数值和右边的数值
                String str = "/";
                float separatorWidth = topPaint.measureText(str);
                topPaint.setColor(percentColor);
                canvas.drawText(str, x - separatorWidth / 2,
                        -(Math.max(dataWrapper.complete, dataWrapper.plan) * percentHeight)
                                - dp2px(topMarginBottom), topPaint);
                //绘制完成数值200
                topPaint.setColor(completeColor);
                textWidth = topPaint.measureText(String.valueOf(dataWrapper.complete));
                canvas.drawText(String.valueOf(dataWrapper.complete), x - separatorWidth / 2 - textWidth,
                        -(Math.max(dataWrapper.complete, dataWrapper.plan) * percentHeight)
                                - dp2px(topMarginBottom), topPaint);
                //绘制计划数值100
                topPaint.setColor(planColor);
                canvas.drawText(String.valueOf(dataWrapper.plan), x + separatorWidth / 2,
                        -(Math.max(dataWrapper.complete, dataWrapper.plan) * percentHeight)
                                - dp2px(topMarginBottom), topPaint);
                //绘制百分比 200%
                topPaint.setColor(percentColor);
                float percent = (float) dataWrapper.complete / dataWrapper.plan * 100;
                str = (int) percent + "%";
                textWidth = topPaint.measureText(str);
                Paint.FontMetricsInt fontMetricsInt = topPaint.getFontMetricsInt();
                int textHeight = fontMetricsInt.bottom - fontMetricsInt.top;
                canvas.drawText(str, x - textWidth / 2, -(Math.max(dataWrapper.complete, dataWrapper.plan) * percentHeight)
                        - dp2px(topMarginBottom) - textHeight, topPaint);
            }
        }
```
完成.https://blog.csdn.net/weixin_42703445/article/details/105005175
