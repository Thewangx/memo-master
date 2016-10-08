package com.giot.memo.analysis.overall;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.giot.memo.R;
import com.giot.memo.data.entity.CountBill;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mr.wang on 2016/9/29.
 */

public class OverallViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.month)
    TextView month;
    @BindView(R.id.month_start)
    TextView monthStart;
    @BindView(R.id.month_end)
    TextView monthEnd;
    @BindView(R.id.textView_income_overall)
    TextView incomeOverall;
    @BindView(R.id.textView_expenditure_overall)
    TextView expenditureOverall;
    @BindView(R.id.textVew_last_overall)
    TextView totalOverall;

    public OverallViewHolder(ViewGroup parent){
        this(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_analysis_bill_overall,parent,false));
    }



    public OverallViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);

    }

    public void bindData(CountBill countBill){
        int transMonth = Integer.parseInt(countBill.getMonth());
        SimpleDateFormat format = new SimpleDateFormat("MM-dd");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH,transMonth-1);
        c.set(Calendar.DAY_OF_MONTH,1);
        String first = format.format(c.getTime());
        monthStart.setText(first);
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.MONTH,transMonth);
        ca.set(Calendar.DAY_OF_MONTH, 0);
        String end = format.format(ca.getTime());
        monthEnd.setText(end);

        month.setText(countBill.getMonth());
        DecimalFormat decimalFormat=new DecimalFormat("0.00");
        incomeOverall.setText("+"+decimalFormat.format(countBill.getIncome()));
        expenditureOverall.setText("-"+decimalFormat.format(countBill.getExpenditure()));


        String total;
        if ((countBill.getIncome()-countBill.getExpenditure())>0){
            total = "+"+decimalFormat.format(countBill.getTotal());
            totalOverall.setTextColor(ContextCompat.getColor(itemView.getContext(),R.color.income_color));
            totalOverall.setText(total);
        }else if ((countBill.getIncome()-countBill.getExpenditure()<0)){
            total = decimalFormat.format(countBill.getTotal())+"";
            totalOverall.setTextColor(ContextCompat.getColor(itemView.getContext(),R.color.pay_color));
            totalOverall.setText(total);
        }else {
            totalOverall.setTextColor(ContextCompat.getColor(itemView.getContext(),R.color.overall_zero));
            totalOverall.setText("0.00");
        }




    }






}
