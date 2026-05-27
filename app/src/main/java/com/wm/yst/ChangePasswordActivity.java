package com.wm.yst;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.wm.yst.db.NewsDbHelper;
import com.wm.yst.util.SessionManager;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText etOldPassword;
    private EditText etNewPassword;
    private EditText etConfirmPassword;
    private NewsDbHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        dbHelper = new NewsDbHelper(this);
        sessionManager = new SessionManager(this);

        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        Button btnSave = findViewById(R.id.btnSave);

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> savePassword());
    }

    private void savePassword() {
        String username = sessionManager.getUsername();
        String oldPassword = etOldPassword.getText().toString();
        String newPassword = etNewPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if (username == null || username.trim().isEmpty()) {
            Toast.makeText(this, "登录状态已失效，请重新登录", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "请完整填写密码信息", Toast.LENGTH_SHORT).show();
            return;
        }
        if (newPassword.length() < 6) {
            Toast.makeText(this, "新密码至少 6 位", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "两次输入的新密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }
        if (oldPassword.equals(newPassword)) {
            Toast.makeText(this, "新密码不能与当前密码相同", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!dbHelper.updatePassword(username, oldPassword, newPassword)) {
            Toast.makeText(this, "当前密码错误", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "密码修改成功", Toast.LENGTH_SHORT).show();
        finish();
    }
}
