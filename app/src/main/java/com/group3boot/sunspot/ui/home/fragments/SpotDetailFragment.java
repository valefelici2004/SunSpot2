package com.group3boot.sunspot.ui.home.fragments;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.BundleCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.group3boot.sunspot.R;
import com.group3boot.sunspot.models.Spot;
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

public class SpotDetailFragment extends Fragment {

    private Spot spot;
    private SpotViewModel spotViewModel;
    private WeatherViewModel weatherViewModel;
    private UserViewModel userViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IUserRepository userRepository = ServiceLocator.getInstance().getUserRepository();
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);

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

        String currentUserId = userViewModel.getLoggedUser() != null
                ? userViewModel.getLoggedUser().getUid() : null;

        TextView textViewName = view.findViewById(R.id.textViewName);
        TextView textViewPosizione = view.findViewById(R.id.textViewPosizione);
        CheckBox favoriteButton = view.findViewById(R.id.favoriteButton);
        ImageView imageViewSpot = view.findViewById(R.id.imageViewSpot);

        textViewName.setText(spot.getName());
        textViewPosizione.setText(spot.getPosizione());
        favoriteButton.setChecked(currentUserId != null && spot.isFavoritedBy(currentUserId));

        String firstPhoto = (spot.getPhotoUrls() != null && !spot.getPhotoUrls().isEmpty())
                ? spot.getPhotoUrls().get(0) : null;

        if (firstPhoto != null && firstPhoto.startsWith("http")) {
            Glide.with(this)
                    .load(firstPhoto)
                    .placeholder(new ColorDrawable(requireContext().getColor(R.color.md_theme_inverseOnSurface)))
                    .into(imageViewSpot);
        } else if (firstPhoto != null) {
            android.graphics.Bitmap bitmap = com.group3boot.sunspot.util.ImageUtil.decodeBase64(firstPhoto);
            if (bitmap != null) {
                imageViewSpot.setImageBitmap(bitmap);
            } else {
                imageViewSpot.setImageDrawable(new ColorDrawable(requireContext().getColor(R.color.md_theme_inverseOnSurface)));
            }
        } else {
            imageViewSpot.setImageDrawable(new ColorDrawable(requireContext().getColor(R.color.md_theme_inverseOnSurface)));
        }

        favoriteButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (currentUserId != null) {
                spotViewModel.toggleFavorite(spot, currentUserId)
                        .observe(getViewLifecycleOwner(), result -> {});
            }
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

        Button buttonDelete = view.findViewById(R.id.buttonDelete);
        if (currentUserId != null && currentUserId.equals(spot.getAddedByUserId())) {
            buttonDelete.setVisibility(View.VISIBLE);
            buttonDelete.setOnClickListener(v -> {
                spotViewModel.deleteSpot(spot);
                Navigation.findNavController(requireView()).navigateUp();
            });
        } else {
            buttonDelete.setVisibility(View.GONE);
        }

        fetchWeather();
    }

    private void fetchWeather() {
        TextView textViewTemperature = requireView().findViewById(R.id.textViewTemperature);
        TextView textViewWeatherDescription = requireView().findViewById(R.id.textViewWeatherDescription);
        TextView textViewSunrise = requireView().findViewById(R.id.textViewSunrise);
        TextView textViewSunset = requireView().findViewById(R.id.textViewSunset);

        weatherViewModel.getWeather(spot.getLatitude(), spot.getLongitude())
                .observe(getViewLifecycleOwner(), result -> {
                    if (result.isSuccess()) {
                        var weather = ((WeatherResult.Success) result).getData();
                        textViewTemperature.setText(
                                getString(R.string.temperature_format, weather.getCurrent().getTemperature_2m()));
                        textViewWeatherDescription.setText(
                                WeatherUtil.getWeatherDescription(weather.getCurrent().getWeather_code()));

                        if (weather.getDaily() != null && !weather.getDaily().getSunrise().isEmpty()) {
                            String sunrise = WeatherUtil.formatTime(weather.getDaily().getSunrise().get(0));
                            String sunset = WeatherUtil.formatTime(weather.getDaily().getSunset().get(0));
                            textViewSunrise.setText(getString(R.string.sunrise_format, sunrise));
                            textViewSunset.setText(getString(R.string.sunset_format, sunset));
                        }
                    } else {
                        textViewTemperature.setText(R.string.error_weather_unavailable);
                    }
                });
    }
}