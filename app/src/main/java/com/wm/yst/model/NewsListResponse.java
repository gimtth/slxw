package com.wm.yst.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewsListResponse {
    @SerializedName("reason")
    private String reason;

    @SerializedName("result")
    private Result result;

    @SerializedName("error_code")
    private int errorCode;

    public String getReason() {
        return reason;
    }

    public Result getResult() {
        return result;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public static class Result {
        @SerializedName("stat")
        private String stat;

        @SerializedName("data")
        private List<NewsItem> data;

        @SerializedName("page")
        private String page;

        @SerializedName("pageSize")
        private String pageSize;

        public String getStat() {
            return stat;
        }

        public List<NewsItem> getData() {
            return data;
        }

        public String getPage() {
            return page;
        }

        public String getPageSize() {
            return pageSize;
        }
    }
}
