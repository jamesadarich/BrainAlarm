package com.jamesrichford.brainalarm.com.jamesrichford.brainalarm.puzzles;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.WindowManager;

import com.jamesrichford.brainalarm.AlarmTrigger;

import java.util.Calendar;

/**
 * Created by James on 19/12/13.
 */
public abstract class Puzzle extends Activity {

    private boolean isReset = false;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Wake the phone from whatever is going on at the time.

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                + WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                + WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                + WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                + WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        long[] s = { 500, 500 };
        vibrator.vibrate(s, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        vibrator.cancel();
    }


    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        snooze();
    }

    @Override
    public void onBackPressed() {
        // Do nothing!
    }

    private void snooze() {
        /*
        ActivityManager am = (ActivityManager) getSystemService(Service.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks;
        tasks = am.getRunningTasks(1);
        RunningTaskInfo running = tasks.get(0);
        */
        //if(!isReset)
        {
            Context context = this;
            Intent activate = new Intent(context, AlarmTrigger.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, activate, 0);

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + 1);

            AlarmManager alarms ;
            alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarms.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), alarmIntent);
            finish();
            isReset = true;
        }
    }
}
