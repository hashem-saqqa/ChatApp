package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        passwordET = findViewById(R.id.passwordET);
        emailET = findViewById(R.id.emailET);
        remeberMe = findViewById(R.id.rememberMe);

        firebaseAuth = FirebaseAuth.getInstance();
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//            Intent intent = new Intent(getApplicationContext(), ChatHome.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivityForResult(intent, 0);
//        }
//    }

    public void GoToHome(View view) {
        email = emailET.getText().toString();
        password = passwordET.getText().toString();
        if (!email.equals("") & !password.equals("")) {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(getApplicationContext(), ChatHome.class);
                        startActivity(intent);
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
            } else {
                //Hide Password
                passwordET.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }
        }
    }
}