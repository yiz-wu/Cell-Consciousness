package com.example.ProgettoAMIF.UI;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.eserciziobroadcastreceiver.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private View askPermissionLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // a navController to control what would happen when we click on navbar element
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavigationUI.setupWithNavController(navView, navController);


        // before API Version 23, it's not necessary to ask for this permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                askPermissionLayout = findViewById(R.id.askPermissionLayout);
                askPermissionLayout.setVisibility(View.VISIBLE);
                Button grantPermissionButton = findViewById(R.id.grantPermissionButton);
                grantPermissionButton.setClickable(true);
                grantPermissionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(TAG,"onClick grantPermissionButton");
                        // redirect to setting page where user can turn on this permission
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, 0);
                    }
                });
            }
        }

        createChannelID();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(getApplicationContext())) {
                    Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT) .show();
                    askPermissionLayout.setVisibility(View.GONE);
                }
                else {
                    Toast.makeText(MainActivity.this, "Permission denied, Application will not work as desired, please restart app to grant the permission", Toast.LENGTH_LONG) .show();
                }
            }
        }

    }

    private void createChannelID() {
        // for Android 8.0 and higher, u must register your app's notification channel before sending notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "MyChannel";
            String description = "My channel's description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(getText(R.string.channelID).toString(), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}