package com.wm.yst;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.wm.yst.db.NewsDbHelper;

public class RegisterActivity extends AppCompatActivity {
    private EditText etUsername;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private NewsDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new NewsDbHelper(this);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        Button btnRegister = findViewById(R.id.btnRegister);
        TextView tvBackLogin = findViewById(R.id.tvBackLogin);

        btnRegister.setOnClickListener(v -> register());
        tvBackLogin.setOnClickListener(v -> finish());
    }

    private void register() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "请完整填写注册信息", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }
        if (dbHelper.isUserExists(username)) {
            Toast.makeText(this, "账号已存在", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!dbHelper.registerUser(username, password)) {
            Toast.makeText(this, "注册失败，请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "注册成功，请登录", Toast.LENGTH_SHORT).show();
        finish();
    }
}
