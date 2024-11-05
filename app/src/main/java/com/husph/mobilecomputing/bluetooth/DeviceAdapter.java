package com.husph.mobilecomputing.bluetooth;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.husph.mobilecomputing.R;

import java.util.List;

public class DeviceAdapter extends ArrayAdapter<String> {

    private static final int VIEW_TYPE_DEVICE = 0;
    private static final int VIEW_TYPE_HEADER = 1;

    private List<String> items;
    private LayoutInflater inflater;

    public DeviceAdapter(Context context, List<String> items) {
        super(context, 0, items);
        this.items = items;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).contains("Header:") ? VIEW_TYPE_HEADER : VIEW_TYPE_DEVICE;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);

        if (viewType == VIEW_TYPE_HEADER) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item_header, parent, false);
            }
            TextView headerText = convertView.findViewById(R.id.header_text);
            headerText.setText(items.get(position).replace("Header:", ""));
        } else {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item_device, parent, false);
            }
            TextView deviceName = convertView.findViewById(R.id.device_name);
            deviceName.setText(items.get(position));
        }

        return convertView;
    }
}
