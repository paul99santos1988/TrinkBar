package hs_ab.com.TrinkBar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import hs_ab.com.TrinkBar.models.Bar;
import hs_ab.com.TrinkBar.models.Image;

/**
 * Created by tabo on 1/10/18.
 */

public class DBAdapter {

    private DBHelper myDBHelper;
    private SQLiteDatabase dbSQL;

    //dababase setup
    public static final String DB_NAME = "TrinkBar-app-db";
    public static final int DB_VERSION = 1;

    //entity relationship model
    //table bars
    public static final String TABLE_BAR_DETAILS = "barsAschaffenburg";
    public static final String TABLE_BAR_IMAGES = "barImagesAschaffenburg";

    public static final String KEY_ID_DETAILS = "hashID";
    public static final String KEY_DETAILS = "JSONContentBars";

    public static final String KEY_ID_IMAGES = "hashID";
    public static final String KEY_IMAGES = "images";

    private static DBAdapter db = null;

    private Context mctx;
    private Gson gson;
    List<Bar> barList = new ArrayList<>();
    List<Image> imageList = new ArrayList<>();

    //public static final JSONObject KEY_JSON_OBJECT = "JSON-image";

    private static final String TAG = "DBAdapter";


    public static DBAdapter getInstance(Context context) {
        if(db == null) {
            db = new DBAdapter(context);
        }
        return db;
    }


    //constructor
    protected DBAdapter(Context context){
        mctx= context;
        myDBHelper = new DBHelper (context, DB_NAME, null, DB_VERSION);
        gson= new Gson();
        this.open();
    }



    public List<Bar> getBarList(){

        Cursor barTable = db.getAllDataTableBars(); //!!!!


        int numbDBrows = barTable.getCount();
        barTable.moveToLast();

        while(numbDBrows >= 0){
            String barsString = barTable.getString(1);
            Bar barObject = gson.fromJson(barsString, Bar.class);
            barList.add(barObject);

            numbDBrows--;
            if((numbDBrows != 0) && (numbDBrows > 0) ) {
                barTable.moveToPrevious();
            }
        }

        return barList;
    }


    public List<Image> getImageList(){


        Cursor imageTable = db.getAllDataTableBarImages(); //!!!!


        int numbDBrows = imageTable.getCount();
        imageTable.moveToLast();

        while(numbDBrows >= 0){
            String imageString = imageTable.getString(1);
            Image imageObject = gson.fromJson(imageString, Image.class);
            imageList.add(imageObject);

            numbDBrows--;
            if((numbDBrows != 0) && (numbDBrows > 0) ) {
                imageTable.moveToPrevious();
            }
        }

        return imageList;
    }


    public Image getImagebyId(String hash){

        Cursor imageTable = db.getDataTableBarImagesById(hash); //!!!!

        if (imageTable != null)
        {
            imageTable.moveToFirst();
            String imageString = imageTable.getString(0);
            Image imageObject = gson.fromJson(imageString, Image.class);
            return imageObject;
        }
        return null;
    }

    public Bar getBarbyId(String hash){

        Cursor barTable = db.getDataTableBarById(hash);//!!!!

        if (barTable != null)
        {
            barTable.moveToFirst();
        String barsString = barTable.getString(0);
        Bar barObject = gson.fromJson(barsString, Bar.class);

        return barObject;
        }
        return null;
    }



    // open connection to database
    private void open() throws SQLException {
        Log.i(TAG, "try to open database");
        try {
            dbSQL = myDBHelper.getWritableDatabase();
        } catch (SQLException e) {
            dbSQL = myDBHelper.getReadableDatabase();
        }

    }

    // close connection to database
    private void close() {
        dbSQL.close();
        myDBHelper.close();
    }

    // data handling methods of class DBAdapter
    // insert a data object into table "barsAschaffenburg"
    public long insertBars (String barId, String barContent) {

        ContentValues cv_dbContent = new ContentValues();
        cv_dbContent.put(KEY_ID_DETAILS, barId);
        cv_dbContent.put(KEY_DETAILS, barContent);
        Log.i(TAG, "insert bar data");
        long newInsertId = dbSQL.insert(TABLE_BAR_DETAILS, null, cv_dbContent);
        Log.i(TAG, "return bar data");
        return newInsertId;
    }

