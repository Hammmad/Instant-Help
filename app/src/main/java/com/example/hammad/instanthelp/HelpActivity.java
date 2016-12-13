package com.example.hammad.instanthelp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.hammad.instanthelp.Fragments.HelpFragment;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);


        getSupportFragmentManager().beginTransaction().
                add(R.id.activity_help, new HelpFragment()).commit();
    }


}
