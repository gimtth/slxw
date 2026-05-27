package com.wm.yst.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wm.yst.R;
import com.wm.yst.model.NewsItem;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private final List<NewsItem> newsList = new ArrayList<>();
    private final OnNewsClickListener listener;

    public NewsAdapter(OnNewsClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsItem item = newsList.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvMeta.setText(buildMeta(item));
        Glide.with(holder.ivThumb)
                .load(item.getThumbnailPicS())
                .placeholder(R.drawable.bg_news_thumb)
                .error(R.drawable.bg_news_thumb)
                .centerCrop()
                .into(holder.ivThumb);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNewsClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public void submitList(List<NewsItem> items) {
        newsList.clear();
        if (items != null) {
            newsList.addAll(items);
        }
        notifyDataSetChanged();
    }

    private String buildMeta(NewsItem item) {
        String source = item.getAuthorName() == null ? "" : item.getAuthorName();
        String date = item.getDate() == null ? "" : item.getDate();
        String category = item.getCategory() == null ? "" : item.getCategory();
        return source + "  " + category + "  " + date;
    }

    public interface OnNewsClickListener {
        void onNewsClick(NewsItem item);
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivThumb;
        private final TextView tvTitle;
        private final TextView tvMeta;

        NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumb = itemView.findViewById(R.id.ivThumb);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMeta = itemView.findViewById(R.id.tvMeta);
        }
    }
}
