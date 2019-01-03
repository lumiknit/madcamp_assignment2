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

import java.util.ArrayList;

public class Tab2Adapter extends RecyclerView.Adapter<Tab2Adapter.ImageViewHolder> {
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;

        ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            textView = itemView.findViewById(R.id.text_view);
        }

        void setImage(Uri uri) {
            imageView.setImageURI(uri);
        }

        void setTag(String s) {
            textView.setText(s);
        }
    }

    public ArrayList<Pair<Uri, String>> dataSet;

    public Tab2Adapter(ArrayList<Pair<Uri, String>> dataSet) {
        this.dataSet = dataSet;
    }

    public void add(Uri uri, String tag) {
        dataSet.add(new Pair<>(uri, tag));
        notifyDataSetChanged();
    }

    public Uri remove(int i) {
        Uri uri = dataSet.get(i).first;
        dataSet.remove(i);
        notifyDataSetChanged();
        return uri;
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
        Pair<Uri, String> p = dataSet.get(i);
        viewHolder.setImage(p.first);
        viewHolder.setTag(p.second);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public String getOriginalPath(int position) {
        Uri uri = dataSet.get(position).first;
        String dir;
        String filename;
        try {
            String thPath = uri.getPath();
            int i = thPath.lastIndexOf("/");
            dir = thPath.substring(0, i) + "/";
            filename = thPath.substring(i + 1);
        } catch(NullPointerException e) {
            return null;
        }
        return dir + "O_" + filename;
    }
}
