package com.gradians.evident.activity;

import android.app.ProgressDialog;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gradians.evident.R;
import com.gradians.evident.dom.Chapter;
import com.gradians.evident.dom.Question;
import com.gradians.evident.gui.TabsPagerAdapter;

public class InChapter extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_chapter);

        chapter = getIntent().getParcelableExtra("chapter");
        chapter.load(this, getProgressDialog(chapter.name));
    }

    public void onLoad() {
        TabLayout tabs = (TabLayout)findViewById(R.id.tabs);
        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        TabsPagerAdapter adapter = new TabsPagerAdapter(chapter, getSupportFragmentManager());
        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);
    }

    @Override
    public void onBackPressed() {
        chapter.save(this);
        super.onBackPressed();
    }

    public Question getQuestion(int position) {
        return chapter.questions.get(position);
    }

    public void setQuestion(Question question, int position) {
        chapter.questions.set(position, question);
    }

    private Chapter chapter;

    private ProgressDialog getProgressDialog(String name) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        dialog.setProgressNumberFormat(null);
        dialog.setProgressPercentFormat(null);
        dialog.setTitle(String.format("Loading %s...", name));
        dialog.setIndeterminate(true);
        dialog.show();
        return dialog;
    }


}
