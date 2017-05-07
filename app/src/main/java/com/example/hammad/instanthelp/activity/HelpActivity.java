package com.example.hammad.instanthelp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.hammad.instanthelp.Adapter.ListAdapter;
import com.example.hammad.instanthelp.Fragments.FeedbackFragment;
import com.example.hammad.instanthelp.Fragments.HomeFragment;
import com.example.hammad.instanthelp.R;
import com.example.hammad.instanthelp.models.ItemDrawer;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class HelpActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitleTextAppearance(this, R.style.toolbar);
        setSupportActionBar(toolbar);




        String[] drawerMenu = getResources().getStringArray(R.array.drawer_menu);
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ListView drawerList = (ListView) findViewById(R.id.left_drawer);
        List<ItemDrawer> list = new ArrayList<>();
        list.add(new ItemDrawer("Home", R.drawable.ic_home_outline_white_24dp));
        list.add(new ItemDrawer("First Aid Guide", R.drawable.ic_hospital_white_24dp));
        list.add(new ItemDrawer("Be a Volunteer", R.drawable.ic_account_convert_white_24dp));
        list.add(new ItemDrawer("Add location", R.drawable.ic_add_location_white_24dp));
        list.add(new ItemDrawer("Improve your location", R.drawable.ic_account_location_white_24dp));
        list.add(new ItemDrawer("Send feedback", R.drawable.ic_insert_comment_white_24dp));
        list.add(new ItemDrawer("Settings", R.drawable.ic_settings_white_24dp));
        list.add(new ItemDrawer("Logout", R.drawable.ic_logout_white_24dp));

        drawerList.setAdapter(new ListAdapter(this,list));
        final ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        mDrawerToggle.setDrawerIndicatorEnabled(false);


        mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        drawerLayout.setDrawerListener(mDrawerToggle);

        drawerListClickListener(drawerLayout, drawerList);

//        drawerList.setAdapter(new ArrayAdapter<>(this,
//                android.R.layout.simple_list_item_1,drawerMenu));

        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(drawerLayout.isDrawerVisible(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START);
                }else{
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });


        getSupportFragmentManager().beginTransaction().
                replace(R.id.frame_help, new HomeFragment()).commit();
    }

    private void drawerListClickListener(final DrawerLayout drawerLayout, ListView drawerList) {
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i){
                    case  0:
                    {
                        closeDrawer(drawerLayout);
                        getSupportFragmentManager().beginTransaction().
                                replace(R.id.frame_help, new HomeFragment()).commit();
                        break;
                    }
                    case  1:
                    {
                        break;
                    }
                    case  2:
                    {
                        break;
                    }
                    case  3:
                    {
                        break;
                    }
                    case  4:
                    {
                        break;
                    }
                    case  5:
                    {
                        closeDrawer(drawerLayout);
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_help,
                                    new FeedbackFragment()).commit();
                            getSupportActionBar().setTitle("Feedback");

                            break;

                    }
                    case  6:
                    {
                        break;
                    }
                    case 7:{
                        closeDrawer(drawerLayout);
                            mAuth.signOut();
                            Intent intent = new Intent(HelpActivity.this, AuthActivity.class);
                            startActivity(intent);
                            break;

                    }
                }

            }
        });
    }

    private void closeDrawer(DrawerLayout drawerLayout) {
        if(drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }


}
