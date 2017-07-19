package com.example.hammad.instanthelp.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.hammad.instanthelp.R;
import com.example.hammad.instanthelp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Qasim Nawaz on 7/12/2017.
 */

public class DonorsListAdapter extends ArrayAdapter<User> {


    private ArrayList<User> dataSet;
    Context mContext;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseAuth mAuth;

    // View lookup cache
    private static class ViewHolder {
        TextView donorName;
        TextView donorUnits;
        TextView donorContact;
    }

    public DonorsListAdapter(ArrayList<User> data, Context context) {
        super(context, R.layout.donors_list_item, data);
        this.dataSet = data;
        this.mContext = context;
    }

    private int lastPosition = -1;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        User dataModel = getItem(position);
        DonorsListAdapter.ViewHolder viewHolder;
        final View result;

        if (convertView == null) {
            viewHolder = new DonorsListAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.donors_list_item, parent, false);
            viewHolder.donorName = (TextView) convertView.findViewById(R.id.donor_name);
            viewHolder.donorUnits = (TextView) convertView.findViewById(R.id.donor_donated_units);
            viewHolder.donorContact = (TextView) convertView.findViewById(R.id.donor_contact);
            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (DonorsListAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.donorName.setText(dataModel.fname + " " + dataModel.lname);
        viewHolder.donorUnits.setText(Integer.toString(dataModel.donatedUnits));
        viewHolder.donorContact.setText(dataModel.contact);

        return convertView;
    }
}
