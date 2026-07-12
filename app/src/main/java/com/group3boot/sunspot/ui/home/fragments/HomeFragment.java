package com.group3boot.sunspot.ui.home.fragments;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.group3boot.sunspot.models.WeatherResult;
import com.group3boot.sunspot.repository.spot.SpotRepository;
import com.group3boot.sunspot.repository.weather.WeatherRepository;
import com.group3boot.sunspot.ui.home.spotviewmodel.SpotViewModel;
import com.group3boot.sunspot.ui.home.spotviewmodel.SpotViewModelFactory;
import com.group3boot.sunspot.ui.home.weatherviewmodel.WeatherViewModel;
import com.group3boot.sunspot.ui.home.weatherviewmodel.WeatherViewModelFactory;
import com.group3boot.sunspot.util.Constants;
import com.group3boot.sunspot.util.LocationUtil;
import com.group3boot.sunspot.util.ServiceLocator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final double NEARBY_RADIUS_KM = 50;

    private SpotViewModel spotViewModel;
    private WeatherViewModel weatherViewModel;

    private TextView textViewTemperature, textViewNoSpots;
    private RecyclerView recyclerViewNearbySpots;
    private final List<Spot> nearbySpotList = new ArrayList<>();

    private double currentLatitude;
    private double currentLongitude;
    private boolean locationAvailable = false;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    fetchLocationAndData();
                } else {
                    textViewNoSpots.setText(R.string.error_location_permission);
                    textViewNoSpots.setVisibility(View.VISIBLE);
                }
            });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean debugMode = getResources().getBoolean(R.bool.debug_mode);

        SpotRepository spotRepository = ServiceLocator.getInstance()
                .getSpotRepository(requireActivity().getApplication(), debugMode);
        spotViewModel = new ViewModelProvider(
                requireActivity(),
                new SpotViewModelFactory(spotRepository)).get(SpotViewModel.class);

        WeatherRepository weatherRepository = ServiceLocator.getInstance().getWeatherRepository();
        weatherViewModel = new ViewModelProvider(
                requireActivity(),
                new WeatherViewModelFactory(weatherRepository)).get(WeatherViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textViewTemperature = view.findViewById(R.id.textViewTemperature);
        textViewNoSpots = view.findViewById(R.id.textViewNoSpots);
        recyclerViewNearbySpots = view.findViewById(R.id.recyclerViewNearbySpots);
        recyclerViewNearbySpots.setLayoutManager(new LinearLayoutManager(getContext()));

        if (LocationUtil.hasLocationPermission(requireContext())) {
            fetchLocationAndData();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void fetchLocationAndData() {
        LocationUtil.getCurrentLocation(requireContext(), new LocationUtil.LocationCallback() {
            @Override
            public void onLocationResult(Location location) {
                locationAvailable = true;
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();

                fetchWeather();
                fetchNearbySpots();
            }

            @Override
            public void onLocationUnavailable() {
                textViewTemperature.setText(R.string.error_location_unavailable);
            }
        });
    }

    private void fetchWeather() {
        weatherViewModel.getWeather(currentLatitude, currentLongitude)
                .observe(getViewLifecycleOwner(), result -> {
                    if (result.isSuccess()) {
                        double temp = ((WeatherResult.Success) result).getData()
                                .getCurrent().getTemperature_2m();
                        textViewTemperature.setText(getString(R.string.temperature_format, temp));
                    } else {
                        textViewTemperature.setText(R.string.error_weather_unavailable);
                    }
                });
    }

    private void fetchNearbySpots() {
        spotViewModel.getAllSpots(0).observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess()) {
                List<Spot> allSpots = ((SpotResult.Success) result).getData();
                updateNearbyList(allSpots);
            } else {
                textViewNoSpots.setText(R.string.error_unexpected);
                textViewNoSpots.setVisibility(View.VISIBLE);
            }
        });
    }

    private void updateNearbyList(List<Spot> allSpots) {
        if (!locationAvailable) return;

        nearbySpotList.clear();

        for (Spot spot : allSpots) {
            double distance = LocationUtil.distanceInKm(
                    currentLatitude, currentLongitude, spot.getLatitude(), spot.getLongitude());
            if (distance <= NEARBY_RADIUS_KM) {
                nearbySpotList.add(spot);
            }
        }

        Collections.sort(nearbySpotList, Comparator.comparingDouble(spot ->
                LocationUtil.distanceInKm(currentLatitude, currentLongitude,