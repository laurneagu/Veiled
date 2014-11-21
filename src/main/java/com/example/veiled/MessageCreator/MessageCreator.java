package com.example.veiled.MessageCreator;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import com.example.veiled.Factory.AlertDialogFactory;
import com.example.veiled.Utils.DatabaseTable.Message;
import com.example.veiled.R;
import com.example.veiled.Utils.DatabaseConnection;
import com.example.veiled.Utils.GpsPositioning;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;

public class MessageCreator extends Activity implements SurfaceHolder.Callback, LocationListener {

    private Location currentLocation;
    private Camera camera;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private boolean previewing = false;
    private LayoutInflater controlInflater = null;
    private Button btnSaveMessage;
    private GpsPositioning gps;

    final Context context = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        initView();


        gps = new GpsPositioning();
        currentLocation = gps.getLastKnownLocation(this, context);

        createButton();

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
    protected void onResume() {
        super.onResume();
        gps.requestLocationUpdates(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gps.removeUpdates(this);
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void initView(){
        setContentView(R.layout.cameradisplaywithrectangle);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        getWindow().setFormat(PixelFormat.UNKNOWN);

        surfaceView = (SurfaceView)findViewById(R.id.camerapreview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        controlInflater = LayoutInflater.from(getBaseContext());
        View viewControl = controlInflater.inflate(R.layout.cameracontrol, null);

        LayoutParams layoutParamsControl
                = new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT);
        this.addContentView(viewControl, layoutParamsControl);
    }

    public void createButton(){
        btnSaveMessage = (Button) findViewById(R.id.postmessage);
        // Binding Click event to Button
        btnSaveMessage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //save message in database
                MobileServiceClient serviceClient = DatabaseConnection.getMobileService();
                EditText edittextDescription = (EditText) findViewById(R.id.message);
                Message currMessage = new Message(edittextDescription.getText().toString(), currentLocation.getLatitude(), currentLocation.getLongitude(), 0);

                serviceClient.getTable(Message.class).insert(currMessage, new TableOperationCallback<Message>() {
                    public void onCompleted(Message entity, Exception exception, ServiceFilterResponse response) {
                        if (exception == null) {
                            // Insert succeeded
                            AlertDialog.Builder builder = AlertDialogFactory.CreateAlertDialogOneButtonMessage(MessageCreator.this, context, "Message posted", "The message has been posted at current position");
                            // create alert dialog
                            AlertDialog alertDialog = builder.create();
                            // show it
                            alertDialog.show();

                        } else {
                            // Insert failed
                            AlertDialog.Builder builder = AlertDialogFactory.CreateAlertDialogOneButtonMessage(MessageCreator.this, context, "No internet connection!", "Please check your internet connection");
                            // create alert dialog
                            AlertDialog alertDialog = builder.create();
                            // show it
                            alertDialog.show();
                        }
                    }
                });
            }
        });
    }
}




