package de.badgersburrow.fragenrondell;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.internal.ListenerClass;

/**
 * Created by Joe on 06.02.2016.
 */
public class Activity_CategorySelect extends AppCompatActivity {

    public List<Category> selected_categories;
    public static ObservableInteger num_selected_categories;

    ImageView iv_start;
    TextView tv_cat_counter;

    static AudioManager audioManager;
    static SoundPool soundPool;
    static int soundID_openbox;
    static int soundID_shufflecards;
    static boolean loaded = false;

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


        setContentView(R.layout.activity_categoryselect);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        soundPool = new SoundPool(10,AudioManager.STREAM_MUSIC,0);
        soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }
        });
        soundID_openbox = soundPool.load(this,R.raw.box_open,1);
        soundID_shufflecards = soundPool.load(this,R.raw.card_shuffle,1);

        RecyclerView rv_categories = (RecyclerView) findViewById(R.id.rv_categories);
        rv_categories.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_categories.setLayoutManager(llm);


        selected_categories = getAvailableCategories();
        Adapter_CategorySelect ad_catselect = new Adapter_CategorySelect(selected_categories);
        rv_categories.setAdapter(ad_catselect);

        tv_cat_counter = (TextView) findViewById(R.id.tv_cat_counter);
        iv_start = (ImageView) findViewById(R.id.iv_start);
        iv_start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Activity_Main.cards_new_stack();
                float actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                float volume = actualVolume/maxVolume;
                if (loaded) {
                    soundPool.play(soundID_shufflecards,volume,volume,1,0,1f);
                    Log.e("Sound_Test","Played shuffle sound");
                }
                Intent i = new Intent(Activity_CategorySelect.this, Activity_Mode_FreePlay.class);
                startActivityForResult(i, Activity_Main.SHOW_CONTINUE_REQUEST);
                //startActivityFromChild(i);
                //finish();
            }
        });

        num_selected_categories = new ObservableInteger();

        num_selected_categories.setOnIntegerChangeListener(new OnIntegerChangeListener()
        {
            @Override
            public void onIntegerChanged(int newValue)
            {
                if (newValue == 0){
                    iv_start.setVisibility(View.INVISIBLE);
                    tv_cat_counter.setVisibility(View.INVISIBLE);
                } else {
                    iv_start.setVisibility(View.VISIBLE);
                    tv_cat_counter.setVisibility(View.VISIBLE);
                    tv_cat_counter.setText(String.valueOf(newValue));
                }
            }
        });


    }

    private List<Category> getAvailableCategories() {
        Resources res = getResources();
        String[] category_names  = res.getStringArray(R.array.category_names);
        String[] category_labels = res.getStringArray(R.array.category_labels);
        TypedArray category_label_image = res.obtainTypedArray(R.array.category_label_images);
        TypedArray category_design = res.obtainTypedArray(R.array.category_designs);
        TypedArray box_design = res.obtainTypedArray(R.array.category_box);
        int[] color_primary = res.getIntArray(R.array.category_primary_color);
        int[] color_secondary = res.getIntArray(R.array.category_secondary_color);

        SharedPreferences sharedpreferences = getSharedPreferences(Activity_Main.sharedPrefKey, Context.MODE_PRIVATE);

        boolean[] categories_bought = new boolean[7];
        categories_bought[0] = true;
        categories_bought[1] = sharedpreferences.getBoolean("intimacy",false);
        categories_bought[2] = sharedpreferences.getBoolean("sport",false);
        categories_bought[3] = sharedpreferences.getBoolean("friendship",false);
        categories_bought[4] = sharedpreferences.getBoolean("job",false);
        categories_bought[5] = sharedpreferences.getBoolean("relationship",false);
        categories_bought[6] = sharedpreferences.getBoolean("forkids",false);

        List<Category> result = new ArrayList<Category>();
        for (int i=0; i < category_names.length; i++) {
            // do not add unbought categories
            if (categories_bought[i]) {
                Category cat = new Category(i, category_names[i], category_labels[i], category_label_image.getResourceId(i, 0), category_design.getResourceId(i, 0), box_design.getResourceId(i, 0), color_primary[i], color_secondary[i], true);
                result.add(cat);
            }

        }
        category_label_image.recycle();
        category_design.recycle();
        box_design.recycle();

        return result;
    }

    public interface OnIntegerChangeListener
    {
        public void onIntegerChanged(int newValue);
    }

    public class ObservableInteger
    {
        private OnIntegerChangeListener listener;

        private int value = 0;

        public void setOnIntegerChangeListener(OnIntegerChangeListener listener)
        {
            this.listener = listener;
        }

        public int get()
        {
            return value;
        }

        public void set(int value)
        {
            this.value = value;

            if(listener != null)
            {
                listener.onIntegerChanged(value);
            }
        }
        public void inc(){
            this.value++;
            if(listener != null)
            {
                listener.onIntegerChanged(value);
            }
        }
        public void dec(){
            this.value--;
            if(listener != null)
            {
                listener.onIntegerChanged(value);
            }
        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        Log.d("request_res","is: " + requestCode + " and " + resultCode);
        if (requestCode == Activity_Main.SHOW_CONTINUE_REQUEST) {

            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Boolean show_continue_return = data.getBooleanExtra("show_continue", false);
                Intent intent=new Intent();
                intent.putExtra("show_continue",show_continue_return);
                setResult(RESULT_OK,intent);//Activity_Main.SHOW_CONTINUE_REQUEST,intent);
                finish();

            // The user picked a contact.
            // The Intent's data Uri identifies which contact was selected.

            // Do something with the contact here (bigger example below)
            }
        }
    }

}
