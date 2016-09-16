package com.littleones.fragenrondell;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Scene;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.littleones.fragenrondell.custom_views.SelectionWheel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

/**
 * Created by Joe on 03.05.2016.
 */
public class Activity_Mode_WheelSelect extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    public static int background_design;
    public static int card_design;

    private List<Card> cards;

    ViewGroup mRoot;
    Scene sceneIntro;
    Scene sceneCharacterSelect;
    Scene sceneWheel;
    Scene sceneCardSelect;

    boolean shuffle_cards_back;
    boolean allow_joker;
    int player_order; // 0: questioned player, 1: fixed order, 2: random order

    Resources res;

    SeekBar mSeekbar;
    TextView tv_numberplayer, tv_char_counter;
    ImageView iv_forward;

    public static ObservableInteger num_selected_characters;
    public static ObservableInteger selected_player_id;
    public Item_Character player_questioning;
    public Item_Character player_questioned;
    public Card card_selected;
    List<Item_Character> selected_characters = new ArrayList<>();
    List<Item_Character> reduced_characters;
    List<Card> display_cards;

    Random rand = new Random();


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
        setContentView(R.layout.activity_mode_wheelselect);

        sharedpreferences = getSharedPreferences(Activity_Main.sharedPrefKey, Context.MODE_PRIVATE);
        int background_id = sharedpreferences.getInt(Activity_Main.sharedPrefBackground,0);
        int card_id = sharedpreferences.getInt(Activity_Main.sharedPrefCard,0);

        TypedArray card_designs = getResources().obtainTypedArray(R.array.card_designs);
        card_design = card_designs.getResourceId(card_id,0);

        // Create the scene root for the scenes in this app
        mRoot = (ViewGroup) findViewById(R.id.rootContainer);

        res = getResources();

        // Create the scenes
        sceneIntro = Scene.getSceneForLayout(mRoot, R.layout.scene_wheelselect_intro, this);
        sceneCharacterSelect = Scene.getSceneForLayout(mRoot, R.layout.scene_wheelselect_character, this);
        sceneWheel = Scene.getSceneForLayout(mRoot, R.layout.scene_wheelselect_wheel, this);
        sceneCardSelect = Scene.getSceneForLayout(mRoot, R.layout.scene_wheelselect_cardselect, this);
        //mAnotherScene =
        //       Scene.getSceneForLayout(mSceneRoot, R.layout.another_scene, this);
        //Transition mFadeTransition =
        //        TransitionInflater.from(this).
        //                inflateTransition(R.transition.fade);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                cards = Auxiliar.cards_new_stack();
                sceneIntro.enter();
                initialize_Intro();
            } else {
                Continue continue_info = (Continue) extras.getSerializable(Activity_Main.EXTRA_CONTINUE);
                shuffle_cards_back = continue_info.getShuffleCardsBack();
                player_order = continue_info.getPlayerOrder();
                cards = continue_info.getCards();
                selected_characters = continue_info.getCharacters();
                int scene = continue_info.getCurrentScene();
                player_questioning = continue_info.getPlayerQuestioning();
                player_questioned = continue_info.getPlayerQuestioned();

                if (scene == 0){
                    sceneWheel.enter();
                    initialize_Wheel();
                } else if (scene == 1){
                    sceneCardSelect.enter();
                    initialize_CardSelect();
                }
            }
        } else {

        }

        //sceneIntro.enter();
        //initialize_Intro();


    }

    void initialize_Intro(){
        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();

            }
        });

        /*mSeekbar = (SeekBar) findViewById(R.id.sb_numberplayer);
        tv_numberplayer = (TextView)  findViewById(R.id.tv_numberplayer);

        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                tv_numberplayer.setText(Integer.toString(progress + 2));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {}

            public void onStopTrackingTouch(SeekBar seekBar) {}
        });*/

        iv_forward = (ImageView) findViewById(R.id.iv_forward);
        iv_forward.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox cb_shuffel = (CheckBox) findViewById(R.id.cb_shuffle);
                CheckBox cb_joker = (CheckBox) findViewById(R.id.cb_joker);
                RadioGroup rg_player_order = (RadioGroup) findViewById(R.id.rg_player_order);

                //RadioButton rb_po_question = (RadioButton) findViewById(R.id.rb_po_question);
                //RadioButton rb_po_fix = (RadioButton) findViewById(R.id.rb_po_fix);
                //RadioButton rb_po_random = (RadioButton) findViewById(R.id.rb_po_random);


                shuffle_cards_back = cb_shuffel.isChecked();
                allow_joker = cb_joker.isChecked();
                int selected_playerorder = rg_player_order.getCheckedRadioButtonId();
                if (selected_playerorder == R.id.rb_po_question){
                    player_order = 0 ;
                } else if (selected_playerorder == R.id.rb_po_fix){
                    player_order = 1;
                } else if(selected_playerorder == R.id.rb_po_random){
                    player_order = 2;
                }

                sceneCharacterSelect.enter();
                initialize_CharacterSelect();
            }
        });
    }

    void initialize_CharacterSelect(){

        RecyclerView rv_characters = (RecyclerView) findViewById(R.id.rv_characters);


        TypedArray character_design = res.obtainTypedArray(R.array.character_design);
        int[] character_color = res.getIntArray(R.array.character_color);
        //List<Integer> character_color = Arrays.asList(character_color_array) ;
        //Collections.shuffle(character_color);



        final List<Item_Character> character_list = new ArrayList<Item_Character>();
        for (int i = 0; i < character_color.length; ++i) {
            character_list.add(new Item_Character(i,character_design.getResourceId(i,0),character_color[i], allow_joker));
        }
        character_design.recycle();

        Adapter_Character ad_character = new Adapter_Character(character_list);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(),4);
        rv_characters.setLayoutManager(mLayoutManager);
        rv_characters.setItemAnimator(new DefaultItemAnimator());

        rv_characters.setAdapter(ad_character);
        ad_character.notifyDataSetChanged();



        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sceneIntro.enter();
                initialize_Intro();
            }
        });

        tv_char_counter = (TextView) findViewById(R.id.tv_char_counter);
        tv_char_counter.setText("Too few players");
        iv_forward = (ImageView) findViewById(R.id.iv_forward);
        iv_forward.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selected_characters = new ArrayList<>();
                for (int i=0; i < character_list.size(); i++){
                    if (character_list.get(i).getChecked()){
                        selected_characters.add(character_list.get(i));
                    }
                }
                Collections.shuffle(selected_characters);
                for (int i=0; i < selected_characters.size(); i++){
                    selected_characters.get(i).setNumber(i);
                }
                player_questioning = selected_characters.get(0);
                Auxiliar.cards_new_stack();
                sceneWheel.enter();
                initialize_Wheel();
            }
        });

        num_selected_characters = new ObservableInteger();
        num_selected_characters.setOnIntegerChangeListener(new OnIntegerChangeListener()
        {
            @Override
            public void onIntegerChanged(int newValue)
            {
                if (newValue == 0) {
                    iv_forward.setVisibility(View.INVISIBLE);
                    tv_char_counter.setVisibility(View.INVISIBLE);
                    tv_char_counter.setText("Too few players");
                } else if (newValue < 3) {
                    iv_forward.setVisibility(View.INVISIBLE);
                    tv_char_counter.setVisibility(View.VISIBLE);
                    tv_char_counter.setText("Too few players");
                } else {
                    iv_forward.setVisibility(View.VISIBLE);
                    tv_char_counter.setVisibility(View.VISIBLE);
                    tv_char_counter.setText(String.valueOf(newValue));
                }
            }
        });
    }

    void initialize_Wheel(){

        final SelectionWheel sc_wheel = (SelectionWheel) this.findViewById(R.id.sc_wheel);
        ImageView iv_questioning = (ImageView) findViewById(R.id.iv_questioning);
        iv_questioning.setImageResource(player_questioning.getDesign());

        reduced_characters = new ArrayList<>();
        for (int i = 0; i < selected_characters.size(); i++){
            if (i != player_questioning.getNumber()){
                reduced_characters.add(selected_characters.get(i));
            }
        }
        sc_wheel.addCharacters(reduced_characters);
        selected_player_id = new ObservableInteger();


        selected_player_id.setOnIntegerChangeListener(new OnIntegerChangeListener()
        {
            @Override
            public void onIntegerChanged(int newValue) {
                player_questioned = reduced_characters.get(selected_player_id.get());
                sceneCardSelect.enter();
                initialize_CardSelect();
            }
        });


        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Activity_Main.active_scene = 1;
                Intent intent=new Intent();
                intent.putExtra(Activity_Main.EXTRA_CONTINUE,new Continue(shuffle_cards_back, player_order,cards,0, selected_characters, player_questioning, player_questioned ));
                setResult(RESULT_OK,intent);
                finish();
            }
        });


    }

    public void initialize_CardSelect(){



        ImageView iv_questioning = (ImageView) findViewById(R.id.iv_questioning);
        iv_questioning.setImageResource(player_questioning.getDesign());
        ImageView iv_questioned = (ImageView) findViewById(R.id.iv_questioned);
        iv_questioned.setImageResource(player_questioned.getDesign());

        ImageView iv_joker =  (ImageView) findViewById(R.id.iv_joker);


        LinearLayout ll_cards = (LinearLayout) findViewById(R.id.ll_cards);
        display_cards = new ArrayList<Card>();
        for (int i = 0; i < 3; i++) {
            if (cards.size() > 0) {
                display_cards.add(cards.get(0));
                cards.remove(0);
            }
        }



        if (display_cards.size()==3){
            View v_card;
            TextView tv_question;
            RelativeLayout rl_card;
            RelativeLayout rl_card_inside;

            if (player_questioning.hasJoker()){
                iv_joker.setVisibility(View.VISIBLE);
            }

            for (int i=0;i<3;i++){
                // Check if an existing view is being reused, otherwise inflate the view
                v_card  = LayoutInflater.from(this).inflate(R.layout.card2, ll_cards, false);
                v_card.setTag(i);
                // Lookup view for data population
                tv_question = (TextView) v_card.findViewById(R.id.tv_question);
                rl_card = (RelativeLayout) v_card.findViewById(R.id.rl_card);
                rl_card_inside = (RelativeLayout) v_card.findViewById(R.id.rl_card_inside);
                rl_card_inside.setBackgroundResource(card_design);

                // Populate the data into the template view using the data object
                tv_question.setText(display_cards.get(i).getQuestion());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    rl_card.setElevation(8);
                }

                rl_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int card_num =  (int) v.getTag();
                        card_selected = display_cards.get(card_num);
                        display_cards.remove(card_num);
                        Log.d("Card display","clicked" + v.getTag());

                        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
                        iv_back.setVisibility(View.INVISIBLE);
                        ImageView iv_joker =  (ImageView) findViewById(R.id.iv_joker);
                        iv_joker.setVisibility(View.INVISIBLE);

                        LinearLayout ll_cards = (LinearLayout) findViewById(R.id.ll_cards);
                        for (int i = 0; i < 3; i++){
                            View view = ll_cards.getChildAt(i);
                            if ((int) view.getTag() != card_num){
                                view.setVisibility(View.GONE);
                            } else {

                            }
                        }

                        if (shuffle_cards_back){
                            cards.addAll(display_cards);
                            Auxiliar.cards_shuffle(cards);

                        }
                        iv_forward = (ImageView) findViewById(R.id.iv_forward);
                        iv_forward.setVisibility(View.VISIBLE);
                        iv_forward.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {

                                if (player_order == 0){
                                    player_questioning = player_questioned;
                                } else if (player_order == 1){
                                    selected_characters.get((player_questioning.getNumber()+1) % selected_characters.size());
                                } else if (player_order == 2){

                                    int randomNum = rand.nextInt(selected_characters.size()-1);
                                    if (randomNum >= player_questioning.getNumber()){
                                        randomNum = (randomNum + 1) % selected_characters.size();
                                    }
                                    player_questioning = selected_characters.get(randomNum);
                                }

                                sceneWheel.enter();
                                initialize_Wheel();
                            }
                        });
                    }
                });
                ll_cards.addView(v_card);
            }
        } else {
            // inform card stack empty, click
            View v_notification = LayoutInflater.from(this).inflate(R.layout.card_stack_empty, ll_cards, false);
            v_notification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Auxiliar.cards_new_stack();
                    sceneCardSelect.enter();
                    initialize_CardSelect();
                }});
            ll_cards.addView(v_notification);
        }

        iv_joker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ideally one should make sure that the new cards are all different
                if (cards.size()>=3){
                    List<Card> nextThreeCards = new ArrayList<Card>();
                    for (int i = 0; i <3 ; i++){
                        nextThreeCards.add(cards.get(0));
                        cards.remove(0);
                    }

                    if (shuffle_cards_back) {
                        cards.addAll(display_cards);
                        Auxiliar.cards_shuffle(cards);
                    }
                    cards.addAll(0,nextThreeCards);
                    player_questioning.usedJoker();
                    sceneCardSelect.enter();
                    initialize_CardSelect();
                } else {
                    Toast.makeText(Activity_Mode_WheelSelect.this,"Too few cards left",Toast.LENGTH_SHORT).show();
                }
            }});

        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Activity_Main.active_scene = 1;
                Intent intent=new Intent();
                cards.addAll(0,display_cards);
                intent.putExtra(Activity_Main.EXTRA_CONTINUE,new Continue(shuffle_cards_back, player_order,cards,1, selected_characters, player_questioning, player_questioned ));
                setResult(RESULT_OK,intent);
                finish();
            }
        });



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
}
