package com.gradians.evident.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.gradians.evident.EvidentApp;
import com.gradians.evident.R;
import com.gradians.evident.dom.Chapter;
import com.gradians.evident.dom.Question;
import com.gradians.evident.dom.Skill;
import com.gradians.evident.dom.Snippet;
import com.gradians.evident.util.Sources;
import com.gradians.evident.gui.ChapterList;
import com.gradians.evident.gui.ChapterListAdapter;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class SelectChapter extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_chapter);

        // Set the adapter on the list
        chapterList = EvidentApp.app.chapterList;
        chapterList.loadCatalog("chapters", getAssets());
        ChapterListAdapter adapter = new ChapterListAdapter(this, chapterList.getChapters());

        // Set onItemClickListener on the list
        final Activity parent = this;
        ListView list = (ListView)findViewById(R.id.chapter_list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(parent, InChapter.class) ;
                intent.putExtra("chapterId", chapterList.getChapters()[i].id);
                parent.startActivity(intent);
            }
        });

        chapterList.download(this);
        new Sources(this).sync();
    }

    private ChapterList chapterList;

}
