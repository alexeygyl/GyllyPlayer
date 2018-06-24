package com.example.smit.sadr;
import android.media.MediaPlayer;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class MPlayer {


    Integer OFF =0;
    Integer ON =1;
    Integer PAUSE = 2;
    Integer STOP =0;
    Integer status=STOP;
    boolean LOOP=false;
    Integer PHONE_STATUS = OFF;
    Integer lastMusPos = 0;

    public List<MusicUnits> musicUnits;
    public  static List<String> folderUnits;
    public static final MPlayer INSTANCE = new MPlayer();
    MediaPlayer mediaPlayer = new MediaPlayer();

    public MPlayer(){
        musicUnits = DBHelper.INSTANCE.getMusicList();
        folderUnits = DBHelper.INSTANCE.getFolderList();
    }

    public void playNext(){
        lastMusPos = lastMusPos+1 > musicUnits.size() ? 0:lastMusPos+1;
        Play(lastMusPos);
    }

    public void playPrev(){
        lastMusPos = lastMusPos-1 < 0 ? musicUnits.size():lastMusPos-1;
        Play(lastMusPos);
    }

    public void Play(int position){
        if(status.equals(ON) || status.equals(PAUSE)){
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        if(position == -1)position = lastMusPos;
        try {
            mediaPlayer.setDataSource(musicUnits.get(position).Folder +"/"+ musicUnits.get(position).File );
            mediaPlayer.prepare();
            mediaPlayer.start();
            status = ON;
            lastMusPos = position;

        } catch (IOException e) {
            removeMusic(musicUnits.get(position).File);
            musicUnits.remove(position);
            //adapter.notifyDataSetChanged();
            //Toast.makeText(this, "This file not more exists: DELETED", Toast.LENGTH_SHORT).show();
        }
        catch (IllegalStateException e) {

            e.printStackTrace();
        }catch (IndexOutOfBoundsException e){
            //Mplay.setBackgroundResource(R.drawable.play64_off);
            //status = STOP;
        }

    }

    public void start(){
        mediaPlayer.start();
        status = ON;
    }

    public void pause(){
        mediaPlayer.pause();
        status = PAUSE;
    }

    public void seekTo(int pos){
        mediaPlayer.seekTo(pos);
    }

    public  int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }

    public  int getDuration(){
        return mediaPlayer.getDuration();
    }

    public boolean isPlaying(){
        return  mediaPlayer.isPlaying();
    }

    public boolean isPause(){
       if(status.equals(PAUSE))return true;
       return false;
    }

    public boolean isPlay(){
        if(status.equals(ON))return true;
        return false;
    }

    public boolean isStop(){
        if(status.equals(OFF))return true;
        return false;
    }

    public boolean isLoop(){
       return LOOP;
    }

    public void setLoop(boolean loop){
        LOOP = loop;
    }

    public void onInputCall(){
        if(status == ON) {
            mediaPlayer.pause();
            status = PAUSE;
            PHONE_STATUS = ON;
        }
    }

    public void onOutputCall(){
        if(status == ON) {
            mediaPlayer.pause();
            status = PAUSE;
            PHONE_STATUS = ON;
        }
    }

    public void onEndCall(){
        if( PHONE_STATUS == ON && status == PAUSE) {
            mediaPlayer.start();
            status = ON;
            PHONE_STATUS = OFF;
        }
    }

    public  void removeMusicByFolder(String folder){
        List<MusicUnits> out = new ArrayList<>();
        for (MusicUnits unit: musicUnits) {
            if(!unit.Folder.equalsIgnoreCase(folder))out.add(unit);
        }
        musicUnits.clear();
        for (MusicUnits unit: out) {
            musicUnits.add(unit);
        }
    }

    public  void removeMusic(String file){
        try {
            for (MusicUnits unit: musicUnits) {
                if(unit.File.equalsIgnoreCase(file)) {
                    musicUnits.remove(unit);
                }
            }
            //adapter.notifyDataSetChanged();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void updateMusicList(String folder){
        FFmpegMediaMetadataRetriever meta = new FFmpegMediaMetadataRetriever();
        //String tmp;
        String name;
        //String fullPath;
        int pos;
        File file = new File(folder);
        if(file.isFile()){
            name = file.getName();
            if(!isMusic(name))return;
            if(ifMusicExists(name))return;
            MusicUnits unit = new MusicUnits();
            unit.Folder = file.getParent();
            unit.File = name;
            try
            {
                meta.setDataSource(file.getAbsolutePath());
                unit.MAuthor = meta.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
                if (unit.MAuthor == null) {
                    if ((pos = name.indexOf("-")) > 0) {
                        unit.MAuthor = name.substring(0, pos);
                    } else {
                        unit.MAuthor = "Unknown";
                    }
                }
                unit.MDuration =  Integer.parseInt(meta.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION));
                if(unit.MDuration==null)unit.MDuration = 0;
                unit.Mtime = getStrTime(unit.MDuration);

                unit.Mname = meta.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_TITLE);

                if(unit.Mname == null) {
                    if ((pos = name.indexOf("-")) > 0) {
                        unit.Mname = name.substring(pos + 1, name.lastIndexOf("."));
                    } else {
                        unit.Mname = name.substring(0, name.lastIndexOf("."));
                    }
                }

            }
            catch (Exception e)
            {
                try {
                    if((pos=name.indexOf("-"))>0){
                        unit.MAuthor = name.substring(0,pos);
                        unit.Mname = name.substring(pos+1,name.lastIndexOf("."));
                    }
                    else{
                        unit.Mname = name.substring(0,name.lastIndexOf("."));
                        unit.MAuthor = "Unknown";
                    }
                    unit.MDuration = 0;
                    unit.Mtime = getStrTime(unit.MDuration);
                }catch (Exception x){}
            }
            musicUnits.add(unit);
            DBHelper.INSTANCE.insertNewMusic(unit);
        }
        else {
            File[] fileList = file.listFiles();
            for (Integer i = 0; i < fileList.length; i++) {
                if(fileList[i].isDirectory())continue;
                name = fileList[i].getName();
                // Log.e("SADR", "FOLDER : "+ name);
                MusicUnits unit = new MusicUnits();
                if(!isMusic(name))continue;
                if(ifMusicExists(name))continue;
                unit.Folder = folder;
                unit.File = name;
                try
                {
                    meta.setDataSource(fileList[i].getAbsolutePath());
                    unit.MAuthor = meta.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);

                    if (unit.MAuthor == null) {
                        if ((pos = name.indexOf("-")) > 0) {
                            unit.MAuthor = name.substring(0, pos);
                        } else {
                            unit.MAuthor = "Unknown";
                        }
                    }
                    unit.MDuration =  Integer.parseInt(meta.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION));
                    if(unit.MDuration == null)unit.MDuration = 0;
                    unit.Mtime = getStrTime(unit.MDuration);

                    unit.Mname = meta.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_TITLE);
                    if(unit.Mname == null) {
                        if ((pos = name.indexOf("-")) > 0) {
                            unit.Mname = name.substring(pos + 1, name.lastIndexOf("."));
                        } else {
                            unit.Mname = name.substring(0, name.lastIndexOf("."));
                        }
                    }
                }
                catch (Exception e)
                {
                    try {
                        if((pos=name.indexOf("-"))>0){
                            unit.Mname = name.substring(pos+1,name.lastIndexOf("."));
                            unit.MAuthor = name.substring(0,pos);
                        }
                        else{
                            unit.Mname = name.substring(0,name.lastIndexOf("."));
                            unit.MAuthor = "Unknown";
                        }
                        unit.MDuration = 0;
                        unit.Mtime = getStrTime(unit.MDuration);
                    }catch (Exception x){}
                }
                musicUnits.add(unit);
                DBHelper.INSTANCE.insertNewMusic(unit);
            }

        }

    }

    private boolean isMusic(String name){
        String ext = name.substring(name.lastIndexOf(".") +1,name.length());
        if(ext.equalsIgnoreCase("mp3"))return true;
        return false;
    }

    public String getStrTime(Integer curTime){
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
            time += m<10?"0"+Integer.toString(m):Integer.toString(m);
            time +=":";
            time += s<10?"0"+Integer.toString(s):Integer.toString(s);

        }

        return time;
    }

    public  boolean ifMusicExists(String file){
        for (MusicUnits unit : musicUnits) {
            if(unit.File.equalsIgnoreCase(file))return true;
        }
        return  false;
    }

    public  boolean ifFolderExists(String folder){
        return folderUnits.contains(folder);

    }

    public  boolean addFolder(String folder){
        return folderUnits.add(folder);
    }

    public void removeFolder(String folder){
        folderUnits.remove(folder);
    }

    public boolean isInFolder(String folder){
        for (MusicUnits unit: musicUnits) {
            if(unit.Folder.contains(folder))return true;
        }
        return false;
    }

    public String getName(int pos){
        if(musicUnits.size() != 0){
            if(pos == -1)return musicUnits.get(lastMusPos).Mname;
            return musicUnits.get(pos).Mname;
        }
        return null;
    }

    public String getAuthor(int pos){
        if(musicUnits.size() != 0){
            if(pos == -1)return musicUnits.get(lastMusPos).MAuthor;
            return musicUnits.get(pos).MAuthor;
        }
        return null;
    }


}
