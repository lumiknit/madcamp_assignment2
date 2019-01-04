package io.madcamp.jh.madcamp_assignment2;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    public static class Item implements Comparable {
        public int index;
        public SpannableStringBuilder name;
        public SpannableStringBuilder number;

        public Item(int index, SpannableStringBuilder name, SpannableStringBuilder number) {
            this.index = index;
            this.name = name;
            this.number = number;
        }

        @Override
        public int compareTo(Object other) {
            if(other instanceof Item) {
                return this.name.toString().compareTo(((Item)other).name.toString());
            } else {
                return 0;
            }
        }
    }

    ArrayList<Item> data;
    public int shared;
    private LayoutInflater inflater;
    private int layout;

    public ListViewAdapter(Context context, int layout, ArrayList<Item> contacts) {
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = contacts;
        this.shared = 0;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate(layout, parent,false);
        }

        Item contact = data.get(position);

        TextView contact_name = (TextView) convertView.findViewById(R.id.name_textview);
        contact_name.setText(contact.name);

        TextView contact_num = (TextView) convertView.findViewById(R.id.number_textview);
        contact_num.setText(contact.number);

        return convertView;
    }
}