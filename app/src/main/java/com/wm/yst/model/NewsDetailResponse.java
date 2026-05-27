package com.wm.yst.model;

import com.google.gson.annotations.SerializedName;

public class NewsDetailResponse {
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
        @SerializedName("uniquekey")
        private String uniquekey;

        @SerializedName("detail")
        private NewsItem detail;

        @SerializedName("content")
        private String content;

        public String getUniquekey() {
            return uniquekey;
        }

        public NewsItem getDetail() {
            return detail;
        }

        public String getContent() {
            return content;
        }
    }
}
