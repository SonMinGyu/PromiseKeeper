package org.application.promisekeeper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.application.promisekeeper.Model.LocationDataModel;
import org.application.promisekeeper.Model.MarkerItem;
import org.application.promisekeeper.Model.PromiseModel;
import org.application.promisekeeper.Model.UserModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.application.promisekeeper.PromiseOfTheDayActivity.staticPromiseModel;


public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    Marker selectedMarker;
    View marker_root_view;
    TextView tv_marker;

    private GoogleMap mMap;
    private Marker currentMarker = null;
    private Marker currentMarker2 = null;
    private Marker currentMarker3 = null;

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 5000;  // 5초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 1000; // 1초

    // onRequestPermissionsResult에서 수신된 결과에서 ActivityCompat.requestPermissions를 사용한 퍼미션 요청을 구별하기 위해 사용됩니다.
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;

    // 앱을 실행하기 위해 필요한 퍼미션을 정의합니다.
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소

    Location mCurrentLocatiion;
    LatLng currentPosition;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;

    private View mLayout;  // Snackbar 사용하기 위해서는 View가 필요합니다.
    // (참고로 Toast에서는 Context가 필요했습니다.)

    public static final List<UserModel> userModels = bringUserName();
    int moveCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        mLayout = findViewById(R.id.main_mainLayout);

        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);


        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.main_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG, "onMapReady :");

        setCustomMarkerView();

        mMap = googleMap;

        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        setDefaultLocation();
        setfriendsLocation();

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)

            startLocationUpdates(); // 3. 위치 업데이트 시작
        }
        else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                        ActivityCompat.requestPermissions( MainActivity.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();

            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions( this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }
        }

        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                Log.d( TAG, "onMapClick :");
            }
        });
    }

    // 나의 현재 경도와 위도를 가져온다
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);
                //location = locationList.get(0);

                // 현재 내위치를 firebase로 업데이트
                /*
                final LocationDataModel locationDataModel = new LocationDataModel();
                locationDataModel.setFriendUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                locationDataModel.setFriendLatitude(location.getLatitude());
                locationDataModel.setFriendLongitude(location.getLongitude());


                FirebaseDatabase.getInstance().getReference().child("users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("latestLocation").setValue(locationDataModel);
                 */

                Map<String, Object> taskMap = new HashMap<String, Object>();
                taskMap.put("userLatitude", location.getLatitude());
                taskMap.put("userLongitude", location.getLongitude());
                FirebaseDatabase.getInstance().getReference().child("users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(taskMap);


                /*
                FirebaseDatabase.getInstance().getReference().child("friendsLocation")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(locationDataModel);
                 */

                /*
                FirebaseDatabase.getInstance().getReference().child("users")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot item : dataSnapshot.getChildren()) {
                                    for (int i = 0; i < item.getValue(PromiseModel.class).getMemberUids().size(); i++) {
                                        if (item.getValue(PromiseModel.class).getMemberUids().get(i).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                            if (getIntent().getExtras().getString("promiseDate").equals(item.getValue(PromiseModel.class).getPromiseDate())
                                                    && getIntent().getExtras().getString("promiseTitle").equals(item.getValue(PromiseModel.class).getPromiseTitle())) {


                                                //System.out.println("findPlaceFragment uids" + item.getValue(PromiseModel.class).getMemberUids().get(0));
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                 */


                currentPosition
                        = new LatLng(location.getLatitude(), location.getLongitude());

                String markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                        + " 경도:" + String.valueOf(location.getLongitude());

                Log.d(TAG, "onLocationResult : " + markerSnippet);


                //현재 위치에 마커 생성하고 이동
                setCurrentLocation(location, markerTitle, markerSnippet);

                mCurrentLocatiion = location;
            }
        }

    };

    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {

            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else {

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);

            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED   ) {

                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;
            }

            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");

            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if (checkPermission())
                mMap.setMyLocationEnabled(true);

        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        if (checkPermission()) {

            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            if (mMap!=null)
                mMap.setMyLocationEnabled(true);

        }
    }

    @Override
    protected void onStop() {

        super.onStop();

        if (mFusedLocationClient != null) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    // latlng를 받아와서 위도와 경도를 파악, 주소를 가져오는 함수
    public String getCurrentAddress(LatLng latlng) {
        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // 함수 인자로 받은 location의 위치에 마크 생성하고 카메라 움직이는 함수
    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {
        String name = null;

        for(int i = 0; i < userModels.size(); i++) {
            if (userModels.get(i).getUserUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
            {
                name = userModels.get(i).getUserName();
            }
        }

        //if (currentMarker != null) currentMarker.remove();

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        /*
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        //markerOptions.title(markerTitle);
        markerOptions.title(name);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);

        currentMarker = mMap.addMarker(markerOptions);

         */

        if(moveCount <= 0)
        {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
            mMap.moveCamera(cameraUpdate);
            moveCount = 1;
        }
        //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        //mMap.moveCamera(cameraUpdate);
    }

    public void setDefaultLocation() {
        /*
        bringUserName();

        String name = null;

        for(int i = 0; i < userModels.size(); i++) {
            if (userModels.get(i).getUserUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
            {
                name = userModels.get(i).getUserName();
            }
        }

         */

        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.551941, 126.991767);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 여부 확인하세요";

        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        //markerOptions.title(name);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mMap.addMarker(markerOptions);

        // 현재위치로 카메라 이동
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 13);
        mMap.moveCamera(cameraUpdate);
    }

    public void setfriendsLocation() {
        // 현재 두명일때만 돌아가도록 함. 추후 약속(방) 인원에 따라 동적으로 바뀌도록 수정하자.
        final List<UserModel> mainUserModels = new ArrayList<>();
        final List<LocationDataModel> allLocationDataModels = new ArrayList<>();
        final List<LocationDataModel> locationDataModels = new ArrayList<>();
        //final List<PromiseModel> promiseModels = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mainUserModels.clear();
                        allLocationDataModels.clear();
                        locationDataModels.clear();
                        for (DataSnapshot item : dataSnapshot.getChildren()) {
                            LocationDataModel locationDataModel = new LocationDataModel();
                            locationDataModel.setUid(item.getValue(UserModel.class).getUserUid());
                            locationDataModel.setLatitude(item.getValue(UserModel.class).getUserLatitude());
                            locationDataModel.setLongitude(item.getValue(UserModel.class).getUserLongitude());

                            allLocationDataModels.add(locationDataModel);
                            //System.out.println("mainmainmain useruid " + item.getValue(UserModel.class).getUserUid());
                            //System.out.println("mainmainmain MainActivity " + item.getValue(UserModel.class).getLocationDataModel());
                            /*
                            for (int i = 0; i < item.getValue(PromiseModel.class).getMemberUids().size(); i++) {
                                if (item.getValue(PromiseModel.class).getMemberUids().get(i).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    if (getIntent().getExtras().getString("promiseDate").equals(item.getValue(PromiseModel.class).getPromiseDate())
                                            && getIntent().getExtras().getString("promiseTitle").equals(item.getValue(PromiseModel.class).getPromiseTitle())) {
                                        //promiseModels.add(item.getValue(PromiseModel.class));
                                        //System.out.println("findPlaceFragment uids" + item.getValue(PromiseModel.class).getMemberUids().get(0));
                                    }
                                }
                            }
                             */
                        }

                        //System.out.println("mainmainmain mainUserModel" + mainUserModels.get(0).getLocationDataModel().getFriendUid());

                        for(int i = 0; i < staticPromiseModel.get(0).getMemberUids().size(); i++)
                            // staticPromiseModel의 0번째에 현재 약속의 정보 들어있음
                        {
                            for(int j = 0; j < allLocationDataModels.size(); j++) {
                                if (staticPromiseModel.get(0).getMemberUids().get(i).equals(allLocationDataModels.get(j).getUid()))
                                        //&& !staticPromiseModel.get(0).getMemberUids().get(i).equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                {
                                    locationDataModels.add(allLocationDataModels.get(j));
                                    // allLocationDataModels에는 모든 유저들 최근 위치 들어있음
                                    // locationDateModels에 약속에 참여한 유저들 최근 위치 들어있음
                                }
                            }
                        }

                        System.out.println("mainmainmain locationModel " + locationDataModels.get(0).getUid());
                        System.out.println("mainmainmain locationModel " + locationDataModels.get(0).getLatitude());
                        System.out.println("mainmainmain locationModel " + locationDataModels.get(0).getLongitude());
                        System.out.println("mainmainmain locationModel " + locationDataModels.size());


                        if(locationDataModels.size() != 0)
                        {
                            mMap.clear();
                            setPromiseLocation();

                            // custom 마커
                            setCustomMarkerView();
                            getSampleMarkerItems(locationDataModels);

                            /* /// 기본 마커
                            Marker[] friendMarker = new Marker[locationDataModels.size()];
                            for(int i = 0; i < locationDataModels.size(); i++)
                            {
                                friendMarker[i] = null;
                            }

                            for(int i = 0; i < locationDataModels.size(); i++) {
                                //Marker NewMarker = null;
                                String name = null;
                                for (int j = 0; j < userModels.size(); j++) {
                                    if (userModels.get(j).getUserUid().equals(locationDataModels.get(i).getUid())) {
                                        name = userModels.get(j).getUserName();
                                    }
                                }

                                LatLng DEFAULT_LOCATION = new LatLng(locationDataModels.get(i).getLatitude(), locationDataModels.get(i).getLongitude());
                                String markerTitle = getCurrentAddress(DEFAULT_LOCATION);
                                String markerSnippet = "위도:" + String.valueOf(DEFAULT_LOCATION.latitude)
                                        + " 경도:" + String.valueOf(DEFAULT_LOCATION.longitude);
                                //String markerTitle = "위치정보 가져올 수 없음";
                                //String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";

                                //if (currentMarker2 != null) currentMarker2.remove();
                                if (friendMarker[i] != null) friendMarker[i].remove();

                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(DEFAULT_LOCATION);
                                //markerOptions.title(markerTitle);
                                markerOptions.title(name);
                                markerOptions.snippet(markerSnippet);
                                markerOptions.draggable(true);
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                friendMarker[i] = mMap.addMarker(markerOptions);
                            }

                             */
                        }
                        else
                        {
                            //디폴트 위치, 중앙대학교
                            LatLng DEFAULT_LOCATION = new LatLng(37.505092, 126.957101);
                            String markerTitle = getCurrentAddress(DEFAULT_LOCATION);
                            String markerSnippet = "위도:" + String.valueOf(DEFAULT_LOCATION.latitude)
                                    + " 경도:" + String.valueOf(DEFAULT_LOCATION.longitude);
                            //String markerTitle = "위치정보 가져올 수 없음";
                            //String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";

                            if (currentMarker2 != null) currentMarker2.remove();

                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(DEFAULT_LOCATION);
                            markerOptions.title(markerTitle);
                            markerOptions.snippet(markerSnippet);
                            markerOptions.draggable(true);
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                            currentMarker2 = mMap.addMarker(markerOptions);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        /*
        FirebaseDatabase.getInstance().getReference().child("promise")
                .child("friendsLocation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                locationDataModels.clear();
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    if(FirebaseAuth.getInstance().getCurrentUser().getUid() != item.getValue(LocationDataModel.class).getFriendUid())
                    {
                        locationDataModels.add(item.getValue(LocationDataModel.class));
                    }
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        */

        /*
        LatLng DEFAULT_LOCATION = new LatLng(37.505092, 126.957101);
        String markerTitle = getCurrentAddress(DEFAULT_LOCATION);
        String markerSnippet = "위도:" + String.valueOf(DEFAULT_LOCATION.latitude)
                + " 경도:" + String.valueOf(DEFAULT_LOCATION.longitude);
        //String markerTitle = "위치정보 가져올 수 없음";
        //String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";

        if (currentMarker2 != null) currentMarker2.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker2 = mMap2.addMarker(markerOptions);

         */

        //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 13);
        //mMap.moveCamera(cameraUpdate);
    }

    private void setPromiseLocation()
    {
        if(staticPromiseModel.get(0).getPromisePlace().equals("아직 약속 장소를 정하지 않았습니다!"))
        {

        }
        else {
            LatLng DEFAULT_LOCATION = new LatLng(staticPromiseModel.get(0).getPromisePlaceLatitude(), staticPromiseModel.get(0).getPromisePlaceLongitude());
            String markerTitle = getCurrentAddress(DEFAULT_LOCATION);
            String markerSnippet = "위도:" + String.valueOf(DEFAULT_LOCATION.latitude)
                    + " 경도:" + String.valueOf(DEFAULT_LOCATION.longitude);
            //String markerTitle = "위치정보 가져올 수 없음";
            //String markerSnippet = "위치 퍼미션과 GPS 활성 여부 확인하세요";

            if (currentMarker3 != null) currentMarker3.remove();

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(DEFAULT_LOCATION);
            markerOptions.title(markerTitle);
            markerOptions.snippet(markerSnippet);
            markerOptions.draggable(true);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            currentMarker3 = mMap.addMarker(markerOptions);
        }
    }

    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
            return true;
        }

        return false;
    }

    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if ( check_result ) {

                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.
                startLocationUpdates();
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {


                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();

                }else {


                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();
                }
            }

        }
    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d(TAG, "onActivityResult : GPS 활성화 되있음");


                        needRequest = true;

                        return;
                    }
                }

                break;
        }
    }

    public static ArrayList<UserModel> bringUserName()
    {
        final ArrayList<UserModel> user = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user.clear();
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    user.add(item.getValue(UserModel.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return user;
    }

    //////////////////////////////////// custom marker를 위한 함수들
    private void setCustomMarkerView() {

        marker_root_view = LayoutInflater.from(this).inflate(R.layout.marker_layout, null);
        tv_marker = (TextView) marker_root_view.findViewById(R.id.tv_marker);
    }


    private void getSampleMarkerItems(List<LocationDataModel> locationDataModels) {



        ArrayList<MarkerItem> sampleList = new ArrayList();

        for(int i = 0; i < locationDataModels.size(); i++) {
            String name = null;

            for (int j = 0; j < userModels.size(); j++) {
                if (userModels.get(j).getUserUid().equals(locationDataModels.get(i).getUid())) {
                    name = userModels.get(j).getUserName();
                }
            }
            sampleList.add(new MarkerItem(locationDataModels.get(i).getLatitude(), locationDataModels.get(i).getLongitude(), name));
        }

        for (MarkerItem markerItem : sampleList) {
            addMarker(markerItem, false);
        }

    }

    private Marker addMarker(MarkerItem markerItem, boolean isSelectedMarker) {


        LatLng position = new LatLng(markerItem.getLat(), markerItem.getLon());
        String formatted = markerItem.getName();

        tv_marker.setText(formatted);

        tv_marker.setBackgroundResource(R.drawable.marker2);
        tv_marker.setTextColor(Color.BLACK);

        MarkerOptions markerOptions = new MarkerOptions();
        String markerSnippet = "위도:" + String.valueOf(position.latitude)
                + " 경도:" + String.valueOf(position.longitude);
        markerOptions.title(formatted);
        markerOptions.position(position);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker_root_view)));


        return mMap.addMarker(markerOptions);

    }




    // View를 Bitmap으로 변환
    private Bitmap createDrawableFromView(Context context, View view) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }


    private Marker addMarker(Marker marker, boolean isSelectedMarker) {
        double lat = marker.getPosition().latitude;
        double lon = marker.getPosition().longitude;
        String name = marker.getTitle();
        MarkerItem temp = new MarkerItem(lat, lon, name);
        return addMarker(temp, isSelectedMarker);

    }

    private void changeSelectedMarker(Marker marker) {
        // 선택했던 마커 되돌리기
        if (selectedMarker != null) {
            addMarker(selectedMarker, false);
            selectedMarker.remove();
        }

        // 선택한 마커 표시
        if (marker != null) {
            selectedMarker = addMarker(marker, true);
            marker.remove();
        }


    }
}