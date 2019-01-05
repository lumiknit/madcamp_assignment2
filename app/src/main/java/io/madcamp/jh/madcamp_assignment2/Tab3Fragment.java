package io.madcamp.jh.madcamp_assignment2;

import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Tab3Fragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    private Context context;
    private View top;

    private MapView mapView;
    private GoogleMap map;

    public ArrayList<Image> imageList = null;

    public static Tab3Fragment newInstance(int page, ArrayList<Image> imageList) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        Tab3Fragment fragment = new Tab3Fragment();
        fragment.setArguments(args);
        fragment.imageList = imageList;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        top = inflater.inflate(R.layout.fragment_tab3, container, false);
        this.context = top.getContext();

        mapView = top.findViewById(R.id.map);

        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                if(imageList == null) Log.d("Test@ListLn", "null");
                else Log.d("Test@ListLn", "" + imageList.size());

                Tab3Fragment.this.map = googleMap;
                Log.d("Test@GoogleMap", "" + (googleMap == null));

                updateMarkers();

                googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(getActivity()));

                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        TabLayout tabLayout = (TabLayout)getActivity().findViewById(R.id.sliding_tabs);
                        tabLayout.getTabAt(1).select();
                        Toast.makeText(getActivity(), "" + marker.getTag(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return top;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        Log.d("Test@TryMapView", "getView(): " + (getView() == null));
        Log.d("Test@TryMapView", "findViewById(map): " + (getView().findViewById(R.id.map) == null));
        updateMarkers();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


    public void updateMarkers() {
        if(mapView == null) {
            mapView = getView().findViewById(R.id.map);
            //Log.d("Test@updateMarkers", "MapView was not loaded");
            //return;
        }
        if(imageList == null) {
            Log.d("Test@updateMarkers", "List was not loaded");
            return;
        }

        final ArrayList<Image> list = imageList;

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                BitmapDescriptor desc = getMarkerIconFromDrawable(
                        getResources().getDrawable(R.drawable.ic_baseline_camera_alt));

                googleMap.clear();

                LatLng first = null;

                for(Image img : imageList) {
                    if(img != null && img.latLng != null) {
                        if(first == null) first = img.latLng;
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(img.latLng)
                                .title(img.name)
                                .snippet("스니펫")
                                .icon(desc)
                                .alpha(0.8f);
                        googleMap.addMarker(markerOptions).setTag(new CustomInfoWindowAdapter.Tag(img));
                    }
                }
                if(first != null) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(first));
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                }
            }
        });
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicWidth());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
