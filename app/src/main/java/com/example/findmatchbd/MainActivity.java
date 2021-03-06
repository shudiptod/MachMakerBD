package com.example.findmatchbd;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.findmatchbd.Cards.arrayAdapter;
import com.example.findmatchbd.Cards.cards;
import com.example.findmatchbd.Matches.MatchesActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private cards cards_data[];

    private com.example.findmatchbd.Cards.arrayAdapter arrayAdapter;
    private int i;

    private FirebaseAuth mAuth;

    private String currentUId;
    private DatabaseReference usersDb;


    ListView listView;
    List<cards> rowItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_FindMatchBD);
        setContentView(R.layout.activity_main);


        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth=FirebaseAuth.getInstance();
        currentUId = mAuth.getCurrentUser().getUid();
        checkUserPreferences();

        rowItems = new ArrayList<cards>();

        arrayAdapter = new arrayAdapter(this, R.layout.item, rowItems );

        SwipeFlingAdapterView flingContainer = findViewById(R.id.frame);
        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {

                cards obj = (cards) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("connections").child("no").child(currentUId).setValue(true);
                Toast.makeText(MainActivity.this, "Left",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {


                cards obj = (cards) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("connections").child("yes").child(currentUId).setValue(true);

                isMatched(userId);

                Toast.makeText(MainActivity.this, "Right",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here

            }

            @Override
            public void onScroll(float scrollProgressPercent) {

            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(MainActivity.this, "Click",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void isMatched(String userId) {
        DatabaseReference currentUserConnectionDb = usersDb.child(currentUId).child("connections").child("yes").child(userId);
        currentUserConnectionDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {

                    Toast.makeText(MainActivity.this, "New Match",Toast.LENGTH_SHORT).show();

                    String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();

                    usersDb.child(snapshot.getKey()).child("connections").child("matches").child(currentUId).child("ChatId").setValue(key);

                    usersDb.child(currentUId).child("connections").child("matches").child(snapshot.getKey()).child("ChatId").setValue(key);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public String userGender;
    public String userOppositeGender;

    public void checkUserPreferences(){

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference userDb = usersDb.child(user.getUid());
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    if (snapshot.child("gender").getValue() != null)
                    {
                        userGender = snapshot.child("gender").getValue().toString();
                        switch (userGender){
                            case "Male":
                                userOppositeGender="Female";
                                break;
                            case "Female":
                                userOppositeGender = "Male";
                                break;
                        }
                        getOppositeGenderUsers();


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    };

    public void getOppositeGenderUsers(){

        usersDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.child("gender").getValue() != null) {
                    if (snapshot.exists() && !snapshot.child("connections").child("no").hasChild(currentUId) && !snapshot.child("connections").child("yes").hasChild(currentUId) && snapshot.child("gender").getValue().toString().equals(userOppositeGender)) {

                        String profileImageUrl = "default";
                        if (snapshot.child("profileImageUrl").getValue() != null) {
                            profileImageUrl = snapshot.child("profileImageUrl").getValue().toString();
                        }
                        String profession = "";
                        if (snapshot.child("profession").getValue() != null) {
                            profession = snapshot.child("profession").getValue().toString();
                        }
                        cards item = new cards(snapshot.getKey(), snapshot.child("name").getValue().toString(), profileImageUrl,profession);

                        rowItems.add(item);
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }




    public void LogoutUser(View view) {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this,ChooseLoginOrRegistrationActivity.class);
        startActivity(intent);
        finish();
        return;
    }

    public void goToSettings(View view) {
        Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
        startActivity(intent);
        return;

    }

    public void goToMatches(View view) {
        final FirebaseUser userMatch = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference currentUserConnectionDb = usersDb.child(currentUId);
        currentUserConnectionDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("connections").exists())
                {
                    if(snapshot.child("connections").child("matches").exists())
                    {
                        Intent intent = new Intent(MainActivity.this, MatchesActivity.class);
                        startActivity(intent);
                        return;
                    }

                }
                else
                    {
                        Toast.makeText(MainActivity.this, "You Have No Matches", Toast.LENGTH_SHORT).show();
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}