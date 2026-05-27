package com.wm.yst.network;

public final class ApiConfig {
    public static final String APP_KEY = "d5c25a29073928909b98dad450c5d6fc";
    public static final String NEWS_LIST_URL = "https://v.juhe.cn/toutiao/index";
    public static final String NEWS_DETAIL_URL = "https://v.juhe.cn/toutiao/content";

    public static final int DEFAULT_PAGE = 1;
    public static final int DEFAULT_PAGE_SIZE = 20;

    public static final String[][] NEWS_CATEGORIES = {
            {"推荐", "top"},
            {"国内", "guonei"},
            {"国际", "guoji"},
            {"娱乐", "yule"},
            {"体育", "tiyu"},
            {"科技", "keji"},
            {"财经", "caijing"},
            {"游戏", "youxi"},
            {"汽车", "qiche"},
            {"健康", "jiankang"}
    };

    private ApiConfig() {
    }
}
