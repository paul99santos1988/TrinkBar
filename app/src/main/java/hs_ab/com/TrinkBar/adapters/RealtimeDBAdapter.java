package hs_ab.com.TrinkBar.adapters;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import hs_ab.com.TrinkBar.models.Bar;
import hs_ab.com.TrinkBar.models.Image;


public class RealtimeDBAdapter {

    private static RealtimeDBAdapter mRealtimeDBAdapter = null;
    private final Context mctx;
    private List<Bar> mbarList = null;
    private List<Image> mimageList = null;


    protected RealtimeDBAdapter(Context context){
        mctx= context;
        mbarList = new ArrayList<>();
        mimageList = new ArrayList<>();
    }


    public static RealtimeDBAdapter getInstance(Context context) {
        if(mRealtimeDBAdapter == null) {
            mRealtimeDBAdapter = new RealtimeDBAdapter(context);
        }
        return mRealtimeDBAdapter;
    }



    public List<Bar> getBarList(){
        return mbarList;
    }


    public List<Image> getImageList(){
        return mimageList;
    }


    public Image getImagebyId(String hash){
        Image image= null;
        for(int i=0;i<mimageList.size();i++){
            if(mimageList.get(i).getId().equals(hash)) {
                image = mimageList.get(i);
            }
        }
       return image;
    }

    public Bar getBarbyId(String hash){

        Bar bar= null;
        for(int i=0;i<mbarList.size();i++){
            if(mbarList.get(i).getId().equals(hash)) {
                bar = mbarList.get(i);
            }
        }
        return bar;
    }

    public Bar getBarbyName(String name){
        Bar bar= null;
        for(int i=0;i<mbarList.size();i++){
            if(mbarList.get(i).getName().equals(name)) {
                bar = mbarList.get(i);
            }
        }
        return bar;
    }

    public void setBarList(Iterator<DataSnapshot> iterator){
        mbarList.clear();
        while((iterator.hasNext())){
            Bar bar = iterator.next().getValue(Bar.class);
            mbarList.add(bar);
        }

    }

    public void setImageList(Iterator<DataSnapshot> iterator){
        mimageList.clear();
        while((iterator.hasNext())){
        Image image = iterator.next().getValue(Image.class);
        mimageList.add(image);
        }
    }

    public void pushBarToList(Bar bar){


    }

    public void pushImageToList(Image image){


    }

    public void DataSnapshotHandler(DataSnapshot dataSnapshot){
        String key = dataSnapshot.getKey();

        Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
        Iterator<DataSnapshot> iterator = snapshotIterator.iterator();

        switch(key){
            case "bars":
                setBarList(iterator);
                break;
            case "images":
                setImageList(iterator);
                break;
        }
    }
}
