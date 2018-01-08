package hs_ab.com.sightseeing_aschaffenburg;

/**
 * Created by student on 08.06.17.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder> {

    int mitem;
    private static final String TAG = "LOG";

    public static class PersonViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView name;
        TextView description;
        ImageView photo;


        PersonViewHolder(View itemView) {
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

    List<PointOfInterest> persons;
    Context mCtx;

    RVAdapter(Context mCtx, List<PointOfInterest> persons) {
        this.mCtx = mCtx;
        this.persons = persons;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final PersonViewHolder personViewHolder, int i) {
        mitem= i;
        Log.d(TAG, "onBindViewHolder: "+i);
        personViewHolder.name.setText(persons.get(i).name);
        personViewHolder.description.setText(persons.get(i).description);
        Picasso.with(mCtx).load(persons.get(i).image_link).into(personViewHolder.photo);
//      personViewHolder.photo.setImageResource(persons.get(i).photoId);

    }

    @Override
    public int getItemCount() {
        return persons.size();
    }



}
