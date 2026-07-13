package com.group3boot.sunspot.ui.home.fragments;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.group3boot.sunspot.R;
import com.group3boot.sunspot.models.Spot;
import com.group3boot.sunspot.models.SpotResult;
import com.group3boot.sunspot.repository.spot.SpotRepository;
import com.group3boot.sunspot.ui.home.spotviewmodel.SpotViewModel;
import com.group3boot.sunspot.ui.home.spotviewmodel.SpotViewModelFactory;
import com.group3boot.sunspot.util.Constants;
import com.group3boot.sunspot.util.ServiceLocator;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.MapEventsOverlay;

import java.util.List;

public class MapsFragment extends Fragment {

    private MapView map;
    private SpotViewModel spotViewModel;
    private boolean addSpotMode = false;
    private TextView textViewAddSpotHint;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(requireContext(),
                PreferenceManager.getDefaultSharedPreferences(requireContext()));

        boolean debugMode = getResources().getBoolean(R.bool.debug_mode);
        SpotRepository spotRepository = ServiceLocator.getInstance()
                .getSpotRepository(requireActivity().getApplication(), debugMode);
        spotViewModel = new ViewModelProvider(
                requireActivity(),
                new SpotViewModelFactory(spotRepository)).get(SpotViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        map = view.findViewById(R.id.map);
        textViewAddSpotHint = view.findViewById(R.id.textViewAddSpotHint);
        FloatingActionButton fabAddSpot = view.findViewById(R.id.fabAddSpot);

        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.getController().setZoom(6.0);
        map.getController().setCenter(new GeoPoint(41.9, 12.5)); // centro Italia come default

        MapEventsReceiver mapEventsReceiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint geoPoint) {
                if (addSpotMode) {
                    goToAddSpot(geoPoint.getLatitude(), geoPoint.getLongitude());
                    exitAddSpotMode();
                }
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint geoPoint) {
                return false;
            }
        };
        map.getOverlays().add(new MapEventsOverlay(mapEventsReceiver));

        fabAddSpot.setOnClickListener(v -> {
            if (addSpotMode) {
                exitAddSpotMode();
            } else {
                enterAddSpotMode();
            }
        });

        boolean debugMode = getResources().getBoolean(R.bool.debug_mode);
        spotViewModel.getAllSpots(0).observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess()) {
                List<Spot> spotList = ((SpotResult.Success) result).getData();
                showSpotsOnMap(spotList);
            }
        });
    }

    private void enterAddSpotMode() {
        addSpotMode = true;
        textViewAddSpotHint.setVisibility(View.VISIBLE);
    }

    private void exitAddSpotMode() {
        addSpotMode = false;
        textViewAddSpotHint.setVisibility(View.GONE);
    }

    private void showSpotsOnMap(List<Spot> spotList) {
        map.getOverlays().removeIf(overlay -> overlay instanceof Marker);

        Drawable markerIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_map_marker);

        for (Spot spot : spotList) {
            GeoPoint point = new GeoPoint(spot.getLatitude(), spot.getLongitude());
            Marker marker = new Marker(map);
            marker.setPosition(point);
            marker.setTitle(spot.getName());
            marker.setIcon(markerIcon);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

            marker.setOnMarkerClickListener((clickedMarker, mapView) -> {
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.BUNDLE_KEY_CURRENT_SPOT, spot);
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_mapsFragment_to_spotDetailFragment, bundle);
                return true;
            });

            map.getOverlays().add(marker);
        }
        map.invalidate();
    }


    private void goToAddSpot(double latitude, double longitude) {
        Bundle bundle = new Bundle();
        bundle.putDouble(Constants.BUNDLE_KEY_LATITUDE, latitude);
        bundle.putDouble(Constants.BUNDLE_KEY_LONGITUDE, longitude);
        Navigation.findNavController(requireView())
                .navigate(R.id.action_mapsFragment_to_addSpotFragment, bundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (map != null) map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (map != null) map.onPause();
    }
}