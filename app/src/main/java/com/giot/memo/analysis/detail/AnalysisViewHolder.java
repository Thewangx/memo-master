package com.giot.memo.analysis.detail;

import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.giot.memo.R;
import com.giot.memo.data.entity.Analysis;
import com.giot.memo.data.entity.Bill;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 账单分析界面账单详情
 * Created by reed on 16/8/17.
 */
public class AnalysisViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.imageView_analysis_flag)
    ImageView flagImageView;
    @BindView(R.id.imageView_analysis_icon)
    ImageView iconImageView;
    @BindView(R.id.textView_analysis_type)
    TextView typeTextView;
    @BindView(R.id.textView_analysis_money)
    TextView moneyTextView;

    private TypedArray array;

    //初始化布局
    public AnalysisViewHolder(ViewGroup parent) {
        this(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_analysis_bill, parent, false));
        array = parent.getContext().getResources().obtainTypedArray(R.array.analysis_color);
    }

    public AnalysisViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    /**
     * 因传递至adapter的analysisList是进过排序的, 所以position位置也反映占比大小
     * @param analysis analysis
     * @param position 位置, 便于读取占比大小对应的颜色
     */
    public void bindData(Analysis analysis, int position) {
        flagImageView.setBackgroundResource(array.getResourceId(position, R.color.analysis_six));
        String money;
        if (analysis.getMode() == Bill.PAY) {
            money = "- ";
            moneyTextView.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.pay_color));
        } else {
            money = "+ ";
            moneyTextView.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.income_color));
        }
        money += analysis.getMoney();
        if (money.charAt(money.length() - 1) == '0') {//判断倒数第一位是否为0
            money = money.substring(0, money.length() - 2);
        }
        moneyTextView.setText(money);
        typeTextView.setText(analysis.getType());
        iconImageView.setImageDrawable(analysis.getDrawable());

    }
}
