package com.littleones.fragenrondell;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class FullwidthRelativeLayout extends RelativeLayout {

    public FullwidthRelativeLayout(Context context) {
        super(context);
    }

    public FullwidthRelativeLayout(Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    public void onFinishInflate() {
        if(Activity_Main.active_scene == 2){
            this.setTranslationX(-Activity_Main.screenWidth);
        }
        else{
            this.setTranslationX(0);
        }

        super.onFinishInflate();
    }


    public void draw(Canvas canvas) {
        super.draw(canvas);
    }
}