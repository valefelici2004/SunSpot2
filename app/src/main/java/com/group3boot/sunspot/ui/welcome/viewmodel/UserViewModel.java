package com.group3boot.sunspot.ui.welcome.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group3boot.sunspot.models.User;
import com.group3boot.sunspot.models.UserResult;
import com.group3boot.sunspot.repository.user.IUserRepository;

public class UserViewModel extends ViewModel {

    private final IUserRepository userRepository;
    private MutableLiveData<UserResult> userMutableLiveData;

    public UserViewModel(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public MutableLiveData<UserResult> getUserMutableLiveData(
            String name, String email, String password, boolean isUserRegistered) {
        if (userMutableLiveData == null) {
            getUserData(name, email, password, isUserRegistered);
        }
        return userMutableLiveData;
    }

    public User getLoggedUser() {
        return userRepository.getLoggedUser();
    }

    public MutableLiveData<UserResult> logout() {
        if (userMutableLiveData == null) {
            userMutableLiveData = userRepository.logout();
        } else {
            userRepository.logout();
        }
        return userMutableLiveData;
    }

    private void getUserData(String name, String email, String password, boolean isUserRegistered) {
        userMutableLiveData = userRepository.getUser(name, email, password, isUserRegistered);
    }
}