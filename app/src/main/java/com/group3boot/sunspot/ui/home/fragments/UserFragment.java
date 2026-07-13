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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.group3boot.sunspot.R;
import com.group3boot.sunspot.adapter.SpotRecyclerAdapter;
import com.group3boot.sunspot.models.Spot;
import com.group3boot.sunspot.models.SpotResult;
import com.group3boot.sunspot.models.User;
import com.group3boot.sunspot.repository.spot.SpotRepository;
import com.group3boot.sunspot.repository.user.IUserRepository;
import com.group3boot.sunspot.ui.home.spotviewmodel.SpotViewModel;
import com.group3boot.sunspot.ui.home.spotviewmodel.SpotViewModelFactory;
import com.group3boot.sunspot.ui.welcome.WelcommeActivity;
import com.group3boot.sunspot.ui.welcome.viewmodel.UserViewModel;
import com.group3boot.sunspot.ui.welcome.viewmodel.UserViewModelFactory;
import com.group3boot.sunspot.util.Constants;
import com.group3boot.sunspot.util.ServiceLocator;

import java.util.ArrayList;
import java.util.List;

public class UserFragment extends Fragment {

    private SpotViewModel spotViewModel;
    private UserViewModel userViewModel;

    private RecyclerView recyclerViewMySpots, recyclerViewFavorites;
    private TextView textViewMySpotsEmpty, textViewFavoritesEmpty;
    private ProgressBar progressBar;

    private final List<Spot> mySpotsList = new ArrayList<>();
    private final List<Spot> favoriteSpotsList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean debugMode = getResources().getBoolean(R.bool.debug_mode);
        SpotRepository spotRepository = ServiceLocator.getInstance()
                .getSpotRepository(requireActivity().getApplication(), debugMode);
        spotViewModel = new ViewModelProvider(
                requireActivity(),
                new SpotViewModelFactory(spotRepository)).get(SpotViewModel.class);

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
        recyclerViewMySpots = view.findViewById(R.id.rv_my_spots);
        recyclerViewFavorites = view.findViewById(R.id.rv_favorites);
        textViewMySpotsEmpty = view.findViewById(R.id.tv_my_spots_empty);
        textViewFavoritesEmpty = view.findViewById(R.id.tv_favorites_empty);
        progressBar = view.findViewById(R.id.progress_bar);
        Button buttonLogout = view.findViewById(R.id.btn_logout);

        recyclerViewMySpots.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(getContext()));

        User loggedUser = userViewModel.getLoggedUser();
        if (loggedUser != null) {
            textViewFullName.setText(loggedUser.getName());
            textViewEmail.setText(loggedUser.getEmail());
        }

        loadMySpots();
        loadFavoriteSpots();

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

    private void loadMySpots() {
        User loggedUser = userViewModel.getLoggedUser();
        if (loggedUser == null) return;

        spotViewModel.getMySpots(loggedUser.getUid()).observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess()) {
                mySpotsList.clear();
                mySpotsList.addAll(((SpotResult.Success) result).getData());
                updateMySpotsUI();
            }
        });
    }

    private void loadFavoriteSpots() {
        spotViewModel.getFavoriteSpots().observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess()) {
                favoriteSpotsList.clear();
                favoriteSpotsList.addAll(((SpotResult.Success) result).getData());
                updateFavoritesUI();
            }
        });
    }

    private void updateMySpotsUI() {
        if (mySpotsList.isEmpty()) {
            textViewMySpotsEmpty.setVisibility(View.VISIBLE);
            recyclerViewMySpots.setVisibility(View.GONE);
        } else {
            textViewMySpotsEmpty.setVisibility(View.GONE);
            recyclerViewMySpots.setVisibility(View.VISIBLE);
            recyclerViewMySpots.setAdapter(buildAdapter(mySpotsList));
        }
    }

    private void updateFavoritesUI() {
        if (favoriteSpotsList.isEmpty()) {
            textViewFavoritesEmpty.setVisibility(View.VISIBLE);
            recyclerViewFavorites.setVisibility(View.GONE);
        } else {
            textViewFavoritesEmpty.setVisibility(View.GONE);
            recyclerViewFavorites.setVisibility(View.VISIBLE);
            recyclerViewFavorites.setAdapter(buildAdapter(favoriteSpotsList));
        }
    }

    private SpotRecyclerAdapter buildAdapter(List<Spot> spotList) {
        return new SpotRecyclerAdapter(
                R.layout.card_spot,
                spotList,
                true,
                new SpotRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onSpotItemClick(Spot spot) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(Constants.BUNDLE_KEY_CURRENT_SPOT, spot);
                        Navigation.findNavController(requireView())
                                .navigate(R.id.action_userFragment_to_spotDetailFragment, bundle);
                    }

                    @Override
                    public void onFavoriteButtonPressed(int position) {
                        Spot spot = spotList.get(position);
                        spot.setLiked(!spot.isLiked());
                        spotViewModel.updateSpot(spot);
                    }
                });
    }
}