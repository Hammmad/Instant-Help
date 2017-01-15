package com.example.hammad.instanthelp.Fragments;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
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
    EditText userNameEditText;
    EditText passwordEditText;
    EditText confirmPasswordEditText;
    CheckBox bloodDonorCheckBox;
    CheckBox firstAiderCheckBox;
    Spinner bloodgroupSpinner;
    RadioButton yesRadioButton;
    RadioButton noRadioButton;
    View coordinatorLayout;
    CallbackSignupFragment callbackSignupFragment;
    AlphaAnimation buttonClick;
    String blockCharacters = "~!@#$%^&*() `-+={}[]:;|?/>.<,";
    InputFilter inputFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {

            if(charSequence != null && blockCharacters.contains(charSequence)){
                return "";
            }
            return null;
        }
    };






    View rootView;

    public SignupFragment() {
        // Required empty public constructor
    }

    public interface CallbackSignupFragment{
        void showSigninFragment();


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


        userNameEditText = (EditText) rootView.findViewById(R.id.email_editText);
        passwordEditText = (EditText) rootView.findViewById(R.id.password_editText);
        confirmPasswordEditText = (EditText) rootView.findViewById(R.id.confirmPassword_editText);
        bloodDonorCheckBox = (CheckBox) rootView.findViewById(R.id.bloodDonor_chkbox);
        firstAiderCheckBox = (CheckBox) rootView.findViewById(R.id.firstAider_chkbox);
        bloodgroupSpinner = (Spinner) rootView.findViewById(R.id.bloodgroup_spinner);
        yesRadioButton = (RadioButton) rootView.findViewById(R.id.yes_radiobtn);
        noRadioButton = (RadioButton) rootView.findViewById(R.id.no_radiobtn);
        coordinatorLayout = rootView.findViewById(R.id.coord_layout);
        buttonClick = new AlphaAnimation(1F, 0.7F);

        userNameEditText.setFilters(new InputFilter[] {inputFilter} );
        showkeyboard(userNameEditText);
        editTextFocusListener();
        populateSpinner();

        return rootView;
    }


    public void showkeyboard(EditText editText) {

        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

    }

    private void editTextFocusListener() {
        userNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!view.hasFocus()) {
                    if (userNameEditText.getText().toString().isEmpty() || userNameEditText.getText().toString().length() < 3) {
                        showErrorMessage("Username contain atleast 3 characters");
                    }
                }
            }
        });

        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!view.hasFocus()){
                    if (passwordEditText.getText().toString().isEmpty() || passwordEditText.getText().toString().length() < 6) {
                        showErrorMessage("Password contain atleast 6 characters");
                    }
                }
            }
        });

        confirmPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!view.hasFocus()){
                    if(confirmPasswordEditText.getText().toString().isEmpty() ||
                            !confirmPasswordEditText.getText().toString().equals(passwordEditText.getText().toString())) {
                        showErrorMessage("Password is not matching");
                    }
                }
            }
        });


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
        switch (id){
            case R.id.signup_button:{
                view.startAnimation(buttonClick);
                ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                String userName = userNameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();
                if(isValidate(userName, password, confirmPassword)) {
                    if(networkInfo != null && networkInfo.isConnected()) {
                        createAccount();
                    }else{
                        showErrorMessage("No Network Connection !");
                    }
                }
                break;
            }
            case R.id.yes_radiobtn:{
                bloodDonorCheckBox.setVisibility(View.VISIBLE);
                firstAiderCheckBox.setVisibility(View.VISIBLE);
                break;
            }
            case R.id.no_radiobtn:{
                bloodDonorCheckBox.setVisibility(View.GONE);
                firstAiderCheckBox.setVisibility(View.GONE);
                bloodgroupSpinner.setVisibility(View.GONE);

                bloodDonorCheckBox.setChecked(false);
                firstAiderCheckBox.setChecked(false);
                break;
            }
            case R.id.bloodDonor_chkbox:{
                if(bloodDonorCheckBox.isChecked()) {
                    bloodgroupSpinner.setVisibility(View.VISIBLE);
                }else{
                    bloodgroupSpinner.setVisibility(View.GONE);
                }
                break;
            }
            case R.id.signIn:{
                callbackSignupFragment.showSigninFragment();
                break;
            }
            default:{
                break;
            }
        }
    }

    private void createAccount() {
        mAuth.createUserWithEmailAndPassword(userNameEditText.getText().toString() + "@instanthelp.com",
                passwordEditText.getText().toString())
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                String Uid = null;
                if(task.isSuccessful()) {
                    Uid = mAuth.getCurrentUser().getUid();
                    User user = getUserInfo();
                    databaseReference.child("userinfo").child(Uid).setValue(user);

                    Intent intent = new Intent(getActivity(), HelpActivity.class);
                    startActivity(intent);
                }else {
                    showErrorMessage("Failed to Register");
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

        return new User(userNameEditText.getText().toString(), passwordEditText.getText().toString(),isVolunteer, isBloodDonor,
                bloodGroup, isFirstAider);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        callbackSignupFragment = (CallbackSignupFragment) context;

    }
    public boolean isValidate(String userName, String password, String confirmPassword) {

        if (userName.isEmpty() || userName.length() < 3) {
            userNameEditText.requestFocus();
            showErrorMessage("Username contain atleast 3 characters");
            return false;
        } else if (password.isEmpty() || password.length() < 6) {
            passwordEditText.requestFocus();
            showErrorMessage("Password contain atleast 6 characters");
            return false;
        }else if(confirmPassword.isEmpty() || !confirmPassword.equals(password)){
            confirmPasswordEditText.requestFocus();
            showErrorMessage("Password is not matching");
            return false;
        }else {
            return true;
        }
    }

    private void showErrorMessage(String message) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorError));
        snackbar.show();
    }
}
