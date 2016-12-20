package com.example.hammad.instanthelp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.hammad.instanthelp.Fragments.SigninFragment;
import com.example.hammad.instanthelp.Fragments.SignupFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements SignupFragment.CallbackSignupFragment, SigninFragment.CallbackSigninFragment{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private static final String TAG = "DEBUGGING";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Intent intent = new Intent(MainActivity.this, HelpActivity.class);
                    startActivity(intent);
                    Log.e(TAG, "user is signedIn"+ user.getUid());
                }else{
                    getSupportFragmentManager().beginTransaction().
                        replace(R.id.activity_main, new SigninFragment()).commit();
                    Log.e(TAG, "user is signedOut ");
                }
            }
        };

    }


    @Override
    public void showSigninFragment() {
        getSupportFragmentManager().beginTransaction().
                replace(R.id.activity_main, new SigninFragment()).commit();
    }

    @Override
    public void showSignupFragment() {
        getSupportFragmentManager().beginTransaction().
                replace(R.id.activity_main, new SignupFragment()).commit();

    }

    @Override
    public void startHelpActivity() {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuth!=null){
            mAuth.removeAuthStateListener(authStateListener);
        }
    }

}
