package com.example.termproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;

public class JangoDbHelper extends SQLiteOpenHelper {

    private static JangoDbHelper sInstance;

    private static final int DB_VERSION=1;
    public static final String DB_NAME = "Jango.db";

    String DbDate;
    DateFormat format = new SimpleDateFormat("yyyyMMdd");


    private static final String SQL_CREATE_ENTRIES=
            String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s BLOB)",
                    JangoDB.JangoTable.TABLE_NAME,
                    JangoDB.JangoTable._ID,
                    JangoDB.JangoTable.COLUMN_NAME_TITLE,
                    JangoDB.JangoTable.COLUMN_NAME_CONTENTS,
                    JangoDB.JangoTable.COLUMN_NAME_IMAGE);


    private static String objectTable=ObjectDB.ObjectTable.TABLE_NAME;
    private static String ID= ObjectDB.ObjectTable._ID;
    private static String nameColumn=ObjectDB.ObjectTable.COLUMN_NAME_TITLE;
    private static String memoColum=ObjectDB.ObjectTable.COLUMN_NAME_CONTENTS;
    private static String dead=ObjectDB.ObjectTable.COLUMN_NAME_DEAD;
    private static String count= ObjectDB.ObjectTable.COLUMN_NAME_COUNT;
    private static String JangoId= ObjectDB.ObjectTable.COLUMN_JANGO_ID;
    private static String typeColum=ObjectDB.ObjectTable.COLUMN_NAME_TYPE;
    private static String image=ObjectDB.ObjectTable.COLUMN_NAME_IMAGE;
    private static String JangoName= ObjectDB.ObjectTable.COLUMN_JANGO_NAME;


    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + JangoDB.JangoTable.TABLE_NAME;

    private static final String SQL_DELETE_ENTRIES2 =
            "DROP TABLE IF EXISTS " + ObjectDB.ObjectTable.TABLE_NAME;

    public static JangoDbHelper getInstance(Context context){
        if(sInstance == null){
            sInstance = new JangoDbHelper(context);
        }
        return sInstance;
    }
    public Context mContext;
    public JangoDbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL("create table " + objectTable + "("+
                ID + " integer primary key autoincrement, " +
                nameColumn + " text, " +
                count + " text," +
                memoColum + " text, " +
                dead + " text, " +
                typeColum + " text, " +
                image + " blob, " +
                JangoName + " string references " + JangoDB.JangoTable.TABLE_NAME + "(" + JangoDB.JangoTable.COLUMN_NAME_TITLE + ")," +
                JangoId + " interger references " + JangoDB.JangoTable.TABLE_NAME + "(" + JangoDB.JangoTable._ID + ")" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_DELETE_ENTRIES2);
        onCreate(db);
    }

    // 알림을 위한 유통기한 현황 계산 함수
    public void notice(String currdate, ArrayList arraylistOver, ArrayList arraylistYet) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from Object", null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {

            DbDate = res.getString(res.getColumnIndex(ObjectDB.ObjectTable.COLUMN_NAME_DEAD)+1-1);
            String DbDateFormat = DbDate.replace("/", "");
            try{
                Date currdateF = format.parse(currdate);
                Date DbDateF = format.parse(DbDateFormat);
                long calDate = currdateF.getTime() - DbDateF.getTime();
                long calDateDays= calDate / (24*60*60*1000);

                if (calDateDays > 0) { //유통기한이 지났을 때 리스트에 추가
                    if (arraylistOver.isEmpty()) {
                        arraylistOver.add(res.getString(res.getColumnIndex(ObjectDB.ObjectTable.COLUMN_JANGO_NAME)+1-1) + " 속 " +
                                res.getString(res.getColumnIndex(ObjectDB.ObjectTable.COLUMN_NAME_TITLE)+1-1) + " " +
                                res.getString(res.getColumnIndex(ObjectDB.ObjectTable.COLUMN_NAME_COUNT)+1-1) + "개");
                    }
                    else{
                        arraylistOver.add(", " + res.getString(res.getColumnIndex(ObjectDB.ObjectTable.COLUMN_JANGO_NAME)+1-1) + " 속 " +
                                res.getString(res.getColumnIndex(ObjectDB.ObjectTable.COLUMN_NAME_TITLE)+1-1) + " " +
                                res.getString(res.getColumnIndex(ObjectDB.ObjectTable.COLUMN_NAME_COUNT)+1-1) + "개");
                    }
                }

                else if (calDateDays == 0 || calDateDays == -1) { //유통기한이 당일이거나 하루 남았을 때 리스트에 추가
                    if(arraylistYet.isEmpty()){
                        arraylistYet.add(res.getString(res.getColumnIndex(ObjectDB.ObjectTable.COLUMN_JANGO_NAME)+1-1) + " 속 " +
                                res.getString(res.getColumnIndex(ObjectDB.ObjectTable.COLUMN_NAME_TITLE)+1-1) + " " +
                                res.getString(res.getColumnIndex(ObjectDB.ObjectTable.COLUMN_NAME_COUNT)+1-1) + "개");
                    }
                    else{
                        arraylistYet.add(", " + res.getString(res.getColumnIndex(ObjectDB.ObjectTable.COLUMN_JANGO_NAME)+1-1) + " 속 " +
                                res.getString(res.getColumnIndex(ObjectDB.ObjectTable.COLUMN_NAME_TITLE)+1-1) + " " +
                                res.getString(res.getColumnIndex(ObjectDB.ObjectTable.COLUMN_NAME_COUNT)+1-1) + "개");
                    }
                }
            }
            catch (ParseException e){}
            res.moveToNext();
        }

        if (arraylistOver.isEmpty()){
            arraylistOver.add("유통기한이 지난 품목이 없습니다.");
        }
        else{
            arraylistOver.add("의 유통기한이 지났습니다.");
        }

        if (arraylistYet.isEmpty()){
            arraylistYet.add("유통기한이 임박한 품목이 없습니다.");
        }
        else{
            arraylistYet.add("의 유통기한이 임박했습니다.");
        }
    }


}
