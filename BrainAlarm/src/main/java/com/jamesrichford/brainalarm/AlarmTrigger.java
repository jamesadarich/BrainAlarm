package com.jamesrichford.brainalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.os.Vibrator;

import com.jamesrichford.brainalarm.com.jamesrichford.brainalarm.puzzles.MathPuzzle;

import java.sql.Time;

public class AlarmTrigger extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //MediaPlayer mp = MediaPlayer.create(context, R.raw.ferry_sound);
        //mp.start();
        PowerManager pm = (PowerManager)     context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");
        wl.acquire();
        Intent puzzleIntent = new Intent(context, MathPuzzle.class);
        puzzleIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                + Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(puzzleIntent);
    }

}
