package com.example.hammad.instanthelp.Fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.example.hammad.instanthelp.R;
import com.example.hammad.instanthelp.models.PostModule;
import com.example.hammad.instanthelp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.michaelmuenzer.android.scrollablennumberpicker.ScrollableNumberPicker;
import com.michaelmuenzer.android.scrollablennumberpicker.ScrollableNumberPickerListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostFragment extends Fragment {

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseAuth mAuth;
    View view;
    MaterialBetterSpinner bloodSpinner, countrySpinner, citySpinner, hospitalSpinner;
    MaterialEditText contact;
    ScrollableNumberPicker unitPicker, dayPicker;
    Button postBtn;
    ArrayAdapter bloodSpinnerAdapter, countrySpinnerAdapter, citySpinnerAdapter, hospitalSpinnerAdapter;
    String bloodGrpValue, countryValue, cityValue, hospitalValue, contactValue;
    int  noOfUnitValue, days;
    ProgressDialog progressDialog;

    public PostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_post, container, false);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();

        bloodSpinner = (MaterialBetterSpinner) view.findViewById(R.id.bloodGroupSpinner);
        countrySpinner = (MaterialBetterSpinner) view.findViewById(R.id.countrySpinner);
        citySpinner = (MaterialBetterSpinner) view.findViewById(R.id.citySpinner);
        hospitalSpinner = (MaterialBetterSpinner) view.findViewById(R.id.hospitalSpinner);
        unitPicker = (ScrollableNumberPicker) view.findViewById(R.id.number_picker_units);
        dayPicker = (ScrollableNumberPicker) view.findViewById(R.id.number_picker_day);

        contact = (MaterialEditText) view.findViewById(R.id.contact);
        postBtn = (Button) view.findViewById(R.id.postButton);


        setAdaptersToSpinners();
