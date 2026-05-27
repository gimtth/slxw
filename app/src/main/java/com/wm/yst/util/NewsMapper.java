package com.wm.yst.util;

import com.wm.yst.model.Collect;
import com.wm.yst.model.NewsItem;

public final class NewsMapper {
    private NewsMapper() {
    }

    public static NewsItem fromCollect(Collect collect) {
        NewsItem item = new NewsItem();
        item.setUniquekey(collect.getUniquekey());
        item.setTitle(collect.getNewsTitle());
        item.setUrl(collect.getNewsUrl());
        item.setAuthorName(collect.getNewsSource());
        item.setDate(collect.getNewsDate());
        item.setCategory(collect.getCategory());
        item.setThumbnailPicS(collect.getThumbnailUrl());
        item.setIsContent("1");
        return item;
    }
}
