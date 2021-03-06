package hs_ab.com.TrinkBar.adapters;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import hs_ab.com.TrinkBar.R;
import hs_ab.com.TrinkBar.activities.DetailsActivity;
import hs_ab.com.TrinkBar.activities.ListActivity;
import hs_ab.com.TrinkBar.models.Bar;

public class FavoritesListAdapter extends RecyclerView.Adapter<FavoritesListAdapter.BarViewHolder> {

    private static final String TAG = "FavoritesListAdapter";
    private static  String barId;
    private static List<Bar> bars;
    private final Context mCtx;


    public static class BarViewHolder extends RecyclerView.ViewHolder {

        final CardView cv;
        final TextView name;
        final TextView distance;
        final ImageView photo;
        final ImageView icon_walker;
        String dummyBarText;


        BarViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.favorites_list_item);
            name = (TextView) itemView.findViewById(R.id.favorites_list_barname);
            distance = (TextView) itemView.findViewById(R.id.favorites_list_distance);
            photo = (ImageView) itemView.findViewById(R.id.favorites_list_photo);
            icon_walker = (ImageView) itemView.findViewById(R.id.imageView);
            
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v)
                {
                    Log.d(TAG, "onClick: "+ name.getText());
                    for (int i=0;i<bars.size();i++){
                        if(bars.get(i).getName() == name.getText()){

                            barId=  bars.get(i).getId();
                        }

                    }
                    //if no favorites added jump to the bar list instead of the details of a specific bar
                    dummyBarText = v.getResources().getString(R.string.dummy_text_no_favorites);
                    if(name.getText().equals(dummyBarText)){
                        Intent i = new Intent (v.getContext(), ListActivity.class);
                        v.getContext().startActivity(i);
                    }
                    else{
                        Intent i = new Intent(v.getContext(), DetailsActivity.class);
                        i.putExtra("EXTRA_DETAILS_TITLE", barId);
                        v.getContext().startActivity(i);
                    }

                }

            });
        }
    }



    public FavoritesListAdapter(Context mCtx, List<Bar> bars) {
        this.mCtx = mCtx;
        FavoritesListAdapter.bars = bars;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public BarViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.favorites_list_item, viewGroup, false);
        return new BarViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final BarViewHolder barViewHolder, int i) {
        Log.d(TAG, "onBindViewHolder: "+i);
        barViewHolder.name.setText(bars.get(i).getName());

        if(bars.get(i).getId() != mCtx.getResources().getString(R.string.dummy_id_favorites)) {
            String base64String = bars.get(i).getImageData();
            String base64Image = base64String.split(",")[1];

            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            barViewHolder.photo.setImageBitmap(decodedByte);

            if (bars.get(i).getDistance() == null){
                barViewHolder.distance.setText("Ermittlung der Entfernung aktuell nicht möglich");
            }
            else {
                if (Double.valueOf(bars.get(i).getDistance()) < 1000) {
                    barViewHolder.distance.setText(bars.get(i).getDistance().split("\\.")[0] + " m");
                } else {
                    String distanceKm = String.valueOf(Double.valueOf(bars.get(i).getDistance()) / 1000).substring(0,4); //conversion to km
                    barViewHolder.distance.setText(distanceKm.split("\\.")[0] + ","+ distanceKm.split("\\.")[1] +" km");
                }
            }
        }
        else{
            barViewHolder.icon_walker.setVisibility(View.GONE);
            barViewHolder.distance.setText(bars.get(i).getDistance());
        }
    }

    @Override
    public int getItemCount() {
        return bars.size();
    }


}
