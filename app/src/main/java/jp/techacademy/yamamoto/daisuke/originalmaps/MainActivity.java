package jp.techacademy.yamamoto.daisuke.originalmaps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    // 六甲山：北緯34度46分41秒, 東経135度15分49秒}
    private double mLatitude = 34.0d + 46.0d/60 + 41.0d/(60*60);
    private double mLongitude = 135.0d + 15.0d/60 + 49.0d/(60*60);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }



        //Topのフローティングボタンが押された時
        FloatingActionButton fab=(FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                LatLng location = new LatLng(mLatitude, mLongitude);
                CameraPosition cameraPos = new CameraPosition.Builder()
                        .target(location).zoom(10.0f)
                        .bearing(0).build();

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos));

                // マーカー設定
                MarkerOptions options = new MarkerOptions();
                options.position(location);
                mMap.addMarker(options);


            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        //現在地ボタン
        mMap.setMyLocationEnabled(true);
        //コンパス
        mMap.getUiSettings().setCompassEnabled(true);
        //ズームインアウトボタン
        mMap.getUiSettings().setZoomControlsEnabled(false);

    }

}

