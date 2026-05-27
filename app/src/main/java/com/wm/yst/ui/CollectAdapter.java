package com.wm.yst.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wm.yst.R;
import com.wm.yst.model.Collect;

import java.util.ArrayList;
import java.util.List;

public class CollectAdapter extends RecyclerView.Adapter<CollectAdapter.CollectViewHolder> {
    private final List<Collect> collectList = new ArrayList<>();
    private final OnCollectClickListener listener;

    public CollectAdapter(OnCollectClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CollectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_collect, parent, false);
        return new CollectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectViewHolder holder, int position) {
        Collect collect = collectList.get(position);
        holder.tvTitle.setText(collect.getNewsTitle());
        holder.tvMeta.setText(buildMeta(collect));
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOpen(collect);
            }
        });
        holder.tvRemove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemove(collect);
            }
        });
    }

    @Override
    public int getItemCount() {
        return collectList.size();
    }

    public void submitList(List<Collect> items) {
        collectList.clear();
        if (items != null) {
            collectList.addAll(items);
        }
        notifyDataSetChanged();
    }

    private String buildMeta(Collect collect) {
        String source = collect.getNewsSource() == null ? "" : collect.getNewsSource();
        String category = collect.getCategory() == null ? "" : collect.getCategory();
        String date = collect.getNewsDate() == null ? "" : collect.getNewsDate();
        return source + "  " + category + "  " + date;
    }

    public interface OnCollectClickListener {
        void onOpen(Collect collect);

        void onRemove(Collect collect);
    }

    static class CollectViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvMeta;
        private final TextView tvRemove;

        CollectViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMeta = itemView.findViewById(R.id.tvMeta);
            tvRemove = itemView.findViewById(R.id.tvRemove);
        }
    }
}
