package com.aren.thewitnesspuzzle.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;

import com.aren.thewitnesspuzzle.R;
import com.aren.thewitnesspuzzle.activity.MainActivity;
import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.animation.PuzzleFadeInAnimation;
import com.aren.thewitnesspuzzle.puzzle.animation.PuzzleFadeOutAnimation;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactory;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactoryManager;
import com.aren.thewitnesspuzzle.puzzle.sound.Sounds;
import com.aren.thewitnesspuzzle.render.PuzzleRenderer;

import java.util.Random;

public class LockscreenService extends Service {

    public ScreenReceiver mReceiver = null;

    public Game game;

    public PuzzleFactoryManager puzzleFactoryManager;

    public PuzzleRenderer puzzle;

    public int phoneState = TelephonyManager.CALL_STATE_IDLE;
    public boolean interrupted = false;

    public int lockToken = 0;

    public final Handler handler = new Handler();

    private int sequenceIndex = 0;

    public LockscreenService() {

    }

    @Override
    public void onCreate() {
        game = new Game(this, Game.Mode.PLAY);
        game.setOnSolved(new Runnable() {
            @Override
            public void run() {
                PuzzleFactoryManager.Profile profile = puzzleFactoryManager.getLockProfile();
                if(profile.getType() == PuzzleFactoryManager.ProfileType.DEFAULT){
                    unlockScreen();
                    puzzle = null;
                }
                else if(profile.getType() == PuzzleFactoryManager.ProfileType.SEQUENCE){
                    setRandomPuzzle();
                    if(puzzle == null){
                        // SUCCESS!

                        unlockScreen();

                        game.stopExternalSound();
                        game.stopTimerMode();
                    }
                    else{
                        game.setPuzzle(puzzle);
                        game.update();
                    }
                }
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

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                new Intent(this, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        Notification notification;
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel("com.aren.thewitnesspuzzle", "MyChannel", NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            notification = new Notification.Builder(this, "com.aren.thewitnesspuzzle")
                    .setContentTitle("The Witness Lock Screen")
                    .setContentText("Service is running")
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentIntent(pendingIntent)
                    .build();
        } else {
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

                if (phoneState != TelephonyManager.CALL_STATE_IDLE) return;

                if(puzzleFactoryManager.getLockProfile().getType() == PuzzleFactoryManager.ProfileType.SEQUENCE && isLocked() && puzzle != null){
                    game.stopExternalSound();
                    game.stopTimerMode();
                    game.playSound(Sounds.CABLE_UNPOWERED);

                    puzzle = null;
                }

                final int currentKey = lockToken;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sequenceIndex = 0;
                        setRandomPuzzle();

                        try {
                            Thread.sleep(game.getSettings().getLockDelay() * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (puzzle != null && currentKey == lockToken) {
                            game.setPuzzle(puzzle);
                            // Run on Main thread
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    lockScreen();
                                }
                            });
                        }
                    }
                }).start();
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                Log.i("TAG", "SCREEN_ON");

                PuzzleFactoryManager.Profile profile = puzzleFactoryManager.getLockProfile();
                if(profile.getType() == PuzzleFactoryManager.ProfileType.SEQUENCE && isLocked() && puzzle != null){
                    setTimerMode();
                }

                lockToken++;
            }
        }
    }

    public void setTimerMode(){
        final PuzzleFactoryManager.Profile profile = puzzleFactoryManager.getLockProfile();
        game.setTimerMode(profile.getTimeLength(), new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Runnable retry = new Runnable() {
                            @Override
                            public void run() {
                                if (game.isTLE) {
                                    sequenceIndex = 0;
                                    setRandomPuzzle();

                                    if (puzzle != null) {
                                        game.setPuzzle(puzzle);
                                        puzzle.addAnimation(new PuzzleFadeInAnimation(puzzle, 2000));
                                        setTimerMode();
                                    } else {
                                        unlockScreen();
                                    }
                                }
                            }
                        };

                        game.setOnClicked(retry);

                        game.getPuzzle().addAnimation(new PuzzleFadeOutAnimation(game.getPuzzle(), 1000));
                        game.playSound(Sounds.ABORT_TRACING);

                        game.update();
                    }
                });
            }
        });

        game.playSound(Sounds.CHALLENGE_START);
        if(profile.getMusicFile().exists()){
            game.playExternalSound(profile.getMusicFile().getPath());
        }
        puzzle.addAnimation(new PuzzleFadeInAnimation(puzzle, 2000));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    public void setRandomPuzzle() {
        PuzzleFactoryManager.Profile profile = puzzleFactoryManager.getLockProfile();
        if(profile.getType() == PuzzleFactoryManager.ProfileType.DEFAULT){
            if (!game.getSettings().getHoldingPuzzles() || puzzle == null) {
                Random random = new Random();
                PuzzleFactory factory = profile.getRandomPuzzleFactory(random);
                if (factory != null) {
                    puzzle = factory.generate(game, random);
                }
            }
        }
        else if(profile.getType() == PuzzleFactoryManager.ProfileType.SEQUENCE){
            if(sequenceIndex < profile.getSequence().size()){
                puzzle = profile.getSequence().get(sequenceIndex).generate(game, new Random());
                sequenceIndex++;
            }
            else{
                puzzle = null;
            }
        }
    }

    public void lockScreen() {
        if (isLocked()){
            game.update();
            return;
        }

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

        if (game.getSurfaceView().getParent() == null) {
            //권한이 없다면 여기서 에러 발생
            mWindowManager.addView(game.getSurfaceView(), mLayoutParams);
        }

        // First draw
        game.update();
    }

    public void unlockScreen() {
        if (!isLocked()) return;

        WindowManager mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.removeView(game.getSurfaceView());
    }

    public boolean isLocked() {
        return game.getSurfaceView().getParent() != null;
    }

    PhoneStateListener phoneListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            phoneState = state;
            if (state == TelephonyManager.CALL_STATE_IDLE) {
                if (interrupted) {
                    lockScreen();
                    interrupted = false;
                }
            } else if (state == TelephonyManager.CALL_STATE_RINGING) {
                if (isLocked()) {
                    interrupted = true;
                    unlockScreen();
                }
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    };
}
