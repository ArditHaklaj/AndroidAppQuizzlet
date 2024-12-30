package com.example.quizzlet;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.quizzlet.databinding.ActivityMainBinding;
import com.example.quizzlet.databinding.NavHeaderMainBinding;
import com.example.quizzlet.ui.login.LoginActivity;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    // Notification-related constants
    private static final String CHANNEL_ID = "quizzlet_welcome_channel";
    private static final int NOTIFICATION_ID = 1001;

    // For Android 13+ POST_NOTIFICATIONS permission request
    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set the app theme before setContentView if toggling dark mode dynamically:
        // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        super.onCreate(savedInstanceState);

        // Inflate the main layout
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        // Set up nav header binding (to set userMail, etc.)
        NavHeaderMainBinding navBinding = NavHeaderMainBinding.inflate(getLayoutInflater());
        binding.navView.addHeaderView(navBinding.getRoot());

        // Retrieve userEmail from intent
        Intent intent = getIntent();
        if (intent != null) {
            String userEmail = intent.getStringExtra("userEmail");
            if (userEmail != null) {
                navBinding.userMail.setText(userEmail);
            }
        }

        // Set the content view
        setContentView(binding.getRoot());

        // Set up the toolbar
        setSupportActionBar(binding.appBarMain.toolbar);

        // Set up DrawerLayout
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Top-level destinations
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home)
                .setOpenableLayout(drawer)
                .build();

        // Set up Navigation Component
        NavController navController =
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Handle menu item selections (logout, etc.)
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.logout) {
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                finish();
            }
            return true;
        });

        // Create the notification channel
        createNotificationChannel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check and request notification permission if needed (Android 13+)
        requestNotificationPermissionIfNeeded();

        // Show a simple welcome notification each time the user arrives on the main screen
        showWelcomeNotification();
    }

    // Inflate the menu (contains dark mode toggle, etc.)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu); // main.xml with a "Dark Mode" item
        return true;
    }

    // Handle menu item clicks (e.g., dark mode toggle)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_toggle_dark_mode) {
            int currentMode = AppCompatDelegate.getDefaultNightMode();
            if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Navigation up button support
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController =
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Creates a NotificationChannel for devices running Android O (8.0) or higher.
     */
    private void createNotificationChannel() {
        // Check API level
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Quizzlet Welcome";
            String description = "Channel for welcome notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Builds and displays a simple welcome notification.
     */
    private void showWelcomeNotification() {
        // Construct a basic notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)  // or use a custom drawable
                .setContentTitle("Welcome to Quizzlet!")
                .setContentText("Ready to tackle your next quiz?")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    /**
     * Only required on Android 13+ (API level 33+).
     * If permission not granted, prompt user.
     */
    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_CODE_POST_NOTIFICATIONS);
            }
        }
    }

    /**
     * Handle result of POST_NOTIFICATIONS permission request.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            // If user granted POST_NOTIFICATIONS, we can show notifications
            // If user denied, we might show a message, but for simplicity do nothing
        }
    }
}
