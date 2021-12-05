package de.badgersburrow.fragenrondell;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.VectorDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;


import butterknife.ButterKnife;
import butterknife.Bind;

/**
 * Created by Joe on 17.01.2016.
 */
public class Activity_Mode_FreePlay extends AppCompatActivity {

    SharedPreferences sharedpreferences;

    private List<Card> cards;
    public static int position;
    private Adapter_CardView stackAdapter;
    private int i;

    static AudioManager audioManager;
    static SoundPool soundPool;
    static int soundID_pickcard;
    static int soundID_dropcard;
    static boolean loaded = false;

    static int background_current = 0;
    static int card_current = 0;
    static int background_num;
    TypedArray background_tiles;
    RelativeLayout rl_background_pattern;
    TextView tv_bg_counter;

    @Bind(R.id.frame)
    StackCardAdapterView moveContainer;

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
        setContentView(R.layout.activity_cardview);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                cards = Auxiliar.cards_new_stack();
                position = 0;
            } else {
                Continue continue_info = (Continue) extras.getSerializable(Activity_Main.EXTRA_CONTINUE);
                cards = continue_info.getCards();
                position = continue_info.getPosition();
            }
        } else {
            //newString= (String) savedInstanceState.getSerializable("STRING_I_NEED");
        }


        sharedpreferences = getSharedPreferences(Activity_Main.sharedPrefKey, Context.MODE_PRIVATE);
        background_current = sharedpreferences.getInt(Activity_Main.sharedPrefBackground,0);
        card_current = sharedpreferences.getInt(Activity_Main.sharedPrefCard,0);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        soundPool = new SoundPool(10,AudioManager.STREAM_MUSIC,0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }
        });
        soundID_pickcard = soundPool.load(this,R.raw.card_pickup,1);
        soundID_dropcard = soundPool.load(this,R.raw.card_drop,1);

        rl_background_pattern = (RelativeLayout) findViewById(R.id.rl_background_pattern);
        background_tiles = getResources().obtainTypedArray(R.array.background_tiles);
        background_num = background_tiles.length();
        rl_background_pattern.setBackgroundResource(background_tiles.getResourceId(background_current,0));

        tv_bg_counter = (TextView) findViewById(R.id.tv_bg_counter);
        tv_bg_counter.setText(background_current + "/" + background_num);

        final StackCardAdapterView moveContainer = (StackCardAdapterView) findViewById(R.id.frame);
        //LayerDrawable bg_tile_layer = (LayerDrawable) ContextCompat.getDrawable(this, R.drawable.background_tile01);
        //bg_tile_layer..setT
        //moveContainer.setBackgroundResource(R.drawable.background_tile01);

        TypedArray card_designs = getResources().obtainTypedArray(R.array.card_designs);

        stackAdapter = new Adapter_CardView(this,cards,card_designs.getResourceId(card_current,0));

        //set the listener and the adapter
        moveContainer.setAdapter(stackAdapter);

        moveContainer.setMoveListener(new StackCardAdapterView.onMoveListener() {

            @Override
            public void onDismissCard(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                //Toast.makeText(Activity_CardView.this, "Left!", Toast.LENGTH_SHORT).show();
                position = cards.indexOf(dataObject) + 1;
                stackAdapter.notifyDataSetChanged();
            }

            @Override
            public void onRetractCard(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                //Toast.makeText(Activity_CardView.this, "Left!", Toast.LENGTH_SHORT).show();
                position = cards.indexOf(dataObject);
                stackAdapter.notifyDataSetChanged();
            }


            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
                cards.add(new Card(i, "XML ".concat(String.valueOf(i)), 0, 0));
                stackAdapter.notifyDataSetChanged();
                Log.d("LIST", "notified");
                i++;
            }

            public void onScroll(float fu) {

            }
        });

        // Optionally add an OnItemClickListener
        moveContainer.setOnItemClickListener(new StackCardAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                //makeToast(Activity_CardView.this, "Clicked!");
            }
        });

        ImageView iv_backButton = (ImageView) findViewById(R.id.iv_back);
        iv_backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Activity_Main.active_scene = 1;

                Intent intent=new Intent();
                intent.putExtra(Activity_Main.EXTRA_CONTINUE,new Continue(cards, position));
                setResult(RESULT_OK,intent);//Activity_Main.SHOW_CONTINUE_REQUEST,intent);
                finish();
            }
        });

        ImageView iv_next_bg = (ImageView) findViewById(R.id.iv_next_bg);
        iv_next_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                background_current += 1;
                if (background_current >= background_num-1){
                    background_current = 0;
                }
                rl_background_pattern.setBackground(getResources().getDrawable(background_tiles.getResourceId(background_current,0)));
                tv_bg_counter.setText(background_current + "/" + background_num);
            }
        });

        ImageView iv_prev_bg = (ImageView) findViewById(R.id.iv_prev_bg);
        iv_prev_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                background_current -= 1;
                if (background_current < 0){
                    background_current = Math.max(background_num -1,0);
                }
                rl_background_pattern.setBackground(getResources().getDrawable(background_tiles.getResourceId(background_current,0)));
                tv_bg_counter.setText(background_current + "/" + background_num);
            }
        });
    }

    static void makeToast(Context ctx, String s){
        Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
    }




}
