package com.gradians.evident.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gradians.evident.EvidentApp;
import com.gradians.evident.R;
import com.gradians.evident.dom.Chapter;
import com.gradians.evident.gui.TabsPagerAdapter;


public class InChapter extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_chapter);

        int chapterId = getIntent().getIntExtra("chapterId", 0);

        Chapter chapter = EvidentApp.app.chapterList.getChapter(chapterId);
        chapter.load(this);

        TabLayout tabs = (TabLayout)findViewById(R.id.tabs);
        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        TabsPagerAdapter adapter = new TabsPagerAdapter(chapter, getSupportFragmentManager());
        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);
    }

}
