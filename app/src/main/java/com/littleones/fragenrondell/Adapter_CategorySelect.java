package com.littleones.fragenrondell;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.OvershootInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;


/**
 * Created by Joe on 06.02.2016.
 */
public class Adapter_CategorySelect extends RecyclerView.Adapter<Adapter_CategorySelect.ViewHolder_CategorySelect>{


    private List<Category> categoryList;
    private onMoveListener mMoveListener;



    private CategoryStackCardListener categoryStackCardListener;



    public Adapter_CategorySelect(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder_CategorySelect contactViewHolder, final int i) {
        ViewGroup parent = (ViewGroup) contactViewHolder.iv_lid_closed.getParent();
        Category ci = categoryList.get(i);
        contactViewHolder.tv_label.setText(ci.get_label());
        contactViewHolder.rl_box_top.setBackgroundColor(ci.get_primary_color());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            contactViewHolder.rl_box_top.setElevation(10);
        }
        contactViewHolder.iv_lid_closed.setBackgroundColor(ci.get_primary_color());
        contactViewHolder.iv_lid_open.setBackgroundColor(ci.get_primary_color());
        if (ci.get_box_design() != 0){
            contactViewHolder.iv_box_top_frame.setBackground(parent.getResources().getDrawable(ci.get_box_design()));
        }


        //else has to set back to default
        if (ci.get_design() != 0){
            Drawable design = parent.getResources().getDrawable(ci.get_design());
            design.setColorFilter(ci.get_secondary_color(), PorterDuff.Mode.SRC_IN);
            contactViewHolder.iv_box_top_design.setBackground(design);
        } else {
            contactViewHolder.iv_box_top_design.setBackgroundResource(R.color.transparent);
        }

        if (ci.get_label_image() != 0){
            contactViewHolder.v_tape.setVisibility(View.VISIBLE);
            contactViewHolder.iv_category_label.setImageDrawable( parent.getResources().getDrawable(ci.get_label_image()));
            contactViewHolder.tv_box_top.setText("");
        }else{
            contactViewHolder.v_tape.setVisibility(View.INVISIBLE);
            contactViewHolder.tv_box_top.setText(ci.get_label() + " " + String.valueOf(ci.get_state()));
        }


