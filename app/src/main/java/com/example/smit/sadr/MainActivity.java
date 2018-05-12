package com.example.smit.sadr;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.example.smit.sadr.Adapters.ListMusicAdapter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

     public List<MusicUnits> musicUnits = new ArrayList<MusicUnits>();

    ImageButton Mplay;
    ImageButton Mstop;
    ImageButton MplayNext;
    ImageButton MplayPrev;
    static Integer ON =1;
    static Integer PAUSE = 2;
    static  Integer STOP =0;
    static Integer status=STOP;
    static Integer sendStatus = STOP;
    Integer lastMusPos = 0;
    static Long lastTime;
    static MediaPlayer mediaPlayer;
    SeekBar seekBar;
    Handler seekHandler = new Handler();
    TextView musicName;
    TextView musicAuthor;
    ListView listmusic;
    TextView mDuration;
    static Integer MODE=1;
    ListMusicAdapter adapter;
    public  static ProgressBar vProgressBar;
    Bundle bundle=new Bundle();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        if(!General.folderUnits.contains("/storage/sdcard1/music/all"))
            General.folderUnits.add("/storage/sdcard1/music/all");
        if(!General.folderUnits.contains("/storage/sdcard1/music/rapR"))
            General.folderUnits.add("/storage/sdcard1/music/rapR");
        musicUnits = General.getMusicList(General.folderUnits);
        initText();


        //vProgressBar = (ProgressBar)findViewById(R.id.vprogressbar);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        Mplay.setBackgroundColor(Color.WHITE);
        Mplay.setBackgroundResource(R.drawable.ic_play_arrow_black_36dp);
        musicName.setMaxWidth(350);
        musicName.setTextColor(Color.WHITE);
        musicAuthor.setTextColor(Color.WHITE);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(40, 40, 40)));
        adapter = new ListMusicAdapter(this, musicUnits);
        listmusic.setAdapter(adapter);
        mediaPlayer = new MediaPlayer();

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
                Mplay.setBackgroundResource(R.drawable.ic_pause_black_36dp);
                lastMusPos = position;
            }
        });
        Mplay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setBackgroundColor(Color.rgb(230, 230, 230));
                        return true;
                    case MotionEvent.ACTION_UP:
                        v.setBackgroundColor(Color.WHITE);
                        if (status == PAUSE) {
                            v.setBackgroundResource(R.drawable.ic_pause_black_36dp);
                            mediaPlayer.start();
                            //startPlayProgressUpdater();
                            status = ON;
                        } else if (status == ON) {
                            v.setBackgroundResource(R.drawable.ic_play_arrow_black_36dp);
                            mediaPlayer.pause();
                            status = PAUSE;
                            lastTime = SystemClock.elapsedRealtime();

                        } else if (status == STOP) {
                            v.setBackgroundResource(R.drawable.ic_pause_black_36dp);
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
                        v.setBackgroundColor(Color.rgb(230, 230, 230));
                        return true;
                    case MotionEvent.ACTION_UP:
                        v.setBackgroundColor(Color.WHITE);
                        lastMusPos++;
                        if (lastMusPos >= musicUnits.size()) lastMusPos = 0;
                        musicName.setText(musicUnits.get(lastMusPos).Mname);
                        musicAuthor.setText(musicUnits.get(lastMusPos).MAuthor);
                        startPlay(lastMusPos, status);
                        status = ON;
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
                        v.setBackgroundColor(Color.rgb(230, 230, 230));
                        return true;
                    case MotionEvent.ACTION_UP:
                        v.setBackgroundColor(Color.WHITE);
                        lastMusPos--;
                        if (lastMusPos < 0) lastMusPos = musicUnits.size() - 1;
                        musicName.setText(musicUnits.get(lastMusPos).Mname);
                        musicAuthor.setText(musicUnits.get(lastMusPos).MAuthor);
                        startPlay(lastMusPos, status);
                        status = ON;
                        return true;
                }
                return false;
            }
        });

        Mstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status==ON){
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    status =STOP;
                }
            }
        });


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

    public void startPlay( int position, final Integer status){
        if(status==ON || status ==PAUSE){
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        try {
            mediaPlayer.setDataSource(musicUnits.get(position).Path);
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());
            startPlayProgressUpdater();

        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (IllegalStateException e) {
            e.printStackTrace();
        }

    }

    public void startPlayProgressUpdater() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        mDuration.setText(General.getStrTime(mediaPlayer.getCurrentPosition()));
        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    startPlayProgressUpdater();
                }
            };
            seekHandler.postDelayed(notification,1000);
        }else{
            if(status == ON){
                startPlay(++lastMusPos,status);
                musicName.setText(musicUnits.get(lastMusPos).Mname);
                musicAuthor.setText(musicUnits.get(lastMusPos).MAuthor);
            }
            else  mediaPlayer.pause();
        }

    }

    public void initViews(){
    listmusic = (ListView) findViewById(R.id.listViewMusic);
    musicName = (TextView) findViewById(R.id.MusicName2);
    musicAuthor = (TextView) findViewById(R.id.MusicAuthor2);
    Mplay = (ImageButton) findViewById(R.id.Mplay);
    MplayNext = (ImageButton) findViewById(R.id.MplayNext);
    MplayPrev = (ImageButton) findViewById(R.id.MplayPrev);
    mDuration = (TextView)findViewById(R.id.mDuration);
    Mstop = (ImageButton) findViewById(R.id.MStop);
    //mChronometer = (Chronometer) findViewById(R.id.chronometer);

}

    public void initText(){
        if(musicUnits.size()!=0){
            musicName.setText(musicUnits.get(0).Mname);
            musicAuthor.setText(musicUnits.get(0).MAuthor);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                musicUnits.clear();
                musicUnits = General.getMusicList(General.folderUnits);
                adapter = new ListMusicAdapter(this, musicUnits);
                listmusic.setAdapter(adapter);
                break;
        }
    }



}
