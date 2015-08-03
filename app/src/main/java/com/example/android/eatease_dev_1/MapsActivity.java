package com.example.android.eatease_dev_1;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

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
                        .snippet("Simple example about Marker")

        );
      mMap.addPolyline(new PolylineOptions()
              .add(new LatLng(-37.81319, 144.96298), new LatLng(-31.95285, 115.85734))
              .width(25)
              .color(Color.BLUE)
              .geodesic(true));

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(12.9568, 77.5668), 13));
        Marker marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(12.9568, 77.5668))
                .title("Bangalore")
                .snippet("Simple example about Customized Marker")
                .draggable(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .alpha(0.7f)
                .flat(true)

        );
        marker.showInfoWindow();
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);

        Polyline line = googleMap.addPolyline(new PolylineOptions()
                .add(new LatLng(12.9568, 77.5668), new LatLng(13, 78))
                .color(Color.BLUE)
                .zIndex(10)
                .geodesic(true));

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
            case R.id.groundOverlaymenu:
                Toast.makeText(this,"intent raised",Toast.LENGTH_SHORT).show();
                groundOverlay();
                return true;
            case R.id.polygonMenu:
                Toast.makeText(this,"intent raised",Toast.LENGTH_SHORT).show();
                polygonActivity();
                return true;
            case R.id.circleMenu:
                Toast.makeText(this,"intent raised",Toast.LENGTH_SHORT).show();
                circleActivity();
                return true;
            case R.id.polyLineMenu:
                Toast.makeText(this,"intent raised",Toast.LENGTH_SHORT).show();
                polyLineActivity();
                return true;
            case R.id.heatMapMenu:
                Toast.makeText(this,"intent raised",Toast.LENGTH_SHORT).show();
                heatMapActivity();
                return true;
            case R.id.markerClusteringMenu:
                Toast.makeText(this,"intent raised",Toast.LENGTH_SHORT).show();
                markerClusteringActivity();
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
    private void groundOverlay(){
        Intent intent = new Intent(this,GroundOverlay.class);
        Toast.makeText(this,"intent raised",Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    private void polygonActivity(){
        Intent intent = new Intent(this,PolygonActivity.class);
        Toast.makeText(this,"intent raised",Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    private void circleActivity(){
        Intent intent = new Intent(this,CircleActivity.class);
        startActivity(intent);
    }
    private void polyLineActivity() {
        Intent intent = new Intent(this, PolyLine.class);
        startActivity(intent);
    }

    private void heatMapActivity() {
        Intent intent = new Intent(this, HeatMapActivity.class);
        startActivity(intent);
    }
    private void markerClusteringActivity(){
        Intent intent = new Intent(this, MarkerClustering.class);
        startActivity(intent);
    }

}
