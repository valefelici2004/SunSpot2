package com.group3boot.sunspot.ui.home.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.group3boot.sunspot.models.WeatherResult;
import com.group3boot.sunspot.repository.spot.SpotRepository;
import com.group3boot.sunspot.repository.user.IUserRepository;
import com.group3boot.sunspot.repository.weather.WeatherRepository;
import com.group3boot.sunspot.ui.home.spotviewmodel.SpotViewModel;
import com.group3boot.sunspot.ui.home.spotviewmodel.SpotViewModelFactory;
import com.group3boot.sunspot.ui.home.weatherviewmodel.WeatherViewModel;
import com.group3boot.sunspot.ui.home.weatherviewmodel.WeatherViewModelFactory;
import com.group3boot.sunspot.ui.welcome.viewmodel.UserViewModel;
import com.group3boot.sunspot.ui.welcome.viewmodel.UserViewModelFactory;
import com.group3boot.sunspot.util.Constants;
import com.group3boot.sunspot.util.ServiceLocator;
import com.group3boot.sunspot.util.WeatherUtil;

import java.util.ArrayList;
import java.util.List;

public class FavoriteSpotsFragment extends Fragment {

    private SpotViewModel spotViewModel;
    private UserViewModel userViewModel;
    private WeatherViewModel weatherViewModel;

    private RecyclerView recyclerView;
    private TextView textViewEmpty;
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

        WeatherRepository weatherRepository = ServiceLocator.getInstance().getWeatherRepository();
        weatherViewModel = new ViewModelProvider(
                requireActivity(),
                new WeatherViewModelFactory(weatherRepository)).get(WeatherViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite_spots, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rv_favorites);
        textViewEmpty = view.findViewById(R.id.tv_favorites_empty);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        User loggedUser = userViewModel.getLoggedUser();
        if (loggedUser == null) return;

        spotViewModel.getAllSpots(0).observe(getViewLifecycleOwner(), syncResult -> {
            loadFavoriteSpots(loggedUser.getUid());
        });
    }

    private void loadFavoriteSpots(String userId) {
        spotViewModel.getFavoriteSpots(userId).observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess()) {
                favoriteSpotsList.clear();
                favoriteSpotsList.addAll(((SpotResult.Success) result).getData());
                updateUI();
            }
        });
    }

    private void updateUI() {
        if (favoriteSpotsList.isEmpty()) {
            textViewEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            textViewEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            String currentUserId = userViewModel.getLoggedUser().getUid();

            recyclerView.setAdapter(new SpotRecyclerAdapter(
                    R.layout.card_spot,
                    favoriteSpotsList,
                    true,
                    currentUserId,
                    new SpotRecyclerAdapter.OnItemClickListener() {
                        @Override
                        public void onSpotItemClick(Spot spot) {
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(Constants.BUNDLE_KEY_CURRENT_SPOT, spot);
                            Navigation.findNavController(requireView())
                                    .navigate(R.id.action_favoriteSpotsFragment_to_spotDetailFragment, bundle);
                        }

                        @Override
                        public void onFavoriteButtonPressed(int position) {
                            Spot spot = favoriteSpotsList.get(position);
                            spotViewModel.toggleFavorite(spot, currentUserId)
                                    .observe(getViewLifecycleOwner(), result -> {});
                        }
                    },
                    (spot, callback) -> weatherViewModel.getWeather(spot.getLatitude(), spot.getLongitude())
                            .observe(getViewLifecycleOwner(), result -> {
                                if (result.isSuccess()) {
                                    var weather = ((WeatherResult.Success) result).getData();
                                    if (weather.getDaily() != null && !weather.getDaily().getSunrise().isEmpty()) {
                                        String time = spot.isSunriseSpot()
                                                ? WeatherUtil.formatTime(weather.getDaily().getSunrise().get(0))
                                                : WeatherUtil.formatTime(weather.getDaily().getSunset().get(0));
                                        callback.onTimeReady(time);
                                    }
                                }
                            })));
        }
    }
}