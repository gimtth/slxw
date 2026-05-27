package com.wm.yst;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wm.yst.db.NewsDbHelper;
import com.wm.yst.model.Collect;
import com.wm.yst.ui.CollectAdapter;
import com.wm.yst.util.NewsMapper;
import com.wm.yst.util.SessionManager;

import java.util.List;

public class CollectActivity extends AppCompatActivity {
    private NewsDbHelper dbHelper;
    private SessionManager sessionManager;
    private CollectAdapter adapter;
    private TextView tvState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);

        dbHelper = new NewsDbHelper(this);
        sessionManager = new SessionManager(this);
        tvState = findViewById(R.id.tvState);

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());
        RecyclerView rvCollect = findViewById(R.id.rvCollect);
        adapter = new CollectAdapter(new CollectAdapter.OnCollectClickListener() {
            @Override
            public void onOpen(Collect collect) {
                Intent intent = new Intent(CollectActivity.this, NewsDetailActivity.class);
                intent.putExtra(NewsDetailActivity.EXTRA_NEWS, NewsMapper.fromCollect(collect));
                startActivity(intent);
            }

            @Override
            public void onRemove(Collect collect) {
                boolean removed = dbHelper.removeCollect(sessionManager.getUsername(), collect.getUniquekey());
                Toast.makeText(CollectActivity.this, removed ? "已取消收藏" : "取消失败", Toast.LENGTH_SHORT).show();
                loadCollects();
            }
        });
        rvCollect.setLayoutManager(new LinearLayoutManager(this));
        rvCollect.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCollects();
    }

    private void loadCollects() {
        List<Collect> collectList = dbHelper.getCollectList(sessionManager.getUsername());
        adapter.submitList(collectList);
        if (collectList.isEmpty()) {
            tvState.setVisibility(View.VISIBLE);
            tvState.setText("暂无收藏新闻");
        } else {
            tvState.setVisibility(View.GONE);
        }
    }
}
