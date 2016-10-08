package com.giot.memo.analysis.overall;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.giot.memo.BaseFragment;
import com.giot.memo.R;
import com.giot.memo.data.entity.CountBill;
import com.giot.memo.util.MaxValueUtil;
import com.giot.memo.view.DividerItemDecoration;
import com.giot.memo.view.LineChart;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 总体分析界面
 * 此界面用到的折线图采用第三库, 介绍和用法参见https://github.com/PhilJay/MPAndroidChart 修改中
 * Created by reed on 16/8/16.
 */
public class OverallFragment extends BaseFragment implements OverallContract.View {

    @BindView(R.id.income_show)
    Button income_show;
    @BindView(R.id.expenditure_show)
    Button expenditure_show;
    @BindView(R.id.overall_recycler)
    RecyclerView overallRecycler;
    @BindView(R.id.overall_chat)
    LineChart lineChart;

    private OverallContract.Presenter mPresenter;
    private Unbinder unbinder;
    private OverallAdapter overallAdapter;
    private float[] expenditureList = new float[12];
    private float[] incomeList = new float[12];
    private int maxValue;
    private int lastMonth;
    private int incomeState = 0;
    private int expenditureState = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overall, container, false);
        unbinder = ButterKnife.bind(this, view);
        overallAdapter = new OverallAdapter();
        overallRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        overallRecycler.setAdapter(overallAdapter);
        overallRecycler.addItemDecoration(new DividerItemDecoration(container.getContext(), 1, LinearLayoutManager.VERTICAL));
        income_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (incomeState) {
                    case 0:
                        income_show.setBackgroundResource(R.drawable.chat_button);
                        income_show.setTextColor(getResources().getColor(R.color.overall_zero));
                        incomeState = 1;
                        lineChart.incomeView(incomeState);
                        break;
                    case 1:
                        income_show.setBackgroundResource(R.drawable.chat_button_income);
                        income_show.setTextColor(getResources().getColor(R.color.default_text_color));
                        incomeState = 0;
                        lineChart.incomeView(incomeState);
                        break;
                }
            }
        });
        expenditure_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (expenditureState) {
                    case 0:
                        expenditure_show.setBackgroundResource(R.drawable.chat_button);
                        expenditure_show.setTextColor(getResources().getColor(R.color.overall_zero));
                        expenditureState = 1;
                        lineChart.payView(expenditureState);
                        break;
                    case 1:
                        expenditure_show.setBackgroundResource(R.drawable.chat_button_exp);
                        expenditure_show.setTextColor(getResources().getColor(R.color.default_text_color));
                        expenditureState = 0;
                        lineChart.payView(expenditureState);
                        break;
                }

            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void setPresenter(OverallContract.Presenter presenter) {
        if (presenter == null) {
            throw new NullPointerException("overall presenter is null");
        }
        mPresenter = presenter;
    }

    @Override
    public void initChart(List<CountBill> countBills) {
        lineChart.setCountBills(countBills);
        maxValue = MaxValueUtil.getMaxValue(countBills);
        int[] yAxis = {maxValue, maxValue / 2, 0};
        lineChart.setYAxis(yAxis);

        for (int i = 0; i < countBills.size(); i++) {
            incomeList[Integer.parseInt(countBills.get(i).getMonth()) - 1] = (countBills.get(i).getIncome() / maxValue);
            expenditureList[Integer.parseInt(countBills.get(i).getMonth()) - 1] = (countBills.get(i).getExpenditure() / maxValue);
        }

        lineChart.setIncomeList(incomeList);
        lineChart.setExpenditureList(expenditureList);
        lastMonth = Integer.parseInt(countBills.get(countBills.size() - 1).getMonth());
        lineChart.setLastMonth(lastMonth);


    }

    @Override
    public void showDetail(List<CountBill> countBills) {
        overallAdapter.setOverallCountBill(countBills);
        overallAdapter.notifyDataSetChanged();
    }

}