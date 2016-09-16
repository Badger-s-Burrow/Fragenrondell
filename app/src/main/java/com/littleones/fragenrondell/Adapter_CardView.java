package com.littleones.fragenrondell;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joe on 17.01.2016.
 */
public class Adapter_CardView extends ArrayAdapter<Card> {

    private int length;
    private int mCardDesign;

    public Adapter_CardView(Context context, List<Card> cards, int cardDesign) {
        super(context, 0, cards);
        length = cards.size();
        mCardDesign = cardDesign;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Card card = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.card, parent, false);
        }
        // Lookup view for data population
        TextView tv_question = (TextView) convertView.findViewById(R.id.tv_question);
        RelativeLayout rl_card = (RelativeLayout) convertView.findViewById(R.id.rl_card);
        RelativeLayout rl_card_inside = (RelativeLayout) convertView.findViewById(R.id.rl_card_inside);
        rl_card_inside.setBackgroundResource(mCardDesign);
        // Populate the data into the template view using the data object
        tv_question.setText(card.getQuestion());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rl_card.setElevation(2);
        }

        // Return the completed view to render on screen
        return convertView;
    }

}

