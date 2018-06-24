package com.example.smit.sadr;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class DBHelper extends SQLiteOpenHelper {
    public static  int DATABASE_VERSION = 1;
    public static  String DATABASE_NAME = "BD";
    public static  String M_TABLE = "MusicList";
    public static  String F_TABLE = "FolderList";
    public static  String C_TABLE = "Config";

    public static  String NAME = "Name";
    public static  String OWNER = "Owner";
    public static  String TIME = "Time";
    public static  String DURATION = "Duration";
    public static  String FILE = "File";
    public static  String FOLDER = "folder";

    public static  String CURDIR = "curdir";

    String rootDir = "/storage";

    private SQLiteDatabase database;
    ContentValues contentValues;
    public static DBHelper INSTANCE = null;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        if(INSTANCE == null)INSTANCE = this;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + M_TABLE + "(" +
                NAME + " text," +
                OWNER + " text," +
                FILE + " text," +
                FOLDER + " text," +
                TIME + " text," +
                DURATION + " integer " +
                ")");
        db.execSQL("create table " + F_TABLE + "(" +
                FOLDER + " text" +
                ")");

        db.execSQL("create table " + C_TABLE + "(" +
                CURDIR + " text" +
                ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + M_TABLE);
        db.execSQL("drop table if exists " + F_TABLE);
        db.execSQL("drop table if exists " + C_TABLE);
        onCreate(db);

    }


    public List<MusicUnits> getMusicList(){
        List<MusicUnits> musicList = new ArrayList<MusicUnits>();
        database = this.getReadableDatabase();
        String coluns[] = {NAME,OWNER,TIME,FILE,DURATION,FOLDER };
        try {
            Cursor cursor = database.query(M_TABLE, coluns, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                int nameInd = cursor.getColumnIndex(NAME);
                int ownerInd = cursor.getColumnIndex(OWNER);
                int timeInd = cursor.getColumnIndex(TIME);
                int fileInd = cursor.getColumnIndex(FILE);
                int folderInd = cursor.getColumnIndex(FOLDER);
                int durationInd = cursor.getColumnIndex(DURATION);
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

    public void setCurDir(String newDir){
        database = this.getWritableDatabase();
        try {
            contentValues = new ContentValues();
            contentValues.put(CURDIR, newDir);
            database.update(C_TABLE, contentValues, null, null);
            database.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getCurDir(){
        String dir = null;
        database = this.getReadableDatabase();
        String coluns[] = {CURDIR};
        try {
            Cursor cursor = database.query(C_TABLE, coluns, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                int dirInd = cursor.getColumnIndex(CURDIR);
                do {
                    dir = cursor.getString(dirInd);
                } while (cursor.moveToNext());

            }else  {
                database.close();
                database = this.getWritableDatabase();
                try {
                    contentValues = new ContentValues();
                    contentValues.put(DBHelper.CURDIR, rootDir);
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

    public void removeMusicByFolder(String folder){
        database = this.getWritableDatabase();
        try {
            database.execSQL("DELETE FROM " + DBHelper.M_TABLE + " WHERE " + DBHelper.FOLDER + "= '" + folder + "'");
            database.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertNewMusic(MusicUnits unit){
        database = this.getWritableDatabase();
        try {
            contentValues = new ContentValues();
            contentValues.put(NAME, unit.Mname);
            contentValues.put(TIME, unit.Mtime);
            contentValues.put(DURATION, unit.MDuration);
            contentValues.put(OWNER, unit.MAuthor);
            contentValues.put(FILE, unit.File);
            contentValues.put(FOLDER, unit.Folder);
            database.insert(M_TABLE, null, contentValues);
            database.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeMusic(String file){
        database = this.getWritableDatabase();
        try {
            database.execSQL("DELETE FROM " + M_TABLE + " WHERE " + FILE + "= '" + file + "'");
            database.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeFolder(String folder){
        database = this.getWritableDatabase();
        try {
            database.execSQL("DELETE FROM " + F_TABLE + " WHERE " + FOLDER + "= '" + folder + "'");
            database.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertNewFolder(String folder){
        database = this.getWritableDatabase();
        try {
            contentValues = new ContentValues();
            contentValues.put(FOLDER, folder);
            database.insert(F_TABLE, null, contentValues);
            database.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  List<String> getFolderList(){
        List<String> folderUnits = new ArrayList<String>();
        database = this.getReadableDatabase();
        String coluns[] = {FOLDER};
        try {
            Cursor cursor = database.query(F_TABLE, coluns, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                int folderInd = cursor.getColumnIndex(FOLDER);
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

}
