package io.madcamp.jh.madcamp_assignment2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.concurrent.ExecutionException;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    public static class Tag {
        public View view;
        public Image image;

        public Tag(Image image) {
            this.view = view;
            this.image = image;
        }
    }

    private Context context;
    private LayoutInflater layoutInflater;

    public CustomInfoWindowAdapter(Activity context) {
        this.context = context;
        layoutInflater = context.getLayoutInflater();
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(final Marker marker) {
        Tag tag = (Tag)marker.getTag();
        boolean isFirst = false;
        View view = tag.view;
        if(view == null) {
            view = layoutInflater.inflate(R.layout.custom_info_window, null);
            isFirst = true;
        }

        final Image img = ((Tag)marker.getTag()).image;

        final TextView nameTextView = view.findViewById(R.id.text_view_name);
        final TextView dateTextView = view.findViewById(R.id.text_view_date);
        final TextView likeTextView = view.findViewById(R.id.text_view_like);
        final ImageView imageView = view.findViewById(R.id.image_view);

        nameTextView.setText(img.name == null ? "" : img.name);
        dateTextView.setText(img.getDateAsString());
        likeTextView.setText(img.getLikeAsString());

        if(isFirst) {
            Glide.with(context).load(img.uri.toString())
                    .thumbnail(0.2f)
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            imageView.setImageDrawable(resource);
                            marker.showInfoWindow();
                        }
                    });
            tag.view = view;
        }
        return view;
    }
}
