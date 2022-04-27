package com.example.statusdemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.statusdemo.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirestoreRegistrar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SignupActivity extends AppCompatActivity {

    ActivitySignupBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore database;
    ProgressDialog dialog;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage storage;
    Uri imageuri;
    String IMAGEURI;
    FirebaseUser firebaseUser;
   String userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();
        if(auth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }


        binding.createNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, pass, name, referCode;

                email = binding.emailBox.getText().toString();
                pass = binding.passwordBox.getText().toString();
                name = binding.nameBox.getText().toString();


                final User user = new User(name, email, pass);


                auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            String uid = task.getResult().getUser().getUid();

                            database
                                    .collection("users")
                                    .document(uid)
                                    .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
//                                        dialog.dismiss();
//                                        startActivity(new Intent(SignupActivity.this, MainActivity.class));
//                                        finish();
                                        if (task.isSuccessful()){
                                            DatabaseReference reference=firebaseDatabase.getReference().child("images");
                                            StorageReference storageReference=storage.getReference().child("myloginimages");

                                            if (imageuri !=null){
                                                storageReference.putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                                        if (task.isSuccessful()){
                                                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                @Override
                                                                public void onSuccess(Uri uri) {
                                                                    IMAGEURI=uri.toString();
                                                                    imagemodel imagemodels=new imagemodel(IMAGEURI);
                                                                    reference.setValue(imagemodels).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()){
                                                                                Intent intent = new Intent(SignupActivity.this,MainActivity.class);
                                                                                startActivity(intent);
                                                                                Toast.makeText(SignupActivity.this, "user created succesfully", Toast.LENGTH_SHORT).show();
                                                                            }else{
                                                                                Toast.makeText(SignupActivity.this, "something wrong", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                            }else{
                                                IMAGEURI="https://firebasestorage.googleapis.com/v0/b/fir-retreivedata.appspot.com/o/avatar.png?alt=media&token=14a4879f-1697-41fb-903f-c681d8d515b4";
                                                imagemodel imagemodels=new imagemodel(IMAGEURI);
                                                reference.setValue(imagemodels).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            Intent intent = new Intent(SignupActivity.this,MainActivity.class);
                                                            startActivity(intent);
                                                            Toast.makeText(SignupActivity.this, "user created succesfully", Toast.LENGTH_SHORT).show();
                                                        }else{
                                                            Toast.makeText(SignupActivity.this, "something wrong", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }

                                            Toast.makeText(SignupActivity.this, "user created succesfully", Toast.LENGTH_SHORT).show();

                                        }
                                    } else {
                                        Toast.makeText(SignupActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {

                            Toast.makeText(SignupActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,20);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data !=null){
            imageuri=data.getData();
            binding.profileImage.setImageURI(imageuri);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}