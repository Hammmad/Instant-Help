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
import android.widget.Button;
import android.widget.TextView;

import com.example.hammad.instanthelp.R;
import com.example.hammad.instanthelp.models.PostModule;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Qasim Nawaz on 7/11/2017.
 */

public class MyPostAdapter extends ArrayAdapter<PostModule> implements View.OnClickListener{

    private ArrayList<PostModule> dataSet;
    Context mContext;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseAuth mAuth;
    int spinner;

    FirebaseAuth.AuthStateListener mAuthListener;

    // View lookup cache
    private static class ViewHolder {
        TextView txtDesc;
        TextView txtContact;
        TextView txtCurrentRequir;
        Button fullfillBtn;
    }

    public MyPostAdapter(ArrayList<PostModule> data, Context context) {
        super(context, R.layout.my_post_list_item, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();

        int position = (Integer) v.getTag();
        PostModule dataModel = getItem(position);

        switch (v.getId()) {
            case R.id.myPost_fullfill_button:
//                CurrentUser currentUser = new CurrentUser(mContext);
//                User user = currentUser.getCurrentUser();
//                PostModule updatePostModule = new PostModule(dataModel.getUuid(), dataModel.getmName(), dataModel.getmGroup(), dataModel.getmNoofUnits(), dataModel.getmCountry(), dataModel.getmCity(), dataModel.getmHospital(), dataModel.getmContact(), dataModel.getDonatedUnits()+spinner, dataModel.getCurrentRequirement()-spinner, dataModel.getWithinDuration(), dataModel.getPushkey());
//                myRef.child("post-feed").child(dataModel.getPushkey()).setValue(updatePostModule);
//                myRef.child("my-post").child(dataModel.getUuid()).child(dataModel.getPushkey()).setValue(updatePostModule);
//                myRef.child("donors").child(dataModel.getUuid()).child(dataModel.getPushkey()).setValue(user);
//                myRef.child("donors").child(dataModel.getUuid()).child(dataModel.getPushkey()).setValue(spinner);
        }
    }

    private int lastPosition = -1;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        PostModule dataModel = getItem(position);
        MyPostAdapter.ViewHolder viewHolder;
        final View result;

        if (convertView == null) {
            viewHolder = new MyPostAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.my_post_list_item, parent, false);
            viewHolder.txtDesc = (TextView) convertView.findViewById(R.id.myPost_description);
            viewHolder.txtContact = (TextView) convertView.findViewById(R.id.myPost_contact);
            viewHolder.txtCurrentRequir = (TextView) convertView.findViewById(R.id.myPost_current_requir);
            viewHolder.fullfillBtn = (Button) convertView.findViewById(R.id.myPost_fullfill_button);
            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MyPostAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtDesc.setText(dataModel.getmName() + " needs " + dataModel.getmNoofUnits() + " bottles of " + dataModel.getmGroup() + " blood group at " + dataModel.getmHospital() + ", " + dataModel.getmCity() + " within " + dataModel.getWithinDuration() + " days.");
        viewHolder.txtContact.setText(dataModel.getmContact());
        viewHolder.txtCurrentRequir.setText(Integer.toString(dataModel.getCurrentRequirement()));
        if (dataModel.getCurrentRequirement() <= 0){
            viewHolder.fullfillBtn.setVisibility(View.VISIBLE);
        }else {
            viewHolder.fullfillBtn.setVisibility(View.GONE);
        }

        viewHolder.fullfillBtn.setOnClickListener(this);
        viewHolder.fullfillBtn.setTag(position);
        return convertView;
    }
}
