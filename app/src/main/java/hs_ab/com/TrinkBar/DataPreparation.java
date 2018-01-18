package hs_ab.com.TrinkBar;

import android.content.Context;
import android.database.Cursor;

import com.google.gson.Gson;

import java.util.List;

import hs_ab.com.TrinkBar.models.Bar;
import hs_ab.com.TrinkBar.models.Image;

/**
 * Created by agrein on 1/18/18.
 */

public class DataPreparation {

    private DBAdapter db;
    private Context mctx;
    private Gson gson;
    List<Bar> barList;
    List<Image> imageList;

    public DataPreparation(Context ctx){
        mctx= ctx;
        db = new DBAdapter(mctx);
        gson= new Gson();
    }

    public List<Bar> getBarList(){

        db.open();
        Cursor barTable = db.getAllDataTableBarImages(); //!!!!


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


        db.open();
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

        db.open();
        Cursor barTable = db.getAllDataTableBarImages(); //!!!!

        String imageString = barTable.getString(1);
        Image imageObject = gson.fromJson(imageString, Image.class);

        return imageObject;
    }

    public Bar getBarbyId(String hash){

        db.open();
        Cursor imageTable = db.getAllDataTableBarImages(); //!!!!

        String barsString = imageTable.getString(1);
        Bar barObject = gson.fromJson(barsString, Bar.class);

        return barObject;
    }


    public void insertBar(String hash, String jsonData){
        db.open();


    }

    public void insertImage(String hash, String jsonData){
        db.open();


    }





}
