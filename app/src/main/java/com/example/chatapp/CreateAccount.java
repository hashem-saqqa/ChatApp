package com.example.chatapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class CreateAccount extends AppCompatActivity {
    EditText passwordET, emailET, phoneET, nameET;
    String password, email, phone, name, photo;
    ImageView profileImage;
    Uri profileAvatar = null;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    StorageReference storageReference;


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
                passwordET.setSelection(passwordET.getText().toString().length());

            } else {
                //Hide Password
                passwordET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                passwordET.setSelection(passwordET.getText().toString().length());


            }
        }
    }

    public void GoToHome(View view) {
        email = emailET.getText().toString().trim();
        password = passwordET.getText().toString().trim();
        phone = phoneET.getText().toString().trim();
        name = nameET.getText().toString().trim();

        Toast.makeText(this, "Please Wait", Toast.LENGTH_SHORT).show();

        if (!email.equals("") & !phone.equals("") & !name.equals("") & !password.equals("") & profileAvatar != null) {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        User user = new User(name, email, phone);

                        databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).setValue(user)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        uploadImageToFirebase();

                                        firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(CreateAccount.this, "Go verify your Email", Toast.LENGTH_LONG).show();

                                                    Intent intent = new Intent(getApplicationContext(), ChatHome.class);
                                                    startActivity(intent);

                                                } else {
                                                    Toast.makeText(CreateAccount.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });


                                    }
                                });
                    } else {
                        Toast.makeText(getApplicationContext(), "check email or password", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        } else {
            Toast.makeText(this, "data is missing", Toast.LENGTH_SHORT).show();

        }

    }

    private void uploadImageToFirebase() {
        storageReference = FirebaseStorage.getInstance().getReference(firebaseAuth.getCurrentUser().getUid());
        storageReference.putFile(profileAvatar).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        photo = uri.toString();
                        databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid())
                                .child("photo").setValue(photo).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });
                    }
                });
            }
        });
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

        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {

            if (data == null) {
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
            } else {
                profileAvatar = data.getData();
                profileImage.setImageURI(profileAvatar);

            }
        }
    }
}