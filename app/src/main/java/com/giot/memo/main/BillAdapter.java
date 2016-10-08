package com.giot.memo.main;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.giot.memo.R;
import com.giot.memo.data.entity.Bill;

import java.util.List;

/**
 * 主界面账单展示的Adapter
 * Created by reed on 16/7/28.
 */
public class BillAdapter extends RecyclerView.Adapter {

    private List<Bill> bills;
    private Context context;

    public void setBills(List<Bill> bills) {
        this.bills = bills;
    }

    public interface OnItemClickListener {
        void onClick(View view, int position);

        void onDelClick(View view, int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new BillViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_bill, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        TypedArray images;
        String[] types;
        if (bills != null) {
            if (bills.get(position).getMode() == Bill.INCOME) {
                types = context.getResources().getStringArray(R.array.type_income);
                images = context.getResources().obtainTypedArray(R.array.type_income_ic);
            } else {
                types = context.getResources().getStringArray(R.array.type_expenditure);
                images = context.getResources().obtainTypedArray(R.array.type_expenditure_ic);
            }
            String type = bills.get(position).getType();
            int temp = -1;
            for (int i = 0; i < types.length; i++) {
                if (types[i].equals(type)) {
                    temp = i;
                    break;
                }
            }
            ((BillViewHolder) holder).bindData(images.getDrawable(temp), bills.get(position));
            images.recycle();
            ((BillViewHolder) holder).deleteItem(onItemClickListener);
        }

        //加载动画
        /*Animator animator = ObjectAnimator.ofFloat(holder.itemView, "alpha", 0f, 1f);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(600).start();*/
    }

    @Override
    public int getItemCount() {
        return bills == null ? 0 : bills.size();
    }

    public List<Bill> getBills() {
        return bills;
    }
}
