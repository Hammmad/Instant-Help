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
import android.widget.Toast;

import com.example.hammad.instanthelp.R;
import com.example.hammad.instanthelp.models.PostModule;
import com.example.hammad.instanthelp.models.User;
import com.example.hammad.instanthelp.utils.CurrentUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.michaelmuenzer.android.scrollablennumberpicker.ScrollableNumberPicker;
import com.michaelmuenzer.android.scrollablennumberpicker.ScrollableNumberPickerListener;

import java.util.ArrayList;

/**
 * Created by Qasim Nawaz on 5/22/2017.
 */

public class PostFeedAdapter extends ArrayAdapter<PostModule> implements View.OnClickListener, ScrollableNumberPickerListener {

    private ArrayList<PostModule> dataSet;
    Context mContext;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseAuth mAuth;
    int spinner;

    FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    public void onNumberPicked(int value) {
        spinner = value;
        Toast.makeText(mContext, "" + value, Toast.LENGTH_SHORT).show();
    }

    // View lookup cache
    private static class ViewHolder {
        TextView txtDesc;
        TextView txtContact;
        TextView txtCurrentRequir;
        ScrollableNumberPicker donationPicker;
        Button post;
    }

    public PostFeedAdapter(ArrayList<PostModule> data, Context context) {
        super(context, R.layout.list_item, data);
        this.dataSet = data;
        this.mContext = context;

    }

    @Override
    public void onClick(final View v) {

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();

        int position = (Integer) v.getTag();
        final PostModule dataModel = getItem(position);

        switch (v.getId()) {
            case R.id.post_donate_button:
                CurrentUser currentUser = new CurrentUser(mContext);
                User user = currentUser.getCurrentUser();
                User donor = new User(user.uId, user.fname, user.lname, user.emaiAddress, user.contact, spinner, user.profileImagePath);
                PostModule updatePostModule = new PostModule(dataModel.getUuid(), dataModel.getmName(), dataModel.getmGroup(), dataModel.getmNoofUnits(), dataModel.getmCountry(), dataModel.getmCity(), dataModel.getmHospital(), dataModel.getmContact(), dataModel.getDonatedUnits() + spinner, dataModel.getCurrentRequirement() - spinner, dataModel.getWithinDuration(), dataModel.getPushkey());
//                if (dataModel.getCurrentRequirement() == 0){
//                    myRef.child("post-feed").child(dataModel.getPushkey()).removeValue();
//                    int deletePos = Integer.parseInt(v.getTag().toString());
//                    dataSet.remove(deletePos);
//                }else {

//                }
                myRef.child("post-feed").child(dataModel.getPushkey()).setValue(updatePostModule).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        myRef.child("post-feed").child(dataModel.getPushkey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                PostModule checkReq = dataSnapshot.getValue(PostModule.class);
                                if (checkReq.getCurrentRequirement() == 0){
                                    myRef.child("post-feed").child(dataModel.getPushkey()).removeValue();
                                    int deletePos = Integer.parseInt(v.getTag().toString());
                                    dataSet.remove(deletePos);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
                myRef.child("my-post").child(dataModel.getUuid()).child(dataModel.getPushkey()).setValue(updatePostModule);
                myRef.child("donors").child(dataModel.getPushkey()).push().setValue(donor);

        }
    }

    private int lastPosition = -1;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        PostModule dataModel = getItem(position);
        ViewHolder viewHolder;
        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            viewHolder.txtDesc = (TextView) convertView.findViewById(R.id.post_description);
            viewHolder.txtContact = (TextView) convertView.findViewById(R.id.post_contact);
            viewHolder.txtCurrentRequir = (TextView) convertView.findViewById(R.id.post_current_requir);
            viewHolder.post = (Button) convertView.findViewById(R.id.post_donate_button);
            viewHolder.donationPicker = (ScrollableNumberPicker) convertView.findViewById(R.id.post_feed_donate_picker);
            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtDesc.setText(dataModel.getmName() + " needs " + dataModel.getmNoofUnits() + " bottles of " + dataModel.getmGroup() + " blood group at " + dataModel.getmHospital() + ", " + dataModel.getmCity() + " within " + dataModel.getWithinDuration() + " days.");
        viewHolder.txtContact.setText(dataModel.getmContact());
//        if (dataModel.getCurrentRequirement() == 0) {
//            myRef.child("post-feed").child(dataModel.getPushkey()).removeValue();
//            dataSet.remove(position);
//        }
        viewHolder.txtCurrentRequir.setText(Integer.toString(dataModel.getCurrentRequirement()));
        viewHolder.donationPicker.setListener(this);
        viewHolder.donationPicker.setTag(position);
        viewHolder.post.setOnClickListener(this);
        viewHolder.post.setTag(position);
        return convertView;

    }
}
