package com.xomena.so45856063;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

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
        LatLng pos = new LatLng(33.748589, -84.390392);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 7));

        int radius = 40 * 1000; //radius in meters
        drawHorizontalHexagonGrid(pos, radius,6);

        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private void drawHorizontalHexagonGrid(LatLng startPosition, int radius, int count){
        LatLng curPos = startPosition;
        double width = radius * 2 * Math.sqrt(3)/2 ;
        for(int i = 0; i < count; i++) {
            drawHorizontalHexagon(curPos, radius, "" + (i+1));
            curPos = SphericalUtil.computeOffset(curPos, width,90);
        }
    }

    private void drawHorizontalHexagon(LatLng position, int radius, String label){
        List<LatLng> coordinates = new ArrayList<>();
        for(int angle = 0; angle < 360; angle += 60) {
            coordinates.add(SphericalUtil.computeOffset(position, radius, angle));
        }

        PolygonOptions opts = new PolygonOptions().addAll(coordinates)
                .fillColor(Color.argb(35,255, 0,0))
                .strokeColor(Color.RED).strokeWidth(3);

        mMap.addPolygon(opts);

        this.showText(position, label);
    }

    private void showText(LatLng pos, String label) {
        mMap.addGroundOverlay(new GroundOverlayOptions().position(pos, 10000)
                .image(
                    BitmapDescriptorFactory.fromBitmap(
                        getBitmapFromView(label)
                    )
                )
                .zIndex(1000)
                .transparency(0)
                .visible(true)
        );
    }

    private Bitmap getBitmapFromView(String label) {
        Bitmap myRefBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.transparent);

        Bitmap myWrittenBitmap = Bitmap.createBitmap(myRefBitmap.getWidth(),
                myRefBitmap.getHeight(), Bitmap.Config.ARGB_4444);

        float scale = getResources().getDisplayMetrics().density;

        Canvas canvas = new Canvas(myWrittenBitmap);
        Paint txtPaint = new Paint();
        txtPaint.setColor(Color.BLUE);
        txtPaint.setTextSize(48*scale);
        txtPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setTypeface(Typeface.DEFAULT_BOLD);

        //draw ref bitmap then text on our canvas
        canvas.drawBitmap(myRefBitmap, 0, 0, null);
        canvas.drawText(label, 5, myRefBitmap.getHeight(), txtPaint);

        return myWrittenBitmap;
    }


}
