package com.example.hammad.instanthelp.Fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.hammad.instanthelp.R;
import com.example.hammad.instanthelp.models.User;
import com.example.hammad.instanthelp.utils.CurrentUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import dmax.dialog.SpotsDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class SigninFragment extends Fragment {

    private static final String TAG = "DEBUGGING";
    private View rootView;
    private DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    EditText userNameEditText;
    EditText passwordEditText;
    TextView instantTextView;
    View coordinatorLayout;
    AlertDialog progressDialog;
    CallbackSigninFragment callbackSigninFragment;
    AlphaAnimation buttonClick;
    NetworkInfo networkInfo;
    String blockCharacters = "~!@#$%^&*() `-+={}[]:;|?/>.<,";
    InputFilter inputFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {

            if (charSequence != null && blockCharacters.contains(charSequence)) {
                return "";
            }
            return null;
        }
    };
    StorageReference storageReference;


    public interface CallbackSigninFragment {
        void showSignupFragment();

        void startHelpActivity();
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
        instantTextView = (TextView) rootView.findViewById(R.id.instanhelptText);
        instantTextView.setText("@intanthelp.com");
        progressDialog = new SpotsDialog(getActivity(), R.style.Custom);
        coordinatorLayout = rootView.findViewById(R.id.coord_layout);
        buttonClick = new AlphaAnimation(1F, 0.7F);


        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://instant-help.appspot.com");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.e(TAG, "user is signedIn" + user.getUid());
                } else {
                    Log.e(TAG, "user is signedOut ");
                }
            }
        };

        userNameEditText.setFilters(new InputFilter[]{inputFilter});
        showkeyboard(userNameEditText);

        onSigninClickListener();
        onSignupClickListener();
        return rootView;
    }

    public void showkeyboard(EditText editText) {

        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

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
                view.startAnimation(buttonClick);
                ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                networkInfo = connectivityManager.getActiveNetworkInfo();
                String userName = String.valueOf(userNameEditText.getText());
                String password = String.valueOf(passwordEditText.getText());

                progressDialog.show();
                if (isValidate(userName, password)) {
                    if (networkInfo != null && networkInfo.isConnected()) {
                        logIn();
                    } else {
                        progressDialog.dismiss();
                        showErrorMessage("No network connection");
                    }
                }
            }
        });
    }

    private void logIn() {
        mAuth.signInWithEmailAndPassword(userNameEditText.getText().toString() + "@intanthelp.com",
                passwordEditText.getText().toString())
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // start help activity


                        if (task.isSuccessful()) {
                            userInfoListener(databaseReference);
                            progressDialog.show();
                            userNameEditText.setText(null);
                            passwordEditText.setText(null);
                        } else {
                            progressDialog.dismiss();
                            showErrorMessage("Username or Password is incorrect !");
                            userNameEditText.requestFocus();
                        }
                    }
                });
    }

    private void userInfoListener(DatabaseReference databaseReference) {
        databaseReference.child("userinfo").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "userInfo Listener:   " + dataSnapshot.getKey());
                Log.d(TAG, "USERINFO: "+ dataSnapshot);
                final User user = dataSnapshot.getValue(User.class);

//                if(user.profileImagePath == null){
//                    CurrentUser currentUser = new CurrentUser(getActivity());
//                    currentUser.setNoImageCurrentUser(user);
//                    callbackSigninFragment.startHelpActivity();
//                }else{
                final long ONE_MEGABYTE = 1024 * 1024;
                storageReference.child(user.profileImagePath).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        String profileImage = Base64.encodeToString(bytes, Base64.DEFAULT);
                        User updatedUser = new User(
                                user.uId,

                                user.fname,
                                user.lname,
                                user.emaiAddress,

                                user.contact,
                                user.country,
                                user.city,
                                user.password,
                                user.gender,
                                user.volunteer,
                                user.bloodDonor,
                                user.bloodGroup,
                                user.firstAider,
                                user.ambulance,
                                profileImage);
                        CurrentUser currentUser = new CurrentUser(getActivity());
                        currentUser.setCurrentUser(updatedUser);
                        callbackSigninFragment.startHelpActivity();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
//                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
        if (mAuth != null) {
            mAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        callbackSigninFragment = (CallbackSigninFragment) context;
    }

    public boolean isValidate(String userName, String password) {

        if (userName.equals("")) {
            userNameEditText.requestFocus();
            showErrorMessage("Username is required !");
            progressDialog.dismiss();
            return false;
        } else if (password.equals("")) {
            passwordEditText.requestFocus();
            showErrorMessage("Password is required !");
            progressDialog.dismiss();
            return false;
        } else {
            return true;
        }
    }


    private void showErrorMessage(String message) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorError));
        snackbar.show();
    }
}
