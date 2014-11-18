package com.example.veiled.Utils;

import android.app.Activity;
import android.content.Context;
import android.hardware.*;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.veiled.R;

/**
 * Created by Laur on 10/22/2014.
 */
public class SensorActivity extends Activity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetic;

    float[] mGravity;
    float[] mGeomagnetic;

    float last_azimuth;
    float last_pitch;
    float last_roll;


    TextView first;
    TextView second;
    TextView third;
    static final float ALPHA = 0.15f;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensoractivity);

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

    protected void onPause() {
        super.onPause();
        //mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

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
        //last_roll = last_roll +ALPHA*(roll - last_roll);


        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = event.values;
            last_roll = last_roll +ALPHA*(roll - last_roll);
            third.setText("last_roll is " + Math.round(last_roll));//+" rotation roll "+Math.round(rotation2));

        }


        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;

        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float R2[] = new float[9];
            float I[] = new float[9];

        if (SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)) {
                SensorManager.remapCoordinateSystem(R,
                    SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, R2);

                // orientation contains azimut, pitch and roll
                float orientation[] = new float[3];
                SensorManager.getOrientation(R2, orientation);

                float azimut = orientation[0];

                float rotation = -azimut * 360 / (2 * 3.14159f);
                second.setText("rotation is " + Math.round(rotation));
       }
    }
  }

}