package com.giot.memo.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * 以RecyclerView实现ViewPager效果
 * Created by reed on 16/8/1.
 */
public class PageRecyclerView extends RecyclerView {

    private int totalPage;
    private int shortestDistance;
    private float slideDistance = 0; // 滑动的距离
    private float scrollX = 0; // X轴当前的位置
    private PageIndicatorView mIndicator;
    int currentPage ;




    public PageRecyclerView(Context context) {
        super(context);
    }

    public PageRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PageRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    public void setIndicator(PageIndicatorView mIndicator) {
        this.mIndicator = mIndicator;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        update(adapter);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        shortestDistance = getMeasuredWidth() / 3;
    }


    @Override
    public void onScrolled(int dx, int dy) {
        scrollX += dx;
        if (getScrollState() == SCROLL_STATE_DRAGGING) {
            slideDistance += dx;
        }
        super.onScrolled(dx, dy);
    }

    @Override
    public void onScrollStateChanged(int state) {
        switch (state) {
            case SCROLL_STATE_DRAGGING:
                break;
            case SCROLL_STATE_SETTLING:
                break;
            case SCROLL_STATE_IDLE:

                if (slideDistance == 0) {
                    break;
                }
                if (slideDistance < 0) {
                    currentPage = (int) Math.ceil(scrollX / getWidth());
                    if (currentPage * getWidth() - scrollX < shortestDistance) {
                        currentPage += 1;
                    }
                } else {
                    currentPage = (int) Math.ceil(scrollX / getWidth()) + 1;
                    if (currentPage <= totalPage) {
                        if (scrollX - (currentPage - 2) * getWidth() < shortestDistance) {
                            // 如果这一页滑出距离不足，则定位到前一页
                            currentPage -= 1;
                        }

                    }else {
                        currentPage = totalPage;
                    }
                }
                scrollBy((int) ((currentPage - 1) * getWidth() - scrollX), 0);
                if (mIndicator != null) {
                    mIndicator.setSelectedPage(currentPage - 1);
                }
                slideDistance = 0;
                break;
        }
        super.onScrollStateChanged(state);
    }

    public void update(Adapter adapter) {
        totalPage = (int) Math.ceil(adapter.getItemCount() / 10);
        if (mIndicator != null) {
            if (totalPage > 1) {
                if (mIndicator.getVisibility() != VISIBLE) {
                    mIndicator.setVisibility(VISIBLE);
                }
                mIndicator.initIndicator(totalPage);
                scrollX=0;
            } else {
                mIndicator.setVisibility(INVISIBLE);
            }
        }
    }

}
