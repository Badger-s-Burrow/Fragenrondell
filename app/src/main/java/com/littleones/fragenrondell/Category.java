package com.littleones.fragenrondell;

/**
 * Created by Joe on 06.02.2016.
 */
public class Category {

    private int number, design, box_design, label_image, primary_color, secondary_color;
    private String label, name;
    private boolean bought;
    private int state;


    public Category(int number, String name, String label, int label_image, int design, int boxDesign, int primaryColor, int secondaryColor, boolean bought){
        this.number = number;
        this.name = name;
        this.label = label;
        this.bought = bought;
        this.label_image = label_image;
        this.design = design;
        this.box_design = boxDesign;
        this.primary_color = primaryColor;
        this.secondary_color = secondaryColor;
        this.state = 0; // 0: closed, 1: open, 2: category selected
    }

    public String get_label() {
        return label;
    }
    public int get_state() {
        return state;
    }
    public void set_state(int state) { this.state = state; }

    public int get_box_design() {
        return box_design;
    }

    public int get_design() {
        return design;
    }

    public int get_label_image() {
        return label_image;
    }

    public int get_primary_color() { return primary_color; }

    public int get_secondary_color() { return secondary_color; }
}
