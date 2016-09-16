package com.littleones.fragenrondell;

import android.content.ClipData;
import java.io.Serializable;

import java.util.List;

/**
 * Created by Joe on 07.05.2016.
 */
public class Continue implements Serializable{

    // Class holding all the necessary infos to continue a previous single device game

    boolean continuable, shuffle_cards_back;
    int game_mode;
    int position;
    List<Card> cards;
    int current_scene, player_order;;
    List<Item_Character> characters;
    Item_Character player_questioning, player_questioned;

    /*
    List of game modes and scenes:
    game mode: free_play : 0
        no scenes
    game mode: wheelselect : 1
        wheel: 0
        card select: 1
     */

    public Continue(){
        this.continuable = false;
    }

    public Continue(List<Card> cards, int position){
        this.continuable = true;
        this.game_mode = 0;
        this.cards = cards;
        this.position = position;
    }

    public Continue(boolean shuffle_cards_back, int player_order, List<Card> cards, int current_scene, List<Item_Character> characters, Item_Character player_questioning, Item_Character player_questioned ){

        this.continuable = true;
        this.game_mode = 1;
        this.shuffle_cards_back = shuffle_cards_back;
        this.player_order = player_order;
        this.cards = cards;
        this.current_scene = current_scene;
        this.characters = characters;
        this.player_questioning = player_questioning;
        this.player_questioned = player_questioned;
    }

    public boolean getContinuable(){
        return continuable;
    }

    public int getGameMode(){
        return game_mode;
    }

    public boolean getShuffleCardsBack(){
        return shuffle_cards_back;
    }

    public int getPlayerOrder(){
        return player_order;
    }

    public List<Card> getCards(){
        return cards;
    }

    public int getPosition(){
        return position;
    }

    public int getCurrentScene(){
        return current_scene;
    }

    public List<Item_Character> getCharacters(){
        return characters;
    }

    public Item_Character getPlayerQuestioning(){
        return player_questioning;
    }

    public Item_Character getPlayerQuestioned(){
        return player_questioned;
    }

}
