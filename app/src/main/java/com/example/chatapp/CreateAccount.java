package com.example.chatapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class CreateAccount extends AppCompatActivity {
    EditText passwordET, emailET, phoneET, nameET;
    String password, email, phone, name;
    ImageView profileImage;
    Uri profileAvatar;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        passwordET = findViewById(R.id.passwordET);
        emailET = findViewById(R.id.emailET);
        phoneET = findViewById(R.id.phoneET);
        nameET = findViewById(R.id.nameET);
        profileImage = findViewById(R.id.imageView);

        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference();


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
//        email = emailET.getText().toString().trim();
//        password = passwordET.getText().toString().trim();
//        phone = phoneET.getText().toString().trim();
//        name = nameET.getText().toString().trim();
//
//        if (!email.equals("") & !phone.equals("") & !name.equals("") & !password.equals("")) {
//            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                @Override
//                public void onComplete(@NonNull Task<AuthResult> task) {
//                    if (task.isSuccessful()) {
//                        User user = new User(name, email, password, phone);
//                    }
//
//                }
//            });
//        }

        Intent intent = new Intent(this, ChatHome.class);
        startActivity(intent);
    }

    public void GoToLogin(View view) {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    public void uploadPhoto(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select imagee"), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("TAGggg out if", "onActivityResult: " + data);

        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            Log.d("TAGggg in if", "onActivityResult: " + data);

            if (data == null) {
                Log.d("TAGggg error", "onActivityResult: " + data);
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("TAGggg", "onActivityResult: " + data);
                Log.d("TAGggg", "onActivityResult: " + data.getData());

//            profileAvatar = data.getData().getPath().trim();
                profileAvatar = data.getData();


//            profileImage.setImageBitmap(BitmapFactory.decodeFile(profileAvatar));
                profileImage.setImageURI(profileAvatar);

//            Picasso.get().load(profileAvatar).into(profileImage);

            }
        }
    }
}