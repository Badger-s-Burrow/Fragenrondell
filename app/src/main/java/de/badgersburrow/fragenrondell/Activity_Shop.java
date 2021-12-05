package de.badgersburrow.fragenrondell;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Activity_Shop extends AppCompatActivity {

    CheckBox cb_premium;
    CheckBox cb_intimacy;
    CheckBox cb_sport;
    CheckBox cb_friendship;
    CheckBox cb_job;
    CheckBox cb_relationship;
    CheckBox cb_forkids;

    SharedPreferences sharedpreferences;

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

        setContentView(R.layout.activity_shop);

        cb_premium = (CheckBox) findViewById(R.id.cb_premium);
        cb_intimacy = (CheckBox) findViewById(R.id.cb_intimacy);
        cb_sport = (CheckBox) findViewById(R.id.cb_sport);
        cb_friendship = (CheckBox) findViewById(R.id.cb_friendship);
        cb_job = (CheckBox) findViewById(R.id.cb_job);
        cb_relationship = (CheckBox) findViewById(R.id.cb_relationship);
        cb_forkids = (CheckBox) findViewById(R.id.cb_forkids);

        sharedpreferences = getSharedPreferences(Activity_Main.sharedPrefKey, Context.MODE_PRIVATE);
        cb_premium.setChecked(sharedpreferences.getBoolean("premium",false));
        cb_intimacy.setChecked(sharedpreferences.getBoolean("intimacy",false));
        cb_sport.setChecked(sharedpreferences.getBoolean("sport",false));
        cb_friendship.setChecked(sharedpreferences.getBoolean("friendship",false));
        cb_job.setChecked(sharedpreferences.getBoolean("job",false));
        cb_relationship.setChecked(sharedpreferences.getBoolean("relationship",false));
        cb_forkids.setChecked(sharedpreferences.getBoolean("forkids",false));

        Button bt_save = (Button) findViewById(R.id.save);
        bt_save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putBoolean("premium",cb_premium.isChecked());
                editor.putBoolean("intimacy",cb_intimacy.isChecked());
                editor.putBoolean("sport",cb_sport.isChecked());
                editor.putBoolean("friendship",cb_friendship.isChecked());
                editor.putBoolean("job",cb_job.isChecked());
                editor.putBoolean("relationship",cb_relationship.isChecked());
                editor.putBoolean("forkids",cb_forkids.isChecked());
                editor.commit();
                finish();

            }
        });


    }
}
