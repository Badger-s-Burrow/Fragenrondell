package de.badgersburrow.fragenrondell;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Created by Joe on 10.04.2016.
 */
// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class Adapter_Background extends
        RecyclerView.Adapter<Adapter_Background.ViewHolder> {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView iv_background;


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            iv_background = (ImageView) itemView.findViewById(R.id.iv_background);

        }


    }

    // Store a member variable for the contacts
    private List<Background> mBackgrounds;
    private OnClickListener onClickListener = null;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    // Pass in the contact array into the constructor
    public Adapter_Background(List<Background> backgrounds) {
        mBackgrounds = backgrounds;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public Adapter_Background.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_background, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(Adapter_Background.ViewHolder viewHolder, final int position) {
        // Get the data model based on position
        Background background = mBackgrounds.get(position);

        // Set item views based on the data model
        ImageView iv_background = viewHolder.iv_background;
        iv_background.setImageResource(background.getDesign());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(onClickListener != null) {
                    onClickListener.onClick(position);
                }
            }
        });


    }

    // Return the total count of items
    @Override
    public int getItemCount() {
        return mBackgrounds.size();
    }



    public interface OnClickListener {
        void onClick(int position);
    }

}

