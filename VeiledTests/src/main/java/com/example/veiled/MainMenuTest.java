package com.example.veiled;

import android.annotation.TargetApi;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.test.ActivityInstrumentationTestCase2;
import android.test.InstrumentationTestCase;
import android.test.mock.MockApplication;
import android.test.mock.MockContext;
import android.view.KeyEvent;
import android.widget.Button;
import com.example.veiled.MainMenu;
import com.example.veiled.Utils.DatabaseConnection;
import junit.framework.Assert;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;


/**
 * Created by Laur on 11/5/2014.
 */
@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class MainMenuTest extends ActivityInstrumentationTestCase2<MainMenu> {
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public MainMenuTest() {
        super("com.example.Veiled", MainMenu.class);
    }

    private MainMenu tested_activity;
    private Intent tested_intent = null;

    public void testDatabaseConnection() throws Exception {
        tested_activity = getActivity();
        DatabaseConnection tested_connection = DatabaseConnection.SetConnectionToDatabase(tested_activity);

        Assert.assertNotNull(tested_connection);
    }

    public void testPostNewMessageButton() throws Exception {
        tested_activity = getActivity();

        // set null value on nextScreen intent
        tested_activity.setNextScreen(tested_intent);

        final Button testPostNewMessageButton = (Button) tested_activity.findViewById(R.id.button1);
        Runnable uRunnable = new Runnable() {
            public void run() {
                testPostNewMessageButton.performClick();
             }
        };

       tested_activity.runOnUiThread(uRunnable);
       // wait button to be pressed and listeners to be notified
       Thread.sleep(100);

       assertNotNull(testPostNewMessageButton);
       assertNotNull(tested_activity.getNextScreen());
    }


}
