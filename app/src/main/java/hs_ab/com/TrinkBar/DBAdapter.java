package hs_ab.com.TrinkBar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
    public static final String TABLE_BARS = "barsAschaffenburg";
    public static final String KEY_ID = "_id";
    public static final String KEY_CONTENT = "JSONContent";
    //public static final JSONObject KEY_JSON_OBJECT = "JSON-image";

    private static final String TAG = "DBAdapter";

    //constructor
    public DBAdapter(Context context){
        myDBHelper = new DBHelper (context, DB_NAME, null, DB_VERSION);
    }

    // open connection to database
    public void open() throws SQLException {
        Log.i(TAG, "try to open database");
        try {
            dbSQL = myDBHelper.getWritableDatabase();
        } catch (SQLException e) {
            dbSQL = myDBHelper.getReadableDatabase();
        }

    }

    // close connection to database
    public void close() {
        dbSQL.close();
        myDBHelper.close();
    }

    // data handling methods of class DBAdapter
    // insert a data object into table "bars-Aschaffenburg"
    public long insertData(Object myContentObject) {
        // Datensammlung für den einzufügenden Datensatz erstellen (ContentValues)
        // nutzt Schlüssel-Wert-Mechanismus
        // es werden die Konstanten v. o. genutzt, um Fehler zu vermeiden

        ContentValues cv_dbContent = new ContentValues();
        cv_dbContent.put(KEY_CONTENT, myContentObject.toString());
        //v.put(KEY_SECOND_EXAMPLE, myExampleObject.toString()); // exemparisch einfach toString()
        Log.i(TAG, "insert Data");
        long newInsertId = dbSQL.insert(TABLE_BARS, null, cv_dbContent);
        Log.i(TAG, "return Data");
        return newInsertId;
    }

    // get all content values from table "bars-Aschaffenburg"
    public Cursor getAllData() {
        String[] allColumns = new String[] { KEY_ID, KEY_CONTENT };
        Cursor results = dbSQL.query(TABLE_BARS, allColumns, null, null, null, null, null);
        return results;
    }


    // delete one object tuple in table "bars-Aschaffenburg"
    public void removeData(long id) {
        String toDelete = KEY_ID + "=?";
        String[] deleteArgs = new String[] { String.valueOf(id) };
        dbSQL.delete(TABLE_BARS, toDelete, deleteArgs);
    }

    //
    // internal derivative of class SQLiteOpenHelper which offers creation functions
    private class DBHelper extends SQLiteOpenHelper {

        private static final String CREATE_DB = "create table if not exists " + TABLE_BARS + " (" + KEY_ID + " integer primary key autoincrement, " + KEY_CONTENT + " text not null);";

        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i(TAG, "create database with execSQL");
            db.execSQL(CREATE_DB);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Upgrage bei Versionsänderung: Wie hat sich das Datenmodell verändert? Immer individuell je nach Datenbankversion!
        }
    }
}

