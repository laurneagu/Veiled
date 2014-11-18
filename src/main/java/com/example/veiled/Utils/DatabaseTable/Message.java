package com.example.veiled.Utils.DatabaseTable;

/**
 * Created by Laur on 10/21/2014.
 */
public class Message {

        public String Id;
        public String message;
        public double latitude;
        public double longitude;
        public double altitude;

        public Message(String i_message, double i_latitude, double i_longitude, double i_altitude){
            message = i_message;
            latitude = i_latitude;
            longitude = i_longitude;
            altitude = i_altitude;
        }

 }

