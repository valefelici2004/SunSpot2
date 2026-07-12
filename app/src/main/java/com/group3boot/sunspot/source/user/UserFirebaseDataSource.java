package com.group3boot.sunspot.source.user;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group3boot.sunspot.models.User;
import com.group3boot.sunspot.util.Constants;

/**
 * Classe che gestisce il profilo utente usando Firebase Realtime Database.
 */
public class UserFirebaseDataSource extends BaseUserDataRemoteDataSource {

    private final DatabaseReference databaseReference;

    public UserFirebaseDataSource() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public void saveUserData(User user) {
        databaseReference.child(Constants.FIREBASE_USERS_COLLECTION).child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // L'utente esiste già nel database, non serve riscriverlo
                            userResponseCallback.onSuccessFromRemoteDatabase(user);
                        } else {
                            databaseReference.child(Constants.FIREBASE_USERS_COLLECTION).child(user.getUid())
                                    .setValue(user)
                                    .addOnSuccessListener(unused -> userResponseCallback.onSuccessFromRemoteDatabase(user))
                                    .addOnFailureListener(e -> userResponseCallback.onFailureFromRemoteDatabase(e.getLocalizedMessage()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        userResponseCallback.onFailureFromRemoteDatabase(error.getMessage());
                    }
                });
    }

    @Override
    public void getUserData(String uid) {
        databaseReference.child(Constants.FIREBASE_USERS_COLLECTION).child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            user.setUid(uid);
                        }
                        userResponseCallback.onSuccessFromRemoteDatabase(user);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        userResponseCallback.onFailureFromRemoteDatabase(error.getMessage());
                    }
                });
    }
}