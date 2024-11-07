package com.husph.mobilecomputing.countries;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.husph.mobilecomputing.R;

import java.util.List;
import java.util.Map;

public class CountryAdapter extends BaseAdapter {
    private Context context;
    private List<Map.Entry<String, String>> countryList;

    public CountryAdapter(Context context, List<Map.Entry<String, String>> countryList) {
        this.context = context;
        this.countryList = countryList;
    }

    @Override
    public int getCount() {
        return countryList.size();
    }

    @Override
    public Object getItem(int position) {
        return countryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_country, parent, false);
        }

        TextView textCountryCode = convertView.findViewById(R.id.textCountryCode);
        TextView textCountryName = convertView.findViewById(R.id.textCountryName);
        ImageView imageView = convertView.findViewById(R.id.img_flag);


        Map.Entry<String, String> entry = countryList.get(position);
        Glide.with(context).load("https://flagsapi.com/"+ entry.getKey() +"/flat/64.png").into(imageView);
        textCountryCode.setText(entry.getKey());  // Set the country code
        textCountryName.setText(entry.getValue()); // Set the country name

        return convertView;
    }
}