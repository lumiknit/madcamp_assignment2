package io.madcamp.jh.madcamp_assignment2;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class Tab2Adapter extends RecyclerView.Adapter<Tab2Adapter.ImageViewHolder> {
    protected static int STAMP_LIKE_THRESHOLD = 3;


    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;
        private ImageView stampImageView;

        private Image image;

        ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            textView = itemView.findViewById(R.id.text_view);
            stampImageView = itemView.findViewById(R.id.image_view_stamp);
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
            if(image.like >= STAMP_LIKE_THRESHOLD) {
                stampImageView.setVisibility(View.VISIBLE);
            } else {
                stampImageView.setVisibility(View.INVISIBLE);
            }
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
        itemView.setRotation((float)Math.random() * 4 - 2.f);
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
