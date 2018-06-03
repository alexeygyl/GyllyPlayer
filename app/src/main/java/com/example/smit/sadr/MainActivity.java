package com.example.smit.sadr;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smit.sadr.Adapters.ListMusicAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import wseemann.media.FFmpegMediaMetadataRetriever;


public class MainActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener{

    public static List<MusicUnits> musicUnits = new ArrayList<>();
    public  static List<String> folderUnits = new ArrayList<>();
    public static  String root = "/storage";
    RelativeLayout mainView,SeekRL;
    ProgressBar SeekProgress;
    int SeekProgressVal;
    int MPCurrPos,MPCurrPos2;
    ImageButton Mloop;
    ImageButton MplayNext;
    ImageButton MplayPrev;
    ImageButton Mplay;
    ImageButton Mseek;
    static Integer OFF =0;
    static Integer ON =1;
    static Integer PAUSE = 2;
    static  Integer STOP =0;
    static Integer status=STOP;
    static Integer LOOP=OFF;
    static Integer PHONE_STATUS = OFF;
    public static Integer lastMusPos = 0;
    static MediaPlayer mediaPlayer;
    ProgressBar progressBar;
    Handler seekHandler = new Handler();
    TextView musicName;
    TextView musicAuthor;
    ListView listmusic;
    TextView mDuration;
    int musicCurPos = 0;
    TextView qwe;

    int first, last;
    float mX;
    float mY;
    float pass;
    int w;

