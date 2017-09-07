package com.example.hanbin.googlemaps;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by hanbin on 2017. 9. 4..
 */

public class Home implements ClusterItem {

    public final String name;
    public final int profilePhoto;
    private final LatLng mPosition;

    public Home(LatLng position, String name, int profilePhoto) {
        this.name = name;
        this.profilePhoto = profilePhoto;
        mPosition = position;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getSnippet() {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }
}
