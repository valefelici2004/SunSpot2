package com.group3boot.sunspot.ui.home.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.group3boot.sunspot.R;
import com.group3boot.sunspot.models.Spot;
import com.group3boot.sunspot.repository.spot.SpotRepository;
import com.group3boot.sunspot.ui.home.viewmodel.SpotViewModel;
import com.group3boot.sunspot.ui.home.viewmodel.SpotViewModelFactory;
import com.group3boot.sunspot.util.Constants;
import com.group3boot.sunspot.util.ServiceLocator;

import java.util.ArrayList;
import java.util.List;

public class AddSpotFragment extends Fragment {

    private SpotViewModel spotViewModel;
    private TextInputEditText editTextName, editTextPosizione;
    private ImageView imageViewPreview;
    private View progressBar;
    private Uri selectedPhotoUri;
    private double latitude, longitude;

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedPhotoUri = uri;
                    Glide.with(this).load(uri).into(imageViewPreview);
                }
            });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            latitude = getArguments().getDouble(Constants.BUNDLE_KEY_LATITUDE);
            longitude = getArguments().getDouble(Constants.BUNDLE_KEY_LONGITUDE);
        }

        boolean debugMode = getResources().getBoolean(R.bool.debug_mode);
        SpotRepository spotRepository = ServiceLocator.getInstance()
                .getSpotRepository(requireActivity().getApplication(), debugMode);
        spotViewModel = new ViewModelProvider(
                requireActivity(),
                new SpotViewModelFactory(spotRepository)).get(SpotViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_spot, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextName = view.findViewById(R.id.editTextName);
        editTextPosizione = view.findViewById(R.id.editTextPosizione);
        imageViewPreview = view.findViewById(R.id.imageViewPreview);
        progressBar = view.findViewById(R.id.progressBar);

        view.findViewById(R.id.cardPhotoPicker).setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        view.findViewById(R.id.buttonSave).setOnClickListener(v -> {
            String name = editTextName.getText() != null ? editTextName.getText().toString().trim() : "";
            String posizione = editTextPosizione.getText() != null ? editTextPosizione.getText().toString().trim() : "";

            if (isNameOk(name)) {
                saveSpot(name, posizione);
            }
        });
    }

    private boolean isNameOk(String name) {
        if (name.isEmpty()) {
            editTextName.setError(getString(R.string.error_spot_name_empty));
            return false;
        }
        editTextName.setError(null);
        return true;
    }

    private void saveSpot(String name, String posizione) {
        progressBar.setVisibility(View.VISIBLE);

        Spot spot = new Spot();
        spot.setName(name);
        spot.setPosizione(posizione);
        spot.setLatitude(latitude);
        spot.setLongitude(longitude);
        spot.setLiked(false);
        spot.setAddedByMe(true);

        List<String> photoUrls = new ArrayList<>();
        if (selectedPhotoUri != null) {
            photoUrls.add(selectedPhotoUri.toString());
        }
        spot.setPhotoUrls(photoUrls);

        spotViewModel.addSpot(spot).observe(getViewLifecycleOwner(), result -> {
            progressBar.setVisibility(View.GONE);

            if (result.isSuccess()) {
                Navigation.findNavController(requireView()).navigateUp();
            } else {
                editTextName.setError(getString(R.string.error_unexpected));
            }
        });
    }
}