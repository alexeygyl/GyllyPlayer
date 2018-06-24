package com.example.smit.sadr.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smit.sadr.MainActivity;
import com.example.smit.sadr.MusicUnits;
import com.example.smit.sadr.R;

import java.util.List;

public class ListMusicAdapter extends ArrayAdapter<MusicUnits>  {
    private final Context context;
    private List<MusicUnits> musicUnitses;

    public ListMusicAdapter(Context context, List<MusicUnits> musicUnitses) {
        super(context, R.layout.list_music,musicUnitses);
        this.context = context;
        this.musicUnitses =  musicUnitses;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.list_music, parent, false);

        }
        TextView textName =convertView.findViewById(R.id.MusicName);
        TextView textAuthor =  convertView.findViewById(R.id.MusicAuthor);
        TextView textTime =  convertView.findViewById(R.id.MusicTime);
        ImageView imageView = convertView.findViewById(R.id.logo);

        textName.setText(musicUnitses.get(position).Mname);
        textName.setTextColor(Color.BLACK);
        textName.setMaxWidth(320);
        textAuthor.setText(musicUnitses.get(position).MAuthor);
        textAuthor.setTextColor(Color.rgb(150,150,150));
        textAuthor.setMaxWidth(320);
        textTime.setText(musicUnitses.get(position).Mtime);
        //if(MainActivity.lastMusPos != position)convertView.setBackgroundResource(R.drawable.musiclist);
        //else convertView.setBackgroundResource(R.drawable.presed);
        String s = musicUnitses.get(position).MAuthor;
        try{
            if (s.substring(0,1).equalsIgnoreCase("A") || s.substring(0,1).equalsIgnoreCase("А")) {
                imageView.setImageResource(R.drawable.alph_a);
            }else if (s.substring(0,1).equalsIgnoreCase("B") || s.substring(0,1).equalsIgnoreCase("Б")) {
                imageView.setImageResource(R.drawable.alph_b);
            }else if (s.substring(0,1).equalsIgnoreCase("C") || s.substring(0,1).equalsIgnoreCase("Ц")) {
                imageView.setImageResource(R.drawable.alph_c);
            } else if (s.substring(0,1).equalsIgnoreCase("D") || s.substring(0,1).equalsIgnoreCase("Д")) {
                imageView.setImageResource(R.drawable.alph_d);
            }else if (s.substring(0,1).equalsIgnoreCase("E") || s.substring(0,1).equalsIgnoreCase("Е")) {
                imageView.setImageResource(R.drawable.alph_e);
            }else if (s.substring(0,1).equalsIgnoreCase("F") || s.substring(0,1).equalsIgnoreCase("Ф")) {
                imageView.setImageResource(R.drawable.alph_f);
            }else if (s.substring(0,1).equalsIgnoreCase("G") || s.substring(0,1).equalsIgnoreCase("Г")) {
                imageView.setImageResource(R.drawable.alph_g);
            }else if (s.substring(0,1).equalsIgnoreCase("H") || s.substring(0,1).equalsIgnoreCase("Х")) {
                imageView.setImageResource(R.drawable.alph_h);
            }else if (s.substring(0,1).equalsIgnoreCase("I") || s.substring(0,1).equalsIgnoreCase("И")) {
                imageView.setImageResource(R.drawable.alph_i);
            }else if (s.substring(0,1).equalsIgnoreCase("J") || s.substring(0,1).equalsIgnoreCase("Ж")) {
                imageView.setImageResource(R.drawable.alph_j);
            }else if (s.substring(0,1).equalsIgnoreCase("K") || s.substring(0,1).equalsIgnoreCase("К")) {
                imageView.setImageResource(R.drawable.alph_k);
            }
            else if (s.substring(0,1).equalsIgnoreCase("S") || s.substring(0,1).equalsIgnoreCase("С")) {
                imageView.setImageResource(R.drawable.alph_s);
            }
            else if (s.substring(0,1).equalsIgnoreCase("M") || s.substring(0,1).equalsIgnoreCase("М")) {
                imageView.setImageResource(R.drawable.alph_m);
            }
            else if (s.substring(0,1).equalsIgnoreCase("X")) {
                imageView.setImageResource(R.drawable.alph_x);
            }
            else if (s.substring(0,1).equalsIgnoreCase("Z") || s.substring(0,1).equalsIgnoreCase("З")) {
                imageView.setImageResource(R.drawable.alph_z);
            }
        }catch (Exception e){

        }

        return convertView;
    }

}
