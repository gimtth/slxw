package com.wm.yst.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {
    private SwipeRefreshLayout swipeRefresh;
    private TextView tvState;
    private NewsAdapter adapter;
    private final NewsRepository newsRepository = new NewsRepository();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        tvState = view.findViewById(R.id.tvState);
        RecyclerView rvNews = view.findViewById(R.id.rvNews);

        adapter = new NewsAdapter(this::openDetail);
        rvNews.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvNews.setAdapter(adapter);

        swipeRefresh.setColorSchemeResources(R.color.sl_news_blue);
        swipeRefresh.setOnRefreshListener(this::loadNews);

        loadNews();
    }

    private void loadNews() {
        showLoading();
        newsRepository.fetchNewsList("top", ApiConfig.DEFAULT_PAGE, ApiConfig.DEFAULT_PAGE_SIZE,
                new NewsRepository.RepositoryCallback<NewsListResponse>() {
                    @Override
                    public void onSuccess(NewsListResponse data) {
                        runOnUiThread(() -> handleNewsResponse(data));
                    }

                    @Override
                    public void onFailure(Exception e) {
                        runOnUiThread(() -> showError("新闻加载失败，请下拉重试"));
                    }
                });
    }

    private void handleNewsResponse(NewsListResponse response) {
        if (response == null || response.getErrorCode() != 0 || response.getResult() == null) {
            String reason = response == null ? "新闻加载失败，请下拉重试" : response.getReason();
            showError(reason == null || reason.isEmpty() ? "新闻加载失败，请下拉重试" : reason);
            return;
        }
        List<NewsItem> data = response.getResult().getData();
        if (data == null || data.isEmpty()) {
            adapter.submitList(Collections.emptyList());
            showState("暂无新闻");
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

    private void openDetail(NewsItem item) {
        Intent intent = new Intent(requireContext(), NewsDetailActivity.class);
        intent.putExtra(NewsDetailActivity.EXTRA_NEWS, item);
        startActivity(intent);
    }
}
