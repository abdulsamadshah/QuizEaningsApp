package com.example.statusdemo;

import android.content.Intent;
import android.os.Environment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.statusdemo.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    FirebaseFirestore database;
    RecyclerView categoryList;
    String userid;
    User user;
    FirebaseAuth auth;
    FirebaseFirestore categoriesdata;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage storage;
    CategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userid = auth.getCurrentUser().getUid();
        categoriesdata = FirebaseFirestore.getInstance();
        categoryList = findViewById(R.id.categoryList);
        auth = FirebaseAuth.getInstance();
        getSupportActionBar().setTitle("Programming Quiz");


        categoriesdatashowrecyclerview();
        logindataretraive();

        binding.mycoins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),ProfileActivity.class);
                startActivity(intent);
            }
        });

        binding.myprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MainActivity.this,ProfileActivity.class);
                startActivity(intent);
            }
        });

    }

    private void logindataretraive() {
        DocumentReference reference = database.collection("users").document(userid);
        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()) {
                    user = documentSnapshot.toObject(User.class);
                    binding.myname.setText(documentSnapshot.getString("name"));
                    binding.mycoins.setText(String.valueOf(user.getCoins()));

                } else {
                    Toast.makeText(MainActivity.this, "document is not exists", Toast.LENGTH_SHORT).show();
                }
            }
        });


        firebaseDatabase = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        //data retreive
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("images");
        StorageReference storageReference = storage.getReference().child("upload").child(auth.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String image = snapshot.child("image").getValue().toString();

                Picasso.get().load(image).into(binding.myprofile);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void categoriesdatashowrecyclerview() {
        final ArrayList<CategoryModel> categories = new ArrayList<>();

         adapter = new CategoryAdapter(getApplicationContext(), categories);

        categoriesdata.collection("categories")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        categories.clear();
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            CategoryModel model = snapshot.toObject(CategoryModel.class);
                            model.setCategoryId(snapshot.getId());
                            categories.add(model);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });


        categoryList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        categoryList.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sharedata, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plan");

                String sharesub = "Notepad data Share";

                shareIntent.putExtra(Intent.EXTRA_SUBJECT, sharesub);

                startActivity(Intent.createChooser(shareIntent, "share using"));
                break;

        }
        return super.onOptionsItemSelected(item);
    }

}