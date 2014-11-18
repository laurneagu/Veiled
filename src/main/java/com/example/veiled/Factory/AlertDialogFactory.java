package com.example.veiled.Factory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.example.veiled.MessageCreator.MessageCreator;

/**
 * Created by Laur on 11/14/2014.
 */
public class AlertDialogFactory {

    public static AlertDialog.Builder CreateAlertDialog(final Activity curr_activity, Context context,
                                                        String title_message, String message_content) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle(title_message);
        // set dialog message
        alertDialogBuilder
                .setMessage(message_content)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        curr_activity.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        return  alertDialogBuilder;
    }

    public static AlertDialog.Builder CreateAlertDialogOneButtonMessage(final Activity curr_activity, Context context,
                                                       String title_message, String message_content) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle(title_message);
        // set dialog message
        alertDialogBuilder
                .setMessage(message_content)
                .setCancelable(false)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        return  alertDialogBuilder;
    }
}
