package com.example.veiled.Utils;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


public class SensorManagement {
    // Phone sensors
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetic;

    // Values from sensors
    float[] mGravity;
    float[] mGeomagnetic;

    // Last values from sensors
    float last_azimuth;
    float last_pitch;
    float last_roll;

    static final float ALPHA = 0.35f;

    public void InitializeSensors(Activity curr_activity, Context curr_context){
        mSensorManager = (SensorManager) curr_activity.getSystemService(curr_context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void ResumeActivityRegisterSensors(Activity curr_activity){
        mSensorManager.registerListener((SensorEventListener) curr_activity, mAccelerometer, SensorManager.SENSOR_DELAY_UI);//SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener((SensorEventListener) curr_activity,mMagnetic, SensorManager.SENSOR_DELAY_UI);
    }

    public float[] GetSensorValues(SensorEvent curr_event) {
        float[] sensorFinalValues = new float[3];
        float pitch = curr_event.values[1];
        float roll = curr_event.values[2];
        float azimuth = curr_event.values[0];

        if (last_azimuth == last_pitch && last_azimuth == last_roll) {
            last_pitch = pitch;
            last_roll = roll;
            last_azimuth = azimuth;
            return null;
        }


        //last_pitch = last_pitch + ALPHA * (pitch - last_pitch);

        if (curr_event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = curr_event.values;
            last_roll = last_roll + ALPHA * (roll - last_roll);
            sensorFinalValues[0] = Math.round(last_roll);
        }

        if (curr_event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = curr_event.values;

        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float R2[] = new float[9];
            float I[] = new float[9];

            if (SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)) {
                SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, R2);

                float orientation[] = new float[3];
                SensorManager.getOrientation(R2, orientation);

                float azimut = orientation[0];
                float pitchIn = orientation[1];

                if(last_azimuth != 0)
                    last_azimuth = last_azimuth + ALPHA * (azimut - last_azimuth);
                else
                    last_azimuth = azimut;

                if(last_pitch != 0)
                    last_pitch = last_pitch + ALPHA * (pitchIn - last_pitch);
                else
                    last_pitch = pitchIn;

                float rotation = -last_azimuth * 360 / (2 * 3.14159f);

                sensorFinalValues[1] = Math.round(rotation);
            }

        }
        return  sensorFinalValues;
    }


}