    static ListMusicAdapter adapter;
    //Bundle bundle=new Bundle();
    static DBHelper dbHelper;
    static SQLiteDatabase database;
    static ContentValues contentValues;
    FloatingActionButton fab;
    FloatingActionButton fabRadio;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        initViews();
        w = 540000;
        dbHelper = new DBHelper(this);
        musicUnits = getMusicListFromBD();
        folderUnits = getFolderListFromBD();
        initText();
        musicName.setMaxWidth(350);
        musicName.setTextColor(Color.WHITE);
        musicAuthor.setTextColor(Color.WHITE);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(12, 145, 56)));
        adapter = new ListMusicAdapter(this, musicUnits);
        listmusic.setAdapter(adapter);
        mediaPlayer = new MediaPlayer();


        listmusic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                first =  parent.getFirstVisiblePosition();
                last = parent.getLastVisiblePosition();
                if(lastMusPos>=first && lastMusPos <=last){
                   parent.getChildAt(lastMusPos).setBackgroundResource(R.drawable.musiclist);
                }
                view.setBackgroundResource(R.drawable.presed);
                musicName.setText(musicUnits.get(position).Mname);
                musicAuthor.setText(musicUnits.get(position).MAuthor);
                startPlay(position, status);
                status = ON;
                Mplay.setBackgroundResource(R.drawable.play_on);
                lastMusPos = position;
            }
        });



        fab.setOnClickListener(this);
        fabRadio.setOnClickListener(this);
        Mloop.setOnClickListener(this);
        Mseek.setOnClickListener(this);
        SeekRL.setOnClickListener(this);
        progressBar.setOnTouchListener(this);
        Mplay.setOnTouchListener(this);
        MplayNext.setOnTouchListener(this);
        MplayPrev.setOnTouchListener(this);
        SeekProgress.setOnTouchListener(this);

    }

    public void startPlay( int position, Integer status){
        if(status.equals(ON) || status.equals(PAUSE)){
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        try {
            mediaPlayer.setDataSource(musicUnits.get(position).Folder +"/"+ musicUnits.get(position).File );
            mediaPlayer.prepare();
            mediaPlayer.start();
            pass = (float)mediaPlayer.getDuration() / (float)w;
            //Log.e("SADR",mediaPlayer.getDuration() + " / " +  w  + " = " + pass);
            progressBar.setProgress(0);
            progressBar.setMax(mediaPlayer.getDuration());
            startPlayProgressUpdater();

        } catch (IOException e) {
            removeMusic(musicUnits.get(position).File);
            musicUnits.remove(position);
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "This file not more exists: DELETED", Toast.LENGTH_SHORT).show();
        }
        catch (IllegalStateException e) {

            e.printStackTrace();
        }catch (IndexOutOfBoundsException e){
            Mplay.setBackgroundResource(R.drawable.play64_off);
            //status = STOP;
        }

    }

    public void startPlayProgressUpdater() {
        try {
            //seekBar.setProgress(mediaPlayer.getCurrentPosition());
            progressBar.setProgress(mediaPlayer.getCurrentPosition());
            mDuration.setText(getStrTime(mediaPlayer.getCurrentPosition()));
            if (mediaPlayer.isPlaying()) {
                    Runnable notification = new Runnable() {
                        public void run() {
                            startPlayProgressUpdater();
                        }
                    };
                seekHandler.postDelayed(notification,1000);
            }else{
                if(status.equals(ON) ){
                    if(LOOP.equals(OFF))startPlay(++lastMusPos,status);
                    else startPlay(lastMusPos,status);
                    musicName.setText(musicUnits.get(lastMusPos).Mname);
                    musicAuthor.setText(musicUnits.get(lastMusPos).MAuthor);
                }
                else  mediaPlayer.pause();
            }
        }catch (Exception e){ e.printStackTrace();}
    }

    public void initViews(){
    mainView = findViewById(R.id.MainL);
    SeekRL = findViewById(R.id.SeekRL);
    SeekProgress = findViewById(R.id.SeekProgress);
    listmusic =  findViewById(R.id.listViewMusic);
    musicName =  findViewById(R.id.MusicName2);
    musicAuthor =  findViewById(R.id.MusicAuthor2);
    Mplay = findViewById(R.id.Mplay);
    Mplay.setBackgroundResource(R.drawable.play64_off);
    MplayNext = findViewById(R.id.MplayNext);
    MplayNext.setBackgroundResource(R.drawable.forward_off);
    MplayPrev = findViewById(R.id.MplayPrev);
    MplayPrev.setBackgroundResource(R.drawable.backward_off);
    Mseek = findViewById(R.id.Mseek);
    Mseek.setBackgroundResource(R.drawable.volume_control);
    mDuration = findViewById(R.id.mDuration);
    Mloop =  findViewById(R.id.MLoop);
    Mloop.setBackgroundResource(R.drawable.loop_off);
    progressBar =  findViewById(R.id.progressBar);
    fab = findViewById(R.id.fab);
    fabRadio = findViewById(R.id.fabRadio);
    qwe = findViewById(R.id.TextViewTMP);
}

    public void initText(){
        if(musicUnits.size()!=0){
            musicName.setText(musicUnits.get(0).Mname);
            musicAuthor.setText(musicUnits.get(0).MAuthor);
        }
    }

    public static void insertNewMusic(MusicUnits unit){
        database = dbHelper.getWritableDatabase();
        try {
            contentValues = new ContentValues();
            contentValues.put(DBHelper.NAME, unit.Mname);
            contentValues.put(DBHelper.TIME, unit.Mtime);
            contentValues.put(DBHelper.DURATION, unit.MDuration);
            contentValues.put(DBHelper.OWNER, unit.MAuthor);
            contentValues.put(DBHelper.FILE, unit.File);
            contentValues.put(DBHelper.FOLDER, unit.Folder);
            database.insert(DBHelper.M_TABLE, null, contentValues);
            database.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeMusic(String file){
        database = dbHelper.getWritableDatabase();
        try {
            database.execSQL("DELETE FROM " + DBHelper.M_TABLE + " WHERE " + DBHelper.FILE + "= '" + file + "'");
            database.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeMusicFromList(String file){
        try {
            for (MusicUnits unit: musicUnits) {
                if(unit.File.equalsIgnoreCase(file)) {
                    musicUnits.remove(unit);
                }
            }
            adapter.notifyDataSetChanged();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeFolder(String folder){
        folderUnits.remove(folder);
    }

    public static void removeMusicByFolderFromBD(String folder){
        database = dbHelper.getWritableDatabase();
        try {
            database.execSQL("DELETE FROM " + DBHelper.M_TABLE + " WHERE " + DBHelper.FOLDER + "= '" + folder + "'");
            database.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeFolderFromBD(String folder){
        Log.e("SADR", "DIR:" + folder);
        database = dbHelper.getWritableDatabase();
        try {
            database.execSQL("DELETE FROM " + DBHelper.F_TABLE + " WHERE " + DBHelper.FOLDER + "= '" + folder + "'");
            database.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeMusicByFolderFromList(String folder){
        List<MusicUnits> out = new ArrayList<>();
        for (MusicUnits unit: musicUnits) {
            if(!unit.Folder.equalsIgnoreCase(folder))out.add(unit);
        }
        musicUnits.clear();
        for (MusicUnits unit: out) {
            musicUnits.add(unit);
        }
    }

    public static void insertNewFolder(String folder){
        database = dbHelper.getWritableDatabase();
        try {
            contentValues = new ContentValues();
            contentValues.put(DBHelper.FOLDER, folder);
            database.insert(DBHelper.F_TABLE, null, contentValues);
            database.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  List<MusicUnits> getMusicListFromBD(){
        List<MusicUnits> musicList = new ArrayList<MusicUnits>();
        database = dbHelper.getReadableDatabase();
        String coluns[] = {DBHelper.NAME,DBHelper.OWNER,DBHelper.TIME,DBHelper.FILE,DBHelper.DURATION,DBHelper.FOLDER };
        try {
            Cursor cursor = database.query(DBHelper.M_TABLE, coluns, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                int nameInd = cursor.getColumnIndex(DBHelper.NAME);
                int ownerInd = cursor.getColumnIndex(DBHelper.OWNER);
                int timeInd = cursor.getColumnIndex(DBHelper.TIME);
                int fileInd = cursor.getColumnIndex(DBHelper.FILE);
                int folderInd = cursor.getColumnIndex(DBHelper.FOLDER);
                int durationInd = cursor.getColumnIndex(DBHelper.DURATION);
                do {
                    MusicUnits unit = new MusicUnits();
                    unit.Mname = cursor.getString(nameInd);
                    unit.MAuthor = cursor.getString(ownerInd);
                    unit.File = cursor.getString(fileInd);
                    unit.Folder = cursor.getString(folderInd);
                    unit.Mtime = cursor.getString(timeInd);
                    unit.MDuration = cursor.getInt(durationInd);
                    musicList.add(unit);
                } while (cursor.moveToNext());
            }
            cursor.close();
            database.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return musicList;
    }

    public  List<String> getFolderListFromBD(){
        List<String> folderUnits = new ArrayList<String>();
        database = dbHelper.getReadableDatabase();
        String coluns[] = {DBHelper.FOLDER};
        try {
            Cursor cursor = database.query(DBHelper.F_TABLE, coluns, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                int folderInd = cursor.getColumnIndex(DBHelper.FOLDER);
                do {
                    folderUnits.add(cursor.getString(folderInd));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return folderUnits;
    }

    public static  String getCurDirFromBD(){
       String dir = null;
        database = dbHelper.getReadableDatabase();
        String coluns[] = {DBHelper.CURDIR};
        try {
            Cursor cursor = database.query(DBHelper.C_TABLE, coluns, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                int dirInd = cursor.getColumnIndex(DBHelper.CURDIR);
                do {
                    dir = cursor.getString(dirInd);
                } while (cursor.moveToNext());

            }else  {
                database.close();
                database = dbHelper.getWritableDatabase();
                try {
                    contentValues = new ContentValues();
                    contentValues.put(DBHelper.CURDIR, root);
                    database.insert(DBHelper.C_TABLE, null, contentValues);
                    database.close();
                }catch (Exception e) {
                    e.printStackTrace();
                }

            }
            database.close();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return dir;
    }

    public static  void setCurDirBD(String newDir){
        database = dbHelper.getWritableDatabase();
        try {
            contentValues = new ContentValues();
            contentValues.put(DBHelper.CURDIR, newDir);
            database.update(DBHelper.C_TABLE, contentValues, null, null);
            database.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                adapter.notifyDataSetChanged();
                break;
        }
    }

    public static boolean ifMusicExists(String file){
        for (MusicUnits unit : musicUnits) {
            if(unit.File.equalsIgnoreCase(file))return true;
        }
        return  false;
    }

    public  static void updateMusicList(String folder){
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
            insertNewMusic(unit);
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
                insertNewMusic(unit);
            }

        }

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
            time += m<10?"0"+Integer.toString(m):Integer.toString(m);
            time +=":";
            time += s<10?"0"+Integer.toString(s):Integer.toString(s);

        }

        return time;
    }

    private static boolean isMusic(String name){
        String ext = name.substring(name.lastIndexOf(".") +1,name.length());
        if(ext.equalsIgnoreCase("mp3"))return true;
        return false;
    }

    public static boolean isInFolder(String folder){
        for (MusicUnits unit: musicUnits) {
            if(unit.Folder.contains(folder))return true;
        }
        return false;
    }

    public void onTouchProgressBar(View v, MotionEvent event){
        mX = event.getX();
        mY = event.getY();
        int h = v.getHeight();


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if(mY <=h && mY >=0){
                    progressBar. setProgress( (int)(pass * mX*1000));
                    mDuration.setText(getStrTime((int)(pass * mX*1000)));
                }
                break;
            case MotionEvent.ACTION_UP: // отпускание
                mediaPlayer.seekTo((int)(pass * mX*1000));
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
    }

    int calculateGraus(double x, double y , double a){
        double out=0;
        out = Math.abs(y-a) / Math.sqrt(Math.pow( Math.abs(x-a),2) +  Math.pow( Math.abs(y-a),2));
        if(mX-a >= 0 && mY - a >=0) return (int)Math.toDegrees( Math.asin(out));
        else if(mX-a >= 0 && mY - a <0)return (int)(360 - Math.toDegrees( Math.asin(out)));
        else if(mX-a < 0 && mY - a < 0) return (int)(180 + Math.toDegrees( Math.asin(out)));
        else if(mX-a < 0 && mY - a >= 0)return (int)(180 - Math.toDegrees( Math.asin(out)));
        return 0;
    }


    public void onTouchSeekProgress(View v, MotionEvent event){
        mX = event.getX();
        mY = event.getY();
        double a = v.getHeight()/2;

        if((calculateGraus((double)mX,(double)mY,a) - SeekProgressVal) >300){
            MPCurrPos-= 120000;
        }
        else if((calculateGraus((double)mX,(double)mY,a) - SeekProgressVal) <-300){
            MPCurrPos+= 120000;
        }

        MPCurrPos += ((calculateGraus((double)mX,(double)mY,a) - SeekProgressVal)/3)*1000;
        if(MPCurrPos < 0)MPCurrPos=0;
        else if(MPCurrPos>mediaPlayer.getDuration())MPCurrPos = mediaPlayer.getDuration();
        SeekProgressVal = calculateGraus((double)mX,(double)mY,a);
        mDuration.setText(getStrTime(MPCurrPos));
        if((MPCurrPos - MPCurrPos2) >= 0)qwe.setText("+" + getStrTime(MPCurrPos - MPCurrPos2));
        else if((MPCurrPos - MPCurrPos2) < 0)qwe.setText("-" + getStrTime(MPCurrPos2 - MPCurrPos));
        SeekProgress.setProgress(calculateGraus((double)mX,(double)mY,a));
        progressBar.setProgress(MPCurrPos);

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP: // отпускание
                mediaPlayer.seekTo(MPCurrPos);
                break;
        }
    }

    public void onTouchMPlay(View v, MotionEvent event){
        if(musicUnits.size() == 0 )return;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                v.setBackgroundColor(Color.rgb(230, 230, 230));
                return;
            case MotionEvent.ACTION_UP:
                v.setBackgroundColor(Color.WHITE);
                if (status == PAUSE) {
                    v.setBackgroundResource(R.drawable.play64_on);
                    mediaPlayer.start();
                    startPlayProgressUpdater();
                    status = ON;
                } else if (status == ON) {
                    v.setBackgroundResource(R.drawable.play64_off);
                    mediaPlayer.pause();
                    status = PAUSE;

                } else if (status == STOP) {
                    v.setBackgroundResource(R.drawable.play64_on);
                    initText();
                    startPlay(0, status);
                    status = ON;
                }
        }
    }

    public void onTouchMplayNext(View v, MotionEvent event){
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(status == ON || status == PAUSE) {
                    v.setBackgroundResource(R.drawable.forward_on);
                    musicCurPos = mediaPlayer.getCurrentPosition();
                }
                return;
            case 2:
                if(status == ON || status == PAUSE) {
                    musicCurPos += 1000;
                    progressBar.setProgress(musicCurPos);
                    mDuration.setText(getStrTime(musicCurPos));
                }
                return;
            case MotionEvent.ACTION_UP:
                if(status == ON){
                    v.setBackgroundResource(R.drawable.forward_off);
                    mediaPlayer.seekTo(musicCurPos);
                    mediaPlayer.start();
                }else if(status == PAUSE){
                    v.setBackgroundResource(R.drawable.forward_off);
                    mediaPlayer.seekTo(musicCurPos);
                }
                return;

        }
    }

    public void onTouchMplayPrev(View v, MotionEvent event){
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(status == ON || status == PAUSE) {
                    v.setBackgroundResource(R.drawable.backward_on);
                    musicCurPos = mediaPlayer.getCurrentPosition();
                }
                return;
            case 2:
                if(status == ON || status == PAUSE) {
                    musicCurPos =  musicCurPos - 1000 <= 0? 0: musicCurPos - 1000 ;
                    progressBar.setProgress(musicCurPos);
                    mDuration.setText(getStrTime(musicCurPos));
                }
                return;
            case MotionEvent.ACTION_UP:
                if(status == ON){
                    v.setBackgroundResource(R.drawable.backward_off);
                    mediaPlayer.seekTo(musicCurPos);
                    mediaPlayer.start();
                }else if(status == PAUSE){
                    v.setBackgroundResource(R.drawable.backward_off);
                    mediaPlayer.seekTo(musicCurPos);
                }
                return;
        }

    }

    public void onClickMloop(View v){
        if(LOOP.equals(OFF)){
            Mloop.setBackgroundResource(R.drawable.loop_on);
            LOOP =ON;
        }else{
            Mloop.setBackgroundResource(R.drawable.loop_off);
            LOOP =OFF;
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode){
            case 126: //FONES STOP-PLAY
                if(musicUnits.size()==0)return false;
                if (status.equals(PAUSE)) {
                    Mplay.setBackgroundResource(R.drawable.play64_on);
                    mediaPlayer.start();
                    startPlayProgressUpdater();
                    status = ON;
                } else if (status.equals(ON)) {
                    Mplay.setBackgroundResource(R.drawable.play64_off);
                    mediaPlayer.pause();
                    status = PAUSE;
                } else if (status.equals(STOP)) {
                    Mplay.setBackgroundResource(R.drawable.play64_on);
                    initText();
                    startPlay(0, status);
                    status = ON;
                }
                return true;
            case 87: //FONES more
                if(musicUnits.size()==0)return false;
                lastMusPos++;
                if (lastMusPos >= musicUnits.size()) lastMusPos = 0;
                musicName.setText(musicUnits.get(lastMusPos).Mname);
                musicAuthor.setText(musicUnits.get(lastMusPos).MAuthor);
                startPlay(lastMusPos, status);
                Mplay.setBackgroundResource(R.drawable.play64_on);
                status = ON;
                return true;
            case 88: //FONES minus
                if(musicUnits.size()==0)return false;
                lastMusPos--;
                if (lastMusPos < 0) lastMusPos = musicUnits.size() - 1;
                musicName.setText(musicUnits.get(lastMusPos).Mname);
                musicAuthor.setText(musicUnits.get(lastMusPos).MAuthor);
                startPlay(lastMusPos, status);
                Mplay.setBackgroundResource(R.drawable.play64_on);
                status = ON;
                return true;
        }
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.performClick();
        switch (v.getId()){
            case R.id.progressBar:
                onTouchProgressBar(v,event);
                return true;
            case R.id.Mplay:
                onTouchMPlay(v,event);
                return true;
            case R.id.MplayNext:
                onTouchMplayNext(v,event);
                return true;
            case R.id.MplayPrev:
                onTouchMplayPrev(v,event);
                return true;
            case R.id.SeekProgress:
                onTouchSeekProgress(v,event);
                return true;

        }
        return false;
    }

    @Override
    public void onClick(View view) {
        Intent Activity;
        switch (view.getId()){
            case R.id.MLoop:
                onClickMloop(view);
                break;
            case R.id.fab:
                Activity = new Intent(view.getContext(),AddNewFolder.class);
                startActivityForResult(Activity,1);
                break;
            case R.id.fabRadio:
                Activity = new Intent(view.getContext(),Radio.class);
                startActivityForResult(Activity,1);
                break;
            case R.id.Mseek:
                if(SeekRL.getVisibility() == View.VISIBLE)SeekRL.setVisibility(View.INVISIBLE);
                else SeekRL.setVisibility(View.VISIBLE);
                SeekProgress.setProgress(0);
                SeekProgressVal = 0;
                qwe.setText("+00:00");
                MPCurrPos2 = MPCurrPos = mediaPlayer.getCurrentPosition();
                break;
            case R.id.SeekRL:
               SeekRL.setVisibility(View.INVISIBLE);
                break;

        }

    }

}
