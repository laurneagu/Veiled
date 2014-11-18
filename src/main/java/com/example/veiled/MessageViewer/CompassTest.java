package com.example.veiled.MessageViewer;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import com.example.veiled.R;

/**
 * Created by Laur on 10/28/2014.
 */
public class CompassTest extends Activity implements SensorEventListener{
    float[] mGravity;
    float[] mGeomagnetic;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetic;

    float last_azimuth, last_pitch, last_roll;
    float ALPHA;

    TextView first, second, third;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compass);

        first = (TextView) findViewById(R.id.one);
        second = (TextView) findViewById(R.id.two);
        third = (TextView) findViewById(R.id.three);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this,mMagnetic, SensorManager.SENSOR_DELAY_NORMAL);
        // mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_GAME);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        double x180pi = 180.0 / Math.PI;
        float azimuth = (float) (event.values[0]);
        float pitch = (float) (event.values[1]);
        float roll = (float) (event.values[2]);

        if(last_azimuth == last_pitch && last_azimuth == last_roll){
            last_azimuth = azimuth;
            last_pitch = pitch;
            last_roll = roll;
            return;
        }

        last_azimuth = last_azimuth+ALPHA*(azimuth - last_azimuth);
        last_pitch = last_pitch+ALPHA*(pitch - last_pitch);
        last_roll = last_roll +ALPHA*(roll - last_roll);

        /* In landscape mode swap pitch and roll and invert the pitch.*/
        float rotation = (float) (-last_azimuth * 360 / (2 * Math.PI));
        float rotation1 = (float) (-last_pitch * 360 / (2 * Math.PI));
        float rotation2 = (float) (-last_roll * 360 / (2 * Math.PI));

        /*
        float degree1 = Math.round(event.values[0]);
        float degree2 = Math.round(event.values[1]);
        float degree3 = Math.round(event.values[2]);
        */


        first.setText("last_azimut is "+Math.round(last_azimuth)+" rotation azimuth "+Math.round(rotation1));
        second.setText("last_pitch is " + Math.round(last_pitch)+" rotation pitch "+Math.round(rotation1));
        third.setText("last_roll is " + Math.round(last_roll)+" rotation roll "+Math.round(rotation1));

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;

        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
        }
            /*
            if (SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)) {

                // orientation contains azimut, pitch and roll
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);

                float azimut = orientation[0];
                float rotation = -azimut * 360 / (2 * 3.14159f);

                first.setText("azimuth is " + azimut);
                second.setText("pitch is " + orientation[1]);
                third.setText("roll is " + orientation[2]);

           // }
        }*/
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}