package com.example.hammad.instanthelp.Fragments;


import android.app.AlertDialog;
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
import android.util.Base64;
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

import com.example.hammad.instanthelp.R;
import com.example.hammad.instanthelp.activity.HomeActivity;
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
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import dmax.dialog.SpotsDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignupFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "DEBUGGING";
    FirebaseAuth.AuthStateListener authStateListener;
    DatabaseReference databaseReference;
    public FirebaseAuth mAuth;

    EditText userFnameEditText;
    EditText userLnameEditText;
    EditText emailEditText;
    EditText contactEditText;
    EditText guardianEditText;
    TextView instantTextView;
    MaterialBetterSpinner countrySpinner;
    MaterialBetterSpinner citySpinner;
    EditText passwordEditText;
    EditText confirmPasswordEditText;
    RadioButton maleRadioButton;
    RadioButton femaleRadioButton;
    RadioButton yesRadioButton;
    RadioButton noRadioButton;
    CheckBox ambulanceCheckBox;
    CheckBox bloodDonorCheckBox;
    CheckBox firstAiderCheckBox;
    Spinner bloodgroupSpinner;
    String countrySpinnerValue;
    String citySpinnerValue;
    AlertDialog progressDialog;

    View coordinatorLayout;
    CallbackSignupFragment callbackSignupFragment;
    AlphaAnimation buttonClick;
    StorageReference storageReference;

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


    View rootView;

    public SignupFragment() {
        // Required empty public constructor
    }

    public interface CallbackSignupFragment {
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
        if (mAuth != null) {
            mAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_signup, container, false);


        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://instant-help.appspot.com");
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
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
//        inputFilter();

        rootView.findViewById(R.id.signup_button).setOnClickListener(this);
        rootView.findViewById(R.id.yes_radiobtn).setOnClickListener(this);
        rootView.findViewById(R.id.no_radiobtn).setOnClickListener(this);
        rootView.findViewById(R.id.bloodDonor_chkbox).setOnClickListener(this);
        rootView.findViewById(R.id.signIn).setOnClickListener(this);


        userFnameEditText = (EditText) rootView.findViewById(R.id.fname_editText);
        userLnameEditText = (EditText) rootView.findViewById(R.id.lname_editText);
        emailEditText = (EditText) rootView.findViewById(R.id.email_editText);
        contactEditText = (EditText) rootView.findViewById(R.id.signup_contact);
        guardianEditText = (EditText) rootView.findViewById(R.id.signup_guardian);
        instantTextView = (TextView) rootView.findViewById(R.id.instantText);
        instantTextView.setText("@instanthelp.com");
        emailEditText.setFilters(new InputFilter[]{inputFilter});
        showkeyboard(userFnameEditText);

        progressDialog = new SpotsDialog(getActivity(), R.style.Custom);

        countrySpinner = (MaterialBetterSpinner) rootView.findViewById(R.id.signUpCountry_Spinner);
        citySpinner = (MaterialBetterSpinner) rootView.findViewById(R.id.signUpCity_Spinner);

        passwordEditText = (EditText) rootView.findViewById(R.id.password_editText);
        confirmPasswordEditText = (EditText) rootView.findViewById(R.id.confirmPassword_editText);

        maleRadioButton = (RadioButton) rootView.findViewById(R.id.male_radiobtn);
        femaleRadioButton = (RadioButton) rootView.findViewById(R.id.female_radiobtn);

        yesRadioButton = (RadioButton) rootView.findViewById(R.id.yes_radiobtn);
        noRadioButton = (RadioButton) rootView.findViewById(R.id.no_radiobtn);

        ambulanceCheckBox = (CheckBox) rootView.findViewById(R.id.ambulance_chkbox);
        bloodDonorCheckBox = (CheckBox) rootView.findViewById(R.id.bloodDonor_chkbox);
        firstAiderCheckBox = (CheckBox) rootView.findViewById(R.id.firstAider_chkbox);

        bloodgroupSpinner = (Spinner) rootView.findViewById(R.id.bloodgroup_spinner);

        coordinatorLayout = rootView.findViewById(R.id.coord_layout);
        buttonClick = new AlphaAnimation(1F, 0.7F);


        emailEditText.setFilters(new InputFilter[]{inputFilter});

        editTextFocusListener();
        materialSpinnerItemSelected();
        populateSpinner();

        return rootView;
    }

    private void materialSpinnerItemSelected() {
        countrySpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                countrySpinnerValue = countrySpinner.getText().toString();
//                Toast.makeText(getActivity(), ""+countrySpinnerValue, Toast.LENGTH_SHORT).show();


                String country = countrySpinner.getText().toString();
                Toast.makeText(getActivity(), "" + country, Toast.LENGTH_SHORT).show();
                if (country.equals("Bangladesh")) {
                    citySpinner.clearListSelection();
                    ArrayAdapter<CharSequence> citySpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.bangladeshCities,
                            android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(citySpinnerAdapter);
                } else if (country.equals("Nepal")) {
                    citySpinner.clearListSelection();
                    ArrayAdapter<CharSequence> citySpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.nepalCities,
                            android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(citySpinnerAdapter);
                } else if (country.equals("Pakistan")) {
                    citySpinner.clearListSelection();
                    ArrayAdapter<CharSequence> citySpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.pakistanCities,
                            android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(citySpinnerAdapter);
                } else if (country.equals("Iran")) {
                    citySpinner.clearListSelection();
                    ArrayAdapter<CharSequence> citySpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.iranCities,
                            android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(citySpinnerAdapter);
                } else if (country.equals("Srilanka")) {
                    citySpinner.clearListSelection();
                    ArrayAdapter<CharSequence> citySpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.srilankaCities,
                            android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(citySpinnerAdapter);
                } else if (country.equals("Afghanistan")) {
                    citySpinner.clearListSelection();
                    ArrayAdapter<CharSequence> citySpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.afganistanCities,
                            android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(citySpinnerAdapter);
                }
            }
        });

        citySpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                citySpinnerValue = citySpinner.getText().toString();
            }
        });
    }

