package hs_ab.com.TrinkBar;

/**
 * Created by student on 08.06.17.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.BarViewHolder> {

    int mitem;
    private static final String TAG = "LOG";

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

                    Intent i = new Intent(v.getContext(), DetailsActivity.class);
                    i.putExtra("EXTRA_DETAILS_TITLE", name.getText());
                    v.getContext().startActivity(i);
                }

            });
        }
    }

    List<Bar> bars;
    Context mCtx;

    RVAdapter(Context mCtx, List<Bar> bars) {
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
        //barViewHolder.description.setText(bars.get(i).getDescription());
       // Picasso.with(mCtx).load(bars.get(i).getImageLink()).into(barViewHolder.photo);
//      barViewHolder.photo.setImageResource(persons.get(i).photoId);

    }

    @Override
    public int getItemCount() {
        return bars.size();
    }



}
