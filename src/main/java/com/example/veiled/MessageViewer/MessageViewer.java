package com.example.veiled.MessageViewer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.veiled.Utils.DatabaseConnection;
import com.example.veiled.Utils.GpsPositioning;
import com.example.veiled.Utils.MessageQuery;
import com.example.veiled.Utils.DatabaseTable.Message;
import com.example.veiled.R;
import com.example.veiled.Utils.SensorManagement;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;

public class MessageViewer extends Activity implements SurfaceHolder.Callback, LocationListener,SensorEventListener {
    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    boolean previewing = false;
    LayoutInflater controlInflater = null;

    private LocationManager locationManager;
    private String bestProvider;
    private Location currentLocation;
    private GpsPositioning gps;

    private ArrayList<ImageView>imageViews;
    private ArrayList<Double> toMessageRotation;
    private ArrayList<Double> lastRollArray;
    private ArrayList<Double> lastPositionArray;

    private boolean alreadyThere = false;
    private TextView textSensorValues;
    private float[] sensorValues;
    private SensorManagement sensorManager;

    final Context context = this;

    int width;
    int height;
    double lastRollPosition;
    static final float ALPHA = 0.55f;
    static final float BETA = 0.05f;
    private boolean alreadyThere2 = false;
    boolean beenHere = false;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.viewmessagescamera);
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        gps = new GpsPositioning();
        currentLocation = gps.getLastKnownLocation(this, context);

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
        MobileServiceTable<Message> messagesTable = serviceClient.getTable(Message.class);
        MessageQuery query = new MessageQuery(this);

        messagesTable.where().field("latitude").
                lt(currentLocation.getLatitude() + 0.0002).and().field("latitude").
                gt(currentLocation.getLatitude() - 0.0002).and().field("longitude").
                lt(currentLocation.getLongitude() + 0.0002).and().field("longitude").
                gt(currentLocation.getLongitude() - 0.0002).execute(query);
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
        //double userLatitude = 44.44489739;
        //double userLongitude = 26.05656421;

        double userLatitude = currentLocation.getLatitude();
        double userLongitude = currentLocation.getLongitude();

        // for magic
        /*
       for(int i = 0 ; i < messages.size() ; i ++){
            if(Math.abs(messages.get(i).longitude - userLongitude) >= 0.00001 ||
               Math.abs(messages.get(i).latitude - userLatitude) >= 0.00001 ) {
                messages.remove(i);
                i--;
            }
        }
        */

        double to_message_rotation;
        imageViews = new ArrayList<ImageView>(messages.size());
        toMessageRotation = new ArrayList<Double>(messages.size());
        lastPositionArray = new ArrayList<Double>(messages.size());
        lastRollArray = new ArrayList<Double>(messages.size());
        int count = -1;
        for( Message message : messages) {
            count++;
            ImageView imageMessage  = new ImageView(this);
            double curImageViewId = prevTextViewId + 1;

            imageMessage.setId((int)curImageViewId);

            double diffLat = Math.abs(userLatitude - message.latitude);
            double diffLong = Math.abs(userLongitude - message.longitude);

            /*
            folosit pentru a calcula alfa...rotatia mesajului in cadrane
             */
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
            //textToView = textmessage;
            imageViews.add(imageMessage);
            toMessageRotation.add(to_message_rotation);
            lastPositionArray.add(to_message_rotation);
            lastRollArray.add(message.altitude/15);
            viewMessagesLayout.addView(imageMessage);
        }

        if(!beenHere) {
            viewMessagesLayout.addView(textSensorValues);
            beenHere = true;
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
        sensorManager.ResumeActivityRegisterSensors(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
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

    double last_rotation;
    double rotation;
    double alpha = 0.01;
    double roll;
    double last_roll;

    private void setLastRotation(double rotation, int number, double rotationInArray){
        double position_in_screen = rotation - rotationInArray;
        double difference = Math.abs(position_in_screen - lastPositionArray.get(number));
        if(difference > 10 && difference < 60) {
            lastPositionArray.set(number, lastPositionArray.get(number) + ALPHA * (position_in_screen - lastPositionArray.get(number)));
            alreadyThere = false;
        }
        else if(difference < 10) {
            lastPositionArray.set(number, lastPositionArray.get(number) + BETA * (position_in_screen - lastPositionArray.get(number)));
            alreadyThere = false;
        }
        else if(difference > 60){
            if(alreadyThere)
                lastPositionArray.set(number, position_in_screen);
            else
                alreadyThere = true;
        }
    }

    private void setLastRoll(double roll, int number){
        double positionInScreenRoll;
        positionInScreenRoll = lastRollArray.get(number) - roll;
        if(lastRollPosition == 0)
            lastRollPosition = positionInScreenRoll;

        double differenceRoll = Math.abs(positionInScreenRoll - lastRollPosition);
        if (lastRollPosition != 0 && differenceRoll > 2 && differenceRoll < 5) {
            lastRollPosition = lastRollPosition + ALPHA * (positionInScreenRoll - lastRollPosition);
            alreadyThere2 = false;
        } else if (differenceRoll <= 2) {
            lastRollPosition = lastRollPosition + BETA * (positionInScreenRoll - lastRollPosition);
            alreadyThere2 = false;
        } else if (differenceRoll > 5) {
            if (alreadyThere2)
                lastRollPosition = positionInScreenRoll;
            else
                alreadyThere2 = true;
        }
    }

    private void setImagesOnCamera(int number){
        if(imageViews.get(number) != null) {
            RelativeLayout.LayoutParams params =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);

            int widthRatio = 7;
            int heightRatio = 12;

            int elemSizeWidth = width / widthRatio;
            int elemSizeHeight = height / heightRatio;

            params.leftMargin = width / 4 + elemSizeWidth + (int) Math.round(lastPositionArray.get(number)) * 20;
            params.rightMargin = width / 4 + elemSizeWidth - (int) Math.round(lastPositionArray.get(number)) * 20;

            params.topMargin = height/4 +  elemSizeHeight + (int)Math.round(lastRollPosition*150);
            //pus asta
            params.bottomMargin = height/4 +  elemSizeHeight - (int)Math.round(lastRollPosition*150);
            params.height = 2 * elemSizeHeight;
            params.width = 2 * elemSizeWidth;
            if(number == 0)
                imageViews.get(number).setImageResource(R.drawable.coffe_image);
            else
                imageViews.get(number).setImageResource(R.drawable.cupcake);
            imageViews.get(number).setLayoutParams(params);

        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        sensorValues = sensorManager.GetSensorValues(event);

        if(sensorValues != null) {

            roll = sensorValues[0];
            rotation = sensorValues[1];

            if(last_rotation != 0 ){
                rotation = rotation + alpha * (rotation - last_rotation);
            }

            if(last_roll != 0 && Math.abs(roll-last_roll) < 1 ){
                roll = CosineInterpolate(last_roll, roll, 0.15);
            }else if( Math.abs(roll-last_roll) >= 1 ) {
                roll = CosineInterpolate(last_roll, roll, 0.25);
            }
            last_rotation = rotation;
            last_roll = roll;

            textSensorValues.setText("roll is " + roll + "\n" + "rotation is " + rotation + "\n");
            int number = -1;
            if(toMessageRotation == null)
                return;
            for(double rotationInArray : toMessageRotation)
            {
                number++;
                setLastRotation(rotation, number, rotationInArray);
                setLastRoll(roll, number);
                setImagesOnCamera(number);
            }
        }
    }
    double CosineInterpolate(
            double y1,double y2,
            double mu) {
        double mu2;

        mu2 = (1 - Math.cos(mu * Math.PI)) / 2;
        return (y1 * (1 - mu2) + y2 * mu2);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }



}




