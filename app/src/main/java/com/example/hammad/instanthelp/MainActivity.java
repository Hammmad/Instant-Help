package com.example.hammad.instanthelp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.hammad.instanthelp.Fragments.SignupFragment;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().
                replace(R.id.activity_main, new SignupFragment()).commit();

    }


}
