package com.group3boot.sunspot.ui.home.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.BundleCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.group3boot.sunspot.R;
import com.group3boot.sunspot.models.Spot;
import com.group3boot.sunspot.models.WeatherResult;
import com.group3boot.sunspot.repository.spot.SpotRepository;
import com.group3boot.sunspot.repository.weather.WeatherRepository;
import com.group3boot.sunspot.ui.home.viewmodel.SpotViewModel;
import com.group3boot.sunspot.ui.home.viewmodel.SpotViewModelFactory;
import com.group3boot.sunspot.ui.home.viewmodel.WeatherViewModel;
import com.group3boot.sunspot.ui.home.viewmodel.WeatherViewModelFactory;
import com.group3boot.sunspot.util.Constants;
import com.group3boot.sunspot.util.ServiceLocator;

public class SpotDetailFragment extends Fragment {

    private Spot spot;
    private SpotViewModel spotViewModel;
    private WeatherViewModel weatherViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            spot = BundleCompat.getParcelable(getArguments(), Constants.BUNDLE_KEY_CURRENT_SPOT, Spot.class);
        }

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
        return inflater.inflate(R.layout.fragment_spot_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (spot == null) return;

        TextView textViewName = view.findViewById(R.id.textViewName);
        TextView textViewPosizione = view.findViewById(R.id.textViewPosizione);
        CheckBox favoriteButton = view.findViewById(R.id.favoriteButton);
        android.widget.ImageView imageViewSpot = view.findViewById(R.id.imageViewSpot);

        textViewName.setText(spot.getName());
        textViewPosizione.setText(spot.getPosizione());
        favoriteButton.setChecked(spot.isLiked());

        String firstPhoto = (spot.getPhotoUrls() != null && !spot.getPhotoUrls().isEmpty())
                ? spot.getPhotoUrls().get(0) : null;
        Glide.with(this)
                .load(firstPhoto)
                .placeholder(new android.graphics.drawable.ColorDrawable(
                        requireContext().getColor(R.color.md_theme_inverseOnSurface)))
                .into(imageViewSpot);

        favoriteButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            spot.setLiked(isChecked);
            spotViewModel.updateSpot(spot);
        });

        view.findViewById(R.id.buttonOpenMaps).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(spot.getGoogleMapsUri()));
            intent.setPackage("com.google.android.apps.maps");
            if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
                startActivity(intent);
            } else {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(spot.getGoogleMapsUri())));
            }
        });

        fetchWeather();
    }

    private void fetchWeather() {
        TextView textViewTemperature = requireView().findViewById(R.id.textViewTemperature);
        TextView textViewSunrise = requireView().findViewById(R.id.textViewSunrise);
        TextView textViewSunset = requireView().findViewById(R.id.textViewSunset);

        weatherViewModel.getWeather(spot.getLatitude(), spot.getLongitude())
                .observe(getViewLifecycleOwner(), result -> {
                    if (result.isSuccess()) {
                        var weather = ((WeatherResult.Success) result).getData();
                        textViewTemperature.setText(
                                getString(R.string.temperature_format, weather.getCurrent().getTemperature_2m()));

                        if (weather.getDaily() != null && !weather.getDaily().getSunrise().isEmpty()) {
                            textViewSunrise.setText(getString(R.string.sunrise_format,
                                    weather.getDaily().getSunrise().get(0)));
                            textViewSunset.setText(getString(R.string.sunset_format,
                                    weather.getDaily().getSunset().get(0)));
                        }
                    } else {
                        textViewTemperature.setText(R.string.error_weather_unavailable);
                    }
                });
    }
}