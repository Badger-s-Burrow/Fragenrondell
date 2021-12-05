package de.badgersburrow.fragenrondell;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Created by Joe on 03.05.2016.
 */
public class Adapter_Character extends RecyclerView.Adapter<Adapter_Character.MyViewHolder> {

        private List<Item_Character> characterList;
        private int numSelectedCharacters;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public ImageView background, foreground, check;

            public MyViewHolder(View view) {
                super(view);
                background = (ImageView) view.findViewById(R.id.iv_background);
                foreground = (ImageView) view.findViewById(R.id.iv_foreground);
                check = (ImageView) view.findViewById(R.id.iv_check);
            }
        }


        public Adapter_Character(List<Item_Character> characterList) {
            this.characterList = characterList;
            this.numSelectedCharacters = 0;
            for (int i = 0; i<this.characterList.size();i++)
            {
                if (this.characterList.get(i).getChecked()){
                    this.numSelectedCharacters +=1;
                }
            }

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_character, parent, false);


            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            Item_Character character = characterList.get(position);
            holder.foreground.setImageResource(character.getDesign());
            holder.background.setColorFilter(character.getColor());
            holder.check.setVisibility(View.INVISIBLE);
            holder.foreground.setTag(position);
            holder.foreground.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v){
                    int position = (int) v.getTag();
                    Item_Character character = characterList.get(position);
                    if (character.getChecked()){
                        character.setChecked(false);
                        holder.check.setVisibility(View.INVISIBLE);
                        Activity_Mode_WheelSelect.num_selected_characters.dec();
                    } else {
                        character.setChecked(true);
                        holder.check.setVisibility(View.VISIBLE);
                        Activity_Mode_WheelSelect.num_selected_characters.inc();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return characterList.size();
        }

}
