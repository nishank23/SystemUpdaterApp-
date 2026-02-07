package com.lighko.updates.viewModels;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.lighko.updates.services.Repositories.UpdateRepository;
import com.lighko.updates.Utils.SharedPreferencesHelper;

public class UpdateViewModelFactory implements ViewModelProvider.Factory {
    private final UpdateRepository updateRepository;
    private final SharedPreferencesHelper sharedPreferencesHelper;

    public UpdateViewModelFactory(UpdateRepository updateRepository, SharedPreferencesHelper sharedPreferencesHelper) {
        this.updateRepository = updateRepository;
        this.sharedPreferencesHelper = sharedPreferencesHelper;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UpdateViewModel.class)) {
            return (T) new UpdateViewModel(updateRepository, sharedPreferencesHelper);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
