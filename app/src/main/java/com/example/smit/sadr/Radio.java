package com.example.smit.sadr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.smit.sadr.Adapters.ListMusicAdapter;
import com.example.smit.sadr.Adapters.ListRadioAdapter;

import java.util.ArrayList;
import java.util.List;

public class Radio extends AppCompatActivity {

    /*
    public static List<MusicUnits> radioUnits = new ArrayList<MusicUnits>();
    static ListView radioList;
    static ListRadioAdapter adapter;
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio);

        /*
        getSupportActionBar().hide();
        radioList = (ListView) findViewById(R.id.listViewRadio);
        MusicUnits radio = new MusicUnits();
        radio.Folder = "http://retroserver.streamr.ru:8043";
        radio.File = "retro64";
        radio.Mname = "Radio";
        radio.MAuthor = "Radio";
        radioUnits.add(radio);
        adapter = new ListRadioAdapter(this, radioUnits);
        radioList.setAdapter(adapter);
        */
    }
}
