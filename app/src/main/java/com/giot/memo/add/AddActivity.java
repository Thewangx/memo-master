package com.giot.memo.add;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.giot.memo.BaseActivity;
import com.giot.memo.R;
import com.giot.memo.data.entity.Bill;
import com.giot.memo.util.KeyboardUtil;
import com.giot.memo.util.ScreenUtil;
import com.giot.memo.view.PageIndicatorView;
import com.giot.memo.view.PageRecyclerView;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddActivity extends BaseActivity implements AddContract.View {

    @BindView(R.id.textView_add_income)
    TextView incomeTextView;
    @BindView(R.id.textView_add_expenditure)
    TextView expenditureTextView;
    @BindView(R.id.editText_add_money)
    EditText moneyEditText;
    @BindView(R.id.recycler_add_type)
    PageRecyclerView typeRecycler;
    @BindView(R.id.auto_add_remark)
    AutoCompleteTextView remarkAuto;
    @BindView(R.id.indicator_add_type)
    PageIndicatorView typeIndicator;
    @BindView(R.id.textView_add_status)
    TextView statusTextView;

    private AddContract.Presenter mPresenter;
    private TypeAdapter mAdapter;
    private KeyboardUtil keyboardUtil;
    private Date mDate;
    public static int itemWidth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        Bill bill = null;
        if (bundle != null) {
            bill = (Bill) bundle.getSerializable("bill");
            mDate = (Date) bundle.getSerializable("date");
        }
        initView();
        initListener();
        itemWidth = (ScreenUtil.getScreenSize(this).widthPixels-ScreenUtil.dip2px(this,60))/5;
        new AddPresenter(this);
        mPresenter.setModify(bill != null, bill);
        if (mDate != null) {
            mPresenter.setDate(mDate);
        }
        mPresenter.start();
        String[] histories = mPresenter.loadRemarkHistory();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(AddActivity.this,
                R.layout.item_history_remark, histories);
        remarkAuto.setAdapter(adapter);
    }

    private void initView() {
        mAdapter = new TypeAdapter(this);
        typeRecycler.setIndicator(typeIndicator);
        typeRecycler.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL));
        typeRecycler.setAdapter(mAdapter);
        typeRecycler.setItemAnimator(new DefaultItemAnimator());
        keyboardUtil = new KeyboardUtil(AddActivity.this, moneyEditText);
        keyboardUtil.hideSoftInputMethod();

    }

    private void initListener() {
        incomeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMode(Bill.INCOME);
                incomeTextView.setTextColor(Color.WHITE);
                incomeTextView.setBackgroundResource(R.drawable.mode_income_selected);
                expenditureTextView.setTextColor(Color.parseColor("#989FA9"));
                expenditureTextView.setBackgroundResource(R.drawable.mode_expenditure);
                statusTextView.setText("+");
            }
        });
        expenditureTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMode(Bill.PAY);
                incomeTextView.setTextColor(Color.parseColor("#989FA9"));
                incomeTextView.setBackgroundResource(R.drawable.mode_income);
                expenditureTextView.setTextColor(Color.WHITE);
                expenditureTextView.setBackgroundResource(R.drawable.mode_expenditure_selected);
                statusTextView.setText("-");
            }
        });
        mAdapter.setOnItemClickListener(new TypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mPresenter.setType(mAdapter.getType());
            }
        });
        if (keyboardUtil != null) {
            keyboardUtil.setCompleteListener(new KeyboardUtil.Callback() {
                @Override
                public void onComplete() {
                    if (!TextUtils.isEmpty(moneyEditText.getText().toString())) {
                        mPresenter.saveBill(moneyEditText.getText().toString(), remarkAuto.getText().toString());
                    }
                }
            });
        }
        moneyEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager manager = (InputMethodManager) AddActivity.this
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        });

    }

    //更改账单的模式(支出或者收入)
    @Override
    public void changeMode(int mode) {
        mAdapter.setMode(mode);
        typeRecycler.update(mAdapter);
        mPresenter.changeMode(mode);
    }

    //添加账单到数据库之后关闭添加界面
    @Override
    public void saveBill() {
        finish();
    }

    /**
     * 当前是修改状态时, 初始化UI数据
     *
     * @param bill 待修改原始数据
     */
    @Override
    public void initUIData(Bill bill) {
        moneyEditText.setText(String.valueOf(bill.getMoney()));
        if (!TextUtils.isEmpty(bill.getRemark())) {
            remarkAuto.setText(bill.getRemark());
        }
        if (bill.getMode() == Bill.INCOME) {
            incomeTextView.setTextColor(Color.WHITE);
            incomeTextView.setBackgroundResource(R.drawable.mode_income_selected);
            expenditureTextView.setTextColor(Color.parseColor("#989FA9"));
            expenditureTextView.setBackgroundResource(R.drawable.mode_expenditure);
            statusTextView.setText("+");
        } else {
            changeMode(Bill.PAY);
            incomeTextView.setTextColor(Color.parseColor("#989FA9"));
            incomeTextView.setBackgroundResource(R.drawable.mode_income);
            expenditureTextView.setTextColor(Color.WHITE);
            expenditureTextView.setBackgroundResource(R.drawable.mode_expenditure_selected);
            statusTextView.setText("-");
        }
    }

    @Override
    public void setType(String type) {
        mAdapter.setSelected(type);
    }

    @Override
    public void setPresenter(AddContract.Presenter presenter) {
        if (presenter == null) {
            throw new NullPointerException("addPresenter is null");
        }
        mPresenter = presenter;
    }


}
