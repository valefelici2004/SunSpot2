package com.group3boot.sunspot.ui.welcome.fragments;

import static com.group3boot.sunspot.util.Constants.INVALID_CREDENTIALS_ERROR;
import static com.group3boot.sunspot.util.Constants.INVALID_USER_ERROR;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.group3boot.sunspot.util.ServiceLocator;

public class LoginFragment extends Fragment {

    private TextInputEditText editTextEmail, editTextPassword;
    private UserViewModel userViewModel;

    public LoginFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IUserRepository userRepository = ServiceLocator.getInstance().getUserRepository();
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Se l'utente è già loggato (sessione precedente), salta direttamente alla Home
        if (userViewModel.getLoggedUser() != null) {
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_homeActivity);
            return;
        }

        editTextEmail = view.findViewById(R.id.email);
        editTextPassword = view.findViewById(R.id.password);

        Button loginButton = view.findViewById(R.id.accesso);
        Button signupButton = view.findViewById(R.id.registrazione);

        loginButton.setOnClickListener(v -> {
            String email = editTextEmail.getText() != null ? editTextEmail.getText().toString().trim() : "";
            String password = editTextPassword.getText() != null ? editTextPassword.getText().toString().trim() : "";

            if (isEmailOk(email) & isPasswordOk(password)) {
                userViewModel.getUserMutableLiveData(null, email, password, true)
                        .observe(getViewLifecycleOwner(), result -> {
                            if (result.isSuccess()) {
                                Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_homeActivity);
                            } else {
                                UserResult.Error errorResult = (UserResult.Error) result;
                                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                        getErrorMessage(errorResult.getMessage()),
                                        Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        signupButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_signUpFragment2));
    }

    private String getErrorMessage(String errorType) {
        if (errorType == null) return getString(R.string.error_unexpected);

        switch (errorType) {
            case INVALID_CREDENTIALS_ERROR:
                return getString(R.string.error_password_login);
            case INVALID_USER_ERROR:
                return getString(R.string.error_email_login);
            default:
                return getString(R.string.error_unexpected);
        }
    }

    private boolean isEmailOk(String email) {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError(getString(R.string.error_email_login));
            return false;
        }
        editTextEmail.setError(null);
        return true;
    }

    private boolean isPasswordOk(String password) {
        if (password.isEmpty()) {
            editTextPassword.setError(getString(R.string.error_password_login));
            return false;
        }
        editTextPassword.setError(null);
        return true;
    }
}