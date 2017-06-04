package com.gradians.evident.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.gradians.evident.R;
import com.gradians.evident.dom.Chapter;
import com.gradians.evident.dom.Question;
import com.gradians.evident.gui.HelpOverlay;
import com.gradians.evident.gui.TabsPagerAdapter;


public class InChapter extends AppCompatActivity implements DialogInterface.OnDismissListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_chapter);

        chapter = getIntent().getParcelableExtra("chapter");
        chapter.load(this, getProgressDialog(chapter.name));
    }

    public void onLoad() {
        TabLayout tabs = (TabLayout)findViewById(R.id.tabs);
        pager = (ViewPager)findViewById(R.id.pager);
        TabsPagerAdapter adapter = new TabsPagerAdapter(chapter, getSupportFragmentManager());
        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);
        tabs.setOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(pager) {

                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);
                        int textId = 0, titleId = 0;
                        switch (tab.getPosition()) {
                            case 1:
                                textId = R.string.in_chapter_message2;
                                titleId = R.string.in_chapter_title2;
                                break;
                            case 2:
                                textId = R.string.in_chapter_message3;
                                titleId = R.string.in_chapter_title3;
                                break;
                            default:
                        }
                        if (textId != 0) displayOverlay(pager.getChildAt(tab.getPosition()), titleId, textId);
                    }
                }
        );
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

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        displayOverlay(pager.getChildAt(0), R.string.in_chapter_title1, R.string.in_chapter_message1);
    }

    private ViewPager pager;
    private Chapter chapter;

    private ProgressDialog getProgressDialog(String name) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        dialog.setProgressNumberFormat(null);
        dialog.setProgressPercentFormat(null);
        dialog.setTitle(String.format("Loading %s...", name));
        dialog.setIndeterminate(true);
        dialog.setOnDismissListener(this);
        dialog.show();
        return dialog;
    }

    private void displayOverlay(View view, int titleId, int textId) {
        ListView items = (ListView)view.findViewById(R.id.items);
        new HelpOverlay(items.getChildAt(1), this, titleId, textId).show();
    }
}
