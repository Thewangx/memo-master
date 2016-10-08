package com.giot.memo.search;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.giot.memo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * search histories viewHolder
 * Created by reed on 16/8/4.
 */
public class SearchViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.textView_item_history)
    public TextView historyTextView;
    @BindView(R.id.imageView_item_del)
    public ImageView delImageView;

    public SearchViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bindData(String history) {
        historyTextView.setText(history);
    }
}
