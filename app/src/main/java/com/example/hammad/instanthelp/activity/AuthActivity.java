package com.example.hammad.instanthelp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.hammad.instanthelp.Fragments.SigninFragment;
import com.example.hammad.instanthelp.Fragments.SignupFragment;
import com.example.hammad.instanthelp.R;
import com.google.firebase.auth.FirebaseAuth;

public class AuthActivity extends AppCompatActivity implements SignupFragment.CallbackSignupFragment, SigninFragment.CallbackSigninFragment{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private static final String TAG = "DEBUGGING";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);


        mAuth = FirebaseAuth.getInstance();

        getSupportFragmentManager().beginTransaction().
                        replace(R.id.activity_main, new SigninFragment()).commit();
//        authStateListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if(user != null){
//                    Intent intent = new Intent(AuthActivity.this, HelpActivity.class);
//                    startActivity(intent);
//                    Log.e(TAG, "currentUser is signedIn"+ user.getUid());
//                }else{
//                    getSupportFragmentManager().beginTransaction().
//                        replace(R.id.activity_auth, new SigninFragment()).commit();
//                    Log.e(TAG, "currentUser is signedOut ");
//                }
//            }
//        };

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
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
//        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
//        if(mAuth!=null){
//            mAuth.removeAuthStateListener(authStateListener);
//        }
    }

}
