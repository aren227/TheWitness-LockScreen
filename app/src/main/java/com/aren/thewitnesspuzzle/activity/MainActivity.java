package com.aren.thewitnesspuzzle.activity;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aren.thewitnesspuzzle.BuildConfig;
import com.aren.thewitnesspuzzle.R;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactoryManager;
import com.aren.thewitnesspuzzle.service.LockscreenService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import androidx.appcompat.app.AppCompatActivity;

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
                if (serviceRunning) {
                    stopService(intent);
                    serviceRunning = !serviceRunning;
                } else {
                    PuzzleFactoryManager.Profile profile = puzzleFactoryManager.getLockProfile();
                    if (profile.getType() == PuzzleFactoryManager.ProfileType.DEFAULT && profile.getActivatedPuzzleFactories().size() == 0
                    || profile.getType() == PuzzleFactoryManager.ProfileType.SEQUENCE && profile.getSequence().size() == 0) {
                        Toast.makeText(MainActivity.this, "Please activate one or more puzzles in the gallery.", Toast.LENGTH_LONG).show();
                    } else {
                        if (!checkPermission()) {
                            requestPermission();
                        } else {
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

        ImageView githubRepoImage = findViewById(R.id.github_repo);
        githubRepoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/aren227/TheWitness-LockScreen"));
                startActivity(browserIntent);
            }
        });

        TextView versionNameText = findViewById(R.id.version_name);
        versionNameText.setText(BuildConfig.VERSION_NAME);

        checkLatestVersion();
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

    private boolean checkPermission() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermission() {
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri);
        startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
    }

    private void checkLatestVersion() {
        SharedPreferences sharedPreferences = getSharedPreferences("com.aren.thewitnesspuzzle", MODE_PRIVATE);
        long lastCheck = sharedPreferences.getLong("last_update_check", 0);
        if (lastCheck + 3600 * 24 > System.currentTimeMillis() / 1000L) return;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("last_update_check", System.currentTimeMillis() / 1000L);
        editor.commit();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://api.github.com/repos/aren227/TheWitness-LockScreen/releases/latest");
                    InputStream is = url.openStream();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

                    String result;
                    while ((result = br.readLine()) != null) {
                        sb.append(result).append("\n");
                    }
                    result = sb.toString();

                    is.close();

                    JSONObject js = new JSONObject(result);
                    if (!js.has("tag_name")) return;

                    final String newVersion = js.getString("tag_name");

                    if (!BuildConfig.VERSION_NAME.equals(newVersion)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("Update Available");
                                builder.setMessage(String.format(getString(R.string.update_msg), BuildConfig.VERSION_NAME, newVersion));
                                builder.setPositiveButton("DOWNLOAD FROM GITHUB",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/aren227/TheWitness-LockScreen/releases"));
                                                startActivity(browserIntent);
                                            }
                                        });
                                builder.setNegativeButton("NO THANKS",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                AlertDialog dialog = builder.create();
                                dialog.show();

                                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(0xff000000);
                                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(0xff000000);
                            }
                        });
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
