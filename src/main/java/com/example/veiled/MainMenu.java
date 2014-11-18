package com.example.veiled;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.veiled.MessageCreator.MessageCreator;
import com.example.veiled.MessageViewer.MessageViewer;
import com.example.veiled.Utils.DatabaseConnection;
import com.example.veiled.Utils.SensorActivity;


public class MainMenu extends Activity {
    /**
     * Called when the activity is first created.
     */

    private Intent nextScreen;

    public Intent getNextScreen(){ return nextScreen; }
    public void setNextScreen(Intent nextScreen){ this.nextScreen = nextScreen; }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Singleton - Database connection
        DatabaseConnection.SetConnectionToDatabase(this);

        setButtonListeners();
    }

    public void setButtonListeners() {

        // Create event Post Message
        Button btnPostNewMessage = (Button) findViewById(R.id.button1);

        // Binding Click event to Button
        btnPostNewMessage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //Starting a new Intent
                nextScreen = new Intent(getApplicationContext(), MessageCreator.class);
                //Closing SecondScreen Activity
                startActivity(nextScreen);
            }
        });

        // Create event Post Message
        Button btnViewMessages = (Button) findViewById(R.id.button2);

        // Binding Click event to Button
        btnViewMessages.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //Starting a new Intent
                nextScreen = new Intent(getApplicationContext(), MessageViewer.class);
                //Closing SecondScreen Activity
                startActivity(nextScreen);
            }
        });


        ////// TEST AREA !

        // Create event Post Message
        Button btnTestSensors = (Button) findViewById(R.id.testSensors);

        // Binding Click event to Button
        btnTestSensors.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //Starting a new Intent
                nextScreen = new Intent(getApplicationContext(), SensorActivity.class);
                //Closing SecondScreen Activity
                startActivity(nextScreen);
            }
        });


    }
}
