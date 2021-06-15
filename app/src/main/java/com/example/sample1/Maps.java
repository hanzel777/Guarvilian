package com.example.sample1;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;



public class Maps extends AppCompatActivity
        implements OnMapReadyCallback {

    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        mMap = googleMap;

        LatLng reporter_position = new LatLng(MainActivity.Greporter_latitude_Get(), MainActivity.Greporter_longitude_Get());
        LatLng user_position = new LatLng(MainActivity.Guser_latitude_Get(), MainActivity.Guser_lonigtude_Get());

        MarkerOptions reoporter_markerOptions = new MarkerOptions();
        reoporter_markerOptions.position(reporter_position);
        reoporter_markerOptions.title(MainActivity.Gposition_Get());
        reoporter_markerOptions.snippet("환자 위치");
        mMap.addMarker(reoporter_markerOptions);

        MarkerOptions user_markerOptions = new MarkerOptions();
        user_markerOptions.position(user_position);
        user_markerOptions.title("내 위치");
        user_markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mMap.addMarker(user_markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(reporter_position,15));


    }

}