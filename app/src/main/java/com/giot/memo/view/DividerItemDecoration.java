package com.giot.memo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import com.giot.memo.R;

/**
 * 自定义RecyclerView分割线
 * Created by reed on 16/7/29.
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private int orientation;

    //转换成整型后分割线尺寸
    private int itemSize;
    //待传入的分割线尺寸，如果没有传入，初始化的时候默认16
    private float size;

    private Paint mPaint;
    private Context context;
    private int mColor;

    public DividerItemDecoration(Context context) {
        this(context, 16, LinearLayoutManager.VERTICAL);
    }

    public DividerItemDecoration(Context context, float size) {
        this(context, size, LinearLayoutManager.VERTICAL);
    }

    public DividerItemDecoration(Context context, int orientation) {
        this(context, 1, orientation);
    }

    public DividerItemDecoration(Context context, float size, int orientation) {
        if (orientation != LinearLayoutManager.VERTICAL && orientation != LinearLayoutManager.HORIZONTAL) {
            throw new IllegalArgumentException("the orientation is not VERTICAL or HORIZONTAL!");
        }
        this.context = context;
        this.size = size;
        this.orientation = orientation;
        if (size==1){
                mColor=context.getResources().getColor(R.color.analysis_itemDecoration);
        }
        initAttrs(mColor);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (orientation == LinearLayoutManager.VERTICAL) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (orientation == LinearLayoutManager.VERTICAL) {
            outRect.set(0, 0, 0, itemSize);
        } else {
            outRect.set(0, 0, itemSize, 0);
        }
    }

    private void initAttrs(int color) {
        itemSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, context.getResources().getDisplayMetrics());
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        if (color!=0){
            mPaint.setColor(color);
        }else {
            mPaint.setColor(Color.TRANSPARENT);
        }
        mPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * 绘制横向item分割线
     *
     * @param canvas 画布
     * @param parent recyclerView
     */
    private void drawVertical(Canvas canvas, RecyclerView parent) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + itemSize;
            canvas.drawRect(left, top, right, bottom, mPaint);
        }
    }

    /**
     * 绘制横向item分割线
     *
     * @param canvas 画布
     * @param parent recyclerView
     */
    public void drawHorizontal(Canvas canvas, RecyclerView parent) {
        int top = parent.getPaddingTop();
        int bottom = parent.getHeight() - parent.getPaddingBottom();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            int left = child.getRight() + params.rightMargin;
            int right = left + itemSize;
            canvas.drawRect(left, top, right, bottom, mPaint);
        }
    }
}

