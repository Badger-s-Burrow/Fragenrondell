package de.badgersburrow.fragenrondell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Joe on 07.05.2016.
 */
public class Auxiliar {
    public static void cards_shuffle(List<Card> cards){
        Collections.shuffle(cards);
    }

    public static List<Card> cards_new_stack(){
        List<Card> new_stack = new ArrayList<Card>();
        for (int i = 0; i < Activity_Main.questions_general.length; i++) {
            new_stack.add(new Card(i, Activity_Main.questions_general[i], Activity_Main.cat_general, 0));
        }
        cards_shuffle(new_stack);
        return new_stack;
    }
}
