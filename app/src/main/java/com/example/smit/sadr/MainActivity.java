package com.example.smit.sadr;

import android.content.ContentValues;
import android.content.Context;
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
import android.widget.Button;
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


    public static  String root = "/storage";
    RelativeLayout mainView,SeekRL;
    Button SeekProgressApply;
    ProgressBar SeekProgress, progressBar;
    ImageButton Mloop, MplayNext, MplayPrev, Mplay, Mseek;
    TextView musicName, musicAuthor, mDuration, qwe;
    Handler seekHandler = new Handler();
    ListView listmusic;
    int musicCurPos = 0;
    int SeekProgressVal;

    int first, last;
    float mX;
    float mY;
    float pass;
    int MPCurrPos2, MPCurrPos;

    static ListMusicAdapter adapter;
    FloatingActionButton fab;
    FloatingActionButton fabRadio;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new DBHelper(this);
        getSupportActionBar().hide();
        initViews();

        initText();
        musicName.setMaxWidth(350);
        musicName.setTextColor(Color.WHITE);
        musicAuthor.setTextColor(Color.WHITE);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(12, 145, 56)));
        adapter = new ListMusicAdapter(this, MPlayer.INSTANCE.musicUnits);
        listmusic.setAdapter(adapter);



        listmusic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                musicName.setText(MPlayer.INSTANCE.musicUnits.get(position).Mname);
                musicAuthor.setText(MPlayer.INSTANCE.musicUnits.get(position).MAuthor);
                MPlayer.INSTANCE.Play(position);
                Mplay.setBackgroundResource(R.drawable.play_on);
                prepare();
            }
        });

        fab.setOnClickListener(this);
        fabRadio.setOnClickListener(this);
        Mloop.setOnClickListener(this);
        Mseek.setOnClickListener(this);
        SeekRL.setOnClickListener(this);
        SeekProgressApply.setOnClickListener(this);
        progressBar.setOnTouchListener(this);
        Mplay.setOnTouchListener(this);
        MplayNext.setOnTouchListener(this);
        MplayPrev.setOnTouchListener(this);
        SeekProgress.setOnTouchListener(this);

    }

    public void prepare(){
        pass = (float)MPlayer.INSTANCE.getDuration() / (float)540000;
        progressBar.setProgress(0);
        progressBar.setMax(MPlayer.INSTANCE.getDuration());
        startPlayProgressUpdater();
    }

    public void startPlayProgressUpdater() {
        try {
            int pos = MPlayer.INSTANCE.getCurrentPosition();
            progressBar.setProgress(pos);
            mDuration.setText(MPlayer.INSTANCE.getStrTime(pos));
            if(SeekRL.getVisibility() == View.VISIBLE){
                if((pos - MPCurrPos) >= 0)qwe.setText("-" + MPlayer.INSTANCE.getStrTime(pos - MPCurrPos));
                else if((pos- MPCurrPos) < 0)qwe.setText("+" + MPlayer.INSTANCE.getStrTime(MPCurrPos - pos));
            }

            if (MPlayer.INSTANCE.isPlaying()) {
                    Runnable notification = new Runnable() {
                        public void run() {
                            startPlayProgressUpdater();
                        }
                    };
                seekHandler.postDelayed(notification,1000);
            }else{
                if(MPlayer.INSTANCE.isPlay()){
                    if(!MPlayer.INSTANCE.isLoop())MPlayer.INSTANCE.playNext();
                    else MPlayer.INSTANCE.Play(-1);
                    musicName.setText(MPlayer.INSTANCE.getName(-1));
                    musicAuthor.setText(MPlayer.INSTANCE.getAuthor(-1));
                }
                else  MPlayer.INSTANCE.pause();
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
        SeekProgressApply = findViewById(R.id.SeekProgressApply);
    }

    public void initText(){
        musicName.setText(MPlayer.INSTANCE.getName(0));
        musicAuthor.setText(MPlayer.INSTANCE.getAuthor(0));
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


    public void onTouchProgressBar(View v, MotionEvent event){
        mX = event.getX();
        mY = event.getY();
        int h = v.getHeight();


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if(mY <=h && mY >=0){
                    progressBar.setProgress( (int)(pass * mX*1000));
                    mDuration.setText(MPlayer.INSTANCE.getStrTime((int)(pass * mX*1000)));
                }
                break;
            case MotionEvent.ACTION_UP:
                MPlayer.INSTANCE.seekTo((int)(pass * mX*1000));
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
        else if(MPCurrPos>MPlayer.INSTANCE.getDuration())MPCurrPos = MPlayer.INSTANCE.getDuration();
        SeekProgressVal = calculateGraus((double)mX,(double)mY,a);
        mDuration.setText(MPlayer.INSTANCE.getStrTime(MPCurrPos));
        if((MPCurrPos - MPCurrPos2) >= 0)qwe.setText("+" + MPlayer.INSTANCE.getStrTime(MPCurrPos - MPCurrPos2));
        else if((MPCurrPos - MPCurrPos2) < 0)qwe.setText("-" + MPlayer.INSTANCE.getStrTime(MPCurrPos2 - MPCurrPos));
        SeekProgress.setProgress(calculateGraus((double)mX,(double)mY,a));
        //progressBar.setProgress(MPCurrPos);
        SeekProgressApply.setRotation(calculateGraus((double)mX,(double)mY,a));
        //if(event.getAction() == MotionEvent.ACTION_UP)MPlayer.INSTANCE.seekTo(MPCurrPos);
    }

    public void onTouchMPlay(View v, MotionEvent event){
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                v.setBackgroundColor(Color.rgb(230, 230, 230));
                return;
            case MotionEvent.ACTION_UP:
                v.setBackgroundColor(Color.WHITE);
                if (MPlayer.INSTANCE.isPause()) {
                    v.setBackgroundResource(R.drawable.play64_on);
                    MPlayer.INSTANCE.start();
                    startPlayProgressUpdater();
                } else if (MPlayer.INSTANCE.isPlay()) {
                    v.setBackgroundResource(R.drawable.play64_off);
                    MPlayer.INSTANCE.pause();
                } else if (MPlayer.INSTANCE.isStop()) {
                    v.setBackgroundResource(R.drawable.play64_on);
                    initText();
                    MPlayer.INSTANCE.Play(0);
                }
        }
    }

    public void onTouchMplayNext(View v, MotionEvent event){
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                v.setBackgroundResource(R.drawable.forward_on);
                return;
            case 2:
                return;
            case MotionEvent.ACTION_UP:
                v.setBackgroundResource(R.drawable.forward_off);
                MPlayer.INSTANCE.playNext();
                prepare();
                return;
        }
    }

    public void onTouchMplayPrev(View v, MotionEvent event){
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                v.setBackgroundResource(R.drawable.backward_on);
                return;
            case 2:
                return;
            case MotionEvent.ACTION_UP:
                v.setBackgroundResource(R.drawable.backward_off);
                MPlayer.INSTANCE.playPrev();
                prepare();
                return;
        }

    }

    public void onClickMloop(View v){
        if(!MPlayer.INSTANCE.isLoop()){
            Log.e("SADR", "LOOP - TRUE");
            Mloop.setBackgroundResource(R.drawable.loop_on);
            MPlayer.INSTANCE.setLoop(true);
        }else{
            Log.e("SADR", "LOOP - TRUE");
            Mloop.setBackgroundResource(R.drawable.loop_off);
            MPlayer.INSTANCE.setLoop(false);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode){
            case 126: //FONES STOP-PLAY
                if (MPlayer.INSTANCE.isPause()) {
                    Mplay.setBackgroundResource(R.drawable.play64_on);
                    MPlayer.INSTANCE.start();
                    startPlayProgressUpdater();
                } else if (MPlayer.INSTANCE.isPlay()) {
                    Mplay.setBackgroundResource(R.drawable.play64_off);
                    MPlayer.INSTANCE.pause();
                } else if (MPlayer.INSTANCE.isStop()) {
                    Mplay.setBackgroundResource(R.drawable.play64_on);
                    initText();
                    MPlayer.INSTANCE.Play(0);
                }
                return true;
            case 87: //FONES more
                MPlayer.INSTANCE.playNext();
                musicName.setText(MPlayer.INSTANCE.getName(-1));
                musicAuthor.setText(MPlayer.INSTANCE.getAuthor(-1));

                Mplay.setBackgroundResource(R.drawable.play64_on);
                return true;
            case 88: //FONES minus
                MPlayer.INSTANCE.playPrev();
                musicName.setText(MPlayer.INSTANCE.getName(-1));
                musicAuthor.setText(MPlayer.INSTANCE.getAuthor(-1));
                Mplay.setBackgroundResource(R.drawable.play64_on);
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
                qwe.setText("00:00");
                MPCurrPos2 = MPCurrPos = MPlayer.INSTANCE.getCurrentPosition();
                break;
            case R.id.SeekRL:
               //SeekRL.setVisibility(View.INVISIBLE);
                break;
            case R.id.SeekProgressApply:
                MPlayer.INSTANCE.seekTo(MPCurrPos);
                qwe.setText("00:00");
                SeekProgress.setProgress(0);
                SeekProgressVal = 0;
                MPCurrPos2 = MPCurrPos = MPlayer.INSTANCE.getCurrentPosition();
                SeekProgressApply.setRotation(0);
                break;

        }

    }

}
