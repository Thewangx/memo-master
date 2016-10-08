package com.giot.memo.add;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.giot.memo.R;
import com.giot.memo.data.entity.Bill;

/**
 * 类别的Adapter
 * Created by reed on 16/8/1.
 */
public class TypeAdapter extends RecyclerView.Adapter {

    private TypedArray images;
    private TypedArray imagesSelected;
    private String[] types;
    private int selected = 0;
    private Context context;

    public void setMode(int mode) {
        selected = 0;
        if (mode == Bill.INCOME) {
            types = context.getResources().getStringArray(R.array.type_income);
            images = context.getResources().obtainTypedArray(R.array.type_income_ic);
            imagesSelected = context.getResources().obtainTypedArray(R.array.type_income_ic_selected);
        } else {
            types = context.getResources().getStringArray(R.array.type_expenditure);
            images = context.getResources().obtainTypedArray(R.array.type_expenditure_ic);
            imagesSelected = context.getResources().obtainTypedArray(R.array.type_expenditure_ic_selected);

        }
        notifyDataSetChanged();
    }

    public TypeAdapter(Context context) {
        this.context = context;
        types = context.getResources().getStringArray(R.array.type_expenditure);
        images = context.getResources().obtainTypedArray(R.array.type_expenditure_ic);
        imagesSelected = context.getResources().obtainTypedArray(R.array.type_expenditure_ic_selected);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TypeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_type, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ((TypeViewHolder) holder).itemView.getLayoutParams().width = AddActivity.itemWidth;
        if (types[position].equals("空白")) {
            return;
        }
        Drawable image;
        final boolean status;
        if (position == selected) {
            status = true;
            image = imagesSelected.getDrawable(position);
        } else {
            status = false;
            image = images.getDrawable(position);
        }
        ((TypeViewHolder) holder).bindData(status, image, types[position]);
        ((TypeViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    selected = holder.getAdapterPosition();
                    listener.onItemClick(v, holder.getAdapterPosition());
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (images == null || types == null) {
            return 0;
        }
        if (images.length() != types.length) {
            throw new IllegalArgumentException("the images.length must be equal to types.length");
        }
        return types.length;
    }

    public String getType() {
        return types[selected];
    }

    public void setSelected(String type) {
        for (int i = 0;i<types.length;i++) {
            if (type.equals(types[i])) {
                selected = i;
            }
        }
        notifyDataSetChanged();
    }



}
