package com.example.hanbin.googlemaps;

import android.animation.Animator;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraMoveListener, ClusterManager.OnClusterClickListener<Home>, ClusterManager.OnClusterInfoWindowClickListener<Home>, ClusterManager.OnClusterItemClickListener<Home>, ClusterManager.OnClusterItemInfoWindowClickListener<Home>{

    private GoogleMap mMap;
    private static final String TAG = MapsActivity.class.getName();
    private float zoomLevel;
    private CameraPosition mCameraPosition;
    LatLng sydney = new LatLng(-29, 151);
    LatLng seoul = new LatLng(37.52487, 126.92723);
    double seoulLat = 37.52487;
    double seoulLng = 126.92723;
    MarkerOptions seoulMarkerOption = new MarkerOptions().position(seoul).title("Marker in Seoul");
    Circle sydneyCircle;
    private Marker sydneyMarker;
    LatLng toLocation = new LatLng(-35, 151);
    private static Animator animator;
    private ClusterManager<Home> mClusterManager;
    private Random mRandom = new Random(1984);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        Log.e(TAG, "onMapReady: "+zoomLevel );
        sydneyCircle = mMap.addCircle(new CircleOptions().center(new LatLng(-35, 151))
                                                            .radius(1000000).fillColor(Color.BLUE));
//        mMap.addCircle(new CircleOptions().center(new LatLng(-33.87365, 151.20689))
//                .radius(1000000).fillColor(Color.BLUE));
        Marker seoulMarker = mMap.addMarker(seoulMarkerOption);
        mClusterManager = new ClusterManager<Home>(this, mMap);
        mClusterManager.setRenderer(new HomeRenderer());
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);
        mMap.setOnCameraIdleListener(mClusterManager);

        for (int i = 0; i<100; i++){
            double let = seoulLat + (i/200d);
            double lng = seoulLng + (i/200d);
            mClusterManager.addItem(new Home(new LatLng(let, lng), "House"+i, R.drawable.ic_card_giftcard_cyan_400_24dp));
        }
        addItems();
        mClusterManager.cluster();
        sydneyMarker= mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_card_giftcard_cyan_400_24dp)));
        mMap.setOnMarkerClickListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.e(TAG, "onMarkerClick: "+zoomLevel );
        return false;
    }

    @Override
    public void onCameraMove() {
        zoomLevel = mMap.getCameraPosition().zoom;

        if (zoomLevel > 3){
            sydneyCircle.setVisible(false);
            sydneyMarker.setVisible(true);
        }else {
            sydneyCircle.setVisible(true);
            sydneyMarker.setVisible(false);
        }

        Log.e(TAG, "onCameraMove: "+mMap.getCameraPosition().zoom);

    }



    private class HomeRenderer extends DefaultClusterRenderer<Home>{

        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());
        private final ImageView mImageView;
        private final ImageView mClusterImageView;
        private final int mDimension;

        public HomeRenderer() {
            super(MapsActivity.this, mMap, mClusterManager);

            View multiProfile = getLayoutInflater().inflate(R.layout.multi_profile, null);
            mClusterIconGenerator.setContentView(multiProfile);
            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);


            mImageView = new ImageView(getApplicationContext());
            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
            mImageView.setPadding(padding, padding, padding, padding);
            mIconGenerator.setContentView(mImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(Home home, MarkerOptions markerOptions) {
            // Draw a single person.
            // Set the info window to show their name.
            mImageView.setImageResource(home.profilePhoto);
            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(home.name);
        }

        /**
         * Cluster group image setting
         */
//        @Override
//        protected void onBeforeClusterRendered(Cluster<Home> cluster, MarkerOptions markerOptions) {
//            // Draw multiple people.
//            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
//            List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
//            int width = mDimension;
////            int height = mDimension;
////
////            for (Home p : cluster.getItems()) {
////                // Draw 4 at most.
////                if (profilePhotos.size() == 4) break;
////                Drawable drawable = getResources().getDrawable(p.profilePhoto);
////                drawable.setBounds(0, 0, width, height);
////                profilePhotos.add(drawable);
////            }
////            MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
////            multiDrawable.setBounds(0, 0, width, height);
//
////            mClusterImageView.setImageDrawable(multiDrawable);
//            mClusterImageView.setImageResource(R.drawable.ic_launcher);
//            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
//            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
//        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }

    private void addItems() {
        // http://www.flickr.com/photos/sdasmarchives/5036248203/
        mClusterManager.addItem(new Home(position(), "Walter", R.drawable.walter));

        // http://www.flickr.com/photos/usnationalarchives/4726917149/
        mClusterManager.addItem(new Home(position(), "Gran", R.drawable.gran));

        // http://www.flickr.com/photos/nypl/3111525394/
        mClusterManager.addItem(new Home(position(), "Ruth", R.drawable.ruth));

        // http://www.flickr.com/photos/smithsonian/2887433330/
        mClusterManager.addItem(new Home(position(), "Stefan", R.drawable.stefan));

        // http://www.flickr.com/photos/library_of_congress/2179915182/
        mClusterManager.addItem(new Home(position(), "Mechanic", R.drawable.mechanic));

        // http://www.flickr.com/photos/nationalmediamuseum/7893552556/
        mClusterManager.addItem(new Home(position(), "Yeats", R.drawable.yeats));

        // http://www.flickr.com/photos/sdasmarchives/5036231225/
        mClusterManager.addItem(new Home(position(), "John", R.drawable.john));

        // http://www.flickr.com/photos/anmm_thecommons/7694202096/
        mClusterManager.addItem(new Home(position(), "Trevor the Turtle", R.drawable.turtle));

        // http://www.flickr.com/photos/usnationalarchives/4726892651/
        mClusterManager.addItem(new Home(position(), "Teach", R.drawable.teacher));
    }
    private LatLng position() {
        return new LatLng(random(51.6723432, 51.38494009999999), random(0.148271, -0.3514683));
    }

    private double random(double min, double max) {
        return mRandom.nextDouble() * (max - min) + min;
    }

    @Override
    public boolean onClusterClick(Cluster<Home> cluster) {
        return false;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<Home> cluster) {

    }

    @Override
    public boolean onClusterItemClick(Home home) {
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(Home home) {

    }


}
