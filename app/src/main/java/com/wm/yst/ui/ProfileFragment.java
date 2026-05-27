package com.wm.yst.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wm.yst.LoginActivity;
import com.wm.yst.CollectActivity;
import com.wm.yst.ChangePasswordActivity;
import com.wm.yst.R;
import com.wm.yst.util.SessionManager;

public class ProfileFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SessionManager sessionManager = new SessionManager(requireContext());

        TextView tvUsername = view.findViewById(R.id.tvUsername);
        tvUsername.setText(sessionManager.getUsername());

        view.findViewById(R.id.tvCollect).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), CollectActivity.class)));
        view.findViewById(R.id.tvChangePassword).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), ChangePasswordActivity.class)));
        view.findViewById(R.id.btnLogout).setOnClickListener(v -> {
            sessionManager.logout();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
