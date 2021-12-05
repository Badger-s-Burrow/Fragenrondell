package de.badgersburrow.fragenrondell;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Activity_Settings extends AppCompatActivity {


    ArrayList<Background> backgrounds;
    TypedArray card_designs;
    ImageView iv_canvas;
    ImageView iv_card;
    SharedPreferences sharedpreferences;
    int prefBackground=0;
    int prefCard = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_customize);
        iv_canvas = (ImageView) findViewById(R.id.iv_canvas);
        RecyclerView rv_backgrounds = (RecyclerView) findViewById(R.id.rv_backgrounds);

        // Initialize contacts

        TypedArray background_tiles = getResources().obtainTypedArray(R.array.background_tiles);
        card_designs = getResources().obtainTypedArray(R.array.card_designs);

        backgrounds = Background.getBackgrounds(background_tiles);
        // Create adapter passing in the sample user data
        Adapter_Background adapter = new Adapter_Background(backgrounds);
        sharedpreferences = getSharedPreferences(Activity_Main.sharedPrefKey, Context.MODE_PRIVATE);
        prefBackground = sharedpreferences.getInt(Activity_Main.sharedPrefBackground,0);
        prefCard = sharedpreferences.getInt(Activity_Main.sharedPrefCard,0);
        iv_canvas.setBackgroundResource(backgrounds.get(prefBackground).getDesign());


        adapter.setOnClickListener(new Adapter_Background.OnClickListener() {
             @Override
             public void onClick(int position) {
                 iv_canvas.setBackgroundResource(backgrounds.get(position).getDesign());
                 SharedPreferences.Editor editor = sharedpreferences.edit();
                 editor.putInt(Activity_Main.sharedPrefBackground,position);
                 editor.commit();
             }
         });

         // Attach the adapter to the recyclerview to populate items
         rv_backgrounds.setAdapter(adapter);
         // Set layout manager to position the items
         rv_backgrounds.setLayoutManager(new LinearLayoutManager(this));
         // That's all!
         rv_backgrounds.scrollToPosition(prefBackground);

        iv_card = (ImageView) findViewById(R.id.iv_card);
        iv_card.setImageResource(card_designs.getResourceId(prefCard,0));

        ImageView iv_left = (ImageView) findViewById(R.id.iv_left);
        iv_left.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                prefCard -= 1;
                if (prefCard < 0 ){
                    prefCard += card_designs.length();
                }
                iv_card.setImageResource(card_designs.getResourceId(prefCard,0));
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putInt(Activity_Main.sharedPrefCard,prefCard);
                editor.commit();
            }
        });

        ImageView iv_right = (ImageView) findViewById(R.id.iv_right);
        iv_right.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                prefCard += 1;
                if (prefCard >= card_designs.length()){
                    prefCard = 0;
                }
                iv_card.setImageResource(card_designs.getResourceId(prefCard,0));
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putInt(Activity_Main.sharedPrefCard,prefCard);
                editor.commit();
            }
        });



        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
         iv_back.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
                 finish();

             }
         });

    }
}
