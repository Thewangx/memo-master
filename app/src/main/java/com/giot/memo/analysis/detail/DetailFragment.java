package com.giot.memo.analysis.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.giot.memo.BaseFragment;
import com.giot.memo.R;
import com.giot.memo.data.entity.Analysis;
import com.giot.memo.view.DividerItemDecoration;
import com.giot.memo.view.RingChart;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 账单分析详情
 * Created by reed on 16/8/16.
 */
public class DetailFragment extends BaseFragment implements DetailContract.View, AdapterView.OnItemSelectedListener {


    @BindView(R.id.spinner_analysis_mode)
    AppCompatSpinner modeSpinner;
    @BindView(R.id.textView_analysis_total)
    TextView totalTextView;
    @BindView(R.id.recycler_analysis)
    RecyclerView analysisRecycler;
    @BindView(R.id.ringChart_analysis)
    RingChart mRingChart;

    private DetailContract.Presenter mPresenter;
    private Unbinder unbinder;
    private AnalysisAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_detail, container, false);
        unbinder = ButterKnife.bind(this, mView);
        mAdapter = new AnalysisAdapter();
        analysisRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        analysisRecycler.setAdapter(mAdapter);
        analysisRecycler.addItemDecoration(new DividerItemDecoration(container.getContext(),LinearLayoutManager.VERTICAL));
        modeSpinner.setOnItemSelectedListener(this);
        //文字点击监听事件
        mRingChart.setOnTextClickListener(new RingChart.OnTextClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.switchBillMode();
            }
        });
        //左右滑动切换事件监听事件
        mRingChart.setOnScrollChangeListener(new RingChart.OnScrollChangeListener() {
            @Override
            public void onChange(View v, boolean isLeft) {
                if (isLeft) {//向左滑
                    mPresenter.preDate();
                } else {//向右滑
                    mPresenter.nextDate();
                }
            }
        });
        return mView;
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
    public void setPresenter(DetailContract.Presenter presenter) {
        if (presenter == null) {
            throw new NullPointerException("detail presenter is null");
        }
        mPresenter = presenter;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mPresenter.setSpinnerMode(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * 更新spinner的选项信息
     *
     * @param items    选项信息
     * @param position 制定spinner的显示内容(保持原有的周模式或者月模式)
     */
    @Override
    public void updateSpinner(String[] items, int position) {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_analysis_change, items);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_analysis_drop);
        modeSpinner.setAdapter(spinnerAdapter);
        modeSpinner.setSelection(position);

    }

    /**
     * 更新recyclerView和总计的数据
     *
     * @param analysisList 数据源
     * @param count        总计金额
     */
    @Override
    public void updateRecycler(List<Analysis> analysisList, String count) {
        mAdapter.setAnalysisList(analysisList);
        mAdapter.notifyDataSetChanged();
        totalTextView.setText(count);
        if (count.contains("-")) {
            totalTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.pay_color));
        } else {
            totalTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.income_color));
        }
    }

    /**
     * 更新环形图数据
     * @param percent 各比例组成的数组, 数组大小不能超过6个
     */
    @Override
    public void updateChart(float[] percent) {
        mRingChart.setPercent(percent);
    }

    //切换收入支出时更新图标的文字(支出或者收入)
    @Override
    public void switchChart(String text) {
        mRingChart.setName(text);
    }
}
