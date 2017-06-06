package com.gradians.evident.activity;

import android.app.Activity;
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
import com.gradians.evident.gui.AnswerButtonBar;
import com.gradians.evident.gui.HelpOverlay;
import com.gradians.evident.gui.HelpTarget;
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
        tabs = (TabLayout)findViewById(R.id.tabs);
        pager = (ViewPager)findViewById(R.id.pager);
        TabsPagerAdapter adapter = new TabsPagerAdapter(chapter, getSupportFragmentManager());
        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);
        final Activity caller = this;
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            @Override
            public void onPageSelected(int position) { }
            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    View fragment = pager.getChildAt(pager.getCurrentItem());
                    ListView items = (ListView)fragment.findViewById(R.id.items);
                    switch (pager.getCurrentItem()) {
                        case 0:
                            HelpTarget target = new HelpTarget(items.getChildAt(1),
                                    R.string.in_chapter_title2, R.string.in_chapter_message2);
                            break;
                        case 1:
                            HelpTarget target1, target2, target3;
                            target1 = new HelpTarget(items.getChildAt(1),
                                    R.string.in_chapter_title3, R.string.in_chapter_message3);
                            AnswerButtonBar buttonBar = (AnswerButtonBar)fragment.findViewById(R.id.answer_button_bar);
                            target2 = new HelpTarget(buttonBar,
                                    R.string.in_chapter_title4, R.string.in_chapter_message4);
                            target3 = new HelpTarget(items.getChildAt(1),
                                    R.string.in_chapter_title5, R.string.in_chapter_message5);
                            new HelpOverlay(new HelpTarget[] { target1, target2, target3 }, caller).show();
                            break;
                        default:
                            new HelpOverlay(new HelpTarget(items.getChildAt(1),
                                    R.string.in_chapter_title6, R.string.in_chapter_message6), caller).show();
                    }
                }
            }
        });
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
        HelpTarget target = new HelpTarget(tabs,
                R.string.in_chapter_title1, R.string.in_chapter_message1);
        new HelpOverlay(target, this).show();
    }

    private TabLayout tabs;
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

}
