package com.giot.memo.main;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.giot.memo.R;
import com.giot.memo.data.entity.Bill;
import com.giot.memo.util.TransformUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 主页账单的viewHolder
 * Created by reed on 16/7/28.
 */
public class BillViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.imageView_bill_icon)
    public ImageView iconImageView;
    @BindView(R.id.textView_bill_type)
    public TextView typeTextView;
    @BindView(R.id.textView_bill_money)
    public TextView moneyTextView;
    @BindView(R.id.textView_bill_remark)
    public TextView remarkTextView;
    @BindView(R.id.cardView_bill)
    public CardView billCard;
    //@BindView(R.id.imageView_bill_del)
    //public ImageView delImageView;

    //private boolean isDel = false;

    private BillAdapter.OnItemClickListener onItemClickListener;

    public void deleteItem(BillAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public BillViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bindData(Drawable drawable, final Bill bill) {
        String money;
        if (bill.getMode() == Bill.INCOME) {
            money = "+ ";
            moneyTextView.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.income_color));
        } else {
            money = "- ";
            moneyTextView.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.pay_color));
        }
        money += bill.getMoney();
        moneyTextView.setText(TransformUtil.deleteZero(money));
        if (TextUtils.isEmpty(bill.getRemark())) {
            remarkTextView.setText("暂无备注");
        } else {
            remarkTextView.setText(bill.getRemark());
        }
        typeTextView.setText(bill.getType());
        iconImageView.setImageDrawable(drawable);
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                /*if (!isDel) {
                    isDel = true;
                    delImageView.setVisibility(View.VISIBLE);
                }*/
                billCard.setCardBackgroundColor(Color.parseColor("#E6EE9C"));
                AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                builder.setMessage("您确定删除该条账单?");
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onDelClick(v, getAdapterPosition());
                        }

                    }
                });
                builder.setNegativeButton("取消", null);
                builder.setCancelable(true);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        billCard.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                    }
                });
                builder.create().show();
                return true;
            }
        });
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(v, getAdapterPosition());
                }

            }
        });
        /*delImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onDelClick(v, getAdapterPosition());
                }
            }
        });*/

    }


}
