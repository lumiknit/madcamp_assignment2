package io.madcamp.jh.madcamp_assignment2;

import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
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
    private TabPagerAdapter.SharedData shared;

    private MapView mapView;
    private GoogleMap map;

    public ArrayList<Image> imageList = null;

    public static Tab3Fragment newInstance(int page, TabPagerAdapter.SharedData shared) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        Tab3Fragment fragment = new Tab3Fragment();
        fragment.setArguments(args);
        fragment.shared = shared;
        fragment.imageList = shared.imageList;
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

                googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(36.370901, 127.362565)));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

                updateMarkers();

                googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(getActivity()));

                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        TabLayout tabLayout = (TabLayout)getActivity().findViewById(R.id.sliding_tabs);
                        tabLayout.getTabAt(1).select();
                        shared.clickedInfoWindow = imageList.indexOf(marker.getTag());
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
        Log.d("Test@updateMarkers", "called");
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
        shared.clickedInfoWindow = -1;

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.clear();

                int n = 0;
                LatLng center = null;

                for(Image img : imageList) {
                    if(img != null && img.latLng != null) {
                        final LatLng ll = img.latLng;
                        if(center == null) {
                            center = new LatLng(ll.latitude, ll.longitude);
                            n = 1;
                        } else if(approxDistSq(center, ll) < 0.0002) {
                            center = new LatLng(
                                    center.latitude + ll.latitude,
                                    center.longitude + ll.longitude);
                            n += 1;
                        }

                        float like_p = Math.min(10, img.like) / 10.f;

                        BitmapDescriptor desc = getMarkerIconFromDrawable(
                                getResources().getDrawable(R.drawable.ic_animal_paw_print), like_p);
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(img.latLng)
                                .icon(desc)
                                .alpha(0.5f + like_p * 0.3f);
                        googleMap.addMarker(markerOptions).setTag(new CustomInfoWindowAdapter.Tag(img));
                    }
                }
                if(center != null) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(center.latitude / n, center.longitude / n)));
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                }
            }
        });
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable, float like_p) {
        float s = 1 + like_p * 0.5f;
        int ow = drawable.getIntrinsicWidth();
        int oh = drawable.getIntrinsicHeight();
        int w = (int)(ow * s);
        int h = (int)(oh * s);
        int tw = w + w / 2;
        int th = h + h / 2;
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(tw, th, Bitmap.Config.ARGB_8888);

        canvas.setBitmap(bitmap);

        int color = Color.HSVToColor(new float[]{259.f, 0.76f - 0.5f * like_p, 0.5f - 0.2f * like_p});

        drawable.setBounds(0, 0, ow, oh);
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        canvas.translate(w / 4, h / 4);
        canvas.rotate((float)Math.random() * 360.f, w / 2, h / 2);
        canvas.scale(s, s);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private double approxDistSq(LatLng a, LatLng b) {
        double lat = a.latitude - b.latitude;
        double lng = a.longitude - b.longitude;
        while(lng >= 360.0) lng -= 360.0;
        while(lng < 0.0) lng += 360.0;
        return lat * lat + lng * lng;
    }
}
