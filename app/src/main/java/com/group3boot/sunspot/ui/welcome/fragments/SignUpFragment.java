package com.group3boot.sunspot.ui.welcome.fragments;

import static com.group3boot.sunspot.util.Constants.USER_COLLISION_ERROR;
import static com.group3boot.sunspot.util.Constants.WEAK_PASSWORD_ERROR;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.group3boot.sunspot.R;
import com.group3boot.sunspot.repository.user.IUserRepository;
import com.group3boot.sunspot.ui.home.HomeActivity;
import com.group3boot.sunspot.ui.welcome.viewmodel.UserViewModel;
import com.group3boot.sunspot.ui.welcome.viewmodel.UserViewModelFactory;
import com.group3boot.sunspot.util.Constants;
import com.group3boot.sunspot.util.ServiceLocator;

public class SignupFragment extends Fragment {

    private UserViewModel userViewModel;
    private TextInputEditText textInputName, textInputEmail, textInputPassword;

    public SignupFragment() {}

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

        textInputName = view.findViewById(R.id.textInputName);
        textInputEmail = view.findViewById(R.id.textInputEmail);
        textInputPassword = view.findViewById(R.id.textInputPassword);

        view.findViewById(R.id.signupButton).setOnClickListener(v -> {
            String name = textInputName.getText() != null ? textInputName.getText().toString().trim() : "";
            String email = textInputEmail.getText() != null ? textInputEmail.getText().toString().trim() : "";
            String password = textInputPassword.getText() != null ? textInputPassword.getText().toString().trim() : "";

            if (isNameOk(name) & isEmailOk(email) & isPasswordOk(password)) {
                userViewModel.getUserMutableLiveData(name, email, password, false)
                        .observe(getViewLifecycleOwner(), result -> {
                            if (result.isSuccess()) {
                                Intent intent = new Intent(getContext(), HomeActivity.class);
                                startActivity(intent);
                                requireActivity().finish();
                            } else {
                                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                        getErrorMessage(result.getMessage()),
                                        Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        return view;
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
            textInputName.setError(getString(R.string.error_name_signup));
            return false;
        }
        textInputName.setError(null);
        return true;
    }

    private boolean isEmailOk(String email) {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            textInputEmail.setError(getString(R.string.error_email_login));
            return false;
        }
        textInputEmail.setError(null);
        return true;
    }

    private boolean isPasswordOk(String password) {
        if (password.isEmpty() || password.length() < Constants.MINIMUM_LENGTH_PASSWORD) {
            textInputPassword.setError(getString(R.string.error_password_login));
            return false;
        }
        textInputPassword.setError(null);
        return true;
    }
}