package com.group3boot.sunspot.source.user;

import com.group3boot.sunspot.models.User;
import com.group3boot.sunspot.repository.user.UserResponseCallback;

/**
 * Classe base per gestire l'autenticazione dell'utente.
 */
public abstract class BaseUserAuthenticationRemoteDataSource {
    protected UserResponseCallback userResponseCallback;

    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }

    public abstract User getLoggedUser();
    public abstract void logout();
    public abstract void signUp(String name, String email, String password);
    public abstract void signIn(String email, String password);
}