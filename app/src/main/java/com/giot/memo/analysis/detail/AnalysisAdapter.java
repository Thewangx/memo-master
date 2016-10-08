package com.giot.memo.analysis.detail;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.giot.memo.data.entity.Analysis;

import java.util.ArrayList;
import java.util.List;

/**
 * 账单分析界面RecyclerView的adapter
 * Created by reed on 16/8/17.
 */
public class AnalysisAdapter extends RecyclerView.Adapter {

    List<Analysis> analysisList = new ArrayList<>();

    /**
     * 设置analysisList数据集合, 该数据集里的数据需要经过排序后才能传入
     * @param analysisList analysisList
     */
    public void setAnalysisList(List<Analysis> analysisList) {
        this.analysisList = analysisList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AnalysisViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((AnalysisViewHolder) holder).bindData(analysisList.get(position), position);
    }

    /**
     * 因避免账单分析中小比例分类太多, 故最多只展示出金额最多的五个分类, 之后的分类统一归为其他
     * @return 条目个数
     */
    @Override
    public int getItemCount() {
        return analysisList.size() <= 6 ? analysisList.size() : 6;
    }
}
