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
    private boolean liked;
    private boolean addedByMe;

    public Spot() {
        photoUrls = new ArrayList<>();
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

    public boolean isLiked() { return liked; }
    public void setLiked(boolean liked) { this.liked = liked; }

    public boolean isAddedByMe() { return addedByMe; }
    public void setAddedByMe(boolean addedByMe) { this.addedByMe = addedByMe; }


    private String type; // "sunrise" oppure "sunset"

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isSunriseSpot() {
        return "sunrise".equals(type);
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
        parcel.writeString(this.type);
        parcel.writeByte(this.liked ? (byte) 1 : (byte) 0);
        parcel.writeByte(this.addedByMe ? (byte) 1 : (byte) 0);
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
        this.type = in.readString();
        this.liked = in.readByte() != 0;
        this.addedByMe = in.readByte() != 0;
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