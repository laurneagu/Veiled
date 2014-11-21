package com.example.veiled.Utils;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import com.example.veiled.MessageCreator.MessageCreator;

public class GpsPositioning {
    private LocationManager locationManager;
    private String bestProvider;
    public Location getLastKnownLocation(Activity currentActivity, Context currentContext) {

        locationManager = (LocationManager) currentActivity.getSystemService(currentContext.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(true);
        bestProvider = locationManager.getBestProvider(criteria, false);
        return locationManager.getLastKnownLocation(bestProvider);
    }

    public void requestLocationUpdates(LocationListener currentListener) {
        locationManager.requestLocationUpdates(bestProvider, 10000, 1,  currentListener);
    }

    public void removeUpdates(MessageCreator messageCreator) {
        locationManager.removeUpdates(messageCreator);
    }


}
