package com.giot.memo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.giot.memo.R;

/**
 * 环形分析图
 * Created by reed on 16/8/18.
 */
public class RingChart extends View {

    private String name;//圆环中心文字
    private float radius = DEFAULT_RADIUS;//圆环半径
    private int nameColor = Color.BLACK;//文字颜色
    private float innerRadius = DEFAULT_INNER_RADIUS;//内圆半径
    private int innerColor = Color.WHITE;//内圆背景色

    private static final float DEFAULT_RADIUS = 64;//默认圆环半径

    private static final float DEFAULT_INNER_RADIUS = 0;//默认内圆半径

    private float[] percent;
    private float[] sweep;

    private Paint ringPaint;
    private Paint namePaint;
    private Paint innerPaint;
    private RectF mArcRectF;
    private float nameSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics());

    private long time = 0;//计算触摸时长, 判断是否触发点击事件
    private RingAnimation animation;

    private int lastX = 0;
    private int startLeft = 0;
    private int startRight = 0;

    public RingChart(Context context) {
        this(context, null);
    }

    public RingChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RingChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray type = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RingChart, defStyleAttr, 0);
            name = type.getString(R.styleable.RingChart_name);
            radius = type.getDimensionPixelSize(R.styleable.RingChart_radius, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_RADIUS, getResources().getDisplayMetrics()));
            nameColor = type.getColor(R.styleable.RingChart_name_color, Color.BLACK);
            innerRadius = type.getDimensionPixelSize(R.styleable.RingChart_inner_radius, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_INNER_RADIUS, getResources().getDisplayMetrics()));
            innerColor = type.getColor(R.styleable.RingChart_inner_color, Color.WHITE);
            type.recycle();
        }
        initPaint();
        //mArcRectF = new RectF((radius - innerRadius) / 2, (radius - innerRadius) / 2 , (3 * radius + innerRadius) / 2, (3 * radius + innerRadius) / 2);
        mArcRectF = new RectF(0, 0, radius * 2, radius * 2);
        animation = new RingAnimation();
        animation.setDuration(300);
    }

    public void setName(String name) {
        this.name = name;
        invalidate();
    }

    public void setPercent(float[] percent) {
        this.percent = percent;
        if (percent.length > 6) {
            throw new IllegalArgumentException("percent's length is more than six");
        }
        sweep = null;
        startAnimation(animation);
    }

    public void setRadius(float radius) {
        this.radius = radius;
        invalidate();
    }

    public void setNameColor(int nameColor) {
        this.nameColor = nameColor;
        invalidate();
    }

    public void setInnerRadius(float innerRadius) {
        this.innerRadius = innerRadius;
        invalidate();
    }

    public void setInnerColor(int innerColor) {
        this.innerColor = innerColor;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int length = (int) (2 * radius);
        setMeasuredDimension(length, length);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawRing(canvas);
        drawInner(canvas);
        drawText(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                time = event.getEventTime();
                lastX = x;
                startLeft = getLeft();
                startRight = getRight();
                break;
            case MotionEvent.ACTION_UP:
                //点击事件
                if (event.getEventTime() - time < 150) {
                    float eventX = event.getX();
                    float eventY = event.getY();
                    double distance = Math.pow(eventX - radius, 2) + Math.pow(eventY - radius, 2);
                    if (distance < Math.pow(innerRadius, 2) && Math.abs(startLeft - getLeft()) < 10) {
                        //如果手指按下时间和抬起时间差值在150毫秒以内,
                        //判断触摸区域是在圆环内并且, 手指移动距离不超过10, 则判定为一次有效点击事件
                        if (onTextClickListener != null) {
                            onTextClickListener.onClick(RingChart.this);
                        }
                    }
                }
                time = 0;
                //滑动事件(如果左右滑动的距离没有超过滑动方向的一半, 则判为无效滑动)
                if (Math.abs(startLeft - getLeft()) < (startLeft + getWidth() / 2) / 2) {
                    layout(startLeft, getTop(), startRight, getBottom());
                } else {
                    if (onScrollChangeListener != null) {
                        onScrollChangeListener.onChange(RingChart.this, startLeft - getLeft() < 0);
                    } else {
                        layout(startLeft, getTop(), startRight, getBottom());
                    }
                }
                setAlpha(1);
                break;
            case MotionEvent.ACTION_MOVE:
                int offsetX = x - lastX;
                layout(getLeft() + offsetX, getTop(),
                        getRight() + offsetX, getBottom());
                setAlpha(1 - (float) Math.abs(startLeft - getLeft()) / (float) (startLeft + getWidth() / 2));
                break;
        }
        return true;
    }

    public interface OnTextClickListener {
        void onClick(View v);
    }

    private OnTextClickListener onTextClickListener;

    public void setOnTextClickListener(OnTextClickListener onTextClickListener) {
        this.onTextClickListener = onTextClickListener;
    }

    public interface OnScrollChangeListener {
        void onChange(View v, boolean isLeft);
    }

    private OnScrollChangeListener onScrollChangeListener;

    public void setOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener) {
        this.onScrollChangeListener = onScrollChangeListener;
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        //初始化文字画笔
        namePaint = new Paint();
        namePaint.setStyle(Paint.Style.FILL);
        namePaint.setTextSize(nameSize);
        namePaint.setColor(nameColor);

        //初始化图标画笔
        ringPaint = new Paint();
        ringPaint.setStyle(Paint.Style.FILL);
        ringPaint.setAntiAlias(true);

        //初始化内圆画笔
        innerPaint = new Paint();
        innerPaint.setStyle(Paint.Style.FILL);
        innerPaint.setColor(innerColor);
        innerPaint.setAntiAlias(true);

    }

    //绘制圆环
    private void drawRing(Canvas canvas) {
        float startAngle = -90;
        if (percent == null || percent.length == 0) {//没有数据的情况
            ringPaint.setColor(Color.parseColor("#dadada"));//灰色
            if (sweep == null) {
                sweep = new float[]{1};
            }
            canvas.drawArc(mArcRectF, startAngle, sweep[0], true, ringPaint);
        } else {
            TypedArray colorArray = getResources().obtainTypedArray(R.array.analysis_color);
            for (int i = 0; i < sweep.length; i++) {
                ringPaint.setColor(colorArray.getColor(i, Color.TRANSPARENT));
                canvas.drawArc(mArcRectF, startAngle, sweep[i], true, ringPaint);
                startAngle += sweep[i];
            }
            colorArray.recycle();
        }
    }

    //绘制内圆
    private void drawInner(Canvas canvas) {
        canvas.drawCircle(radius, radius, innerRadius, innerPaint);
    }


    //绘制文字
    private void drawText(Canvas canvas) {
        Paint.FontMetrics fontMetrics = namePaint.getFontMetrics();
        float fontHeight = fontMetrics.descent - fontMetrics.ascent;
        if (name != null) {
            canvas.drawText(name, radius - namePaint.measureText(name) / 2, radius + fontHeight / 4, namePaint);
        }
    }

    /**
     * 圆环加载数据的动画效果
     */
    private class RingAnimation extends Animation {

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            if (sweep == null) {
                if (percent == null || percent.length == 0) {
                    sweep = new float[1];
                } else {
                    sweep = new float[percent.length];
                }
            }
            if (interpolatedTime < 1.0f) {
                if (percent == null || percent.length == 0) {
                    sweep[0] = interpolatedTime * 360;
                } else {
                    for (int i = 0; i < percent.length; i++) {
                        sweep[i] = percent[i] * interpolatedTime * 360;
                    }
                }
            } else {
                if (percent == null || percent.length == 0) {
                    sweep[0] = 360;
                } else {
                    for (int i = 0; i < percent.length; i++) {
                        sweep[i] = percent[i] * 360;
                    }
                }
            }
            invalidate();
        }
    }
}
