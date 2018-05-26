package com.example.smit.sadr;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


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

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
}
