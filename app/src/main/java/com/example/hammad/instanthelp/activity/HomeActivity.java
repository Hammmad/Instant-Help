package com.example.hammad.instanthelp.activity;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hammad.instanthelp.Fragments.FeedbackFragment;
import com.example.hammad.instanthelp.Fragments.HomeFragment;
import com.example.hammad.instanthelp.Fragments.MyPostFragment;
import com.example.hammad.instanthelp.Fragments.PostFeedFrag;
import com.example.hammad.instanthelp.R;
import com.example.hammad.instanthelp.models.Constants;
import com.example.hammad.instanthelp.models.User;
import com.example.hammad.instanthelp.utils.CurrentUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int GALLERY_REQUEST_CODE = 0;
    private static final int CAMERA_REQUESTT_CODE = 1;
    private static final String TAG = "HOME/DEBUGGING";
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    ImageView userImageButton;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    private Uri mCropImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        LayoutInflater inflater = (LayoutInflater) HomeActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View navHeader = inflater.inflate(R.layout.nav_header_home, null);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.addHeaderView(navHeader);
        navigationView.setNavigationItemSelectedListener(this);


        TextView textView = (TextView) navHeader.findViewById(R.id.userName_textView);
        userImageButton = (ImageView) navHeader.findViewById(R.id.user_imageView);
        CurrentUser currentUser = new CurrentUser(this);
        User user = currentUser.getCurrentUser();

        userImageClickListener(userImageButton);


        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://instant-help.appspot.com");
        databaseReference = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    getSupportFragmentManager().beginTransaction().
                            replace(R.id.content_home, new HomeFragment()).commit();
                } else {
                    showSignInFragment();
                }
            }
        };
        if (user != null) {
            if (user.profileImagePath != null) {
                Log.d(TAG, user.profileImagePath);
                byte[] bytes = Base64.decode(user.profileImagePath, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                userImageButton.setImageBitmap(bitmap);
                textView.setText(user.emailAddress);
            }
        }
    }

    private void userImageClickListener(ImageView userImageButton) {
        userImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                showDialogue();

                CropImage.startPickImageActivity(HomeActivity.this);
            }
        });
    }

    private void showDialogue() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Select an action");
        alertDialog.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, GALLERY_REQUEST_CODE);
            }
        }).setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, CAMERA_REQUESTT_CODE);
                }
            }
        });

        alertDialog.show();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {


        if (resultCode == RESULT_OK) {
            if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE) {
                Uri imageUri = CropImage.getPickImageResultUri(this, data);

                // For API >= 23 we need to check specifically that we have permissions to read external storage.
                if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                    // request permissions and handle the result in onRequestPermissionsResult()
                    mCropImageUri = imageUri;
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
                } else {
                    // no permissions required or already grunted, can start crop image activity
                    startCropimageActivity(imageUri);
                }
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri resultUri = result.getUri();
                Picasso.with(this).load(resultUri).into(userImageButton);


                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        uploadImage();
                    }
                }, 5000);
            }

//            if (requestCode == CAMERA_REQUESTT_CODE) {
////                Picasso.with(this).load(data.getE).into(userImageButton);
//
//                CropImage.activity(data.getData()).setGuidelines(CropImageView.Guidelines.ON)
//                        .start(this);
//
//
//
//            } else if (requestCode == GALLERY_REQUEST_CODE) {
//
//                Picasso.with(this).load(data.getData()).into(userImageButton);
//            }else  if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//                CropImage.ActivityResult result = CropImage.getActivityResult(data);
//                Uri resultUri = result.getUri();
//
//
//                Picasso.with(this).load(resultUri).into(userImageButton);
//
////                Bundle extra = data.getExtras();
////                Bitmap bitmap = (Bitmap) extra.get("data");
////                userImageButton.setImageBitmap(bitmap);
//            }
//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//
//                    uploadImage(data);
//                }
//            }, 5000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                startCropimageActivity(mCropImageUri);
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startCropimageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setMinCropResultSize(200, 200)
                .setMaxCropResultSize(500, 500)
                .setRequestedSize(100, 100)
                .start(this);
    }

    private void uploadImage() {
        Bitmap bitmap;
        userImageButton.setDrawingCacheEnabled(true);
        userImageButton.buildDrawingCache();
        bitmap = userImageButton.getDrawingCache();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();

        UploadTask uploadTask = storageReference.child("images/" + databaseReference.push().getKey()).putBytes(bytes);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                CurrentUser currentUser = new CurrentUser(HomeActivity.this);
                User user = currentUser.getCurrentUser();
                if (user != null) {
                    updateUserInfo(taskSnapshot, currentUser, user);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void updateUserInfo(UploadTask.TaskSnapshot taskSnapshot, CurrentUser currentUser, final User user) {
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
                taskSnapshot.getStorage().getPath());
        databaseReference.child("userinfo").child(user.uId).setValue(updatedUser);

        setupPreference(user);

//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//            }
//        });


    }

    private void setupPreference(User user) {
        userImageButton.setDrawingCacheEnabled(true);
        userImageButton.buildDrawingCache();
        Bitmap bitmap = userImageButton.getDrawingCache();
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, boas);
        byte[] bytes = boas.toByteArray();
        String imageString = Base64.encodeToString(bytes, Base64.DEFAULT);

        User updatedUser =new User(
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
                imageString);
        CurrentUser currentUser = new CurrentUser(HomeActivity.this);
        currentUser.setCurrentUser(updatedUser);
    }

    private void showSignInFragment() {
        startActivity(new Intent(this, AuthActivity.class));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.content_home, new HomeFragment()).commit();
        } else if (id == R.id.nav_post) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new PostFeedFrag()).commit();

        } else if (id == R.id.nav_myPost) {
//            Toast.makeText(this, "My Posts", Toast.LENGTH_SHORT).show();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_home, new MyPostFragment()).commit();
        } else if (id == R.id.nav_guide) {
            Intent intent = new Intent(HomeActivity.this, FirstAidGuidActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_improve_location) {

            showSettingsAlert();
        } else if (id == R.id.nav_feedback) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_home, new FeedbackFragment()).commit();

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            clearPreferences();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void clearPreferences() {
        SharedPreferences userPref = getSharedPreferences(Constants.CURRENT_USER, MODE_PRIVATE);
        SharedPreferences NoImageUserPref = getSharedPreferences(Constants.NO_IMAGE_CURRENT_USER, MODE_PRIVATE);
        SharedPreferences.Editor userEditor = userPref.edit();
        SharedPreferences.Editor NoImageuserEditor = NoImageUserPref.edit();
        userEditor.clear().commit();
        NoImageuserEditor.clear().commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuth != null) {
            mAuth.removeAuthStateListener(authStateListener);
        }
    }

    public static Bitmap getRoundedRectBitmap(Bitmap bitmap, int pixels) {
        Bitmap result = null;
        try {
            result = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);

            int color = 0xff424242;
            Paint paint = new Paint();
            Rect rect = new Rect(0, 0, 200, 200);

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawCircle(80, 80, 80, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

        } catch (NullPointerException | OutOfMemoryError e) {
        }
        return result;
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");

        // Setting Dialog Message
        alertDialog
                .setMessage("To improve your location, please turn GPS to 'ON'.");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // Showing Alert Message
        alertDialog.show();
    }
}
