package com.aren.thewitnesspuzzle;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;

import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactory;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactoryManager;

import java.util.List;
import java.util.Random;

public class LockscreenService extends Service {

    public ScreenReceiver mReceiver = null;

    public Game game;

    public PuzzleFactoryManager puzzleFactoryManager;

    public Puzzle puzzle;

    public int phoneState = TelephonyManager.CALL_STATE_IDLE;
    public boolean interrupted = false;

    public LockscreenService() {

    }

    @Override
    public void onCreate() {
        game = new Game(this);
        game.setOnSolved(new Runnable() {
            @Override
            public void run() {
                unlockScreen();
                puzzle = null;
            }
        });

        puzzleFactoryManager = new PuzzleFactoryManager(this);

        Log.i("TAG", "onCreate");
        super.onCreate();
        mReceiver = new ScreenReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.SCREEN_ON");
        filter.addAction("android.intent.action.SCREEN_OFF");
        //filter.setPriority(2147483647);
        registerReceiver(mReceiver, filter);

        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                new Intent(this, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        Notification notification;
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel("com.aren.thewitnesspuzzle", "MyChannel", NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            notification = new Notification.Builder(this, "com.aren.thewitnesspuzzle")
                    .setContentTitle("The Witness Lock Screen")
                    .setContentText("Service is running")
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentIntent(pendingIntent)
                    .build();
        }
        else{
            notification = new Notification.Builder(this)
                    .setContentTitle("The Witness Lock Screen")
                    .setContentText("Service is running")
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentIntent(pendingIntent)
                    .build();
        }
        startForeground(2277, notification);

        return START_STICKY;
    }

    public class ScreenReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Log.i("TAG", "SCREEN_OFF");

                if(phoneState != TelephonyManager.CALL_STATE_IDLE) return;

                if(!game.getSettings().getHoldingPuzzles() || puzzle == null){
                    Random random = new Random();
                    List<PuzzleFactory> factories = puzzleFactoryManager.getActivatedPuzzleFactories();
                    if(factories.size() > 0){
                        puzzle = factories.get(random.nextInt(factories.size())).generate(game, random);
                    }
                }

                if(puzzle != null){
                    game.setPuzzle(puzzle);
                    lockScreen(context);
                }
            }
            else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                Log.i("TAG", "SCREEN_ON");
            }
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mReceiver != null){
            unregisterReceiver(mReceiver);
        }
    }

    public void lockScreen(Context context){
        if(isLocked()) return;

        WindowManager mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        Point point = new Point();
        mWindowManager.getDefaultDisplay().getSize(point);

        WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                point.y + 500, //extra height to fill screen completely
                /*WindowManager.LayoutParams.TYPE_APPLICATION_PANEL,
                //WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
                        | WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_SECURE,
                PixelFormat.RGBA_8888);*/
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);

        if(game.getSurfaceView().getParent() == null){
            //권한이 없다면 여기서 에러 발생
            mWindowManager.addView(game.getSurfaceView(), mLayoutParams);
        }

        // First draw
        game.update();
    }

    public void unlockScreen(){
        if(!isLocked()) return;

        WindowManager mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.removeView(game.getSurfaceView());
    }

    public boolean isLocked(){
        return game.getSurfaceView().getParent() != null;
    }

    PhoneStateListener phoneListener = new PhoneStateListener(){
        @Override
        public void onCallStateChanged(int state, String incomingNumber){
            phoneState = state;
            if(state == TelephonyManager.CALL_STATE_IDLE){
                if(interrupted){
                    lockScreen(LockscreenService.this);
                    interrupted = false;
                }
            }
            else if(state == TelephonyManager.CALL_STATE_RINGING){
                if(isLocked()){
                    interrupted = true;
                    unlockScreen();
                }
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    };
}
