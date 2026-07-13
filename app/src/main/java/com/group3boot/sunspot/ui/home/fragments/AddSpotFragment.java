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
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group3boot.sunspot.R;
import com.group3boot.sunspot.models.Spot;
import com.group3boot.sunspot.repository.spot.SpotRepository;
import com.group3boot.sunspot.repository.user.IUserRepository;
import com.group3boot.sunspot.ui.home.spotviewmodel.SpotViewModel;
import com.group3boot.sunspot.ui.home.spotviewmodel.SpotViewModelFactory;
import com.group3boot.sunspot.ui.welcome.viewmodel.UserViewModel;
import com.group3boot.sunspot.ui.welcome.viewmodel.UserViewModelFactory;
import com.group3boot.sunspot.util.Constants;
import com.group3boot.sunspot.util.ServiceLocator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddSpotFragment extends Fragment {

    private SpotViewModel spotViewModel;
    private UserViewModel userViewModel;

    private TextInputEditText editTextName, editTextPosizione;
    private ImageView imageViewPreview;
    private MaterialButtonToggleGroup toggleSpotType;
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

        IUserRepository userRepository = ServiceLocator.getInstance().getUserRepository();
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);
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
        toggleSpotType = view.findViewById(R.id.toggleSpotType);
        progressBar = view.findViewById(R.id.progressBar);

        view.findViewById(R.id.cardPhotoPicker).setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        view.findViewById(R.id.buttonSave).setOnClickListener(v -> {
            String name = editTextName.getText() != null ? editTextName.getText().toString().trim() : "";
            String posizione = editTextPosizione.getText() != null ? editTextPosizione.getText().toString().trim() : "";

            if (isNameOk(name)) {
                if (selectedPhotoUri != null) {
                    uploadPhotoThenSave(name, posizione);
                } else {
                    saveSpot(name, posizione, null);
                }
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

    private void uploadPhotoThenSave(String name, String posizione) {
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            String base64Photo = null;
            try {
                base64Photo = com.group3boot.sunspot.util.ImageUtil.compressToBase64(requireContext(), selectedPhotoUri);
            } catch (Exception e) {
                // gestito sotto con base64Photo == null
            }

            String finalBase64Photo = base64Photo;
            requireActivity().runOnUiThread(() -> {
                if (finalBase64Photo != null) {
                    saveSpot(name, posizione, finalBase64Photo);
                } else {
                    progressBar.setVisibility(View.GONE);
                    editTextName.setError(getString(R.string.error_photo_upload));
                }
            });
        }).start();
    }

    private void saveSpot(String name, String posizione, @Nullable String photoUrl) {
        progressBar.setVisibility(View.VISIBLE);

        Spot spot = new Spot();
        spot.setName(name);
        spot.setPosizione(posizione);
        spot.setLatitude(latitude);
        spot.setLongitude(longitude);



        spot.setType(toggleSpotType.getCheckedButtonId() == R.id.buttonTypeSunrise
                ? Constants.SPOT_TYPE_SUNRISE : Constants.SPOT_TYPE_SUNSET);

        if (userViewModel.getLoggedUser() != null) {
            spot.setAddedByUserId(userViewModel.getLoggedUser().getUid());
        }

        List<String> photoUrls = new ArrayList<>();
        if (photoUrl != null) {
            photoUrls.add(photoUrl);
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