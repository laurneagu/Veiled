package com.example.veiled.Utils;

import android.location.Location;

/**
 * Created by Laur on 11/21/2014.
 */
public class PositionFinder {

    public static double[] SetMessagePosition(Location userLocation, float roll, float rotation){

        double[] vals = new double[2];

        // user GPS is broken or not connected, post to a fake position and tell the user
        if(userLocation == null) {
            userLocation = new Location("fakeProvider");
            userLocation.setLatitude(65.9667);
            userLocation.setLongitude(-18.5333);
        }

        double r = 0.00002702702; // actually 3 / 111000

        // S - E position
        if(rotation < 0 && rotation > -90){
            double newLatitude = userLocation.getLatitude()  - r * Math.sin(Math.toRadians(rotation));
            vals[0] = newLatitude;

            double newLongitude = userLocation.getLongitude()  + r * Math.cos(Math.toRadians(rotation));
            vals[1] = newLongitude;
        }
        else {
            // N - E position
            if (rotation < 90 && rotation > 0) {
                double newLatitude = userLocation.getLatitude() + r * Math.sin(Math.toRadians(rotation));
                vals[0] = newLatitude;

                double newLongitude = userLocation.getLongitude() + r * Math.cos(Math.toRadians(rotation));
                vals[1] = newLongitude;
            } else {
                // N - W position
                if(rotation > 90 && rotation < 180){
                    double newLatitude = userLocation.getLatitude()  + r * Math.sin(Math.toRadians(rotation));
                    vals[0] = newLatitude;

                    double newLongitude = userLocation.getLongitude()  - r * Math.cos(Math.toRadians(rotation));
                    vals[1] = newLongitude;
                }
                // S - W position
                else {
                    double newLatitude = userLocation.getLatitude()  - r * Math.sin(Math.toRadians(rotation));
                    vals[0] = newLatitude;

                    double newLongitude = userLocation.getLongitude()  - r * Math.cos(Math.toRadians(rotation));
                    vals[1] = newLongitude;
                }
            }
        }

        return vals;
    }
}