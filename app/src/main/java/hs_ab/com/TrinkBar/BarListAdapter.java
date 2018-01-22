package hs_ab.com.TrinkBar;


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

import hs_ab.com.TrinkBar.models.Bar;

public class BarListAdapter extends RecyclerView.Adapter<BarListAdapter.BarViewHolder> {

    int mitem;
    private static final String TAG = "LOG";
    private static  String barId;
    private static List<Bar> bars;
    Context mCtx;

    public static class BarViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView name;
        TextView description;
        ImageView photo;



        BarViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            name = (TextView) itemView.findViewById(R.id.name);
            description = (TextView) itemView.findViewById(R.id.description);
            photo = (ImageView) itemView.findViewById(R.id.photo);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v)
                {
                    Log.d(TAG, "onClick: "+ name.getText());
                    //Snackbar.make(v, name.getText(), Snackbar.LENGTH_LONG)
                     //       .setAction("Action", null).show();

                    for (int i=0;i<bars.size();i++){
                        if(bars.get(i).getName() == name.getText()){

                            barId=  bars.get(i).getId();
                        }

                    }


                    Intent i = new Intent(v.getContext(), DetailsActivity.class);
                    i.putExtra("EXTRA_DETAILS_TITLE", barId);
                    v.getContext().startActivity(i);
                }

            });
        }
    }



    BarListAdapter(Context mCtx, List<Bar> bars) {
        this.mCtx = mCtx;
        this.bars = bars;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public BarViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list, viewGroup, false);
        BarViewHolder pvh = new BarViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final BarViewHolder barViewHolder, int i) {
        mitem= i;
        Log.d(TAG, "onBindViewHolder: "+i);
        barViewHolder.name.setText(bars.get(i).getName());

        String base64String = bars.get(i).getImageData();
                String base64Image = base64String.split(",")[1];

        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        barViewHolder.photo.setImageBitmap(decodedByte);

    }

    @Override
    public int getItemCount() {
        return bars.size();
    }



}
