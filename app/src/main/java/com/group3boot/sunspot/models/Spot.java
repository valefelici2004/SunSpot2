package com.group3boot.sunspot.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@TypeConverters(Converters.class)
public class Spot implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private long uid;

    private String firebaseId;
    private String name;
    private String posizione;
    private double latitude;
    private double longitude;
    private List<String> photoUrls;
    private String addedByUserId;
    private List<String> favoritedByUserIds;
    private String type; // "sunrise" oppure "sunset"

    public Spot() {
        photoUrls = new ArrayList<>();
        favoritedByUserIds = new ArrayList<>();
    }

    public long getUid() { return uid; }
    public void setUid(long uid) { this.uid = uid; }

    public String getFirebaseId() { return firebaseId; }
    public void setFirebaseId(String firebaseId) { this.firebaseId = firebaseId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPosizione() { return posizione; }
    public void setPosizione(String posizione) { this.posizione = posizione; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public List<String> getPhotoUrls() { return photoUrls; }
    public void setPhotoUrls(List<String> photoUrls) { this.photoUrls = photoUrls; }

    public String getAddedByUserId() { return addedByUserId; }
    public void setAddedByUserId(String addedByUserId) { this.addedByUserId = addedByUserId; }

    public List<String> getFavoritedByUserIds() { return favoritedByUserIds; }
    public void setFavoritedByUserIds(List<String> favoritedByUserIds) { this.favoritedByUserIds = favoritedByUserIds; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isSunriseSpot() {
        return "sunrise".equals(type);
    }

    // Restituisce true se QUESTO utente specifico ha messo preferito
    public boolean isFavoritedBy(String userId) {
        return favoritedByUserIds != null && favoritedByUserIds.contains(userId);
    }

    // Aggiunge/rimuove l'utente dalla lista dei preferiti
    public void toggleFavorite(String userId) {
        if (favoritedByUserIds == null) favoritedByUserIds = new ArrayList<>();
        if (favoritedByUserIds.contains(userId)) {
            favoritedByUserIds.remove(userId);
        } else {
            favoritedByUserIds.add(userId);
        }
    }

    public String getGoogleMapsUri() {
        return "geo:0,0?q=" + latitude + "," + longitude + "(" + name + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Spot spot = (Spot) o;
        return Objects.equals(firebaseId, spot.firebaseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firebaseId);
    }

    public static Spot getSampleSpot() {
        Spot sample = new Spot();
        sample.setName("Nome spot di esempio");
        sample.setPosizione("Posizione di esempio");
        return sample;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeLong(this.uid);
        parcel.writeString(this.firebaseId);
        parcel.writeString(this.name);
        parcel.writeString(this.posizione);
        parcel.writeDouble(this.latitude);
        parcel.writeDouble(this.longitude);
        parcel.writeStringList(this.photoUrls);
        parcel.writeString(this.addedByUserId);
        parcel.writeStringList(this.favoritedByUserIds);
        parcel.writeString(this.type);
    }

    protected Spot(Parcel in) {
        this.uid = in.readLong();
        this.firebaseId = in.readString();
        this.name = in.readString();
        this.posizione = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.photoUrls = new ArrayList<>();
        in.readStringList(this.photoUrls);
        this.addedByUserId = in.readString();
        this.favoritedByUserIds = new ArrayList<>();
        in.readStringList(this.favoritedByUserIds);
        this.type = in.readString();
    }

    public static final Parcelable.Creator<Spot> CREATOR = new Parcelable.Creator<Spot>() {
        @Override
        public Spot createFromParcel(Parcel source) {
            return new Spot(source);
        }

        @Override
        public Spot[] newArray(int size) {
            return new Spot[size];
        }
    };
}