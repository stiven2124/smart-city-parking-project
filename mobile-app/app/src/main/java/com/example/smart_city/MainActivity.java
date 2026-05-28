package com.example.smart_city;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://smartcity-app-f7b7c2esbeetf5ad.switzerlandnorth-01.azurewebsites.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        mMap.setMyLocationEnabled(true);

        mMap.setOnMarkerClickListener(marker -> {
            ParkingSlots slot = (ParkingSlots) marker.getTag();
            if (slot != null) {
                calculateDistance(marker, slot);
            }
            return false;
        });

        // 2. Center Camera
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f));
            } else {
                LatLng omonoia = new LatLng(37.9838, 23.7275);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(omonoia, 13f));
            }
        });
        fetchParkingData();
    }

    private void fetchParkingData() {
        apiService.getParkingSlots().enqueue(new Callback<List<ParkingSlots>>() {
            @Override
            public void onResponse(Call<List<ParkingSlots>> call, Response<List<ParkingSlots>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mMap.clear();
                    int count = 0;
                    for (ParkingSlots slot : response.body()) {
                        addMarkerForSlot(slot);
                        count++;
                    }
                    Toast.makeText(MainActivity.this, "Found " + count + " parking lots", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<ParkingSlots>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMarkerForSlot(ParkingSlots slot) {
        LatLng position = new LatLng(slot.getLat(), slot.getLon());
        int status = slot.getStatus();
        String state = (status == 1) ? "Occupied" : "Free";
        float color = (status == 1) ? BitmapDescriptorFactory.HUE_RED : BitmapDescriptorFactory.HUE_GREEN;
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(position)
                .title(slot.getName())
                .snippet("Status: " + state)
                .icon(BitmapDescriptorFactory.defaultMarker(color)));
        if (marker != null) {
            marker.setTag(slot);
        }
    }

    private void calculateDistance(Marker marker, ParkingSlots slot) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    float[] results = new float[1];
                    Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                            slot.getLat(), slot.getLon(), results);
                    float distanceInMeters = results[0];
                    String distanceStr = (distanceInMeters > 1000)
                            ? String.format("%.2f km", distanceInMeters / 1000)
                            : String.format("%d m", (int) distanceInMeters);
                    // Re-check status for the label
                    String state = (slot.getStatus() == 1) ? "Occupied" : "Free";
                    marker.setSnippet("Status: " + state + " | Dist: " + distanceStr);
                    marker.showInfoWindow();
                }
            });
        }
    }
}