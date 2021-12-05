package de.badgersburrow.fragenrondell;

import java.io.Serializable;

/**
 * Created by Joe on 17.01.2016.
 */
public class Card implements Serializable {

    private int number, category, design;
    private String question;

    public Card(int number, String question, int category, int design){
        this.number = number;
        this.question = question;
        this.category = category;
        this.design = design;
    }

    public int getNumber() {
        return this.number;
    }
    public String getQuestion() {
            return this.question;
    }
    public int getCategory() {
        return this.category;
    }
    public int getDesign() {
        return this.design;
    }

}
