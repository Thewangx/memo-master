package com.giot.memo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.giot.memo.R;
import com.giot.memo.data.entity.CountBill;
import com.giot.memo.util.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr.wang on 2016/10/3.
 */

public class LineChart extends View {

    private float nameSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());

    private int mWidth, mHeight;
    private int widthMode;
    private int heightMode;
    private int heightSize;
    private int widthSize;


    private Paint mPaintDataLine;     //画表格
    private Paint mPaintChartPayPoint;   //画pay点
    private Paint mPaintChartIncomePoint; //画income点
    private Paint mPaintText;        //文字
    private Paint mPaintChartPayLine;
    private Paint mPaintChartIncomeLine;
    private Path  mPayPath;
    private Path  mIncomePath;
    


    private List<CountBill> countBills;
    private float[] expenditureList = new float[12];
    private float[] incomeList = new float[12];
    private float[] expLoc = new float[12];
    private float[] incomeLoc = new float[12];

    private int yInterval =0;
    private int yHeight =0;
    private int xPointInterval = 0;
    private int xInterval = 0;

    private int lastMonth;
    private String[] xAxis = new String[]{"1月", "3月", "5月", "7月", "9月", "11月"};
    private int[] yAxis = new int[3];
    private int padding = ScreenUtil.dip2px(getContext(), 40);

    public LineChart(Context context) {
        super(context);
        init();
    }
    public LineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public LineChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setCountBills(List<CountBill> countBills){
        this.countBills = countBills;
    }

    private void init() {
        countBills = new ArrayList<>();
        mPaintDataLine = new Paint();
        mPaintChartPayPoint = new Paint();
        mPaintChartIncomePoint = new Paint();
        mPaintText = new Paint();
        mPaintChartPayLine = new Paint();
        mPaintChartIncomeLine = new Paint();
        mPayPath = new Path();
        mIncomePath = new Path();


        mPaintChartPayPoint.setColor(getResources().getColor(R.color.exp_button_line));
        mPaintChartPayPoint.setStyle(Paint.Style.STROKE);
        mPaintChartPayPoint.setStrokeWidth(5);
        mPaintChartPayPoint.setAntiAlias(true);


        mPaintChartIncomePoint.setColor(getResources().getColor(R.color.income_button_line));
        mPaintChartIncomePoint.setStyle(Paint.Style.STROKE);
        mPaintChartIncomePoint.setStrokeWidth(5);
        mPaintChartIncomePoint.setAntiAlias(true);

        mPaintText.setColor(getResources().getColor(R.color.overall_zero));
        mPaintText.setTextSize(nameSize);
        mPaintText.setTextAlign(Paint.Align.CENTER);                //使yAxis画的时候居中显示
        mPaintText.setAntiAlias(true);
        mPaintText.setStyle(Paint.Style.FILL);


        mPaintDataLine.setColor(getResources().getColor(R.color.analysis_itemDecoration));
        mPaintDataLine.setStyle(Paint.Style.STROKE);
        mPaintDataLine.setStrokeWidth(ScreenUtil.dip2px(getContext(), 1));
        mPaintDataLine.setAntiAlias(true);

        mPaintChartIncomeLine.setColor(getResources().getColor(R.color.income_button_line));
        mPaintChartIncomeLine.setStrokeWidth(3);
        mPaintChartIncomeLine.setAntiAlias(true);
        mPaintChartIncomeLine.setStyle(Paint.Style.STROKE);


        mPaintChartPayLine.setColor(getResources().getColor(R.color.exp_button_line));
        mPaintChartPayLine.setStrokeWidth(3);
        mPaintChartPayLine.setStyle(Paint.Style.STROKE);
        mPaintChartPayLine.setAntiAlias(true);


    }

    public void setYAxis(int[] yAxis){
        this.yAxis = yAxis;
    }

    public void setLastMonth(int lastMonth) {
        this.lastMonth = lastMonth;
    }



    public void setExpenditureList(float[] expenditureList) {
        this.expenditureList = expenditureList;
    }

    public void setIncomeList(float[] incomeList) {
        this.incomeList = incomeList;
    }






    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMode = MeasureSpec.getMode(widthMeasureSpec);
        heightMode = MeasureSpec.getMode(heightMeasureSpec);
        widthSize = MeasureSpec.getSize(widthMeasureSpec);
        heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            throw new IllegalArgumentException("width must be EXACTLY,you should set like android:width=\"200dp\"");
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        } else if (widthMeasureSpec == MeasureSpec.AT_MOST) {

            throw new IllegalArgumentException("height must be EXACTLY,you should set like android:height=\"200dp\"");
        }

        setMeasuredDimension(mWidth, mHeight);
        yInterval =  ((mHeight - 2*padding-6) / 2);
        yHeight = mHeight-2*padding;
        xPointInterval = (mWidth-2*padding)/11;
        xInterval = xPointInterval*2;
    }

    @Override
    protected void onDraw(Canvas canvas) {


        //坐标轴以及x轴文字
        canvas.drawLine(padding, padding,mWidth-padding,padding,mPaintDataLine);
        canvas.drawLine(padding, padding+yInterval,mWidth-padding,padding+yInterval,mPaintDataLine);
        canvas.drawLine(padding, padding+yInterval*2,mWidth-padding,padding+yInterval*2,mPaintDataLine);
        for (int i=0;i<6;i++){
            canvas.drawText(xAxis[i],padding+i*xInterval,6+nameSize+padding+yInterval*2,mPaintText);
        }


        //y轴
            for (int i = 0; i < 3; i++) {
                canvas.drawText(yAxis[i]+"",ScreenUtil.dip2px(getContext(),20), padding+yInterval * i+8, mPaintText);
            }


        drawIncomeLine(canvas,yHeight,xPointInterval);
        drawPayLine(canvas,yHeight,xPointInterval);

    }

    private void drawPayLine(Canvas canvas, int yHeight, int xPointInterval) {
        //pay画点连线
        for (int i=0;i<lastMonth;i++) {
            if (expenditureList[i]==1.0){
                canvas.drawCircle(padding + i * xPointInterval, padding + (1-expenditureList[i]) * yHeight,2, mPaintChartPayPoint);
                expLoc[i] = padding + (1-expenditureList[i]) * yHeight;
                continue;
            }
            canvas.drawCircle(padding + i * xPointInterval, padding + (1-expenditureList[i]) * yHeight-5,2, mPaintChartPayPoint);
            expLoc[i] = padding + (1-expenditureList[i]) * yHeight-5;
        }
        mPayPath.moveTo(padding,expLoc[0]);
        for (int i=1;i<lastMonth;i++){
            mPayPath.lineTo(padding + i * xPointInterval,expLoc[i]);
        }
        canvas.drawPath(mPayPath,mPaintChartPayLine);
    }

    private void drawIncomeLine(Canvas canvas, int yHeight,int xPointInterval) {
        //income画点连线
        for (int i=0;i<lastMonth;i++) {
            if (incomeList[i]==1.0){
                canvas.drawCircle(padding + i * xPointInterval, padding + (1-incomeList[i]) * yHeight,2, mPaintChartIncomePoint);
                incomeLoc[i] = padding + (1-incomeList[i]) * yHeight;
                continue;
            }
            canvas.drawCircle(padding + i * xPointInterval, padding + (1-incomeList[i]) * yHeight-5,2, mPaintChartIncomePoint);
            incomeLoc[i] = padding + (1-incomeList[i]) * yHeight-5;
        }
        mIncomePath.moveTo(padding,incomeLoc[0]);
        for (int i=1;i<lastMonth;i++){
            mIncomePath.lineTo(padding + i * xPointInterval,incomeLoc[i]);
        }
        canvas.drawPath(mIncomePath,mPaintChartIncomeLine);
        canvas.save();
    }

    public void incomeView(int state){
        switch (state){
            case 1:
                mPaintChartIncomeLine.setAlpha(0);
                mPaintChartIncomePoint.setAlpha(0);
                break;
            case 0:
                mPaintChartIncomeLine.setAlpha(255);
                mPaintChartIncomePoint.setAlpha(255);
                break;
        }
        invalidate();
    }

    public void payView(int state){
        switch (state){
            case 1:
                mPaintChartPayLine.setAlpha(0);
                mPaintChartPayPoint.setAlpha(0);
                break;
            case 0:
                mPaintChartPayLine.setAlpha(255);
                mPaintChartPayPoint.setAlpha(255);
                break;
        }
        invalidate();
    }


}
