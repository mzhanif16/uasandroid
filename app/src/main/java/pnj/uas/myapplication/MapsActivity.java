package pnj.uas.myapplication;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import pnj.uas.myapplication.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Boolean ready = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        LocationManager locMan = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            },123);
        }
        locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        //update map arahan ke lokasi sesuai info provider
                        if(ready){
                            // Add a current marker
                            LatLng position= new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(position).title("Lokasi perangkat saat ini "));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                        }
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        LocationListener.super.onStatusChanged(provider, status, extras);
                    }

                    @Override
                    public void onProviderEnabled(@NonNull String provider) {
                        LocationListener.super.onProviderEnabled(provider);
                    }

                    @Override
                    public void onProviderDisabled(@NonNull String provider) {
                        LocationListener.super.onProviderDisabled(provider);
                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        ready = true;
        // Add a marker in Sydney and move the camera
        LatLng PNJ = new LatLng(-6.369442878124297, 106.82324897368693);
        mMap.addMarker(new MarkerOptions().position(PNJ).title("Ini Pnj"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(PNJ));
    }
}