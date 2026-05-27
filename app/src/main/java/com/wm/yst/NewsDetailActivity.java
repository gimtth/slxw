package com.wm.yst;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.wm.yst.db.NewsDbHelper;
import com.wm.yst.model.NewsDetailResponse;
import com.wm.yst.model.NewsItem;
import com.wm.yst.network.NewsRepository;
import com.wm.yst.util.SessionManager;

public class NewsDetailActivity extends AppCompatActivity {
    public static final String EXTRA_NEWS = "extra_news";

    private TextView tvCollect;
    private TextView tvState;
    private WebView webContent;
    private Button btnOpenOriginal;
    private NewsItem newsItem;
    private NewsDbHelper dbHelper;
    private SessionManager sessionManager;
    private final NewsRepository newsRepository = new NewsRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            newsItem = getIntent().getSerializableExtra(EXTRA_NEWS, NewsItem.class);
        } else {
            newsItem = (NewsItem) getIntent().getSerializableExtra(EXTRA_NEWS);
        }
        if (newsItem == null) {
            Toast.makeText(this, "新闻数据无效", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbHelper = new NewsDbHelper(this);
        sessionManager = new SessionManager(this);

        initViews();
        bindBaseInfo(newsItem);
        setupWebView();
        updateCollectText();
        loadDetail();
    }

    private void initViews() {
        findViewById(R.id.tvBack).setOnClickListener(v -> finish());
        tvCollect = findViewById(R.id.tvCollect);
        tvState = findViewById(R.id.tvState);
        webContent = findViewById(R.id.webContent);
        btnOpenOriginal = findViewById(R.id.btnOpenOriginal);

        tvCollect.setOnClickListener(v -> toggleCollect());
        btnOpenOriginal.setOnClickListener(v -> openOriginal());
    }

    private void bindBaseInfo(NewsItem item) {
        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvMeta = findViewById(R.id.tvMeta);
        tvTitle.setText(item.getTitle());
        tvMeta.setText(buildMeta(item));
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings settings = webContent.getSettings();
        settings.setJavaScriptEnabled(false);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        webContent.setBackgroundColor(android.graphics.Color.TRANSPARENT);
    }

    private void loadDetail() {
        tvState.setVisibility(View.VISIBLE);
        tvState.setText("正在加载正文...");
        btnOpenOriginal.setVisibility(View.GONE);

        newsRepository.fetchNewsDetail(newsItem.getUniquekey(), new NewsRepository.RepositoryCallback<NewsDetailResponse>() {
            @Override
            public void onSuccess(NewsDetailResponse data) {
                NewsDetailActivity.this.runOnUiThread(() -> handleDetail(data));
            }

            @Override
            public void onFailure(Exception e) {
                NewsDetailActivity.this.runOnUiThread(() -> showFallback("正文加载失败，可查看原文"));
            }
        });
    }

    private void handleDetail(NewsDetailResponse response) {
        if (response == null || response.getErrorCode() != 0 || response.getResult() == null) {
            showFallback(response == null || response.getReason() == null ? "暂无正文，可查看原文" : response.getReason());
            return;
        }

        NewsItem detail = response.getResult().getDetail();
        if (detail != null) {
            mergeDetail(detail);
            bindBaseInfo(newsItem);
        }

        String content = response.getResult().getContent();
        if (content == null || content.trim().isEmpty()) {
            showFallback("暂无正文，可查看原文");
            return;
        }

        tvState.setVisibility(View.GONE);
        btnOpenOriginal.setVisibility(View.VISIBLE);
        webContent.loadDataWithBaseURL(
                "https://v.juhe.cn/",
                wrapHtml(normalizeHtml(content)),
                "text/html",
                "UTF-8",
                null
        );
    }

    private void mergeDetail(NewsItem detail) {
        if (!isBlank(detail.getTitle())) {
            newsItem.setTitle(detail.getTitle());
        }
        if (!isBlank(detail.getDate())) {
            newsItem.setDate(detail.getDate());
        }
        if (!isBlank(detail.getCategory())) {
            newsItem.setCategory(detail.getCategory());
        }
        if (!isBlank(detail.getAuthorName())) {
            newsItem.setAuthorName(detail.getAuthorName());
        }
        if (!isBlank(detail.getUrl())) {
            newsItem.setUrl(detail.getUrl());
        }
        if (!isBlank(detail.getThumbnailPicS())) {
            newsItem.setThumbnailPicS(detail.getThumbnailPicS());
        }
    }

    private void toggleCollect() {
        String username = sessionManager.getUsername();
        if (isBlank(username)) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean collected = dbHelper.isCollected(username, newsItem.getUniquekey());
        boolean success = collected
                ? dbHelper.removeCollect(username, newsItem.getUniquekey())
                : dbHelper.addCollect(username, newsItem);

        if (!success && !collected) {
            Toast.makeText(this, "收藏失败", Toast.LENGTH_SHORT).show();
            return;
        }
        updateCollectText();
        Toast.makeText(this, collected ? "已取消收藏" : "已收藏", Toast.LENGTH_SHORT).show();
    }

    private void updateCollectText() {
        boolean collected = dbHelper.isCollected(sessionManager.getUsername(), newsItem.getUniquekey());
        tvCollect.setText(collected ? "已收藏" : "收藏");
    }

    private void showFallback(String message) {
        tvState.setVisibility(View.VISIBLE);
        tvState.setText(message);
        btnOpenOriginal.setVisibility(View.VISIBLE);
    }

    private void openOriginal() {
        if (isBlank(newsItem.getUrl())) {
            Toast.makeText(this, "原文链接为空", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(newsItem.getUrl())));
    }

    private String buildMeta(NewsItem item) {
        String source = item.getAuthorName() == null ? "" : item.getAuthorName();
        String category = item.getCategory() == null ? "" : item.getCategory();
        String date = item.getDate() == null ? "" : item.getDate();
        return source + "  " + category + "  " + date;
    }

    private String normalizeHtml(String html) {
        return html.replace("src='//", "src='https://")
                .replace("src=\"//", "src=\"https://");
    }

    private String wrapHtml(String body) {
        return "<html><head><meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<style>body{font-size:16px;line-height:1.7;color:#111827;margin:0;padding:0;}" +
                "img{max-width:100%;height:auto;border-radius:6px;}p{margin:0 0 14px;}</style></head><body>" +
                body +
                "</body></html>";
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
