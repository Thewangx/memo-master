package com.giot.memo.analysis.overall;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.giot.memo.data.entity.CountBill;
import com.giot.memo.util.CutUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr.wang on 2016/9/29.
 */

public class OverallAdapter extends RecyclerView.Adapter {

    List<CountBill> countBills = new ArrayList<>();


    public void setOverallCountBill(List<CountBill> countBills){
        this.countBills = countBills;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OverallViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (countBills.get(position).getIncome()==0&&countBills.get(position).getTotal()==0){
            return;
        }
        ((OverallViewHolder)holder).bindData(countBills.get(position));

    }

    @Override
    public int getItemCount() {
        return CutUtils.cut(countBills).size();
    }
}
