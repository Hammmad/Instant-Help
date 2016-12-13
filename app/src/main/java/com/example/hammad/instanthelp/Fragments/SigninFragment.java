package com.example.hammad.instanthelp.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hammad.instanthelp.HelpActivity;
import com.example.hammad.instanthelp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class SigninFragment extends Fragment {

    private static final String TAG = "DEBUGGING";
    private View rootView;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    EditText userNameEditText;
    EditText passwordEditText;
    CallbackSigninFragment callbackSigninFragment;



    public interface CallbackSigninFragment{
        public void showSignupFragment();
    }

    public SigninFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_signin, container, false);

        userNameEditText = (EditText) rootView.findViewById(R.id.userName_editText);
        passwordEditText = (EditText) rootView.findViewById(R.id.password_editText);


        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Log.e(TAG, "user is signedIn"+ user.getUid());
                }else{
                    Log.e(TAG, "user is signedOut ");
                }
            }
        };

        onSigninClickListener();
        onSignupClickListener();
        return rootView;
    }

    private void onSignupClickListener() {
        TextView signUpButton = (TextView) rootView.findViewById(R.id.signup_textView);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callbackSigninFragment.showSignupFragment();
            }
        });
    }

    private void onSigninClickListener() {

        Button signinButton = (Button) rootView.findViewById(R.id.signin_button);
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signInWithEmailAndPassword(userNameEditText.getText().toString() + "@instanthelp.com",
                        passwordEditText.getText().toString())
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // start help activity
                                if(task.isSuccessful()){
                                    Intent intent = new Intent(getActivity(), HelpActivity.class);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(getActivity(), "Auth Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        callbackSigninFragment = (CallbackSigninFragment) context;
    }
}
