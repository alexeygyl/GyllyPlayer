package com.example.smit.sadr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.example.smit.sadr.Adapters.ListNewFolderAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AddNewFolder extends AppCompatActivity {
    public static List<String> folderUnits;
    public static ListView listFolder;
    public static  String root = "/storage";
    public  static String currentDir;
    ListNewFolderAdapter adapter;
    static Integer posClicked=-1;
    public  static Handler mHandler;
    Integer toUpdate=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_folder);
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(40, 40, 40)));
        //getSupportActionBar().setTitle(root);
        getSupportActionBar().hide();
        listFolder = (ListView)findViewById(R.id.listViewNewFolders);
        currentDir=MainActivity.getCurDirFromBD();
        currentDir = currentDir==null?root:currentDir;
        folderUnits =getListFolder(currentDir);
        adapter = new ListNewFolderAdapter(this, folderUnits);
        listFolder.setAdapter(adapter);
        listFolder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0 && !currentDir.equals(root)){
                    currentDir = currentDir.substring(0,currentDir.lastIndexOf("/"));
                    folderUnits=getListFolder(currentDir);
                    adapter = new ListNewFolderAdapter(AddNewFolder.this, folderUnits);
                    listFolder.setAdapter(adapter);
                    getSupportActionBar().setTitle(currentDir);
                }else{
                    File f = new File(currentDir + "/" + folderUnits.get(position));
                    if (f.isDirectory()) {
                        currentDir += "/" + folderUnits.get(position);
                        folderUnits = getListFolder(currentDir);
                        adapter = new ListNewFolderAdapter(AddNewFolder.this, folderUnits);
                        listFolder.setAdapter(adapter);
                        getSupportActionBar().setTitle(currentDir);
                    }
                    else if(f.isFile()){
                        CheckBox cb = (CheckBox)view.findViewById(R.id.checkFolder);
                        cb.setChecked(!cb.isChecked());
                        if(cb.isChecked()) addFolder(position);
                        else delFolder(position);
                    }
                }
            }
        });

       mHandler = new Handler(){
            public void handleMessage(Message msg) {
                //Toast.makeText(AddNewFolder.class, "Added", Toast.LENGTH_SHORT).show();
            }
        };
    }

    public  List<String> getListFolder(String path){
        List<String> listFolders = new ArrayList<String>();
        try {
            File  list = new File(path);
            if(!path.equalsIgnoreCase(root))listFolders.add("..");
            for(String elem : list.list()){
                File f = new File(path+"/"+elem);
                if(f.isDirectory() && !isFolderEmpty(f))listFolders.add(elem);
                else if(f.isFile()){
                    if(getFileFormat(elem).equals("mp3"))listFolders.add(elem);
                    else if (getFileFormat(elem).equals("wav"))listFolders.add(elem);
                }
            }
        }catch (Exception e){e.printStackTrace();}
        return listFolders;
    }

    public  String getFileFormat(String file){
        return  file.substring(file.lastIndexOf(".")+1, file.length());
    }

    public boolean isFolderEmpty(File f){
        if(f.list().length == 0)return true;
        for (String elem: f.list()) {
            File f2 = new File(f.getAbsolutePath()+"/"+elem);
            if(f2.isDirectory())return false;
            else if(f2.isFile()){
                if(getFileFormat(elem).equals("mp3"))return false;
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(toUpdate, returnIntent);
        MainActivity.setCurDirBD(currentDir);
        this.finish();
    }


    public static void showAlert(int pos){
        posClicked = pos;
        mHandler.sendEmptyMessage(0);
    }

    public static void addFolder(int pos) {
       File file = new File(currentDir + "/" + folderUnits.get(pos));
       if(file.isFile()){
           MainActivity.updateMusicList(currentDir + "/" + folderUnits.get(pos));
       }
       else if (!MainActivity.folderUnits.contains(currentDir + "/" + folderUnits.get(pos))) {
            MainActivity.folderUnits.add(currentDir + "/" + folderUnits.get(pos));
            MainActivity.insertNewFolder(currentDir + "/" + folderUnits.get(pos));
            MainActivity.updateMusicList(currentDir + "/" + folderUnits.get(pos));

        }
    }

    public static void delFolder(int pos){
        File file = new File(currentDir + "/" + folderUnits.get(pos));
        if(file.isFile()){
            MainActivity.removeMusic(folderUnits.get(pos));
            MainActivity.removeMusicFromList(folderUnits.get(pos));
        }else{
            MainActivity.removeMusicByFolderFromBD(currentDir + "/"+folderUnits.get(pos));
            MainActivity.removeMusicByFolderFromList(currentDir + "/"+folderUnits.get(pos));
            MainActivity.removeFolderFromBD(currentDir + "/"+folderUnits.get(pos));
            MainActivity.removeFolder(currentDir + "/"+folderUnits.get(pos));
        }
    }
}
