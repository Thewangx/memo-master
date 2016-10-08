package com.giot.memo.analysis;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.giot.memo.BaseActivity;
import com.giot.memo.R;
import com.giot.memo.analysis.detail.DetailFragment;
import com.giot.memo.analysis.detail.DetailPresenter;
import com.giot.memo.analysis.overall.OverallFragment;
import com.giot.memo.analysis.overall.OverallPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AnalysisActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.toolbar_analysis)
    Toolbar analysisToolbar;
    @BindView(R.id.fab_analysis_toggle)
    FloatingActionButton toggleFAB;

    private FragmentManager mFragmentManager;
    private DetailFragment detailFragment;
    private OverallFragment overallFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);
        ButterKnife.bind(this);
        initView();
        initListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_analysis_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.evaluation:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        analysisToolbar.setTitle("");
        setSupportActionBar(analysisToolbar);
        mFragmentManager = getFragmentManager();
        detailFragment = new DetailFragment();
        overallFragment = new OverallFragment();
        mFragmentManager.beginTransaction().replace(R.id.frame_analysis, detailFragment).commit();
        new DetailPresenter(detailFragment, this);
        new OverallPresenter(overallFragment);
    }

    private void initListener() {
        analysisToolbar.setNavigationOnClickListener(this);
        toggleFAB.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_analysis_toggle:
                if (mFragmentManager.findFragmentById(R.id.frame_analysis) instanceof DetailFragment) {
                    mFragmentManager.beginTransaction()
                            .remove(overallFragment)
                            .setCustomAnimations(
                                    R.animator.card_flip_right_in,
                                    R.animator.card_flip_right_out,
                                    R.animator.card_flip_left_in,
                                    R.animator.card_flip_left_out)
                            .replace(R.id.frame_analysis, overallFragment).commit();
                } else {
                    mFragmentManager.beginTransaction()
                            .remove(detailFragment)
                            .setCustomAnimations(
                                    R.animator.card_flip_right_in,
                                    R.animator.card_flip_right_out,
                                    R.animator.card_flip_left_in,
                                    R.animator.card_flip_left_out)
                            .replace(R.id.frame_analysis, detailFragment).commit();
                }
                break;
            default:
                finish();
                break;
        }
    }
}
