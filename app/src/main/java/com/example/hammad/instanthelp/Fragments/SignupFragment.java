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
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hammad.instanthelp.HelpActivity;
import com.example.hammad.instanthelp.R;
import com.example.hammad.instanthelp.User;
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
public class SignupFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "DEBUGGING";
    FirebaseAuth.AuthStateListener authStateListener;
    DatabaseReference databaseReference;
    public FirebaseAuth mAuth;
    EditText emaiEditText;
    EditText passwordEditText;
    CheckBox bloodDonorCheckBox;
    CheckBox firstAiderCheckBox;
    Spinner bloodgroupSpinner;
    TextView domainAddressTextView;
    RadioButton yesRadioButton;
    RadioButton noRadioButton;
    CallbackSignupFragment callbackSignupFragment;




    View rootView;

    public SignupFragment() {
        // Required empty public constructor
    }

    public interface CallbackSignupFragment{
        public void showSigninFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_signup, container, false);

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

        rootView.findViewById(R.id.signup_button).setOnClickListener(this);
        rootView.findViewById(R.id.yes_radiobtn).setOnClickListener(this);
        rootView.findViewById(R.id.no_radiobtn).setOnClickListener(this);
        rootView.findViewById(R.id.bloodDonor_chkbox).setOnClickListener(this);
        rootView.findViewById(R.id.signIn).setOnClickListener(this);

        emaiEditText = (EditText) rootView.findViewById(R.id.email_editText);
        passwordEditText = (EditText) rootView.findViewById(R.id.password_editText);
        bloodDonorCheckBox = (CheckBox) rootView.findViewById(R.id.bloodDonor_chkbox);
        firstAiderCheckBox = (CheckBox) rootView.findViewById(R.id.firstAider_chkbox);
        bloodgroupSpinner = (Spinner) rootView.findViewById(R.id.bloodgroup_spinner);
        domainAddressTextView = (TextView) rootView.findViewById(R.id.instanthelpdomain_textview);
        yesRadioButton = (RadioButton) rootView.findViewById(R.id.yes_radiobtn);
        noRadioButton = (RadioButton) rootView.findViewById(R.id.no_radiobtn);

        domainAddressTextView.setText("@instanthelp.com");
        populateSpinner();

        return rootView;
    }

    private void populateSpinner() {

        ArrayAdapter<CharSequence> StringArrayAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.bloodgroups,
                android.R.layout.simple_spinner_item);
        StringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodgroupSpinner.setAdapter(StringArrayAdapter);
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        if(id==R.id.signup_button){
            createAccount();

        }else if(id == R.id.yes_radiobtn){

            bloodDonorCheckBox.setVisibility(View.VISIBLE);
            firstAiderCheckBox.setVisibility(View.VISIBLE);

        }else if(id == R.id.no_radiobtn){
            bloodDonorCheckBox.setVisibility(View.GONE);
            firstAiderCheckBox.setVisibility(View.GONE);
            bloodgroupSpinner.setVisibility(View.GONE);

            bloodDonorCheckBox.setChecked(false);
            firstAiderCheckBox.setChecked(false);

        }else if(id == R.id.bloodDonor_chkbox){
            if(bloodDonorCheckBox.isChecked()) {
                bloodgroupSpinner.setVisibility(View.VISIBLE);
            }else{
                bloodgroupSpinner.setVisibility(View.GONE);
            }
        }else if(id == R.id.signIn){
            callbackSignupFragment.showSigninFragment();
        }

    }

    private void createAccount() {
        mAuth.createUserWithEmailAndPassword(emaiEditText.getText().toString() + "@instanthelp.com",
                passwordEditText.getText().toString())
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.e(TAG, " task  successfull  " + task.isSuccessful());

                String Uid = null;
                if(task.isSuccessful()) {
                    Uid = mAuth.getCurrentUser().getUid();
                    User user = getUserInfo();
                    databaseReference.child("userinfo").child(Uid).setValue(user);

                    Intent intent = new Intent(getActivity(), HelpActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(getActivity(), "Auth Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private User getUserInfo() {

        boolean isVolunteer = false;
        boolean isBloodDonor = false;
        boolean isFirstAider = false;
        String bloodGroup = bloodgroupSpinner.getSelectedItem().toString();

        if(yesRadioButton.isChecked()){ isVolunteer = true; }
        if(noRadioButton.isChecked()) {isVolunteer = false; bloodGroup = "N/A";}
        if(bloodDonorCheckBox.isChecked())
        { isBloodDonor = true;
          bloodGroup = bloodgroupSpinner.getSelectedItem().toString();
        }else{
            bloodGroup = "N/A";
        }

        if(firstAiderCheckBox.isChecked()) isFirstAider = true;

        return new User(emaiEditText.getText().toString(), passwordEditText.getText().toString(),isVolunteer, isBloodDonor,
                bloodGroup, isFirstAider);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        callbackSignupFragment = (CallbackSignupFragment) context;

    }
}
