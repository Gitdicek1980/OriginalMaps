package jp.techacademy.yamamoto.daisuke.originalmaps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    // 高尾山
    private double mLatitude = 35.0d + 37.0d / 60 + 56.9d / (60 * 60);
    private double mLongitude = 139.0d + 16.0d / 60 + 11.7d / (60 * 60);

    private View mapView;

    private Toolbar mToolbar;
    private int mGenre = 0;

    private DatabaseReference mDatabaseReference;
    private DatabaseReference mGenreRef;
    private ListView mListView;
    private ArrayList<Course> mCourseArrayList;
    private CourseListAdapter mAdapter;

    private ChildEventListener mEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            HashMap map = (HashMap) dataSnapshot.getValue();
            String title = (String) map.get("title");
            String body = (String) map.get("body");
            String name = (String) map.get("name");
            String uid = (String) map.get("uid");
            String imageString = (String) map.get("image");
            byte[] bytes;
            if (imageString != null) {
                bytes = Base64.decode(imageString, Base64.DEFAULT);
            } else {
                bytes = new byte[0];
            }

            ArrayList<Answer> answerArrayList = new ArrayList<Answer>();
            HashMap answerMap = (HashMap) map.get("answers");
            if (answerMap != null) {
                for (Object key : answerMap.keySet()) {
                    HashMap temp = (HashMap) answerMap.get((String) key);
                    String answerBody = (String) temp.get("body");
                    String answerName = (String) temp.get("name");
                    String answerUid = (String) temp.get("uid");
                    Answer answer = new Answer(answerBody, answerName, answerUid, (String) key);
                    answerArrayList.add(answer);
                }
            }

            Course course = new Course(title, body, name, uid, dataSnapshot.getKey(), mGenre, bytes, answerArrayList);
            mCourseArrayList.add(course);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            HashMap map = (HashMap) dataSnapshot.getValue();

            // 変更があったCourseを探す
            for (Course course: mCourseArrayList) {
                if (dataSnapshot.getKey().equals(course.getCourseUid())) {
                    // このアプリで変更がある可能性があるのは回答(Answer)のみ
                    course.getAnswers().clear();
                    HashMap answerMap = (HashMap) map.get("answers");
                    if (answerMap != null) {
                        for (Object key : answerMap.keySet()) {
                            HashMap temp = (HashMap) answerMap.get((String) key);
                            String answerBody = (String) temp.get("body");
                            String answerName = (String) temp.get("name");
                            String answerUid = (String) temp.get("uid");
                            Answer answer = new Answer(answerBody, answerName, answerUid, (String) key);
                            course.getAnswers().add(answer);
                        }
                    }

                    mAdapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_for_drawer);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ログイン済みのユーザーを取得する
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                // ログインしていなければログイン画面に遷移させる
                if (user == null) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }

            }
        });

        // ナビゲーションドロワーの設定
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar, R.string.app_name, R.string.app_name);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_course) {
                    mToolbar.setTitle("コース");
                    mGenre = 1;
                } else if (id == R.id.nav_plan) {
                    mToolbar.setTitle("プラン");
                    mGenre = 2;
                } else if (id == R.id.nav_mypage) {
                    mToolbar.setTitle("マイページ");
                    mGenre = 3;
                } else if (id == R.id.nav_inquiry) {
                    mToolbar.setTitle("お問合せ");
                    mGenre = 4;
                } else if (id == R.id.nav_logout) {
                    mToolbar.setTitle("ログアウト");
                    mGenre = 5;
                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);

                // --- ここから ---
                // 質問のリストをクリアしてから再度Adapterにセットし、AdapterをListViewにセットし直す
                mCourseArrayList.clear();
                mAdapter.setCourseArrayList(mCourseArrayList);
                mListView.setAdapter(mAdapter);

                // 選択したジャンルにリスナーを登録する
                if (mGenreRef != null) {
                    mGenreRef.removeEventListener(mEventListener);
                }
                mGenreRef = mDatabaseReference.child(Const.ContentsPATH).child(String.valueOf(mGenre));
                mGenreRef.addChildEventListener(mEventListener);
                // --- ここまで追加する ---
                return true;
            }
        });


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }

        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);

        //Topのフローティングボタンが押された時
        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab);
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

                mMap.addMarker(new MarkerOptions().position(new LatLng(36.225833, 140.105)));  // 筑波山 : 1
                mMap.addMarker(new MarkerOptions().position(new LatLng(35.160391, 139.840869)));  // 鋸山 : 2
                mMap.addMarker(new MarkerOptions().position(new LatLng(36.289931, 140.251857)));  // 難台山　: 3


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
            mMap = googleMap;

            //現在地ボタン+右下へ配置
            mMap.setMyLocationEnabled(true);
            if (mapView != null &&
                    mapView.findViewById(Integer.parseInt("1")) != null) {
                // Get the button view
                View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                // and next place it, on bottom right (as Google Maps app)
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                        locationButton.getLayoutParams();
                // position on right bottom
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                layoutParams.setMargins(0, 0, 60, 220);
            }


        //コンパス
        mMap.getUiSettings().setCompassEnabled(true);
        //ズームインアウトボタン
        mMap.getUiSettings().setZoomControlsEnabled(true);


        //LocationManagerの取得
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //GPSから現在地の情報を取得
        Location myLocate = locationManager.getLastKnownLocation("gps");
        if (myLocate != null) {
            //現在地情報取得成功
            //緯度の取得
            int latitude = (int) (myLocate.getLatitude() * 1e6);
            //経度の取得
            int longitude = (int) (myLocate.getLongitude() * 1e6);
            //GeoPointに緯度・経度を指定
        } else {
            //現在地情報取得失敗時の処理
            Toast.makeText(this, "現在地取得できませんでした！", Toast.LENGTH_SHORT).show();
        }

    }

}


