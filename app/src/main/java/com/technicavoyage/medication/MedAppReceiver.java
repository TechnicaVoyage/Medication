package com.technicavoyage.medication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

/**
 * Created by Dhaval Srivastava on 21-Jul-16.
 */
public class MedAppReceiver extends BroadcastReceiver {
    int numberLeft;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent background = new Intent(context, MedServiceNotification.class);
        Toast.makeText(context, "AppReceiver", Toast.LENGTH_SHORT).show();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        numberLeft = sp.getInt("numberLeft", 0);
        numberLeft--;
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt("numberLeft", numberLeft);
        ed.apply();
        context.startService(background);
    }
}
