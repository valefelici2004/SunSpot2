package com.group3boot.sunspot.repository.user;

import androidx.lifecycle.MutableLiveData;

import com.group3boot.sunspot.models.User;
import com.group3boot.sunspot.models.UserResult;
import com.group3boot.sunspot.source.user.BaseUserAuthenticationRemoteDataSource;
import com.group3boot.sunspot.source.user.BaseUserDataRemoteDataSource;

public class UserRepository implements IUserRepository, UserResponseCallback {

    private final BaseUserAuthenticationRemoteDataSource userRemoteDataSource;
    private final BaseUserDataRemoteDataSource userDataRemoteDataSource;
    private final MutableLiveData<UserResult> userMutableLiveData;

    public UserRepository(BaseUserAuthenticationRemoteDataSource userRemoteDataSource,
                          BaseUserDataRemoteDataSource userDataRemoteDataSource) {
        this.userRemoteDataSource = userRemoteDataSource;
        this.userDataRemoteDataSource = userDataRemoteDataSource;
        this.userMutableLiveData = new MutableLiveData<>();
        this.userRemoteDataSource.setUserResponseCallback(this);
        this.userDataRemoteDataSource.setUserResponseCallback(this);
    }

    @Override
    public MutableLiveData<UserResult> getUser(String name, String email, String password, boolean isUserRegistered) {
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
    public MutableLiveData<UserResult> logout() {
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

    @Override
    public void onSuccessFromAuthentication(User user) {
        if (user != null) {
            userDataRemoteDataSource.saveUserData(user);
        }
    }

    @Override
    public void onFailureFromAuthentication(String message) {
        userMutableLiveData.postValue(new UserResult.Error(message));
    }

    @Override
    public void onSuccessFromRemoteDatabase(User user) {
        userMutableLiveData.postValue(new UserResult.Success(user));
    }

    @Override
    public void onFailureFromRemoteDatabase(String message) {
        userMutableLiveData.postValue(new UserResult.Error(message));
    }

    @Override
    public void onSuccessLogout() {
        userMutableLiveData.postValue(new UserResult.Success(null));
    }
}