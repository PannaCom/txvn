package com.quickcar.thuexe.UI;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.quickcar.thuexe.DirectionFinder.DirectionFinder;
import com.quickcar.thuexe.DirectionFinder.DirectionFinderListener;
import com.quickcar.thuexe.DirectionFinder.Route;
import com.quickcar.thuexe.R;
import com.quickcar.thuexe.UI.BookingNowActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by DatNT on 10/7/2016.
 */

public class MapBookingFragment extends Fragment implements OnMapReadyCallback,DirectionFinderListener {
    private GoogleMap mMap;
    private MapView mMapView;
    private double longitude, latitude;
    private ProgressDialog dialog;
    private LatLng latLngFrom, latLngTo;
    private ArrayList<Marker> markerList = new ArrayList<>();
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_booking, container, false);
        SupportMapFragment map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((BookingNowActivity) getActivity()).updateMap(new BookingNowActivity.OnDataMap() {
            @Override
            public void OnDataMap(String location, LatLng latLng) {
                if(location.equals("from"))
                    latLngFrom = latLng;
                else
                    latLngTo = latLng;

                removeAllMarker();
                markLocationToMap();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        //requestToGetListVehicle();
        markLocationToMap();

    }

    private void markLocationToMap() {
        if(latLngFrom !=null) {
            Marker markerFrom = mMap.addMarker(new MarkerOptions().position(latLngFrom).title("Điểm đi"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngFrom, 16));
            originMarkers.add(markerFrom);
        }else {
            if (originMarkers != null)
                for (Marker marker : originMarkers)
                    marker.remove();

            if (polylinePaths != null)
                for (Polyline polyline : polylinePaths)
                    polyline.remove();


        }
        if(latLngTo !=null) {
            Marker markerTo = mMap.addMarker(new MarkerOptions().position(latLngTo).title("Điểm đến"));
            destinationMarkers.add(markerTo);
            if (latLngFrom == null)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngTo, 16));
        }else{
            if (destinationMarkers != null)
                for (Marker marker : destinationMarkers)
                    marker.remove();



            if (polylinePaths != null)
                for (Polyline polyline : polylinePaths)
                    polyline.remove();


        }
        if(latLngTo !=null && latLngFrom != null)
            sendRequest();
    }

    private void sendRequest() {
        try {
            new DirectionFinder(this, latLngFrom, latLngTo).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void removeAllMarker(){
        for (Marker marker : markerList)
            marker.remove();
    }

    @Override
    public void onDirectionFinderStart() {
        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        if (routes.size()==0){
            originMarkers = new ArrayList<>();
            destinationMarkers = new ArrayList<>();
            Marker markerFrom = mMap.addMarker(new MarkerOptions().position(latLngFrom).title("Điểm đi"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngFrom, 16));
            originMarkers.add(markerFrom);

            Marker markerTo = mMap.addMarker(new MarkerOptions().position(latLngTo).title("Điểm đến"));
            destinationMarkers.add(markerTo);

            return;
        }
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            //((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            //((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }
}
