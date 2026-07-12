package com.group3boot.sunspot.ui.welcome.fragments;

import static com.group3boot.sunspot.util.Constants.USER_COLLISION_ERROR;
import static com.group3boot.sunspot.util.Constants.WEAK_PASSWORD_ERROR;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



import static com.group3boot.sunspot.util.Constants.USER_COLLISION_ERROR;
import static com.group3boot.sunspot.util.Constants.WEAK_PASSWORD_ERROR;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.group3boot.sunspot.R;
import com.group3boot.sunspot.models.UserResult;
import com.group3boot.sunspot.repository.user.IUserRepository;
import com.group3boot.sunspot.ui.welcome.viewmodel.UserViewModel;
import com.group3boot.sunspot.ui.welcome.viewmodel.UserViewModelFactory;
import com.group3boot.sunspot.util.Constants;
import com.group3boot.sunspot.util.ServiceLocator;

public class SignUpFragment extends Fragment {

    private UserViewModel userViewModel;
    private TextInputEditText etNome, etEmail, etPassword, etPasswordConfirm;
    private TextView tvError;
    private ProgressBar progressBar;

    public SignUpFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IUserRepository userRepository = ServiceLocator.getInstance().getUserRepository();
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        etNome = view.findViewById(R.id.et_nome);
        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        etPasswordConfirm = view.findViewById(R.id.et_password_confirm);
        tvError = view.findViewById(R.id.tv_error);
        progressBar = view.findViewById(R.id.progress_bar);

        view.findViewById(R.id.btn_register).setOnClickListener(v -> {
            String name = etNome.getText() != null ? etNome.getText().toString().trim() : "";
            String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
            String passwordConfirm = etPasswordConfirm.getText() != null ? etPasswordConfirm.getText().toString().trim() : "";

            tvError.setVisibility(View.GONE);

            if (isNameOk(name) & isEmailOk(email) & isPasswordOk(password) & isPasswordConfirmOk(password, passwordConfirm)) {
                progressBar.setVisibility(View.VISIBLE);

                userViewModel.getUserMutableLiveData(name, email, password, false)
                        .observe(getViewLifecycleOwner(), result -> {
                            progressBar.setVisibility(View.GONE);

                            if (result.isSuccess()) {
                                Navigation.findNavController(v).navigate(R.id.action_signUpFragment2_to_homeActivity);
                            } else {
                                UserResult.Error errorResult = (UserResult.Error) result;
                                showError(getErrorMessage(errorResult.getMessage()));
                            }
                        });
            }
        });

        return view;
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }

    private String getErrorMessage(String message) {
        if (message == null) return getString(R.string.error_unexpected);

        switch (message) {
            case WEAK_PASSWORD_ERROR:
                return getString(R.string.error_password_login);
            case USER_COLLISION_ERROR:
                return getString(R.string.error_collision_user);
            default:
                return getString(R.string.error_unexpected);
        }
    }

    private boolean isNameOk(String name) {
        if (name.isEmpty()) {
            etNome.setError(getString(R.string.error_name_signup));
            return false;
        }
        etNome.setError(null);
        return true;
    }

    private boolean isEmailOk(String email) {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError(getString(R.string.error_email_login));
            return false;
        }
        etEmail.setError(null);
        return true;
    }

    private boolean isPasswordOk(String password) {
        if (password.isEmpty() || password.length() < Constants.MINIMUM_LENGTH_PASSWORD) {
            etPassword.setError(getString(R.string.error_password_login));
            return false;
        }
        etPassword.setError(null);
        return true;
    }

    private boolean isPasswordConfirmOk(String password, String passwordConfirm) {
        if (!password.equals(passwordConfirm)) {
            etPasswordConfirm.setError(getString(R.string.error_password_mismatch));
            return false;
        }
        etPasswordConfirm.setError(null);
        return true;
    }
}