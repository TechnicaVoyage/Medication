package com.technicavoyage.medication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    EditText medName;
    NumberPicker duration, cdr;
    Button selectTime, reset;
    TimePicker timePicker;
    CheckBox repeat;
    String mName;
    int durationV, cdV, hour, minutes;
    Calendar cal;

    private void savePrefs(String mName, int durationV, int cdrV, Boolean repeat, int hour, int minutes) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("mName", mName);
        edit.putInt("durationV", durationV);
        edit.putInt("cdrV", cdrV);
        edit.putBoolean("repeat", repeat);
        edit.putInt("hour", hour);
        edit.putInt("minutes", minutes);
        edit.apply();
    }

    private void deletePrefs() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sp.edit().clear().apply();
        Intent intentMed = new Intent(getApplicationContext(), MedAppReceiver.class);
        PendingIntent pIntendMed = PendingIntent.getBroadcast(getApplicationContext(), 7, intentMed, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmMgr.cancel(pIntendMed);
    }

    private void loadPrefs() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mName = sp.getString("mName", "Enter name");
        durationV = sp.getInt("durationV", 1);
        cdV = sp.getInt("cdrV", 1);
        repeat.setChecked(sp.getBoolean("repeat", false));
        hour = sp.getInt("hour", cal.getInstance().get(Calendar.HOUR));
        timePicker.setCurrentHour(hour);
        minutes = sp.getInt("minutes", cal.getInstance().get(Calendar.MINUTE));
        timePicker.setCurrentMinute(minutes);
        duration.setValue(durationV);
        cdr.setValue(cdV);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        medName = (EditText) findViewById(R.id.medicineName);
        duration = (NumberPicker) findViewById(R.id.durationVal);
        duration.setMinValue(1);
        duration.setMaxValue(31);
        cdr = (NumberPicker) findViewById(R.id.cooldownVal);
        cdr.setMinValue(1);
        cdr.setMaxValue(60);
        repeat = (CheckBox) findViewById(R.id.repeatCheck);
        selectTime = (Button) findViewById(R.id.selectTime);
        reset = (Button) findViewById(R.id.reset);
        timePicker = (TimePicker) findViewById(R.id.timePick);
        loadPrefs();
        medName.setText(mName);
        selectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Save settings and set alarm
                mName = medName.getText().toString();
                durationV = duration.getValue();
                cdV = cdr.getValue();
                hour = timePicker.getCurrentHour();
                minutes = timePicker.getCurrentMinute();
                int nextDay = 0; //If time selected has already passed
                cal = Calendar.getInstance();
                if (hour - cal.get(Calendar.HOUR) > 0 || (cal.get(Calendar.HOUR) == 0 && minutes <= cal.get(Calendar.MINUTE))) {
                    nextDay++;
                    cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE) + nextDay, cal.get(hour), cal.get(minutes), 0);
                } else
                    cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), hour, minutes, 0);
                Intent intentMed = new Intent(getApplicationContext(), MedAppReceiver.class);
                PendingIntent pIntentMed = PendingIntent.getBroadcast(getApplicationContext(), 7, intentMed, PendingIntent.FLAG_UPDATE_CURRENT);
                Toast.makeText(MainActivity.this, cal.getTimeInMillis() + "", Toast.LENGTH_SHORT).show();
                AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmMgr.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), pIntentMed);
                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 86400000, pIntentMed);
                savePrefs(mName, durationV, cdV, repeat.isChecked(), hour, minutes);
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePrefs();
                Toast.makeText(MainActivity.this, "Cycles and Settings cleared", Toast.LENGTH_SHORT).show();
            }
        });
    }
}