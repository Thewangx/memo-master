package com.giot.memo.add;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.giot.memo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 类别viewHolder
 * Created by reed on 16/8/1.
 */
public class TypeViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.textView_item_type)
    public TextView typeTextView;

    public TypeViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }



    public void bindData(boolean status, Drawable image, String type) {
        if (status) {
            typeTextView.setTextColor(Color.parseColor("#003366"));
            typeTextView.setBackgroundColor(Color.parseColor("#FFF6D7"));
        } else {
            typeTextView.setTextColor(Color.parseColor("#989FA9"));
            typeTextView.setBackgroundColor(Color.TRANSPARENT);
        }
        typeTextView.setText(type);
        image.setBounds(0, 0, image.getMinimumWidth(), image.getMinimumHeight());
        typeTextView.setCompoundDrawables(null, image, null, null);

    }
}
