package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    EditText passwordET, emailET;
    String password, email;
    FirebaseAuth firebaseAuth;
    CheckBox remeberMe;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("rememberMe", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        passwordET = findViewById(R.id.passwordET);
        emailET = findViewById(R.id.emailET);
        remeberMe = findViewById(R.id.rememberMe);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void GoToHome(View view) {
        if (remeberMe.isChecked()) {
            editor.putBoolean("rememberMe", true);
            editor.commit();
        } else {
            editor.putBoolean("rememberMe", false);
            editor.commit();
        }
        email = emailET.getText().toString();
        password = passwordET.getText().toString();
        if (!email.equals("") & !password.equals("")) {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        if (firebaseAuth.getCurrentUser().isEmailVerified()) {

                            Intent intent = new Intent(getApplicationContext(), ChatHome.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("loginChecked", false);
                            startActivity(intent);

                        } else {
                            firebaseAuth.signOut();
                            Toast.makeText(Login.this, "Verify your Email first", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(Login.this, "check the email or the password", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "data  is missing", Toast.LENGTH_SHORT).show();
        }

    }

    public void GoToCreateAccount(View view) {
        Intent intent = new Intent(this, CreateAccount.class);
        startActivity(intent);
    }

    public void ShowHidePass(View view) {
        if (view.getId() == R.id.show_pass_btn) {

            if (passwordET.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {

                //Show Password
                passwordET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                passwordET.setSelection(passwordET.getText().toString().length());

            } else {
                //Hide Password
                passwordET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                passwordET.setSelection(passwordET.getText().toString().length());

            }
        }
    }

    public void resetPassword(View view) {
        if (!emailET.getText().toString().equals("")) {
            firebaseAuth.sendPasswordResetEmail(emailET.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Login.this, "Reset password Email sent to your Email", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Enter the Email please", Toast.LENGTH_SHORT).show();
        }
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (sharedPreferences.getBoolean("rememberMe", true)) {
//            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//                Intent intent = new Intent(getApplicationContext(), ChatHome.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//            }
//        }
//    }
}