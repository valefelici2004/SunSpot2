package com.group3boot.sunspot.source.user;

import com.group3boot.sunspot.models.User;
import com.group3boot.sunspot.repository.user.UserResponseCallback;

/**
 * Classe base per gestire il profilo utente sulla Realtime Database.
 */
public abstract class BaseUserDataRemoteDataSource {
    protected UserResponseCallback userResponseCallback;

    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }

    public abstract void saveUserData(User user);
    public abstract void getUserData(String uid);
}