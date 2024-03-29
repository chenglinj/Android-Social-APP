package com.example.mobileapp;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.example.mobileapp.models.User;
import com.example.mobileapp.models.UserData;
import com.example.mobileapp.models.UserState;
import com.example.mobileapp.utilities.ApiHelper;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.util.Log;

import static com.example.mobileapp.models.UserData.updateFriendStates;
//import com.google.android.gms.location.LocationListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    /**This is our main activity, shows friends location,
     * and update friends information and update marker location in each 2 seconds
     * the default location is melbourne -37.814, 144.96332
     */
    public static MapsActivity instance;
    private HashMap<String, Marker> markers = new HashMap<>();
    private GoogleMap mMap;
    public static final LatLng DEFAULT_LOCATION = new LatLng(-37.814, 144.96332);
    public User currentUser;
    public UserState currentState;
    private Circle circle;
    private LocationManager locationManager;
    public static Timer updateTimer;
    private String sessionkey;
    private boolean showingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        updateTimer = new Timer();
        instance = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent previous_intent = getIntent();
        sessionkey = previous_intent.getStringExtra("sessionkey");
        UserData.initSessionKey(null, sessionkey);
        UserData.getInstance().updateSelfProfile(null);
        currentUser = UserData.getInstance().getCurrentUser();
        currentState = new UserState(currentUser.loginId, -37.814, 144.96332,0, true);
        updateLastLocation();
        UserData.getInstance().setCurrentUserState(currentState);
        updateThread();
        // update data
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                UserData.getInstance().updateSelfProfile(null);
                UserData.getInstance().updateFriends(null);
                UserData.getInstance().updateFriendStates(null);
                UserData.getInstance().updateMessages(null);
                UserData.getInstance().updateRequest(null);
                updateLastLocation();
                UserData.updateSelfState(null,currentState);
                Log.d("map:","tick");
            }
        },0,2000);
        showingText = false;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        updateLastLocation();
        mMap = googleMap;
        // Google map ui settings
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setMyLocationButtonEnabled(false);
        // check for permission
        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            updateLastLocation();
            mMap.setMyLocationEnabled(true);
        } else {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0);
        }
        // Set Friend Markers
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                for (String key: markers.keySet()){
                    if(marker.getTitle().equals(markers.get(key).getTitle())){
                        User friend = UserData.getInstance().getFriend(key);
                        Intent intent = new Intent(MapsActivity.this, FriendProfileActivity.class);
                        intent.putExtra("selected_friend", friend);
                        startActivity(intent);
                    }
                }
            }
        });
        updateMarkers();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentState.lat, currentState.lng),12));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (permissions.length == 1 &&
                    permissions[0].equals("android.permission.ACCESS_FINE_LOCATION") &&
                    grantResults[0] == 0) {     // PackageManager.PERMISSION_GRANTED
                mMap.setMyLocationEnabled(true);
            } else {
                // Permission was denied. Display an error message.
                Log.i("","Permission is not granted");
            }
        }
    }

    public void onClick_btnMyLoc(android.view.View v) {
        updateLastLocation();
        if (currentState!=null){
            mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(currentState.lat, currentState.lng)));
        }
    }

    public void onClick_btnList(android.view.View v) {
        Intent intent = new Intent(MapsActivity.this, FriendListActivity.class);
        intent.putExtra("sessionkey", sessionkey);
        startActivityForResult(intent, 999);
    }

    public void onClick_btnMe(android.view.View v) {
        Intent intent = new Intent(MapsActivity.this, MyProfileActivity.class);
        intent.putExtra("sessionkey", sessionkey);
        intent.putExtra("currentState", currentState);
        startActivityForResult(intent, 111);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 111) {
            if (resultCode == 222) {
                finish();
            }
        }
        else if (requestCode == 999) {
            if (resultCode == 888) {
                double friend_lat = data.getDoubleExtra("lat", 0.0);
                double friend_lng = data.getDoubleExtra("lng", 0.0);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(friend_lat, friend_lng)));
            }
        }
    }

    private void updateLastLocation(){

        if (locationManager!=null && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // LocationServices
            Task<Location> locTask = LocationServices.getFusedLocationProviderClient(this).getLastLocation();
            locTask.addOnCompleteListener((Task<Location> t)->{
                Location loc = t.getResult();
                currentState.lat = loc.getLatitude();
                currentState.lng = loc.getLongitude();
            });


            // location Providers
            List<String> providers = locationManager.getProviders(true);
            Location loc = null;
            for(String provider: providers){
                loc = loc == null? locationManager.getLastKnownLocation(provider) :loc;
            }
            if (loc !=null)
            {
                currentState.lat = loc.getLatitude();
                currentState.lng =loc.getLongitude();
                ApiHelper.updateState(sessionkey, currentState.lat, currentState.lng, currentState.state);
                return;
            }
            else
            {

                return;
            }
        } else {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0);

        }
    }

    // let a marker beat
    static void beatMarker(final Marker marker, final BitmapDescriptor bigIco, final BitmapDescriptor smallIco){
        final Handler handler = new Handler();
        final boolean[] toBig = {true};
        handler.post(new Runnable() {
            long elapsed;
            @Override
            public void run() {
                if(toBig[0]){
                    marker.setIcon(bigIco);
                }else{
                    marker.setIcon(smallIco);
                }
                toBig[0] = !toBig[0];
                handler.postDelayed(this, 200);
            }
        });
    }

    private void updateThread() {
        UserData.getInstance().updateFriends(null);
        UserData.getInstance().updateFriendStates(null);
        UserData.getInstance().updateMessages(null);
        UserData.getInstance().updateRequest(null);
    }

    private void updateMarkers(){
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            long elapsed;
            @Override
            public void run() {
                Collection<User> users = UserData.getInstance().getFriends();
                for (User usr : users){
                    if(markers.containsKey(usr.loginId)){
                        updateUserMarker(markers.get(usr.loginId), UserData.getInstance().getState(usr.loginId));
                    }
                    else{
                        markers.put(usr.loginId,getUserMarker(usr,UserData.getInstance().getState(usr.loginId)));
                    }
                }
                handler.postDelayed(this, 500);
            }
        });
    }

    private Marker getUserMarker(User user, UserState userState){

        Bitmap bmp= BitmapFactory.decodeResource(getResources(), user.avatar);
        BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(user.avatar);
        Matrix matrix = new Matrix();
        matrix.postScale(.95f, .95f);
        Bitmap bmpB = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),bmp.getHeight(),matrix, true);
        BitmapDescriptor bdb = BitmapDescriptorFactory.fromBitmap(bmpB);
        Marker marker =  mMap.addMarker(new MarkerOptions()
                .title(user.name)
                .icon(bd)
                .position(new LatLng(userState.lat, userState.lng)));
        beatMarker(marker, bd, bdb);
        return marker;
    }

    private void updateUserMarker(Marker marker, UserState userState){
        marker.setPosition(new LatLng(userState.lat, userState.lng));
    }
}
