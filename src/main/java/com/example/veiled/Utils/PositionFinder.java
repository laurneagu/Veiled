package com.example.veiled.Utils;

import android.location.Location;

/**
 * Created by Laur on 11/21/2014.
 */
public class PositionFinder {

    public static Location SetMessagePosition(Location userLocation, float roll, float rotation){
        Location messageLocation = userLocation;

        rotation += 180;

        // user GPS is broken or not connected, post to a fake position and tell the user
        if(userLocation == null) {
            userLocation = new Location("fakeProvider");
            userLocation.setLatitude(65.9667);
            userLocation.setLongitude(-18.5333);
        }

        // S - E position
        if( rotation < 90){
            double newLatitude = userLocation.getLatitude()  - Math.sin(rotation);
            messageLocation.setLatitude(newLatitude);

            double newLongitude = userLocation.getLongitude()  + Math.cos(rotation);
            messageLocation.setLongitude(newLongitude);
        }
        else {
            // N - E position
            if(rotation >= 90 && rotation < 180){
                double minimizedRotation = rotation - 90;

                double newLatitude = userLocation.getLatitude()  + Math.sin(minimizedRotation);
                messageLocation.setLatitude(newLatitude);

                double newLongitude = userLocation.getLongitude()  + Math.cos(minimizedRotation);
                messageLocation.setLongitude(newLongitude);
            }
            else{
                // N -W position
                if(rotation >= 180 && rotation < 270){
                    double minimizedRotation = rotation - 180;

                    double newLatitude = userLocation.getLatitude()  + Math.sin(minimizedRotation);
                    //messageLocation.setLatitude(newLatitude);

                    double newLongitude = userLocation.getLongitude()  - Math.cos(minimizedRotation);
                    //messageLocation.setLongitude(newLongitude);
                }
                // S - W position
                else{
                    double minimizedRotation = rotation - 270;

                    double newLatitude = userLocation.getLatitude()  - Math.sin(minimizedRotation);
                    //messageLocation.setLatitude(newLatitude);

                    double newLongitude = userLocation.getLongitude()  - Math.cos(minimizedRotation);
                    //messageLocation.setLongitude(newLongitude);
                }
            }
        }

        return messageLocation;
    }
}