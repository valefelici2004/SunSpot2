package com.group3boot.sunspot.ui.home.spotviewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.group3boot.sunspot.repository.spot.SpotRepository;

/**
 * Factory personalizzata per creare SpotViewModel con un costruttore custom.
 */
public class SpotViewModelFactory implements ViewModelProvider.Factory {

    private final SpotRepository spotRepository;

    public SpotViewModelFactory(SpotRepository spotRepository) {
        this.spotRepository = spotRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new SpotViewModel(spotRepository);
    }
}