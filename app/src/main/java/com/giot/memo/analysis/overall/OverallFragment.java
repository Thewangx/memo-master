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

//    @Override
//    public void initChart(List<CountBill> countBills) {
//        mChart.setDrawGridBackground(false);
//        mChart.setDragEnabled(true);
//        mChart.setScaleEnabled(false);
//        mChart.setPinchZoom(true);
//        mChart.getAxisRight().setEnabled(false);
//        mChart.setDescription("");
//        mChart.setNoDataText("你尚未留下任何账单");
//        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
//            @Override
//            public void onValueSelected(Entry e, Highlight h) {
//                mPresenter.showDetail((int) e.getX());
//            }
//
//            @Override
//            public void onNothingSelected() {
//
//            }
//        });
//
//        Legend l = mChart.getLegend();
//        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);
//        l.setForm(Legend.LegendForm.LINE);
//        l.setFormSize(16);
//        l.setTextSize(11f);
//        l.setXEntrySpace(4f);
//
//        //设置 X 轴样式
//        XAxis xAxis = mChart.getXAxis();
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setDrawGridLines(false);
//        xAxis.setDrawAxisLine(true);
//        if (countBills.size() > 6) {
//            xAxis.setLabelCount(6);
//        } else {
//            xAxis.setLabelCount(countBills.size());
//        }
//        xAxis.setTextColor(Color.parseColor("#B0B0B0"));
//
//        MonthAxisValueFormatter xAxisFormatter = new MonthAxisValueFormatter(countBills);
//
//        xAxis.setValueFormatter(xAxisFormatter);
//
//        //设置 Y 轴样式(左边)
//        YAxis yAxis = mChart.getAxisLeft();
//        yAxis.setLabelCount(5, false);
//        yAxis.setDrawGridLines(false);
//        yAxis.setAxisMinValue(0f);
//        yAxis.setTextColor(Color.parseColor("#B0B0B0"));
//        yAxis.setZeroLineColor(Color.parseColor("#B0B0B0"));
//
//        List<Entry> incomeEntry = new ArrayList<>();
//        List<Entry> expenditureEntry = new ArrayList<>();
//
//        for (int i = 0; i < countBills.size(); i++) {
//            incomeEntry.add(new Entry(i, countBills.get(i).getIncome()));
//            expenditureEntry.add(new Entry(i, countBills.get(i).getExpenditure()));
//        }
//
//        LineDataSet incomeSet = new LineDataSet(incomeEntry, "收入");
//        incomeSet.setLineWidth(6);
//        incomeSet.setCircleRadius(6);
//        incomeSet.setCircleColor(ContextCompat.getColor(getActivity(), R.color.income_color));
//        incomeSet.setColor(ContextCompat.getColor(getActivity(), R.color.income_color));
//        incomeSet.setDrawValues(true);
//
//        LineDataSet expenditureSet = new LineDataSet(expenditureEntry, "支出");
//        expenditureSet.setLineWidth(6);
//        expenditureSet.setCircleRadius(6);
//        expenditureSet.setColor(ContextCompat.getColor(getActivity(), R.color.pay_color));
//        expenditureSet.setCircleColor(ContextCompat.getColor(getActivity(), R.color.pay_color));
//        expenditureSet.setDrawValues(true);
//
//        ArrayList<ILineDataSet> sets = new ArrayList<>();
//        sets.add(incomeSet);
//        sets.add(expenditureSet);
//
//        LineData data = new LineData(sets);
//
//        mChart.setData(data);
//
//        mChart.animateX(750);
//    }
//
//    @Override
//    public void showDetail(CountBill countBill) {
//        String income = "+ " + countBill.getIncome();
//        incomeTextView.setText(TransformUtil.deleteZero(income));
//        String expenditure = "- " + countBill.getExpenditure();
//        expenditureTextView.setText(TransformUtil.deleteZero(expenditure));
//        String month = countBill.getMonth() + "月";
//        monthTextView.setText(month);
//        String last;
//        if (countBill.getLast() >= 0) {
//            lastTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.income_color));
//            last = "+ " + countBill.getLast();
//        } else {
//            last = "- " + Math.abs(countBill.getLast());
//            lastTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.pay_color));
//        }
//        lastTextView.setText(TransformUtil.deleteZero(last));
//    }
}
