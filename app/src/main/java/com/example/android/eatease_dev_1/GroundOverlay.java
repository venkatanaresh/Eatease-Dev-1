package com.example.android.eatease_dev_1;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class GroundOverlay extends ActionBarActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ground_overlay);
        ActionBar bar = getSupportActionBar();
        bar.setDisplayShowHomeEnabled(true);
        bar.setIcon(R.mipmap.ic_launcher);
        bar.setDisplayHomeAsUpEnabled(true);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.groundOverlay);
        mapFragment.getMapAsync(this);
    }





    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(40.714086, -74.228697), 13));
        Marker marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(12.9568, 77.5668))
                        .title("Ground OverLay")
                        .snippet("This is a simple Example of Ground overlay")

        );
        marker.showInfoWindow();
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//        LatLng NEWARK = new LatLng(40.714086, -74.228697);
//
//        GroundOverlayOptions newarkMap = new GroundOverlayOptions()
//                .image(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))
//                .anchor(0, 1)
//                .position(new LatLng(40.714086, -74.228697), 8600f, 6500f);
//        googleMap.addGroundOverlay(newarkMap);

        LatLngBounds newarkBounds = new LatLngBounds(
                new LatLng(40.712216, -74.22655),       // South west corner
                new LatLng(40.773941, -74.12544));      // North east corner
        GroundOverlayOptions newarkMap1 = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))
                .positionFromBounds(newarkBounds);
        googleMap.addGroundOverlay(newarkMap1);

    }
}
