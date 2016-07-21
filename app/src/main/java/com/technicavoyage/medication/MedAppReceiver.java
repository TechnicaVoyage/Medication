package com.technicavoyage.medication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by TechnicaVoyage on 21-Jul-16.
 */
public class MedAppReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent background = new Intent(context, MedServiceNotification.class);
        Toast.makeText(context, "AppReceiver", Toast.LENGTH_SHORT).show();
        context.startService(background);
    }
}
