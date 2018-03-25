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

    int mitem;
    private static final String TAG = "LOG";
    private static  String barId;
    private static List<Bar> bars;
    Context mCtx;


    public static class BarViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView name;
        TextView distance;
        ImageView photo;
        String dummyBarText;


        BarViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.favorites_list_item);
            name = (TextView) itemView.findViewById(R.id.favorites_list_barname);
            distance = (TextView) itemView.findViewById(R.id.favorites_list_distance);
            photo = (ImageView) itemView.findViewById(R.id.favorites_list_photo);

            //TODO clicking on bar in favorites list -> push back button -> "musterbar" is added
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
        BarViewHolder pvh = new BarViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final BarViewHolder barViewHolder, int i) {
        mitem= i;
        Log.d(TAG, "onBindViewHolder: "+i);
        barViewHolder.name.setText(bars.get(i).getName());

        if(bars.get(i).getId() != mCtx.getResources().getString(R.string.dummy_id_favorites)) {
            String base64String = bars.get(i).getImageData();
            String base64Image = base64String.split(",")[1];

            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            barViewHolder.photo.setImageBitmap(decodedByte);

            barViewHolder.distance.setText("Entfernung: " + bars.get(i).getDistance() + " m");
        }
        else{
            barViewHolder.distance.setText(bars.get(i).getDistance());
        }
    }

    @Override
    public int getItemCount() {
        return bars.size();
    }


}