//        focusChangeListener();

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post();
            }
        });
        return view;
    }

    private void post() {
        progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Posting...");
        progressDialog.show();

        final String uuid = mAuth.getCurrentUser().getUid();
        final String pushKey = myRef.getKey();
        final String bloodGrp = bloodSpinner.getText().toString();
//
        final String country = countrySpinner.getText().toString();
        final String city = citySpinner.getText().toString();
        final String hosp = hospitalSpinner.getText().toString();
        final String cont = contact.getText().toString();

        myRef.child("userinfo").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);

                PostModule module = new PostModule(uuid, currentUser.fname+" "+currentUser.lname, bloodGrp, noOfUnitValue, country, city, hosp, cont, 0, noOfUnitValue, days, pushKey);
                myRef.child("post-feed").child(country).child(city).child(uuid).setValue(module);
                progressDialog.dismiss();
                HomeFragment homeFragment = new HomeFragment();
                getFragmentManager().beginTransaction().replace(R.id.content_home, homeFragment, null).addToBackStack(null).commit();
                
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                progressDialog.dismiss();
                Toast.makeText(getActivity(), ""+ databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void setAdaptersToSpinners() {

        unitPicker.setListener(new ScrollableNumberPickerListener() {
            @Override
            public void onNumberPicked(int value) {
                noOfUnitValue = value;
                if(value == unitPicker.getMaxValue()) {
                    Toast.makeText(getActivity(), "You can't request more than 10 units", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dayPicker.setListener(new ScrollableNumberPickerListener() {
            @Override
            public void onNumberPicked(int value) {
                days = value;
                if(value == dayPicker.getMaxValue()) {
                    Toast.makeText(getActivity(), "You can't request for more than 5 days", Toast.LENGTH_SHORT).show();
                }
            }
        });
        bloodSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.bloodgroups, android.R.layout.simple_spinner_dropdown_item);
        bloodSpinner.setAdapter(bloodSpinnerAdapter);
        bloodSpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                bloodGrpValue = bloodSpinner.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        countrySpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.country, android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(countrySpinnerAdapter);
        countrySpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                countryValue = countrySpinner.getText().toString();
                if (countryValue.equals("Bangladesh")) {
                    citySpinner.setAdapter(null);
                    citySpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.bangladeshCities, android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(citySpinnerAdapter);
                } else if (countryValue.equals("Nepal")) {
                    citySpinner.setAdapter(null);
                    citySpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.nepalCities, android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(citySpinnerAdapter);
                } else if (countryValue.equals("Pakistan")) {
                    citySpinner.setAdapter(null);
                    citySpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.pakistanCities, android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(citySpinnerAdapter);
                } else if (countryValue.equals("Iran")) {
                    citySpinner.setAdapter(null);
                    citySpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.iranCities, android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(citySpinnerAdapter);
                } else if (countryValue.equals("Srilanka")) {
                    citySpinner.setAdapter(null);
                    citySpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.srilankaCities, android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(citySpinnerAdapter);
                } else if (countryValue.equals("Afghanistan")) {
                    citySpinner.setAdapter(null);
                    citySpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.afganistanCities, android.R.layout.simple_spinner_dropdown_item);
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
                cityValue = citySpinner.getText().toString();
                if (cityValue.equals("Dhaka")) {
                    hospitalSpinner.setAdapter(null);
                    hospitalSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.dhakaHosp, android.R.layout.simple_spinner_dropdown_item);
                    hospitalSpinner.setAdapter(hospitalSpinnerAdapter);
                } else if (cityValue.equals("Chittagong")) {
                    hospitalSpinner.setAdapter(null);
                    hospitalSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.chittagongHosp, android.R.layout.simple_spinner_dropdown_item);
                    hospitalSpinner.setAdapter(hospitalSpinnerAdapter);
                } else if (cityValue.equals("Kathmandu")) {
                    hospitalSpinner.setAdapter(null);
                    hospitalSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.khatmanduHosp, android.R.layout.simple_spinner_dropdown_item);
                    hospitalSpinner.setAdapter(hospitalSpinnerAdapter);
                } else if (cityValue.equals("Pokhara")) {
                    hospitalSpinner.setAdapter(null);
                    hospitalSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.potharaHosp, android.R.layout.simple_spinner_dropdown_item);
                    hospitalSpinner.setAdapter(hospitalSpinnerAdapter);
                } else if (cityValue.equals("Karachi")) {
                    hospitalSpinner.setAdapter(null);
                    hospitalSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.karachiHosp, android.R.layout.simple_spinner_dropdown_item);
                    hospitalSpinner.setAdapter(hospitalSpinnerAdapter);
                } else if (cityValue.equals("Lahore")) {
                    hospitalSpinner.setAdapter(null);
                    hospitalSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.lahoreHosp, android.R.layout.simple_spinner_dropdown_item);
                    hospitalSpinner.setAdapter(hospitalSpinnerAdapter);
                } else if (cityValue.equals("Islamabad")) {
                    hospitalSpinner.setAdapter(null);
                    hospitalSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.islamabadHosp, android.R.layout.simple_spinner_dropdown_item);
                    hospitalSpinner.setAdapter(hospitalSpinnerAdapter);
                } else if (cityValue.equals("Rawalpindi")) {
                    hospitalSpinner.setAdapter(null);
                    hospitalSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.pindiHosp, android.R.layout.simple_spinner_dropdown_item);
                    hospitalSpinner.setAdapter(hospitalSpinnerAdapter);
                } else if (cityValue.equals("Multan")) {
                    hospitalSpinner.setAdapter(null);
                    hospitalSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.multanHosp, android.R.layout.simple_spinner_dropdown_item);
                    hospitalSpinner.setAdapter(hospitalSpinnerAdapter);
                } else if (cityValue.equals("Tehran")) {
                    hospitalSpinner.setAdapter(null);
                    hospitalSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.tehranHosp, android.R.layout.simple_spinner_dropdown_item);
                    hospitalSpinner.setAdapter(hospitalSpinnerAdapter);
                } else if (cityValue.equals("Mashhad")) {
                    hospitalSpinner.setAdapter(null);
                    hospitalSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.mashadHosp, android.R.layout.simple_spinner_dropdown_item);
                    hospitalSpinner.setAdapter(hospitalSpinnerAdapter);
                } else if (cityValue.equals("Colombo")) {
                    hospitalSpinner.setAdapter(null);
                    hospitalSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.colomboHosp, android.R.layout.simple_spinner_dropdown_item);
                    hospitalSpinner.setAdapter(hospitalSpinnerAdapter);
                } else if (cityValue.equals("Galkissa")) {
                    hospitalSpinner.setAdapter(null);
                    hospitalSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.galkissaHosp, android.R.layout.simple_spinner_dropdown_item);
                    hospitalSpinner.setAdapter(hospitalSpinnerAdapter);
                } else if (cityValue.equals("Kabul")) {
                    hospitalSpinner.setAdapter(null);
                    hospitalSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.kabulHosp, android.R.layout.simple_spinner_dropdown_item);
                    hospitalSpinner.setAdapter(hospitalSpinnerAdapter);
                } else if (cityValue.equals("Kandhar")) {
                    hospitalSpinner.setAdapter(null);
                    hospitalSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.kandharHosp, android.R.layout.simple_spinner_dropdown_item);
                    hospitalSpinner.setAdapter(hospitalSpinnerAdapter);
                } else if (cityValue.equals("Jalalabad")) {
                    hospitalSpinner.setAdapter(null);
                    hospitalSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.jalabadHosp, android.R.layout.simple_spinner_dropdown_item);
                    hospitalSpinner.setAdapter(hospitalSpinnerAdapter);
                }
            }
        });

        hospitalSpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                hospitalValue = hospitalSpinner.getText().toString();
            }
        });
    }


}
