package com.example.veiled.MessageViewer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
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
import android.widget.Toast;
import com.example.veiled.Utils.DatabaseConnection;
import com.example.veiled.Utils.MessageQuery;
import com.example.veiled.Utils.DatabaseTable.Message;
import com.example.veiled.R;
import com.example.veiled.Utils.SensorManagement;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;


/**
 * Created by Laur on 10/22/2014.
 */


public class MessageViewer extends Activity implements SurfaceHolder.Callback, LocationListener,SensorEventListener {
    static List<Message> m_Messages= new ArrayList<Message>();
    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    boolean previewing = false;
    LayoutInflater controlInflater = null;

    //gps position variables
    // private Location currentLocation; <-- uncomment this after
    private LocationManager locationManager;
    private String bestProvider;

    private TextView textToView;

    private TextView textSensorValues;
    private float[] sensorValues;
    private SensorManagement sensorManager;

    double to_message_rotation;
    double message_roll;
    int width;
    int height;
    double last_position_in_screen;
    static final float ALPHA = 0.55f;
    static final float BETA = 0.05f;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.viewmessagescamera);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        getWindow().setFormat(PixelFormat.UNKNOWN);
        surfaceView = (SurfaceView)findViewById(R.id.camerapreview2);
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

        //currentLocation = locationManager.getLastKnownLocation(bestProvider);

        // textview sensors
        textSensorValues = new TextView(getApplicationContext());//(TextView) findViewById(R.id.sensorValues);
        textSensorValues.setWidth(100);
        textSensorValues.setHeight(200);
        RelativeLayout.LayoutParams params2 =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        params2.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        textSensorValues.setLayoutParams(params2);

        // sensors
        sensorValues = new float[2];
        sensorManager = new SensorManagement();
        sensorManager.InitializeSensors(this,this);

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

        // get screen size in px --- might be usefull
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        // fake location
        double userLatitude = 44.44489739;
        double userLongitude = 26.05656421;

        for( Message message : messages) {
            TextView textmessage  = new TextView(this);
            double curTextViewId = prevTextViewId + 1;

            textmessage.setId((int)curTextViewId);

            double diffLat = Math.abs(userLatitude - message.latitude);
            double diffLong = Math.abs(userLongitude - message.longitude);

            to_message_rotation  = 57.2957795 * Math.atan(diffLat/diffLong); // radians

            // N - E
            if(userLongitude < message.longitude && userLatitude < message.latitude){
                // keeps its value
            }
            else{
                // N - W
                if(userLongitude < message.longitude && userLatitude > message.latitude){
                    to_message_rotation += 90;
                }
                else
                    // S - W
                    if(userLongitude > message.longitude && userLatitude > message.latitude){
                        to_message_rotation = -90 - to_message_rotation;
                    }
                    // S - E
                    else{
                        to_message_rotation  = - to_message_rotation;
                    }
            }

            // partial solution -- TODO must pass all values
            message_roll = message.altitude / 15;
            textToView = textmessage;

            viewMessagesLayout.addView(textmessage);
        }

        if(beenHere == false) {
            viewMessagesLayout.addView(textSensorValues);
            beenHere = true;
        }

        LayoutParams layoutParamsControl
                = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);

        this.addContentView(viewMessagesLayout, layoutParamsControl);
    }

    boolean beenHere = false;
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                //Log.d(TAG, "Camera found");
                cameraId = i;
                break;
            }
        }
        this.setCameraDisplayOrientation(cameraId, this.camera);

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

    /**
     * Calling this method makes the camera image show in the same orientation as the display.
     * NOTE: This method is not allowed to be called during preview.
     *
     * @param cameraId
     * @param camera
     */
    @SuppressLint("NewApi")
    public void setCameraDisplayOrientation(int cameraId, android.hardware.Camera camera) {
        int rotation = ((WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        //if (Utils.hasGingerbread()) {
            android.hardware.Camera.CameraInfo info =
                    new android.hardware.Camera.CameraInfo();
            android.hardware.Camera.getCameraInfo(cameraId, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360;  // compensate the mirror
            } else {  // back-facing
                result = (info.orientation - degrees + 360) % 360;
            }
          try {
            camera.setDisplayOrientation(result);
        } catch (Exception e) {
            // may fail on old OS versions. ignore it.
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates( bestProvider, 100, 1, this);

        MobileServiceClient serviceClient = DatabaseConnection.getMobileService();
        MessageQuery query = new MessageQuery(this);
        serviceClient.getTable(Message.class).execute(query);

        sensorManager.ResumeActivityRegisterSensors(this);
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
        //this.currentLocation = location;
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        sensorValues = sensorManager.GetSensorValues(event);

        if(sensorValues != null) {
            textSensorValues.setText("roll is " + sensorValues[0] + "\n" + "rotation is " + sensorValues[1] + "\n" +
                    " rotation to message is " + to_message_rotation);

            double position_in_screen = sensorValues[1] - to_message_rotation;
            boolean alreadyThere = false;
            double difference = Math.abs(position_in_screen - last_position_in_screen);
            if(last_position_in_screen != 0 &&  difference > 10 && difference < 60) {
                last_position_in_screen = last_position_in_screen + ALPHA * (position_in_screen - last_position_in_screen);
                alreadyThere = false;
            }
            else if(difference < 10) {
                last_position_in_screen = last_position_in_screen + BETA * (position_in_screen - last_position_in_screen);
                alreadyThere = false;
            }
            else if(difference > 60){
                if(alreadyThere)
                    last_position_in_screen = position_in_screen;
                else
                    alreadyThere = true;
            }
               // last_position_in_screen = last_position_in_screen + ALPHA * (position_in_screen - last_position_in_screen);
               // else last_position_in_screen = position_in_screen;


            // set edittext position
            if(textToView != null) {
                RelativeLayout.LayoutParams params =
                        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);

                int widthRatio = 7;
                int heightRatio = 12;

                int elemSizeWidth = width / widthRatio;
                int elemSizeHeight = height / heightRatio;

                params.leftMargin = width / 4 + elemSizeWidth + (int) Math.round(last_position_in_screen) * 20; // + 20 *70* (int)Math.signum(position_in_screen - last_position_in_screen);

                params.topMargin = height/4 +  elemSizeHeight * (1 - (int)(sensorValues[0] - message_roll)) ;

                textToView.setHeight(2 * elemSizeHeight);
                textToView.setWidth(2 * elemSizeWidth);
                textToView.setTextSize(20);
                textToView.setText("Laur se misca");

                textToView.setLayoutParams(params);

                textToView.setTextAppearance(getApplicationContext(),
                        R.style.AudioFileInfoOverlayText);

                Drawable draw = getResources().getDrawable(R.drawable.mygradient);
                textToView.setBackground(draw);

                textToView.setGravity(Gravity.CENTER);
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}