//    private void inputFilter() {
//        inputFilter = new InputFilter() {
//            @Override
//            public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
//
//                if (charSequence != null && blockCharacters.contains(charSequence)) {
//                    return "";
//                }
//                return null;
//            }
//        };
//    }


    public void showkeyboard(EditText editText) {

        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

    }

    private void editTextFocusListener() {

        userFnameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!view.hasFocus()) {
                    if (userFnameEditText.getText().toString().isEmpty() || userFnameEditText.getText().toString().length() < 3) {
                        showErrorMessage("First name contain atleast 3 characters");
                    }
                }
            }
        });

        userLnameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!view.hasFocus()) {
                    if (userLnameEditText.getText().toString().isEmpty() || userLnameEditText.getText().toString().length() < 3) {
                        showErrorMessage("Last name contain atleast 3 characters");
                    }
                }
            }
        });

        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!view.hasFocus()) {
                    if (emailEditText.getText().toString().isEmpty() || emailEditText.getText().toString().length() < 3) {
                        showErrorMessage("Email must be required");
                    }
                }
            }
        });

        contactEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!v.hasFocus()) {
                    if (contactEditText.getText().toString().isEmpty() || contactEditText.getText().toString().length() < 11) {
                        showErrorMessage("Contact must be defined / Contact no is incorrect");
                    }
                }
            }
        });

        guardianEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!v.hasFocus()) {
                    if (guardianEditText.getText().toString().isEmpty() || guardianEditText.getText().toString().length() < 11) {
                        showErrorMessage("Guardian Contact must be defined / Contact no is incorrect");
                    }
                }
            }
        });
        countrySpinner.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!v.hasFocus()) {
                    if (countrySpinner.getText().toString().isEmpty()) {
                        showErrorMessage("Please select a country");
                    }
                }
            }
        });

        citySpinner.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!v.hasFocus()) {
                    if (citySpinner.getText().toString().isEmpty()) {
                        showErrorMessage("Please select a city");
                    }
                }
            }
        });
        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!view.hasFocus()) {
                    if (passwordEditText.getText().toString().isEmpty() || passwordEditText.getText().toString().length() < 6) {
                        showErrorMessage("Password contain atleast 6 characters");
                    }
                }
            }
        });

        confirmPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!view.hasFocus()) {
                    if (confirmPasswordEditText.getText().toString().isEmpty() ||
                            !confirmPasswordEditText.getText().toString().equals(passwordEditText.getText().toString())) {
                        showErrorMessage("Password is not matching");
                    }
                }
            }
        });


    }

    private void populateSpinner() {

        ArrayAdapter<CharSequence> StringArrayAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.bloodgroups,
                android.R.layout.simple_spinner_dropdown_item);
        bloodgroupSpinner.setAdapter(StringArrayAdapter);

        ArrayAdapter<CharSequence> countrySpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.country,
                android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(countrySpinnerAdapter);
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        switch (id) {
            case R.id.signup_button: {
                progressDialog.show();
                view.startAnimation(buttonClick);
                ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                String userFname = userFnameEditText.getText().toString();
                String userLname = userLnameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String contact = contactEditText.getText().toString();
                String guardian = guardianEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();
                if (isValidate(userFname, userLname, password, contact, guardian, confirmPassword)) {
                    if (networkInfo != null && networkInfo.isConnected()) {
                        createAccount();
                    } else {
                        showErrorMessage("No Network Connection !");
                        progressDialog.dismiss();
                    }
                } else {
                    progressDialog.dismiss();
                }
                break;
            }
            case R.id.yes_radiobtn: {
                bloodDonorCheckBox.setVisibility(View.VISIBLE);
                firstAiderCheckBox.setVisibility(View.VISIBLE);
                ambulanceCheckBox.setVisibility(View.VISIBLE);

                break;
            }
            case R.id.no_radiobtn: {
                bloodDonorCheckBox.setVisibility(View.GONE);
                firstAiderCheckBox.setVisibility(View.GONE);
                ambulanceCheckBox.setVisibility(View.GONE);
                bloodgroupSpinner.setVisibility(View.GONE);

                bloodDonorCheckBox.setChecked(false);
                firstAiderCheckBox.setChecked(false);
                ambulanceCheckBox.setChecked(false);
                break;
            }
            case R.id.bloodDonor_chkbox: {
                if (bloodDonorCheckBox.isChecked()) {
                    bloodgroupSpinner.setVisibility(View.VISIBLE);
                } else {
                    bloodgroupSpinner.setVisibility(View.GONE);
                }
                break;
            }
            case R.id.signIn: {
                callbackSignupFragment.showSigninFragment();
                break;
            }
            default: {
                break;
            }
        }
    }

    private void createAccount() {
        mAuth.createUserWithEmailAndPassword(emailEditText.getText().toString() + "@intanthelp.com",
                passwordEditText.getText().toString())
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        String Uid;
                        if (task.isSuccessful()) {
                            Uid = mAuth.getCurrentUser().getUid();
                            User user = getUserInfo();
                            databaseReference.child("userinfo").child(Uid).setValue(user);
                            if (ambulanceCheckBox.isChecked()) {
                                databaseReference.child("ambulance").child(countrySpinnerValue).child(citySpinnerValue).child(Uid).setValue(user);
                            }
                            if (bloodDonorCheckBox.isChecked()) {
                                databaseReference.child("blood-donor").child(countrySpinnerValue).child(citySpinnerValue).child(bloodgroupSpinner.getSelectedItem().toString()).child(Uid).setValue(user);
                            }
                            if (firstAiderCheckBox.isChecked()) {
                                databaseReference.child("first-aider").child(countrySpinnerValue).child(citySpinnerValue).child(Uid).setValue(user);
                            }
                            userInfoListener(databaseReference);
                            progressDialog.dismiss();

                        } else {
                            progressDialog.dismiss();
                            showErrorMessage("Failed to Register");
                        }
                    }
                });
    }

    private User getUserInfo() {

        boolean isVolunteer = false;
        boolean isBloodDonor = false;
        boolean isFirstAider = false;
        boolean isAmbulance = false;
        String isGender = null;
        String bloodGroup;

        if (yesRadioButton.isChecked()) {
            isVolunteer = true;
        }
        if (noRadioButton.isChecked()) {
            isVolunteer = false;
            bloodGroup = "N/A";
        }
        if (maleRadioButton.isChecked()) {
            isGender = "male";
        }
        if (femaleRadioButton.isChecked()) {
            isGender = "female";

        }
        if (bloodDonorCheckBox.isChecked()) {
            isBloodDonor = true;
            bloodGroup = bloodgroupSpinner.getSelectedItem().toString();
        } else {
            bloodGroup = "N/A";
        }

        if (firstAiderCheckBox.isChecked()) {
            isFirstAider = true;
        }

        if (ambulanceCheckBox.isChecked()) {
            isAmbulance = true;
        }

//        return new User(mAuth.getCurrentUser().getUid(), userNameEditText.getText().toString(), passwordEditText.getText().toString(), isVolunteer, isBloodDonor, bloodGroup, isFirstAider, "ProfilePictures/userImage.PNG");
        return new User(mAuth.getCurrentUser().getUid(),
                userFnameEditText.getText().toString(),
                userLnameEditText.getText().toString(),
                emailEditText.getText().toString(),
                contactEditText.getText().toString(),
                guardianEditText.getText().toString(),
                countrySpinner.getText().toString(),
                citySpinner.getText().toString(),
                passwordEditText.getText().toString(),
                isGender,
                isVolunteer, isBloodDonor,
                bloodGroup,
                isFirstAider, isAmbulance,
                "ProfilePictures/userImage.PNG");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        callbackSignupFragment = (CallbackSignupFragment) context;

    }

    public boolean isValidate(String userFname, String userLname, String password, String contact, String guardian, String confirmPassword) {

        if (userFname.isEmpty() || userFname.length() < 3) {
            userFnameEditText.requestFocus();
            showErrorMessage("First name contain atleast 3 characters");
            return false;
        } else if (userLname.isEmpty() || userLname.length() < 3) {
            userLnameEditText.requestFocus();
            showErrorMessage("Last name contain atleast 3 characters");
            return false;
        } else if (password.isEmpty() || password.length() < 6) {
            passwordEditText.requestFocus();
            showErrorMessage("Password contain atleast 6 characters");
            return false;
        } else if (contact.isEmpty() || contact.length() < 11) {
            contactEditText.requestFocus();
            showErrorMessage("Contact Number is invalid Or incomplete");
            return false;
        } else if (guardian.isEmpty() || guardian.length() < 11) {
            guardianEditText.requestFocus();
            showErrorMessage("Contact Number is invalid Or incomplete");
            return false;
        }else if (confirmPassword.isEmpty() || !confirmPassword.equals(password)) {
            confirmPasswordEditText.requestFocus();
            showErrorMessage("Password is not matching");
            return false;
        } else {
            return true;
        }
    }

    private void userInfoListener(DatabaseReference databaseReference) {
        databaseReference.child("userinfo").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "userInfo Listener:   " + dataSnapshot.getKey());
                final User user = dataSnapshot.getValue(User.class);
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
                                user.guardian,
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

                        Intent intent = new Intent(getActivity(), HomeActivity.class);
                        startActivity(intent);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showErrorMessage(String message) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorError));
        snackbar.show();
    }
}
