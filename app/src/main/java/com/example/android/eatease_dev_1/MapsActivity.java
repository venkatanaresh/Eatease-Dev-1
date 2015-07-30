package com.example.android.eatease_dev_1;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends ActionBarActivity implements OnMapReadyCallback {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Firebase ref; // Firebase reference
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Firebase.setAndroidContext(this);
        ActionBar bar = getSupportActionBar();
        bar.setDisplayShowHomeEnabled(true);
        bar.setIcon(R.mipmap.ic_launcher);
        setUpMapIfNeeded();
        //code 2
        GoogleMapOptions options = new GoogleMapOptions().liteMode(true);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //SupportMapFragment mMapFragment = SupportMapFragment.newInstance();
    }


    @Override
    protected void onStart() {
        super.onStart();
        /***************************************************
         * Start of Firebase Stuff
         ****************************************************/
        Button b1 = (Button) findViewById(R.id.button);
        Button b2 = (Button) findViewById(R.id.button2);
        final TextView tv = (TextView) findViewById(R.id.textView);
        ref = new Firebase("https://androidwithfirebase.firebaseio.com");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("For Firebase", (String) dataSnapshot.getValue());
                String weather = (String) dataSnapshot.getValue();
                tv.setText(weather);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.setValue("Sunny");
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.setValue("Foggy");
            }
        });
        /***************************************************
         * End  of Firebase Stuff
         ****************************************************/
    }





    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(12.9667, 77.5667), 13));
        mMap.addMarker(new MarkerOptions().position(new LatLng(12.9667, 77.5667))
                        .title("Bangalore")
                        .snippet("The most populous city in Karnataka.")
        );
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(12.9568, 77.5668), 13));
        googleMap.addMarker(new MarkerOptions().position(new LatLng(12.9568, 77.5668))
                        .title("Bangalore")
                        .snippet("The most populous city in India.")
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .alpha(0.7f)
                        .flat(true)
        );
          googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        //googleMap.getUiSettings().setMyLocationButtonEnabled(false);


//         LatLng SYDNEY = new LatLng(-33.88,151.21);
//         LatLng MOUNTAIN_VIEW = new LatLng(37.4, -122.1);
//
//
//
//        // Move the camera instantly to Sydney with a zoom of 15.
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SYDNEY, 15));
//
//        // Zoom in, animating the camera.
//        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
//
//        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
//        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
//
//        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(MOUNTAIN_VIEW)      // Sets the center of the map to Mountain View
//                .zoom(17)                   // Sets the zoom
//                .bearing(90)                // Sets the orientation of the camera to east
//                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
//                .build();                   // Creates a CameraPosition from the builder
//        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        //googleMap.setOnMarkerClickListener(new OnMarkerClickListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

         //Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_streetView:
                streetView();
                return true;
            case R.id.intent_launchGmap:
                launchGmapApp();
                return true;
            case R.id.intent_launchGmapStreetView:
                streetView2();
                return true;
            case R.id.litemode:
                liteMode();
                return true;
            case R.id.indoorMap:
                indoorMap();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void streetView(){
        Intent intent = new Intent(this,StreetView.class);
        startActivity(intent);
    }

    private void launchGmapApp(){
        Uri gmmIntentUri = Uri.parse("geo:0,0=10&q=restaurants");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    private void streetView2(){
        // Opens Street View between two Pyramids in Giza. The values passed to the
        // cbp parameter will angle the camera slightly up, and towards the east.
        Uri gmmIntentUri = Uri.parse("google.streetview:cbll=29.9774614,31.1329645&cbp=0,30,0,0,-15");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }
    private void liteMode(){
        Intent intent = new Intent(this,LiteModeActivity.class);
        startActivity(intent);
    }
    private void indoorMap(){
        Intent intent = new Intent(this,IndoorMapActivity.class);
        startActivity(intent);
    }
}
