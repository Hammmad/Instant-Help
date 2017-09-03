package com.example.hammad.instanthelp.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hammad.instanthelp.Adapter.DonorsListAdapter;
import com.example.hammad.instanthelp.R;
import com.example.hammad.instanthelp.models.PostModule;
import com.example.hammad.instanthelp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class PostDetailsActivity extends AppCompatActivity {


    private static DonorsListAdapter adapter;
    PostModule postModule;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    ArrayList<User> dataModels;
    ListView listView;
    AlertDialog progressDialog;
    TextView myPostDesc, myPostCont, myPosCurr;
    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);


        connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        progressDialog = new SpotsDialog(this, R.style.Custom);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();

        listView = (ListView) findViewById(R.id.post_list);
        dataModels = new ArrayList<>();
        myPostDesc = (TextView) findViewById(R.id.myPostDetail_description);
        myPostCont = (TextView) findViewById(R.id.myPostDetail_contact);
        myPosCurr = (TextView) findViewById(R.id.myPostDetail_current_requir);
        Intent i = getIntent();
        postModule = (PostModule) i.getSerializableExtra("module");

        myPostDesc.setText(postModule.getmName() + " needs " + postModule.getmNoofUnits() + " bottles of " + postModule.getmGroup() + " blood group at " + postModule.getmHospital() + ", " + postModule.getmCity() + " within " + postModule.getWithinDuration() + " days.");
        myPostCont.setText(postModule.getmContact());
        myPosCurr.setText(Integer.toString(postModule.getCurrentRequirement()));
        showDonorsList();
    }

    private void showDonorsList() {
        if (networkInfo != null && networkInfo.isConnected()){
            myRef.child("donors").child(postModule.getPushkey()).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    User donor = dataSnapshot.getValue(User.class);
                    dataModels.add(new User(donor.uId, donor.fname, donor.lname, donor.emailAddress, donor.contact, donor.getDonatedUnits(), donor.profileImagePath));
                    adapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else {
            Toast.makeText(this, "No Network Connection !", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

        adapter = new DonorsListAdapter(dataModels, this);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
