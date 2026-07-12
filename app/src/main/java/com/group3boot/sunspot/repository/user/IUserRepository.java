package com.group3boot.sunspot.repository.user;

import androidx.lifecycle.MutableLiveData;

import com.group3boot.sunspot.models.User;
import com.group3boot.sunspot.models.UserResult;

public interface IUserRepository {
    MutableLiveData<UserResult> getUser(String name, String email, String password, boolean isUserRegistered);
    MutableLiveData<UserResult> logout();
    User getLoggedUser();
    void signUp(String name, String email, String password);
    void signIn(String email, String password);
}