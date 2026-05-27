package com.wm.yst.network;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.wm.yst.model.NewsDetailResponse;
import com.wm.yst.model.NewsListResponse;

import java.util.HashMap;
import java.util.Map;

public class NewsRepository {
    private final Gson gson = new Gson();

    public void fetchNewsList(String type, int page, int pageSize, RepositoryCallback<NewsListResponse> callback) {
        Map<String, String> params = new HashMap<>();
        params.put("key", ApiConfig.APP_KEY);
        params.put("type", type == null || type.trim().isEmpty() ? "top" : type);
        params.put("page", String.valueOf(page <= 0 ? ApiConfig.DEFAULT_PAGE : page));
        params.put("page_size", String.valueOf(pageSize <= 0 ? ApiConfig.DEFAULT_PAGE_SIZE : pageSize));
        params.put("is_filter", "1");

        NetworkClient.get(ApiConfig.NEWS_LIST_URL, params, new NetworkClient.ResultCallback() {
            @Override
            public void onSuccess(String responseBody) {
                parseResponse(responseBody, NewsListResponse.class, callback);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public void fetchNewsDetail(String uniquekey, RepositoryCallback<NewsDetailResponse> callback) {
        Map<String, String> params = new HashMap<>();
        params.put("key", ApiConfig.APP_KEY);
        params.put("uniquekey", uniquekey);

        NetworkClient.get(ApiConfig.NEWS_DETAIL_URL, params, new NetworkClient.ResultCallback() {
            @Override
            public void onSuccess(String responseBody) {
                parseResponse(responseBody, NewsDetailResponse.class, callback);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    private <T> void parseResponse(String responseBody, Class<T> clazz, RepositoryCallback<T> callback) {
        try {
            callback.onSuccess(gson.fromJson(responseBody, clazz));
        } catch (JsonSyntaxException e) {
            callback.onFailure(e);
        }
    }

    public interface RepositoryCallback<T> {
        void onSuccess(T data);

        void onFailure(Exception e);
    }
}
