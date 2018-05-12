package com.example.smit.sadr;



import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import wseemann.media.FFmpegMediaMetadataRetriever;


public  class General {

    public  static List<String> folderUnits = new ArrayList<String>();


    public  static List<MusicUnits> getMusicList(List<String> folders){
        Log.e("SADR","getMusicList - Start" );
        Long start, stop,fstart, ffstart;
        int total = 0;
        start = System.currentTimeMillis();
        List<MusicUnits> musicList = new ArrayList<MusicUnits>();
        FFmpegMediaMetadataRetriever  meta = new FFmpegMediaMetadataRetriever();
        String tmp;
        String format;
        String name;
        int pos;
        for(String folder : folders)
        {
            fstart = System.currentTimeMillis();
            File file = new File(folder);
            File[] fileList = file.listFiles();
            for (Integer i = 0; i < fileList.length; i++) {

                MusicUnits unit = new MusicUnits();
                name = fileList[i].getName();
                unit.Path = fileList[i].getAbsolutePath();
                format = name.substring(name.lastIndexOf(".") + 1, name.length());
                if (format.equals("mp3"))unit.formatIndex = 3;
                else if (format.equals("flac"))unit.formatIndex = 2;
                else if (format.equals("wav"))unit.formatIndex = 1;
                ffstart = System.currentTimeMillis();
                try
                {
                    meta.setDataSource(fileList[i].getAbsolutePath());
                    unit.MAuthor = meta.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
                    if (unit.MAuthor == null) {
                        if ((pos = name.indexOf("-")) > 0) {
                            unit.MAuthor = name.substring(pos + 1, name.lastIndexOf("."));
                        } else {
                            unit.MAuthor = "Unknown";
                        }
                    }
                    unit.MDuration =  Integer.parseInt(meta.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION));
                    if(unit.MDuration == null)unit.MDuration = 0;
                    unit.Mtime = General.getStrTime(unit.MDuration);

                    unit.Mname = meta.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_TITLE);
                    if(unit.Mname == null) {
                        if ((pos = name.indexOf("-")) > 0) {
                            unit.Mname = name.substring(0, pos);
                        } else {
                            unit.Mname = name.substring(0, name.lastIndexOf("."));
                        }
                    }
                }
                catch (Exception e)
                {

                    if((pos=name.indexOf("-"))>0){
                        unit.MAuthor = name.substring(0,pos);
                        unit.Mname = name.substring(pos+1,name.lastIndexOf("."));
                    }
                    else{
                        unit.Mname = name.substring(0,name.lastIndexOf("."));
                        unit.MAuthor = "Unknown";
                    }
                    unit.MDuration = 0;
                    unit.Mtime = General.getStrTime(unit.MDuration);

                }
                musicList.add(unit);

                Log.e("SADR", i + ": "+(System.currentTimeMillis() - ffstart) +" : "+name);
                total +=(System.currentTimeMillis() - ffstart);
            }

            Log.e("SADR","for "  + folder+ "Done in " + (System.currentTimeMillis() - fstart) );
        }
        stop = System.currentTimeMillis();
        Log.e("SADR","getMusicList - Done in " + (stop - start) + "total " + total);
        return musicList;
    }

    public static String getStrTime(Integer curTime){
        String time  = new String();
        Integer h,m,s,tmp;
        if(curTime/1000>=60&&curTime/1000<60*60){
            m = curTime/(1000*60);
            s = (curTime/1000)-(60*m);
            time = m<10?"0"+Integer.toString(m):Integer.toString(m);
            time += ":";
            time += s<10?"0"+Integer.toString(s):Integer.toString(s);
        }else if(curTime/1000<60){
            s = curTime/1000;
            time ="00:";
            time += s<10?"0"+Integer.toString(s):Integer.toString(s);
        }else if(curTime/1000 >= 60*60 ){
            h = curTime/(1000*60*60);
            m = ((curTime/1000)-(h*60*60))/60;
            s = (curTime/1000)-(h*60*60)-(m*60);
            time = Integer.toString(h);
            time +=":";
            time = m<10?"0"+Integer.toString(m):Integer.toString(m);
            time +=":";
            time += s<10?"0"+Integer.toString(s):Integer.toString(s);
        }

        return time;
    }










}
