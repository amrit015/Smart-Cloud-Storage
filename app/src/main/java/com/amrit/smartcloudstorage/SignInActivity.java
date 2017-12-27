package com.amrit.smartcloudstorage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

// homepage of the app, users are prompted to sign in or else register a new account
// sign in using Google Firebase Authentication
public class SignInActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    FirebaseAuth auth;

    //Keys for Shared Preferences
    public static final String SHARED_PREF_NAME = "cloudLogIn";

    //This would be used to store the email of current logged in user
    public static final String USER_EMAIL_PREF = "email";

    //We will use this to store the boolean in sharedpreference to track user is loggedin or not
    public static final String LOGGEDIN_SHARED_PREF = "loggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //fix for home button restart... only in release version
        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(intent.getAction())) {
                finish();
                return;
            }
        }
        setContentView(R.layout.activity_sign_in);
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(SignInActivity.this, GroupActivity.class));
            finish();
        }

        btnSignIn = findViewById(R.id.sign_in_button);
        btnSignUp = findViewById(R.id.new_user_button);
        inputEmail = findViewById(R.id.sing_in_email);
        inputPassword = findViewById(R.id.sign_in_password);
        progressBar = findViewById(R.id.progressBar);
        btnResetPassword = findViewById(R.id.reset_password_button);

        // to reset the password
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, ResetPasswordActivity.class));
            }
        });

        // to sign in using Firebase Authorization
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //authenticating user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, a message is displayed to the user. If sign in succeeds,
                                // the auth state listener will be notified and logic to handle the
                                // signed in user is handled in the listener.
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        inputPassword.setError(getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(SignInActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    //Creating a shared preference
                                    SharedPreferences sharedPreferences = SignInActivity.this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

                                    //Creating editor to store values to shared preferences
                                    SharedPreferences.Editor editor = sharedPreferences.edit();

                                    //Adding values to editor
                                    editor.putBoolean(LOGGEDIN_SHARED_PREF, true);
                                    editor.putString(USER_EMAIL_PREF, email);

                                    //Saving values to editor
                                    editor.apply();

                                    // starting the Group Page of the app
                                    Intent intent = new Intent(SignInActivity.this, GroupActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        });

        // sign up page
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
