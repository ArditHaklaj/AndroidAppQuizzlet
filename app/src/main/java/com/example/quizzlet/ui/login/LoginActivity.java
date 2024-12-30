package com.example.quizzlet.ui.login;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quizzlet.MainActivity;
import com.example.quizzlet.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.List;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 4;
    private EditText emailEditText, passwordEditText;
    private TextView forgotPasswordTextView;

    private FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;

    // Regex pattern for password complexity
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{8,}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // If you want to respect system dark mode from launch:
        // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI components
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button emailLoginButton = findViewById(R.id.emailLoginButton);
        Button googleSignInButton = findViewById(R.id.googleSignInButton);
        Button registrationButton = findViewById(R.id.registrationButton);

        // Forgot Password text
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Handle email login
        emailLoginButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                if (validatePassword(password)) {
                    signInWithEmailPassword(email, password);
                } else {
                    Toast.makeText(this,
                            "Password must be 8+ chars, contain upper/lowercase, digit, & special character.",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Email/Password cannot be empty.", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle registration
        registrationButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                if (validatePassword(password)) {
                    registerWithEmailPassword(email, password);
                } else {
                    Toast.makeText(this,
                            "Password must be 8+ chars, contain upper/lowercase, digit, & special character.",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Email/Password cannot be empty.", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle Google Sign-In
        googleSignInButton.setOnClickListener(view -> signInWithGoogle());

        // Handle forgot password
        forgotPasswordTextView.setOnClickListener(view -> {
            // We'll try to send a password reset email to whatever email is typed in
            String email = emailEditText.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email above first.", Toast.LENGTH_SHORT).show();
            } else {
                sendPasswordResetEmail(email);
            }
        });
    }

    // Helper method to validate password with the regex pattern
    private boolean validatePassword(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    private void sendPasswordResetEmail(String email) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this,
                                "Password reset email sent. Check your inbox!",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this,
                                "Error sending reset email: " + task.getException(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void signInWithEmailPassword(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnFailureListener(e ->
                        Toast.makeText(getApplicationContext(),
                                "User not found, register please", Toast.LENGTH_SHORT).show())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        Log.i("login", "signInWithEmailPassword: Login done");
                        loginDone();
                    }
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task =
                    GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                loginDone();
            } catch (ApiException e) {
                Toast.makeText(this, "Error, " + e, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential =
                GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        Log.i("login", "signInWithGoogle: Login done");
                        loginDone();
                    } else {
                        Toast.makeText(this,
                                "Error, please try again",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registerWithEmailPassword(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error, " + e,
                                Toast.LENGTH_SHORT).show())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        loginDone();
                    }
                });
    }

    private void loginDone() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String userEmail = user.getEmail();
            if (!isMainActivityRunning()) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("userEmail", userEmail);
                startActivity(intent);
                finish();
            }
        }
    }

    private boolean isMainActivityRunning() {
        ActivityManager activityManager =
                (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks =
                activityManager.getRunningTasks(Integer.MAX_VALUE);
        for (ActivityManager.RunningTaskInfo taskInfo : runningTasks) {
            if (taskInfo.baseActivity.getPackageName().equals(getPackageName()) &&
                    taskInfo.baseActivity.getClassName().equals(MainActivity.class.getName())) {
                return true;
            }
        }
        return false;
    }
}
