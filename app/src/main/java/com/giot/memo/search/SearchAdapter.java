package com.giot.memo.search;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.giot.memo.R;
import com.giot.memo.data.entity.History;

import java.util.List;

/**
 * 搜索历史记录adapter
 * Created by reed on 16/8/4.
 */
public class SearchAdapter extends RecyclerView.Adapter {

    private List<History> histories;

    public void setHistories(List<History> histories) {
        this.histories = histories;
    }

    public interface OnItemClickListener {

        void onItemClick(View view, int position);

        void onDelClick(View view, int position);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SearchViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_history, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ((SearchViewHolder) holder).bindData(histories.get(position).getContent());
        if (listener != null) {
            ((SearchViewHolder) holder).historyTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v, holder.getAdapterPosition());
                }
            });
            ((SearchViewHolder) holder).delImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDelClick(v, holder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return histories == null ? 0 : histories.size();
    }
}
