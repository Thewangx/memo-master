package com.giot.memo.result;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.giot.memo.BaseActivity;
import com.giot.memo.R;
import com.giot.memo.data.entity.Bill;
import com.giot.memo.main.BillAdapter;
import com.giot.memo.search.SearchActivity;
import com.giot.memo.util.SysConstants;
import com.giot.memo.view.DividerItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultActivity extends BaseActivity implements ResultContract.View {

    @BindView(R.id.textView_result)
    TextView resultTextView;
    @BindView(R.id.textView_result_cancel)
    TextView cancelTextView;
    @BindView(R.id.textView_result_money)
    TextView moneyTextView;
    @BindView(R.id.recycler_result)
    RecyclerView resultRecycler;

    private BillAdapter mAdapter;
    private ResultContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);
        String key = getIntent().getStringExtra(SysConstants.KEY);
        initView(key);
        initListener();
        new ResultPresenter(this);
        mPresenter.start(key);
    }

    private void initView(String key) {
        resultTextView.setText(key);
        mAdapter = new BillAdapter();
        resultRecycler.setLayoutManager(new LinearLayoutManager(this));
        resultRecycler.setAdapter(mAdapter);
        resultRecycler.setItemAnimator(new DefaultItemAnimator());
        resultRecycler.addItemDecoration(new DividerItemDecoration(this));

    }

    private void initListener() {
        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        resultTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, SearchActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void setPresenter(ResultContract.Presenter presenter) {
        if (presenter == null) {
            throw new NullPointerException("the result presenter is null");
        }
        mPresenter = presenter;
    }

    /**
     * 展示账单搜索结果
     * @param bills 查找到的账单
     * @param count 查找到账单的收支总结果
     */
    @Override
    public void showBill(List<Bill> bills, int count) {
        mAdapter.setBills(bills);
        mAdapter.notifyDataSetChanged();
        moneyTextView.setText(String.valueOf(count));
        if (count >= 0) {
            moneyTextView.setTextColor(Color.parseColor("#2CEA78"));
        } else {
            moneyTextView.setTextColor(Color.parseColor("#F50B0B"));
        }
    }
}
