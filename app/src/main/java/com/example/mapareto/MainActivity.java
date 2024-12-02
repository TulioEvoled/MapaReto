package com.example.mapareto;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final int PETICION_PERMISO_LOCALIZACION = 101;
    private GoogleMap mMap;
    private double lat = 0.0, lng = 0.0;
    private LatLng coordenadas;
    private Marker marcador;
    private String calle2 = "";
    private TextView calle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Habilitar diseño Edge-to-Edge
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // Ajustar insets para diseño Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Configurar fragmento del mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        // Configurar botón y campo de texto
        Button lugares = findViewById(R.id.bLocalizacion);
        calle = findViewById(R.id.tvLocalizacion);
        calle.setText(calle2);

        lugares.setOnClickListener(v -> {
            Intent verUbicacion = new Intent(MainActivity.this, Ubicacion.class);
            startActivity(verUbicacion);
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Configurar ajustes del mapa
        UiSettings settings = mMap.getUiSettings();
        settings.setCompassEnabled(true);
        settings.setRotateGesturesEnabled(true);
        settings.setScrollGesturesEnabled(true);
        settings.setZoomControlsEnabled(true);
        settings.setZoomGesturesEnabled(true);

        direccionAct();
    }
    private void direccionAct() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PETICION_PERMISO_LOCALIZACION);
        } else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            updateLocalizacion(location);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 20000, 5, locationListener);
        }
    }

    private void updateLocalizacion(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            coordenadas = new LatLng(lat, lng);
            CameraUpdate ubicacionCam = CameraUpdateFactory.newLatLngZoom(coordenadas, 16);
            if (marcador != null) marcador.remove();
            marcador = mMap.addMarker(new MarkerOptions()
                    .position(coordenadas)
                    .title("Ubicación actual: " + calle2)
                    .icon(BitmapDescriptorFactory.defaultMarker()));
            mMap.animateCamera(ubicacionCam);
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updateLocalizacion(location);
            setLocation(location);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(), "GPS ACTIVADO", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "GPS DESACTIVADO", Toast.LENGTH_SHORT).show();
        }
    };

    private void setLocation(Location location) {
        if (location != null) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    calle2 = DirCalle.getAddressLine(0);
                    calle.setText(calle2);

                    // Guarda la ubicación en la base de datos
                    guardarUbicacionEnBD(location.getLatitude(), location.getLongitude(), calle2);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void guardarUbicacionEnBD(double lat, double lng, String direccion) {
        SqlLocalizacion dbHelper = new SqlLocalizacion(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("calle", direccion);
        values.put("latitud", lat);
        values.put("longitud", lng);

        long id = db.insert("ubicaciones", null, values);
        if (id != -1) {
            Toast.makeText(this, "Ubicación guardada", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al guardar la ubicación", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }

}
