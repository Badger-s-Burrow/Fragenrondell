package de.badgersburrow.fragenrondell;

import android.content.res.TypedArray;

import java.util.ArrayList;

/**
 * Created by Joe on 10.04.2016.
 */
public class Background {

    private int mdesign;

    public Background(int design) {
        mdesign = design;

    }

    public int getDesign(){
        return mdesign;
    }

    public static ArrayList<Background> getBackgrounds(TypedArray background_tiles){
        ArrayList<Background> backgrounds = new ArrayList<>();
        for (int i = 0; i<background_tiles.length(); i++){
            backgrounds.add(new Background(background_tiles.getResourceId(i,0)));
        }

        return backgrounds;
    }
}
