package com.example.smit.sadr.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smit.sadr.MusicUnits;
import com.example.smit.sadr.R;

import java.util.List;

public class ListRadioAdapter extends ArrayAdapter<MusicUnits>  {
    private final Context context;
    private List<MusicUnits> radioUnits;

    public ListRadioAdapter(Context context, List<MusicUnits> radioUnits) {
        super(context, R.layout.list_music,radioUnits);
        this.context = context;
        this.radioUnits =  radioUnits;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.list_radio, parent, false);

        }
        TextView RadioName = convertView.findViewById(R.id.RadioName);
        TextView RadioLink = convertView.findViewById(R.id.RadioLink);

        RadioName.setText(radioUnits.get(position).Mname);
        RadioName.setTextColor(Color.BLACK);
        RadioName.setMaxWidth(320);
        RadioLink.setText(radioUnits.get(position).Folder);
        RadioLink.setTextColor(Color.rgb(150,150,150));
        RadioLink.setMaxWidth(320);

        return convertView;
    }
}
