package com.example.veiled.MessageViewer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.veiled.Utils.DatabaseConnection;
import com.example.veiled.Utils.MessageQuery;
import com.example.veiled.Utils.DatabaseTable.Message;
import com.example.veiled.R;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;


/**
 * Created by Laur on 10/22/2014.
 */


public class MessageViewer extends Activity implements SurfaceHolder.Callback, LocationListener {
    static List<Message> m_Messages= new ArrayList<Message>();
    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    boolean previewing = false;
    LayoutInflater controlInflater = null;

    //gps position variables
    private Location currentLocation;
    private LocationManager locationManager;
    private String bestProvider;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.viewmessagescamera);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        getWindow().setFormat(PixelFormat.UNKNOWN);
        surfaceView = (SurfaceView)findViewById(R.id.camerapreview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        controlInflater = LayoutInflater.from(getBaseContext());

        //get gps position
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(true);
        bestProvider = locationManager.getBestProvider(criteria, false);
        currentLocation = locationManager.getLastKnownLocation(bestProvider);

        // query database
        MobileServiceClient serviceClient = DatabaseConnection.getMobileService();
        MessageQuery query = new MessageQuery(this);
        serviceClient.getTable(Message.class).execute(query);

    }

    public void AddMessages(List<Message> messages){
        // Get messages area
        RelativeLayout viewMessagesLayout = new RelativeLayout(this);
        viewMessagesLayout.setMinimumWidth(500);
        viewMessagesLayout.setMinimumHeight(500);

        // show messages from 5 meters around
        int prevTextViewId = 0;


        // get screen size in px
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        for( Message message : messages) {
            TextView textmessage  = new TextView(this);
            double curTextViewId = prevTextViewId + 1;

            textmessage.setId((int)curTextViewId);
            if (Math.abs(message.latitude - currentLocation.getLatitude()) < 0.00005 &&
                Math.abs(message.longitude - currentLocation.getLongitude()) < 0.00005)
                    textmessage.setText(message.message);
            else
                    textmessage.setText("Not in the area");

            RelativeLayout.LayoutParams params =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);

            params.leftMargin = width/2 ; //* (int)Math.pow(-1, curTextViewId) ;

            params.rightMargin = 30;
            //if(curTextViewId % 2 != 0)
            //    params.rightMargin +=width/2;

            params.topMargin = 30;//height/2;

            params.addRule(RelativeLayout.BELOW, prevTextViewId);
            textmessage.setHeight(100);
            textmessage.setWidth(100);
            textmessage.setTextSize(30);

            textmessage.setLayoutParams(params);
            prevTextViewId = (int)curTextViewId;

            textmessage.setTextAppearance(getApplicationContext(),
                    R.style.AudioFileInfoOverlayText);

            Drawable draw = getResources().getDrawable(R.drawable.mygradient);
            textmessage.setBackground(draw);

            textmessage.setGravity(Gravity.CENTER);
            viewMessagesLayout.addView(textmessage);
        }

        LayoutParams layoutParamsControl
                = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);

        this.addContentView(viewMessagesLayout, layoutParamsControl);
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub
        if(previewing){
            camera.stopPreview();
            previewing = false;
        }

        if (camera != null){
            try {
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
                previewing = true;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates( bestProvider, 100, 1, this);

        MobileServiceClient serviceClient = DatabaseConnection.getMobileService();
        MessageQuery query = new MessageQuery(this);
        serviceClient.getTable(Message.class).execute(query);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        camera = Camera.open();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        camera.stopPreview();
        camera.release();
        camera = null;
        previewing = false;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.currentLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}




