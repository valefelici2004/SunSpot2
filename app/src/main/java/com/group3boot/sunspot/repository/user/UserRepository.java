package com.group3boot.sunspot.repository.user;

import androidx.lifecycle.MutableLiveData;

import com.group3boot.sunspot.models.User;
import com.group3boot.sunspot.models.User;
import com.group3boot.sunspot.source.user.BaseUserAuthenticationRemoteDataSource;
import com.group3boot.sunspot.source.user.BaseUserDataRemoteDataSource;

/**
 * Repository class per gestire l'autenticazione e i dati dell'utente.
 */
public class UserRepository implements IUserRepository, UserResponseCallback {

    private final BaseUserAuthenticationRemoteDataSource userRemoteDataSource;
    private final BaseUserDataRemoteDataSource userDataRemoteDataSource;
    private final MutableLiveData<User> userMutableLiveData;

    public UserRepository(BaseUserAuthenticationRemoteDataSource userRemoteDataSource,
                          BaseUserDataRemoteDataSource userDataRemoteDataSource) {
        this.userRemoteDataSource = userRemoteDataSource;
        this.userDataRemoteDataSource = userDataRemoteDataSource;
        this.userMutableLiveData = new MutableLiveData<>();
        this.userRemoteDataSource.setUserResponseCallback(this);
        this.userDataRemoteDataSource.setUserResponseCallback(this);
    }

    @Override
    public MutableLiveData<User> getUser(String name, String email, String password, boolean isUserRegistered) {
        if (isUserRegistered) {
            signIn(email, password);
        } else {
            signUp(name, email, password);
        }
        return userMutableLiveData;
    }

    @Override
    public User getLoggedUser() {
        return userRemoteDataSource.getLoggedUser();
    }

    @Override
    public MutableLiveData<User> logout() {
        userRemoteDataSource.logout();
        return userMutableLiveData;
    }

    @Override
    public void signUp(String name, String email, String password) {
        userRemoteDataSource.signUp(name, email, password);
    }

    @Override
    public void signIn(String email, String password) {
        userRemoteDataSource.signIn(email, password);
    }

    // --- Metodi del callback (UserResponseCallback) ---

    @Override
    public void onSuccessFromAuthentication(User user) {
        if (user != null) {
            // Dopo il login/registrazione, salva/verifica il profilo sulla Realtime Database
            userDataRemoteDataSource.saveUserData(user);
        }
    }

    @Override
    public void onFailureFromAuthentication(String message) {
        User.Error result = new User.Error(message);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromRemoteDatabase(User user) {
        User.Success result = new User.Success(user);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onFailureFromRemoteDatabase(String message) {
        User.Error result = new User.Error(message);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessLogout() {
        userMutableLiveData.postValue(new User.Success(null));
    }
}