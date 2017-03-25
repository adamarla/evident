package com.gradians.evident.activity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gradians.evident.EvidentApp;
import com.gradians.evident.R;
import com.gradians.evident.dom.Chapter;
import com.gradians.evident.dom.Skill;
import com.gradians.evident.gui.AssetsListAdapter;

import java.util.ArrayList;


public class InChapter extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_chapter);

        Log.d("EvidentApp", "InChapter.onCreate -->");
        // Set the adapter on the list
        ArrayList<Skill> skills = EvidentApp.app.chapters.get(30).skills;
        AssetsListAdapter adapter = new AssetsListAdapter(this,
                skills.toArray(new Skill[skills.size()]));

        // Set onItemClickListener on the list
        ListView list = (ListView)findViewById(R.id.skill_list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("EvidentApp", view.getClass().toString());
                if (view.findViewById(R.id.front).getVisibility() == View.GONE) {
                    view.findViewById(R.id.front).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.rear).setVisibility(View.GONE);
                } else {
                    view.findViewById(R.id.rear).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.front).setVisibility(View.GONE);
                }
            }
        });

    }

}
