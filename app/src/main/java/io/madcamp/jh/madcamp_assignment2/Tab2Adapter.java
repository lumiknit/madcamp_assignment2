package io.madcamp.jh.madcamp_assignment2;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Tab2Adapter extends RecyclerView.Adapter<Tab2Adapter.ImageViewHolder> {
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;

        private Image image;

        ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            textView = itemView.findViewById(R.id.text_view);
            itemView.setRotation((float)Math.random() * 4 - 2.f);
        }

        void setImage(Image image) {
            this.image = image;
            if(image.uri != null) {
                Glide.with(imageView.getContext())
                        .load(image.uri.toString())
                        .thumbnail(0.1f)
                        .into(imageView);
            }
            image.updateTag();
            textView.setText(image.tag);
        }
    }

    public ArrayList<Image> dataSet;

    public Tab2Adapter(ArrayList<Image> dataSet) {
        this.dataSet = dataSet;
    }

    public void add(Image image) {
        dataSet.add(0, image);
        notifyDataSetChanged();
    }

    public Image remove(int i) {
        Image image = dataSet.get(i);
        dataSet.remove(i);
        notifyDataSetChanged();
        return image;
    }

    public Image get(int i) {
        return dataSet.get(i);
    }

    public ArrayList<Image> getDataSet() {
        return dataSet;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.view_tab2_image, viewGroup, false);
        return new ImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder viewHolder, int i) {
        Image p = dataSet.get(i);
        viewHolder.setImage(p);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