    // insert a data object into table "barImagesAschaffenburg"
    public long insertImage (String barId, String image) {

        ContentValues cv_dbContent = new ContentValues();
        cv_dbContent.put(KEY_ID_IMAGES, barId);
        cv_dbContent.put(KEY_IMAGES, image);
        Log.i(TAG, "insert image data");
        long newInsertId = dbSQL.insert(TABLE_BAR_IMAGES, null, cv_dbContent);
        Log.i(TAG, "return image data");
        return newInsertId;
    }

    // get all content values from table "barsAschaffenburg"
    private Cursor getAllDataTableBars() {
        String[] allColumns = new String[] { KEY_ID_DETAILS, KEY_DETAILS };
        Cursor results = dbSQL.query(TABLE_BAR_DETAILS, allColumns, null, null, null, null, null);
        Log.i(TAG, "getAllDataTableBars: return table bars");
        return results;
    }

    // get all content values from table "barImagesAschaffenburg"
    private Cursor getAllDataTableBarImages() {
        String[] allColumns = new String[] { KEY_ID_IMAGES, KEY_IMAGES };
        Cursor results = dbSQL.query(TABLE_BAR_IMAGES, allColumns, null, null, null, null, null);
        Log.i(TAG, "getAllDataTableBars: return table bar_images");
        return results;
    }

    // get content values from table "barsAschaffenburg" via the bar-id
    private Cursor getDataTableBarById(String id) {
        String[] dataBar = new String[] {KEY_DETAILS};
        String[] idArgs = new String[] {id};
        Cursor results = dbSQL.query(TABLE_BAR_DETAILS, dataBar, KEY_ID_DETAILS + " =? " , idArgs, null, null, null);
        Log.i(TAG, "getAllDataTableBars: return bar data from id= "+ id);
        return results;
    }

    // get content values from table "barImagesAschaffenburg" via the bar-id
    private Cursor getDataTableBarImagesById(String id) {
        String[] dataBarImage = new String[] {KEY_IMAGES};
        String[] idArgs = new String[] {id};
        Cursor results = dbSQL.query(TABLE_BAR_IMAGES, dataBarImage, KEY_ID_DETAILS + " = ?;" , idArgs, null, null, null);
        Log.i(TAG, "getAllDataTableBars: return image data from id= "+ id);
        return results;
    }



    // delete one object tuple in table "barsAschaffenburg"
    public void removeDataTableBar(long id) {
        String toDelete = KEY_ID_DETAILS + "=?";
        String[] deleteArgs = new String[] { String.valueOf(id) };
        dbSQL.delete(TABLE_BAR_DETAILS, toDelete, deleteArgs);
    }

    // delete one object tuple in table "barImagesAschaffenburg"
    public void removeDataTableBarImages (long id) {
        String toDelete = KEY_ID_IMAGES + "=?";
        String[] deleteArgs = new String[] { String.valueOf(id) };
        dbSQL.delete(TABLE_BAR_IMAGES, toDelete, deleteArgs);
    }

    //
    // internal derivative of class SQLiteOpenHelper which offers creation functions
    private class DBHelper extends SQLiteOpenHelper {

        private static final String CREATE_DB_TABLE_BARS = "CREATE TABLE IF NOT EXISTS " + TABLE_BAR_DETAILS + " (" + KEY_ID_DETAILS + " TEXT NOT NULL, " + KEY_DETAILS + " TEXT NOT NULL, PRIMARY KEY (" + KEY_ID_DETAILS + ") );";
        private static final String CREATE_DB_TABLE_IMAGES = "CREATE TABLE IF NOT EXISTS " + TABLE_BAR_IMAGES + " (" + KEY_ID_IMAGES + " TEXT NOT NULL, " + KEY_IMAGES + " TEXT NOT NULL, PRIMARY KEY (" + KEY_ID_IMAGES + ") );";

        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        
        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i(TAG, "create databases with execSQL");
            db.execSQL(CREATE_DB_TABLE_BARS);
            db.execSQL(CREATE_DB_TABLE_IMAGES);
            Log.i(TAG,"created tables");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Upgrage bei Versionsänderung: Wie hat sich das Datenmodell verändert? Immer individuell je nach Datenbankversion!
        }
    }
}

