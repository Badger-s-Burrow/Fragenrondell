package com.littleones.fragenrondell;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.widget.RelativeLayout;
import android.widget.Toast;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Activity_Main extends AppCompatActivity {

    static int active_scene;

    Continue continue_info;

    static public int screenHeight;
    static public int screenWidth;

    static public final int SHOW_CONTINUE_REQUEST = 11;
    static public final int CLICKED_CONTINUE_REQUEST = 7;
    static public final String EXTRA_CONTINUE = "continue";

    // categories for design
    public static int cat_general = 1;
    public int cat_intimacy = 2;
    public int cat_sports = 3;
    public int cat_psychology = 4;

    // ressources question arrays
    public static String[] questions_general;
    public String[] questions_intimicy;
    public String[] questions_sports;
    public String[] questions_psychology;

    //public static boolean show_continue = false;
    RelativeLayout iv_continue, iv_new;
    ImageView iv_gentleman_eyes, iv_gentleman, iv_gentleman_thumb;
    ImageView iv_back;
    RelativeLayout rl_fullwidth, rl_part_main, rl_part_modeselect, rl_foreground;

    ObjectAnimator anim_new;
    ObjectAnimator anim_continue;
    int card_rot_pivot;
    int card_rot_oop;

    //public static List<Card> currentCards = new ArrayList<Card>();
    //public static int position;
    public static float cat_box_width;
    public static boolean show_continue;

    public static final String sharedPrefKey = "myPrefsLittleonesFragen";
    public static final String sharedPrefBackground = "prefBackground";
    public static final String sharedPrefCard = "prefCard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupMainWindowDisplayMode();


        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getRealMetrics(outMetrics);

        screenHeight = outMetrics.heightPixels;
        screenWidth = outMetrics.widthPixels;

        active_scene = 1;

        Resources res = getResources();
        questions_general = res.getStringArray(R.array.questions_general);
        cat_box_width = res.getDimension(R.dimen.category_box_top_width);

        card_rot_oop = res.getInteger(R.integer.card_rot_oop);
        show_continue = false;

        setContentView(R.layout.activity_main_v1);


        // set up foreground
        rl_foreground = (RelativeLayout) findViewById(R.id.rl_foreground);
        iv_gentleman = (ImageView)findViewById(R.id.iv_gentleman);
        iv_gentleman_eyes = (ImageView)findViewById(R.id.iv_gentleman_eyes);
        iv_gentleman_thumb = (ImageView)findViewById(R.id.iv_gentleman_thumb);

        iv_new = (RelativeLayout) findViewById(R.id.rl_menu_start);
        iv_new.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                goToSceneModeSelect(true);
            }
        });
        iv_continue = (RelativeLayout) findViewById(R.id.rl_menu_continue);
        iv_continue.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent();
                if (continue_info.getGameMode() == 0) {
                    i = new Intent(Activity_Main.this, Activity_Mode_FreePlay.class);
                } else if (continue_info.getGameMode() == 1) {
                    i = new Intent(Activity_Main.this, Activity_Mode_WheelSelect.class);
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable(EXTRA_CONTINUE, continue_info);
                i.putExtras(bundle);

                //startActivity(i);
                startActivityForResult(i, CLICKED_CONTINUE_REQUEST);
            }
        });

        // set up background:
        rl_fullwidth = (RelativeLayout) findViewById(R.id.rl_fullwidth);
        // set up background: main
        rl_part_main = (RelativeLayout) findViewById(R.id.rl_part_main);

        ImageView bt_shop = (ImageView) findViewById(R.id.frame_01);
        bt_shop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Activity_Main.this, Activity_Shop.class);
                startActivity(i);
            }
        });
        ImageView iv_frame01_highlight = (ImageView) findViewById(R.id.iv_frame01_highlight);

        ImageView iv_settings = (ImageView) findViewById(R.id.frame_04);
        iv_settings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Activity_Main.this, Activity_Settings.class);
                startActivity(i);
            }
        });

        ImageView iv_freeplay = (ImageView)findViewById(R.id.iv_freeplay);
        iv_freeplay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                SharedPreferences sharedpreferences = getSharedPreferences(Activity_Main.sharedPrefKey, Context.MODE_PRIVATE);
                Intent intent;
                if ( sharedpreferences.getBoolean("intimacy",false) ||
                        sharedpreferences.getBoolean("sport",false) ||
                        sharedpreferences.getBoolean("friendship",false) ||
                        sharedpreferences.getBoolean("job",false) ||
                        sharedpreferences.getBoolean("relationship",false) ||
                        sharedpreferences.getBoolean("forkids",false)) {
                    intent = new Intent(Activity_Main.this, Activity_CategorySelect.class);
                } else {

                    intent = new Intent(Activity_Main.this, Activity_Mode_FreePlay.class);
                }

                startActivityForResult(intent, SHOW_CONTINUE_REQUEST);

            }
        });

        // set up background: modeselect
        rl_part_modeselect = (RelativeLayout) findViewById(R.id.rl_part_modeselect);
        ImageView iv_wheelselect = (ImageView)findViewById(R.id.iv_wheelselect);
        iv_wheelselect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Activity_Main.this, Activity_Mode_WheelSelect.class);
                startActivityForResult(intent, SHOW_CONTINUE_REQUEST);
            }
        });

        iv_back = (ImageView)findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToSceneMain(true);
            }
        });


        // start looping animations
        AnimationDrawable gentleman_anim_blink = (AnimationDrawable) iv_gentleman_eyes.getBackground();
        gentleman_anim_blink.start();
        AnimationDrawable frame01_anim_highlight = (AnimationDrawable) iv_frame01_highlight.getBackground();
        frame01_anim_highlight.start();
    }



    public void cards_show(boolean show_continue)
    {
        if (show_continue){

            anim_new = (ObjectAnimator) AnimatorInflater
                    .loadAnimator(this, R.animator.new_show);
            anim_new.setTarget(iv_new);


            iv_continue.setVisibility(View.VISIBLE);
            anim_continue = (ObjectAnimator) AnimatorInflater
                    .loadAnimator(this, R.animator.continue_show);
            anim_continue.setTarget(iv_continue);

            anim_new.start();
            anim_continue.start();

        }
    }

    public void cards_hide(boolean show_continue)
    {
        if (show_continue){
            anim_new.cancel();
            anim_continue.cancel();

            anim_new = (ObjectAnimator) AnimatorInflater
                    .loadAnimator(this, R.animator.new_hide);
            anim_new.setTarget(iv_new);

            anim_continue = (ObjectAnimator) AnimatorInflater
                    .loadAnimator(this, R.animator.continue_hide);
            anim_continue.setTarget(iv_continue);

            anim_new.start();
            anim_continue.start();
        }
    }


    // scene functions

    public void goToSceneModeSelect (boolean animate)
    {
        iv_new.setClickable(false);
        iv_continue.setClickable(false);

        active_scene = 2;

        cards_hide(show_continue);

        if(animate) {
            rl_fullwidth.animate()
                    .setDuration(600)
                    .setInterpolator(new LinearInterpolator())
                    .translationX(-screenWidth);
            rl_foreground.animate()
                    .setDuration(600)
                    .setInterpolator(new LinearInterpolator())
                    .translationX(screenWidth*0.36f);

            // Start the animation (here oneshot), after finishing cards
            iv_gentleman.setBackgroundResource(R.drawable.gentleman_anim_tomodeselect);
            if (show_continue){
            anim_new.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) { }

                @Override
                public void onAnimationCancel(Animator animation) { }

                @Override
                public void onAnimationEnd(Animator animation) {
                    perform_toModeSelect();
                }

                @Override
                public void onAnimationRepeat(Animator animation) { }
            });} else {
                    perform_toModeSelect();

                }

        } else {
            iv_gentleman_thumb.setVisibility(View.INVISIBLE);
            rl_fullwidth.setTranslationX(-screenWidth);
            rl_foreground.setTranslationX(screenWidth*0.36f);
            iv_gentleman.setBackgroundResource(R.drawable.gentleman_anim_frommodeselect);
        }
    }

    public void perform_toModeSelect(){
        iv_continue.setVisibility(View.INVISIBLE);
        iv_gentleman_thumb.setVisibility(View.INVISIBLE);

        Animator anim_movenew = AnimatorInflater
                .loadAnimator(getApplicationContext(), R.animator.new_tomodeselect);
        anim_movenew.setTarget(iv_new);
        anim_movenew.start();
        Animator anim_movecontinue = AnimatorInflater
                .loadAnimator(getApplicationContext(), R.animator.new_tomodeselect);
        anim_movecontinue.setTarget(iv_continue);
        anim_movecontinue.start();


        AnimationDrawable gentleman_anim_tomodeselect = (AnimationDrawable) iv_gentleman.getBackground();
        // should wait until cards rotated together
        gentleman_anim_tomodeselect.start();
    }

    public void goToSceneMain(boolean animate){
        active_scene = 1;

        if(animate) {
            rl_fullwidth.animate()
                    .setDuration(600)
                    .setInterpolator(new LinearInterpolator())
                    .translationX(0);
            rl_foreground.animate()
                    .setDuration(600)
                    .setInterpolator(new LinearInterpolator())
                    .translationX(0);

            Animator anim_movenew = AnimatorInflater
                    .loadAnimator(getApplicationContext(), R.animator.new_frommodeselect);
            anim_movenew.setTarget(iv_new);
            anim_movenew.start();
            Animator anim_movecontinue = AnimatorInflater
                    .loadAnimator(getApplicationContext(), R.animator.new_frommodeselect);
            anim_movecontinue.setTarget(iv_continue);
            anim_movecontinue.start();

            // Start the animation (here oneshot).
            iv_gentleman.setBackgroundResource(R.drawable.gentleman_anim_frommodeselect);
            AnimationDrawable gentleman_anim_frommodeselect = (AnimationDrawable) iv_gentleman.getBackground();
            gentleman_anim_frommodeselect.start();

            checkIfAnimationDone(gentleman_anim_frommodeselect);

        } else {
            iv_new.setClickable(true);
            iv_new.setTranslationX(0);
            iv_new.setTranslationY(0);

            iv_new.setRotationY(card_rot_oop);

            iv_new.setScaleX(1);
            iv_new.setScaleY(1);

            iv_continue.setClickable(true);
            iv_continue.setTranslationX(0);
            iv_continue.setTranslationY(0);
            iv_continue.setRotationY(card_rot_oop);

            iv_continue.setScaleX(1);
            iv_continue.setScaleY(1);

            rl_fullwidth.setTranslationX(0);
            rl_foreground.setTranslationX(0);

            iv_gentleman_thumb.setVisibility(View.VISIBLE);
            iv_gentleman.setBackgroundResource(R.drawable.gentleman_anim_frommodeselect);
            iv_gentleman.setBackgroundResource(R.drawable.gentleman_anim_tomodeselect);
            AnimationDrawable gentleman_anim_tomodeselect = (AnimationDrawable) iv_gentleman.getBackground();
            //gentleman_anim_tomodeselect.selectDrawable(0);
        }
    }

    private void checkIfAnimationDone(AnimationDrawable anim){
        final AnimationDrawable a = anim;
        int timeBetweenChecks = 30;
        Handler h = new Handler();
        h.postDelayed(new Runnable(){
            public void run(){
                int frameNumber = 0;
                Drawable checkFrame, currentFrame;
                 currentFrame = a.getCurrent();
                for (int i = 0; i < a.getNumberOfFrames(); i++) {
                    checkFrame = a.getFrame(i);
                    if (checkFrame == currentFrame) {
                        frameNumber = i;
                        break;
                    }
                }
                if (frameNumber <= a.getNumberOfFrames() - 2){
                    checkIfAnimationDone(a);
                } else{
                    //Toast.makeText(getApplicationContext(), "ANIMATION DONE!", Toast.LENGTH_SHORT).show();
                    iv_new.setClickable(true);
                    iv_continue.setClickable(true);
                    iv_gentleman_thumb.setVisibility(View.VISIBLE);
                    cards_show(show_continue);
                }
            }
        }, timeBetweenChecks);
    };

    // viable listener

    public interface OnBooleanChangeListener
    {
        public void onBooleanChanged(boolean newValue);
    }

    public class ObservableBoolean
    {
        private OnBooleanChangeListener listener;

        private boolean value = false;

        public void setOnBooleanChangeListener(OnBooleanChangeListener listener)
        {
            this.listener = listener;
        }

        public boolean get()
        {
            return value;
        }

        public void set(boolean value)
        {
            if ((this.value != value) && (listener != null)){
                this.value = value;
                listener.onBooleanChanged(value);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == SHOW_CONTINUE_REQUEST) {

            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                continue_info = (Continue) data.getExtras().getSerializable(EXTRA_CONTINUE);
                show_continue = continue_info.getContinuable();
                iv_new.setRotation(0);
                iv_continue.setRotation(0);
                goToSceneMain(false);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        cards_show(show_continue);
                    }
                }, 500);


            }
        } else if (requestCode == CLICKED_CONTINUE_REQUEST) {
            if (resultCode == RESULT_OK) {
                continue_info = (Continue) data.getExtras().getSerializable(EXTRA_CONTINUE);
                show_continue = continue_info.getContinuable();

                if (show_continue) {
                    iv_new.setRotation(20);
                    iv_continue.setRotation(-20);
                } else {
                    iv_new.setRotation(0);
                    iv_continue.setRotation(0);
                }
                goToSceneMain(false);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (active_scene == 1){
            iv_gentleman.setBackgroundResource(R.drawable.gentleman_anim_tomodeselect);
        } else if (active_scene == 2){
            iv_gentleman.setBackgroundResource(R.drawable.gentleman_anim_frommodeselect);
        }
    }

    private void setupMainWindowDisplayMode() {
        View decorView = setSystemUiVisilityMode();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                setSystemUiVisilityMode(); // Needed to avoid exiting immersive_sticky when keyboard is displayed
            }
        });
    }

    private View setSystemUiVisilityMode() {
        View decorView = getWindow().getDecorView();
        int options;
        options =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        decorView.setSystemUiVisibility(options);
        return decorView;
    }

}

