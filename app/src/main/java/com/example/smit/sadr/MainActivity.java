package com.example.smit.sadr;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smit.sadr.Adapters.ListMusicAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import wseemann.media.FFmpegMediaMetadataRetriever;


public class MainActivity extends AppCompatActivity implements View.OnTouchListener{

    public static List<MusicUnits> musicUnits = new ArrayList<MusicUnits>();
    public  static List<String> folderUnits = new ArrayList<String>();
    public static ImageButton Mplay;
    ImageButton Mloop;
    ImageButton MplayNext;
    ImageButton MplayPrev;
    static Integer OFF =0;
    static Integer ON =1;
    static Integer PAUSE = 2;
    static  Integer STOP =0;
    static Integer status=STOP;
    static Integer LOOP=OFF;
    static Integer sendStatus = STOP;
    static Integer PHONE_STATUS = OFF;
    Integer lastMusPos = 0;
    static Long lastTime;
    static MediaPlayer mediaPlayer;
    SeekBar seekBar;
    ProgressBar progressBar;
    Handler seekHandler = new Handler();
    TextView musicName;
    TextView musicAuthor;
    static ListView listmusic;
    TextView mDuration;
    static Integer MODE=1;
    int musicCurPos = 0;
    public  static ProgressBar vProgressBar;

    float mX;
    float mY;
    float pass;
    int w;

