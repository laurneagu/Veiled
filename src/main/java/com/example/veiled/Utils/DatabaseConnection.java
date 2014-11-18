package com.example.veiled.Utils;

import android.app.Activity;
import android.provider.ContactsContract;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import java.net.MalformedURLException;

/**
 * Created by Laur on 10/21/2014.
 */
public class DatabaseConnection {

    private static MobileServiceClient mClient;
    private boolean isSet;

    private String url = "https://veiled.azure-mobile.net/";
    private String connString = "BrWeCcLvUcGVKsTUhLJbDhMAYxXuOU63";

    public DatabaseConnection(Activity current_activity){
        if(!isSet){
            try {
                mClient = new MobileServiceClient(url, connString, current_activity);
                isSet = true;
            } catch (MalformedURLException e) {
                mClient = null;
                e.printStackTrace();
                isSet = false;
            }{}
        }
    }

    public static MobileServiceClient getMobileService(){
        return mClient;
    }

    public static DatabaseConnection SetConnectionToDatabase(Activity m_activity){
        return new DatabaseConnection(m_activity);
    }
}