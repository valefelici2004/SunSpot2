package com.group3boot.sunspot.ui.home.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.group3boot.sunspot.R;
import com.group3boot.sunspot.models.User;
import com.group3boot.sunspot.repository.user.IUserRepository;
import com.group3boot.sunspot.ui.welcome.WelcommeActivity;
import com.group3boot.sunspot.ui.welcome.viewmodel.UserViewModel;
import com.group3boot.sunspot.ui.welcome.viewmodel.UserViewModelFactory;
import com.group3boot.sunspot.util.ServiceLocator;

public class UserFragment extends Fragment {

    private UserViewModel userViewModel;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IUserRepository userRepository = ServiceLocator.getInstance().getUserRepository();
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView textViewFullName = view.findViewById(R.id.tv_full_name);
        TextView textViewEmail = view.findViewById(R.id.tv_email);
        Button buttonLogout = view.findViewById(R.id.btn_logout);
        progressBar = view.findViewById(R.id.progress_bar);

        User loggedUser = userViewModel.getLoggedUser();
        if (loggedUser != null) {
            textViewFullName.setText(loggedUser.getName());
            textViewEmail.setText(loggedUser.getEmail());
        }

        view.findViewById(R.id.cardMySpots).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_userFragment_to_mySpotsFragment));

        view.findViewById(R.id.cardFavorites).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_userFragment_to_favoriteSpotsFragment));

        buttonLogout.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            userViewModel.logout().observe(getViewLifecycleOwner(), result -> {
                progressBar.setVisibility(View.GONE);
                Intent intent = new Intent(getContext(), WelcommeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        });
    }
}