    static ListMusicAdapter adapter;
    Bundle bundle=new Bundle();
    static DBHelper dbHelper;
    static SQLiteDatabase database;
    static ContentValues contentValues;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        w = 540000;
        dbHelper = new DBHelper(this);
        musicUnits = getMusicListFromBD();
        folderUnits = getFolderListFromBD();
        initText();
        //vProgressBar = (ProgressBar)findViewById(R.id.vprogressbar);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        musicName.setMaxWidth(350);
        musicName.setTextColor(Color.WHITE);
        musicAuthor.setTextColor(Color.WHITE);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(12, 145, 56)));
        adapter = new ListMusicAdapter(this, musicUnits);
        listmusic.setAdapter(adapter);
        mediaPlayer = new MediaPlayer();
        for (String folder : folderUnits) {
            updateMusicList(folder);
            adapter.notifyDataSetChanged();
        }

        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (status != STOP) {
                    SeekBar sb = (SeekBar) v;
                    mediaPlayer.seekTo(sb.getProgress());
                }
                return false;
            }
        });

        listmusic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                musicName.setText(musicUnits.get(position).Mname);
                musicAuthor.setText(musicUnits.get(position).MAuthor);
                startPlay(position, status);
                status = ON;
                Mplay.setBackgroundResource(R.drawable.play_on);
                lastMusPos = position;
            }
        });
        Mplay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(musicUnits.size() == 0 )return false;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setBackgroundColor(Color.rgb(230, 230, 230));
                        return true;
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
                        return true;
                }
                return false;
            }
        });

        MplayNext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if(status == ON || status == PAUSE) {
                            v.setBackgroundResource(R.drawable.forward_on);
                            musicCurPos = mediaPlayer.getCurrentPosition();
                        }
                        return true;
                    case 2:
                        if(status == ON || status == PAUSE) {
                            musicCurPos += 1000;
                            progressBar.setProgress(musicCurPos);
                            mDuration.setText(getStrTime(musicCurPos));
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        if(status == ON){
                            v.setBackgroundResource(R.drawable.forward_off);
                            mediaPlayer.seekTo(musicCurPos);
                            mediaPlayer.start();
                        }else if(status == PAUSE){
                            v.setBackgroundResource(R.drawable.forward_off);
                            mediaPlayer.seekTo(musicCurPos);
                        }
                        return true;

                }
                return false;
            }
        });
        MplayPrev.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if(status == ON || status == PAUSE) {
                            v.setBackgroundResource(R.drawable.backward_on);
                            musicCurPos = mediaPlayer.getCurrentPosition();
                        }
                        return true;
                    case 2:
                        if(status == ON || status == PAUSE) {
                            musicCurPos =  musicCurPos - 1000 <= 0? 0: musicCurPos - 1000 ;
                            progressBar.setProgress(musicCurPos);
                            mDuration.setText(getStrTime(musicCurPos));
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        if(status == ON){
                            v.setBackgroundResource(R.drawable.backward_off);
                            mediaPlayer.seekTo(musicCurPos);
                            mediaPlayer.start();
                        }else if(status == PAUSE){
                            v.setBackgroundResource(R.drawable.backward_off);
                            mediaPlayer.seekTo(musicCurPos);
                        }
                        return true;
                }

                return false;
            }
        });

        Mloop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LOOP==OFF){
                   Mloop.setBackgroundResource(R.drawable.loop_on);
                   LOOP =ON;
                }else{
                    Mloop.setBackgroundResource(R.drawable.loop_off);
                    LOOP =OFF;
                }
            }
        });

        progressBar.setOnTouchListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.Folders:
                Intent FoldersActivity = new Intent(this,Folders.class);
                startActivityForResult(FoldersActivity,1);
                return true;
        }
        return(super.onOptionsItemSelected(item));
    }

    public void startPlay( int position, Integer status){
        if(status==ON || status ==PAUSE){
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        try {
            mediaPlayer.setDataSource(musicUnits.get(position).Folder +"/"+ musicUnits.get(position).File );
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());
            pass = (float)mediaPlayer.getDuration() / (float)w;
            Log.e("SADR",mediaPlayer.getDuration() + " / " +  w  + " = " + pass);
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
            status = STOP;
        }

    }

    public void startPlayProgressUpdater() {
        try {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
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
                if(status == ON){
                    if(LOOP == OFF)startPlay(++lastMusPos,status);
                    else startPlay(lastMusPos,status);
                    musicName.setText(musicUnits.get(lastMusPos).Mname);
                    musicAuthor.setText(musicUnits.get(lastMusPos).MAuthor);
                }
                else  mediaPlayer.pause();
            }
        }catch (Exception e){ e.printStackTrace();}
    }

    public void initViews(){
    listmusic = (ListView) findViewById(R.id.listViewMusic);
    musicName = (TextView) findViewById(R.id.MusicName2);
    musicAuthor = (TextView) findViewById(R.id.MusicAuthor2);
    Mplay = (ImageButton) findViewById(R.id.Mplay);
    Mplay.setBackgroundResource(R.drawable.play64_off);
    MplayNext = (ImageButton) findViewById(R.id.MplayNext);
    MplayNext.setBackgroundResource(R.drawable.forward_off);
    MplayPrev = (ImageButton) findViewById(R.id.MplayPrev);
    MplayPrev.setBackgroundResource(R.drawable.backward_off);
    mDuration = (TextView)findViewById(R.id.mDuration);
    Mloop = (ImageButton) findViewById(R.id.MLoop);
    Mloop.setBackgroundResource(R.drawable.loop_off);
    progressBar = (ProgressBar) findViewById(R.id.progressBar);
    //progressBar.setMax(540);
    //progressBar.setProgress(300);
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
        database = dbHelper.getWritableDatabase();
        try {
            database.execSQL("DELETE FROM " + DBHelper.F_TABLE + " WHERE " + DBHelper.FOLDER + "= '" + folder + "'");
            database.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeMusicByFolderFromList(String folder){
        List<MusicUnits> out = new ArrayList<MusicUnits>();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                adapter.notifyDataSetChanged();
                break;
        }
    }

    private static boolean ifMusicExists(String file){
        for (MusicUnits unit : musicUnits) {
            if(unit.File.equalsIgnoreCase(file))return true;
        }
        return  false;
    }

    public  static void updateMusicList(String folder){
        FFmpegMediaMetadataRetriever meta = new FFmpegMediaMetadataRetriever();
        String tmp;
        String name;
        String fullPath;
        int pos;
        File file = new File(folder);
        File[] fileList = file.listFiles();
        for (Integer i = 0; i < fileList.length; i++) {
            if(fileList[i].isDirectory())continue;
            name = fileList[i].getName();
            if(!isMusic(name))continue;
            if(ifMusicExists(name))continue;
            MusicUnits unit = new MusicUnits();
            unit.Folder = folder;
            unit.File = name;
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
                unit.Mtime = getStrTime(unit.MDuration);

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

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode){
            case 126: //FONES STOP-PLAY
                if(musicUnits.size()==0)return false;
                if (status == PAUSE) {
                    Mplay.setBackgroundResource(R.drawable.play64_on);
                    mediaPlayer.start();
                    startPlayProgressUpdater();
                    status = ON;
                } else if (status == ON) {
                    Mplay.setBackgroundResource(R.drawable.play64_off);
                    mediaPlayer.pause();
                    status = PAUSE;
                } else if (status == STOP) {
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
        mX = event.getX();
        mY = event.getY();
        int h = v.getHeight();

        // переключатель в зависимости от типа события
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // нажатие
            case MotionEvent.ACTION_MOVE: // движение
                if(mY <=h && mY >=0){
                    progressBar.setProgress( (int)(pass * mX*1000));
                    //Log.e("SADR",progressBar.getMax() + " : " +  pass + " * " + mX + " ");
                }
                break;
            case MotionEvent.ACTION_UP: // отпускание
                mediaPlayer.seekTo((int)(pass * mX*1000));
                break;
            case MotionEvent.ACTION_CANCEL:
                // ничего не делаем
                break;
        }
        return true;
    }

    private int getPos(int maxPW, int maxMD){
        int out = 0;


     return out;
    }
}
