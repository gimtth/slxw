package com.wm.yst;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.wm.yst.util.SessionManager;

public class SplashActivity extends AppCompatActivity {
    private static final long SPLASH_DELAY_MS = 600L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SessionManager sessionManager = new SessionManager(this);
            Class<?> target = sessionManager.isLoggedIn() ? MainActivity.class : LoginActivity.class;
            startActivity(new Intent(this, target));
            finish();
        }, SPLASH_DELAY_MS);
    }
}
