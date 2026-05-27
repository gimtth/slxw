package com.wm.yst.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.wm.yst.R;
import com.wm.yst.NewsDetailActivity;
import com.wm.yst.model.NewsItem;
import com.wm.yst.model.NewsListResponse;
import com.wm.yst.network.ApiConfig;
import com.wm.yst.network.NewsRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CategoryFragment extends Fragment {
    private SwipeRefreshLayout swipeRefresh;
    private TextView tvState;
    private LinearLayout categoryContainer;
    private NewsAdapter adapter;
    private final NewsRepository newsRepository = new NewsRepository();
    private final List<TextView> categoryViews = new ArrayList<>();
    private String selectedType = "top";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        categoryContainer = view.findViewById(R.id.categoryContainer);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        tvState = view.findViewById(R.id.tvState);
        RecyclerView rvNews = view.findViewById(R.id.rvNews);

        adapter = new NewsAdapter(this::openDetail);
        rvNews.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvNews.setAdapter(adapter);

        swipeRefresh.setColorSchemeResources(R.color.sl_news_blue);
        swipeRefresh.setOnRefreshListener(this::loadNews);

        setupCategories();
        loadNews();
    }

    private void setupCategories() {
        categoryContainer.removeAllViews();
        categoryViews.clear();
        for (String[] category : ApiConfig.NEWS_CATEGORIES) {
            TextView textView = new TextView(requireContext());
            textView.setText(category[0]);
            textView.setTextSize(14);
            textView.setGravity(android.view.Gravity.CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    dp(36)
            );
            params.setMargins(0, 0, dp(8), 0);
            textView.setLayoutParams(params);
            textView.setMinWidth(dp(64));
            textView.setPadding(dp(14), 0, dp(14), 0);
            textView.setOnClickListener(v -> {
                selectedType = category[1];
                updateCategoryStyle();
                loadNews();
            });
            categoryViews.add(textView);
            categoryContainer.addView(textView);
        }
        updateCategoryStyle();
    }

    private void updateCategoryStyle() {
        for (int i = 0; i < categoryViews.size(); i++) {
            TextView textView = categoryViews.get(i);
            boolean selected = ApiConfig.NEWS_CATEGORIES[i][1].equals(selectedType);
            textView.setBackgroundResource(selected ? R.drawable.bg_category_selected : R.drawable.bg_category_normal);
            textView.setTextColor(ContextCompat.getColor(
                    requireContext(),
                    selected ? R.color.white : R.color.sl_text_primary
            ));
        }
    }

    private void loadNews() {
        showLoading();
        newsRepository.fetchNewsList(selectedType, ApiConfig.DEFAULT_PAGE, ApiConfig.DEFAULT_PAGE_SIZE,
                new NewsRepository.RepositoryCallback<NewsListResponse>() {
                    @Override
                    public void onSuccess(NewsListResponse data) {
                        runOnUiThread(() -> handleNewsResponse(data));
                    }

                    @Override
                    public void onFailure(Exception e) {
                        runOnUiThread(() -> showError("分类新闻加载失败，请下拉重试"));
                    }
                });
    }

    private void handleNewsResponse(NewsListResponse response) {
        if (response == null || response.getErrorCode() != 0 || response.getResult() == null) {
            String reason = response == null ? "分类新闻加载失败，请下拉重试" : response.getReason();
            showError(reason == null || reason.isEmpty() ? "分类新闻加载失败，请下拉重试" : reason);
            return;
        }
        List<NewsItem> data = response.getResult().getData();
        if (data == null || data.isEmpty()) {
            adapter.submitList(Collections.emptyList());
            showState("该分类暂无新闻");
            return;
        }
        adapter.submitList(data);
        tvState.setVisibility(View.GONE);
        swipeRefresh.setRefreshing(false);
    }

    private void showLoading() {
        tvState.setVisibility(View.VISIBLE);
        tvState.setText("正在加载新闻...");
        swipeRefresh.setRefreshing(true);
    }

    private void showError(String message) {
        adapter.submitList(Collections.emptyList());
        showState(message);
    }

    private void showState(String message) {
        swipeRefresh.setRefreshing(false);
        tvState.setVisibility(View.VISIBLE);
        tvState.setText(message);
    }

    private void runOnUiThread(Runnable runnable) {
        if (isAdded()) {
            requireActivity().runOnUiThread(runnable);
        }
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void openDetail(NewsItem item) {
        Intent intent = new Intent(requireContext(), NewsDetailActivity.class);
        intent.putExtra(NewsDetailActivity.EXTRA_NEWS, item);
        startActivity(intent);
    }
}
