package com.giot.memo.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.giot.memo.BaseActivity;
import com.giot.memo.R;
import com.giot.memo.data.entity.History;
import com.giot.memo.result.ResultActivity;
import com.giot.memo.util.SysConstants;
import com.giot.memo.view.DividerItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends BaseActivity implements SearchContract.View {

    @BindView(R.id.editText_search)
    EditText searchEditText;
    @BindView(R.id.textView_search_cancel)
    TextView cancelTextView;
    @BindView(R.id.recycler_search)
    RecyclerView searchRecycler;

    private SearchAdapter mAdapter;
    private SearchContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        initView();
        initListener();
        new SearchPresenter(this);
        mPresenter.start();
    }

    private void initView() {
        mAdapter = new SearchAdapter();
        searchRecycler.setLayoutManager(new LinearLayoutManager(this));
        searchRecycler.setAdapter(mAdapter);
        searchRecycler.setItemAnimator(new DefaultItemAnimator());
        searchRecycler.addItemDecoration(new DividerItemDecoration(this, (float) 16));
    }

    private void initListener() {
        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        searchEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {// 修改回车键功能
                    // 先隐藏键盘
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                            v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    showResult();

                }
                return false;
            }
        });
        mAdapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mPresenter.setSearchKey(position);
            }

            @Override
            public void onDelClick(View view, int position) {
                mPresenter.delHistory(position);
            }
        });
    }

    @Override
    public void setPresenter(SearchContract.Presenter presenter) {
        if (presenter == null) {
            throw new NullPointerException("the search presenter is null");
        }
        mPresenter = presenter;
    }

    /**
     * 展示搜索结果
     */
    @Override
    public void showResult() {
        mPresenter.saveHistory(searchEditText.getText().toString());
        Intent intent = new Intent(SearchActivity.this, ResultActivity.class);
        intent.putExtra(SysConstants.KEY, searchEditText.getText().toString());
        startActivity(intent);
        finish();
    }

    /**
     * 载入数据库中的搜索历史记录
     * @param histories 历史记录
     */
    @Override
    public void loadHistories(List<History> histories) {
        mAdapter.setHistories(histories);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void setSearchKey(String key) {
        searchEditText.setText(key);
    }

    @Override
    public void delHistory(int position) {
        mAdapter.notifyItemRemoved(position);
    }
}
