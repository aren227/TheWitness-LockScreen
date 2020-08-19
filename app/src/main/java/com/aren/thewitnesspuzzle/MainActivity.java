package com.aren.thewitnesspuzzle;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactoryManager;

public class MainActivity extends AppCompatActivity {

    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 2323;

    TextView lockText;
    TextView playText;
    TextView galleryText;
    TextView settingsText;
    TextView disclaimerText;

    PuzzleFactoryManager puzzleFactoryManager;

    boolean serviceRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serviceRunning = isServiceRunning(LockscreenService.class);

        puzzleFactoryManager = new PuzzleFactoryManager(this);

        lockText = findViewById(R.id.lock);
        lockText.setText(serviceRunning ? "Unlock" : "Lock");
        lockText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LockscreenService.class);
                if(serviceRunning){
                    stopService(intent);
                    serviceRunning = !serviceRunning;
                }
                else{
                    if(puzzleFactoryManager.getActivatedPuzzleFactories().size() == 0){
                        Toast.makeText(MainActivity.this, "Please activate one or more puzzles in the gallery.", Toast.LENGTH_LONG).show();
                    }
                    else{
                        if(!checkPermission()){
                            requestPermission();
                        }
                        else{
                            startService(intent);
                            serviceRunning = !serviceRunning;
                        }
                    }
                }
                lockText.setText(serviceRunning ? "Unlock" : "Lock");
            }
        });

        playText = findViewById(R.id.play);
        playText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PlayActivity.class);
                startActivity(intent);
            }
        });

        galleryText = findViewById(R.id.puzzles);
        galleryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GalleryActivity.class);
                startActivity(intent);
            }
        });

        settingsText = findViewById(R.id.settings);
        settingsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        disclaimerText = findViewById(R.id.disclaimer);
        disclaimerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DisclaimerActivity.class);
                startActivity(intent);
            }
        });

        TextView textView = findViewById(R.id.github_repo);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private boolean checkPermission(){
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermission(){
        Uri uri = Uri.fromParts("package" , getPackageName(), null);
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri);
        startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
    }
}
