package de.badgersburrow.fragenrondell;

import java.io.Serializable;

/**
 * Created by Joe on 03.05.2016.
 */
public class Item_Character implements Serializable {
    private int number, design, color;
    private boolean selected, joker;

    public Item_Character(int number, int design, int color, boolean joker) {
        this.number = number;
        this.design = design;
        this.color = color;
        this.joker = joker;
        this.selected = false;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getDesign() {
        return design;
    }

    public int getColor() {
        return color;
    }

    public boolean hasJoker() {return joker;}

    public void usedJoker() {this.joker = false;}

    public boolean getChecked(){
        return selected;
    }

    public void setChecked(boolean selected){
        this.selected = selected;
    }
}