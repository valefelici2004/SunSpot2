package com.group3boot.sunspot.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class LocationUtil {

    public interface LocationCallback {
        void onLocationResult(Location location);
        void onLocationUnavailable();
    }

    public interface LocalityCallback {
        void onLocalityFound(String locality);
        void onLocalityUnavailable();
    }

    public static boolean hasLocationPermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static void getCurrentLocation(Context context, LocationCallback callback) {
        if (!hasLocationPermission(context)) {
            callback.onLocationUnavailable();
            return;
        }

        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(context);
        client.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        callback.onLocationResult(location);
                    } else {
                        callback.onLocationUnavailable();
                    }
                })
                .addOnFailureListener(e -> callback.onLocationUnavailable());
    }

    public static double distanceInKm(double lat1, double lon1, double lat2, double lon2) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return results[0] / 1000.0;
    }

    public static void getLocalityName(Context context, double latitude, double longitude,
                                       LocalityCallback callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            String locality = null;
            try {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                @SuppressWarnings("deprecation")
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    locality = addresses.get(0).getLocality();
                    if (locality == null) {
                        locality = addresses.get(0).getSubAdminArea();
                    }
                }
            } catch (IOException ignored) {
            }

            String finalLocality = locality;
            new Handler(Looper.getMainLooper()).post(() -> {
                if (finalLocality != null) {
                    callback.onLocalityFound(finalLocality);
                } else {
                    callback.onLocalityUnavailable();
                }
            });
        });
    }
}