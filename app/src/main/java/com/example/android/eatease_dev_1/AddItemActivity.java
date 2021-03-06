package com.example.android.eatease_dev_1;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class AddItemActivity extends AppCompatActivity implements ConnectionCallbacks,OnConnectionFailedListener,LocationListener {
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    protected String  geoaddress = "";
    private Uri fileUri;
    private Firebase ref; // Firebase reference
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String imageBaseFromat = null ;
    protected GoogleApiClient mGoogleApiClient;
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;

    private static final String STATE_RESOLVING_ERROR = "resolving_error";

    //Device Last Location
    protected Location mLastLocation;
    //Periodic Location updates variable
    protected Location mCurrentLocation;
    protected LocationRequest mLocationRequest;

    private EditText itemName,itemPrice,itemAddress1,itemAddress2,itemAddress3;
    private ImageView itemImage;
    private Button itemUpload,itemSubmit;
    private String addressLine1,addressLine2,addressLine3,ItemName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        Firebase.setAndroidContext(this);
        ActionBar bar = getSupportActionBar();
        bar.setDisplayShowHomeEnabled(true);
        bar.setIcon(R.mipmap.pizza1);
        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);

        //Location request parameters object
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(50000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        itemName = (EditText) findViewById(R.id.item_name);
        itemPrice = (EditText) findViewById(R.id.item_price);
        itemAddress1 = (EditText) findViewById(R.id.item_address1);
        itemAddress2 = (EditText) findViewById(R.id.item_address2);
        itemAddress3 = (EditText) findViewById(R.id.item_address3);
        itemImage = (ImageView) findViewById(R.id.item_image);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        itemImage.setImageBitmap(bm);
        itemSubmit = (Button) findViewById(R.id.submit_details);
        itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });
        itemSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemDetailsSubmit();
            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {  // more about this later
            mGoogleApiClient.connect();
        }
        /***************************************************
         * Start of Firebase Stuff
         ****************************************************/


        ref = new Firebase(getResources().getString(R.string.firebase_url_items));
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
////                 Log.i("For Firebase", (String) dataSnapshot.getValue());
//
//
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });


        /***************************************************
         * End  of Firebase Stuff
         ****************************************************/
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_item, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            startLocationUpdates();
            addressUpdate(mLastLocation);
        }
    }
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        // userLocationChanged(mCurrentLocation);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection has been interrupted.
        // Disable any UI components that depend on Google APIs
        // until onConnected() is called.
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }
    // The rest of this code is all about building the error dialog

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }


    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() { }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode,
                    this.getActivity(), REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((OrderActivity)getActivity()).onDialogDismissed();
        }
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

    private void itemDetailsSubmit(){
        Toast.makeText(this,itemName.getText(), Toast.LENGTH_SHORT).show();
        if(imageBaseFromat !="" && itemName != null&& itemPrice != null &&itemAddress1 != null &&itemAddress2 != null
                &&itemAddress3 != null
                &&mCurrentLocation!= null){
            Toast.makeText(this,"Your Item Added Successfully", Toast.LENGTH_SHORT).show();
            int tempPrice = Integer.parseInt(String.valueOf(itemPrice.getText()));

            Map<String, Object> post1 = new HashMap<String, Object>();
            post1.put("address1", itemAddress1.getText().toString());
            post1.put("address2", itemAddress2.getText().toString());
            post1.put("address3", itemAddress3.getText().toString());
            post1.put("itemname", itemName.getText().toString());
            post1.put("itemprice", tempPrice);
            post1.put("latitude", (Double)mCurrentLocation.getLatitude());
            post1.put("longitude",(Double) mCurrentLocation.getLongitude());
            post1.put("time", DateFormat.getTimeInstance().format(new Date()));
            post1.put("image", imageBaseFromat);
            ItemName = itemName.getText().toString();
            ref.push().setValue(post1);
            itemAddress1.setText("");
            itemAddress2.setText("");
            itemAddress3.setText("");
            itemName.setText("");
            itemPrice.setText("");
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            itemImage.setImageBitmap(bm);
            //itemImage.setImageBitmap(Bitmap.createBitmap());
            Intent intent = new Intent(this,OrderActivity.class);
            intent.putExtra("latitude",mCurrentLocation.getLatitude());
            intent.putExtra("longitude",mCurrentLocation.getLongitude());
            intent.putExtra("addressLine1",addressLine1);
            intent.putExtra("addressLine2",addressLine2);
            intent.putExtra("addressLine3",addressLine3);
            intent.putExtra("ItemName",ItemName);
            intent.putExtra("flag_for_marker",1);
            intent.putExtra("methodName","placeMarker");
            OrderActivity.TRACKER = 1;
            //startActivity(intent);

        }
        else{
            Toast.makeText(this,"Please Enter The Missing Fields", Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

//        Bitmap bp = (Bitmap) data.getExtras().get("data");
//        itemImage.setImageBitmap(bp);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
           // Bitmap imageBitmap = (Bitmap) extras.get("data");
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            itemImage.setImageBitmap(imageBitmap);
            encodeTobase64(imageBitmap);
        }
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    private void encodeTobase64(Bitmap image) {
        Bitmap immagex=image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        Log.e("LOOK", imageEncoded);
        imageBaseFromat = imageEncoded;
    }
    private Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    private void addressUpdate(Location location){

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String errorMessage = "";

        // Get the location passed to this service through an extra.



        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = "service_not_available";
            Log.e("TAG", errorMessage, ioException);
            Toast.makeText(this, "service_not_available", Toast.LENGTH_LONG).show();

        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = "invalid_lat_long_used";
            Toast.makeText(this,"invalid_lat_long_used",Toast.LENGTH_LONG).show();
            Log.e("TAG", errorMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude(), illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                //Toast.makeText(this,"Sorry Address Not Available",Toast.LENGTH_LONG).show();
                geoaddress = "Sorry Address Not Available";

            }

        } else {
            Address address1 = addresses.get(0);
            String str = "";
            Log.e("Address",address1.toString());
            for( int i=0;i<address1.getMaxAddressLineIndex();i++){
                str = str+ address1.getAddressLine(i);
                if(i==0){
                itemAddress1.setText(address1.getAddressLine(i));
                    addressLine1 = address1.getAddressLine(i);
                }
                if(i==1){
                itemAddress2.setText(address1.getAddressLine(i));
                    addressLine2 = address1.getAddressLine(i);
                }
                if(i==2) {
                    itemAddress3.setText(address1.getAddressLine(i));
                    addressLine3 = address1.getAddressLine(i);
                }

            }
            geoaddress = str;
            //Toast.makeText(this,address1.toString(), Toast.LENGTH_LONG).show();


        }

    }
}
