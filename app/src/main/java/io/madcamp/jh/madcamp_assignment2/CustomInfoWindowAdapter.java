package io.madcamp.jh.madcamp_assignment2;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private LayoutInflater layoutInflater;

    public CustomInfoWindowAdapter(Activity context) {
        layoutInflater = context.getLayoutInflater();
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = layoutInflater.inflate(R.layout.custom_info_window, null);

        TextView titleTextView = view.findViewById(R.id.text_view_title);
        TextView snippetTextView = view.findViewById(R.id.text_view_snippet);
        ImageView imageView = view.findViewById(R.id.image_view);

        titleTextView.setText(marker.getTitle());
        snippetTextView.setText(marker.getSnippet());
        imageView.setImageResource(R.drawable.ic_baseline_camera_alt);

        return view;
    }
}