        //contactViewHolder.vSurname.setText(ci.surname);
        //contactViewHolder.vEmail.setText(ci.email);
        //contactViewHolder.vTitle.setText(ci.name + " " + ci.surname);
        float xPos = contactViewHolder.iv_box_bottom.getX();
        float boxWidth = contactViewHolder.iv_box_bottom.getWidth();
        float widthLid = contactViewHolder.iv_lid_closed.getWidth();
        if (ci.get_state() == 0) {
            contactViewHolder.iv_lid_closed.setVisibility(View.VISIBLE);
            contactViewHolder.iv_lid_closed.setX(xPos + boxWidth - widthLid);
            contactViewHolder.iv_lid_closed.setScaleX(1);
            contactViewHolder.iv_lid_closed.setImageAlpha(100);
            parent.bringChildToFront(contactViewHolder.iv_lid_closed);
            contactViewHolder.iv_lid_open.setVisibility(View.INVISIBLE);
            contactViewHolder.rl_card.setX(xPos);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                contactViewHolder.iv_lid_closed.setElevation(11);
            }

        }
        else if (ci.get_state() == 1) {
            contactViewHolder.iv_lid_closed.setVisibility(View.INVISIBLE);
            contactViewHolder.iv_lid_open.setVisibility(View.VISIBLE);
            //contactViewHolder.iv_lid_closed.setX(xPos+boxWidth+0.25f*widthLid);
            //contactViewHolder.iv_lid_closed.setScaleX(-1.5f);
            //contactViewHolder.iv_lid_closed.setImageAlpha(0);
            contactViewHolder.rl_card.setX(xPos);

        }
        else if (ci.get_state() == 2) {
            contactViewHolder.iv_lid_closed.setVisibility(View.INVISIBLE);
            contactViewHolder.iv_lid_open.setVisibility(View.VISIBLE);
            contactViewHolder.rl_card.setX(xPos+contactViewHolder.rl_box_top.getWidth()/2);
        }
        contactViewHolder.iv_lid_closed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView iv = (ImageView) v;
                ViewGroup parent = (ViewGroup) v.getParent();
                Category ci = categoryList.get(i);
                float xPos = v.getX();
                float width = v.getWidth();
                if (ci.get_state() == 0) {

                    float actualVolume = (float) Activity_CategorySelect.audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    float maxVolume = (float) Activity_CategorySelect.audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                    float volume = actualVolume/maxVolume;
                    if (Activity_CategorySelect.loaded) {
                        Activity_CategorySelect.soundPool.play(Activity_CategorySelect.soundID_openbox,volume,volume,1,0,1f);
                        Log.e("Sound_Test","Played open box sound");
                    }

                    v.animate()
                            .setDuration(200)
                            .setInterpolator(new OvershootInterpolator(1.5f))
                            .translationX(width * 0.5f);
                    iv.setImageAlpha(0);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        v.animate()
                                .setDuration(200)
                                .setInterpolator(new OvershootInterpolator(1.5f))
                                .translationX(width * 1.25f)
                                .scaleX(-1.5f);
                    } else {
                        v.animate()
                                .setDuration(200)
                                .setInterpolator(new OvershootInterpolator(1.5f))
                                .translationX(width * 1.25f)
                                .scaleX(-1.5f);
                    }

                    parent.removeView(v);
                    parent.addView(v, 0);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        v.setElevation(0);
                    }
                    ci.set_state(1);
                }
            }
        });

        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) contactViewHolder.iv_box_bottom.getLayoutParams();

        categoryStackCardListener = new CategoryStackCardListener(contactViewHolder.rl_card, ci,
                Activity_Main.cat_box_width, lp.leftMargin,
                new CategoryStackCardListener.MoveListener() {

            @Override
            public void selectCategory(Object dataObject) {
                mMoveListener.onSelectCategory(dataObject);
            }

            @Override
            public void unselectCategory(Object dataObject) {
                mMoveListener.onUnselectCategory(dataObject);
            }

        });

        contactViewHolder.rl_card.setOnTouchListener(categoryStackCardListener);
    }

    public void setMoveListener(onMoveListener onMoveListener) {
        this.mMoveListener = onMoveListener;
    }

    public interface onMoveListener {
        void onSelectCategory(Object dataObject);
        void onUnselectCategory(Object dataObject);
    }

    @Override
    public ViewHolder_CategorySelect onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.category, viewGroup, false);

        return new ViewHolder_CategorySelect(itemView);
    }

    public static class ViewHolder_CategorySelect extends RecyclerView.ViewHolder  {

        protected TextView tv_label;
        protected ImageView iv_lid_closed;
        protected ImageView iv_lid_open;
        protected RelativeLayout rl_card;
        protected TextView tv_box_top;
        protected ImageView iv_box_bottom;
        protected RelativeLayout rl_box_top;
        protected ImageView iv_box_top_frame;
        protected ImageView iv_box_top_design;
        protected View v_tape;
        protected ImageView iv_category_label;

        public ViewHolder_CategorySelect(View v) {
            super(v);
            tv_label = (TextView) v.findViewById(R.id.tv_question);
            iv_lid_closed =  (ImageView) v.findViewById(R.id.iv_lid_closed);
            iv_lid_open =  (ImageView) v.findViewById(R.id.iv_lid_open);
            rl_card = (RelativeLayout) v.findViewById(R.id.rl_card);
            rl_box_top = (RelativeLayout) v.findViewById(R.id.rl_box_top);
            tv_box_top =  (TextView) v.findViewById(R.id.tv_box_top);
            iv_box_bottom =  (ImageView) v.findViewById(R.id.iv_box_bottom);
            iv_box_top_frame =  (ImageView) v.findViewById(R.id.iv_box_top_frame);
            iv_box_top_design =  (ImageView) v.findViewById(R.id.iv_box_top_design);
            v_tape = (View) v.findViewById(R.id.v_tape);
            iv_category_label =  (ImageView) v.findViewById(R.id.iv_category_label);
            //vSurname = (TextView)  v.findViewById(R.id.txtSurname);
            //vEmail = (TextView)  v.findViewById(R.id.txtEmail);
            //vTitle = (TextView) v.findViewById(R.id.title);
        }
    }
}
