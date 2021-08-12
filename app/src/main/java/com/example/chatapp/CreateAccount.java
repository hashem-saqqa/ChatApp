package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class CreateAccount extends AppCompatActivity {
    EditText passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        passwordET = findViewById(R.id.passwordET);


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

    public void GoToHome(View view) {
    }

    public void GoToLogin(View view) {
        Intent intent = new Intent(this,Login.class);
        startActivity(intent);
    }
}