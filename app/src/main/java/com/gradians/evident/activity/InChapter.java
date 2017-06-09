package com.gradians.evident.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.in_chapter, menu);
        return true;
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
                        case 2:
                            new HelpOverlay(new HelpTarget(items.getChildAt(1),
                                    R.string.in_chapter_title6, R.string.in_chapter_message6), caller).show();
                        default:
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
        HelpTarget target1 = new HelpTarget(tabs,
                R.string.in_chapter_title1, R.string.in_chapter_message1, R.string.ack_text);
        View fragment = pager.getChildAt(0);
        ListView items = (ListView)fragment.findViewById(R.id.items);
        HelpTarget target2 = new HelpTarget(items.getChildAt(1),
                R.string.in_chapter_title2, R.string.in_chapter_message2);
        new HelpOverlay(new HelpTarget[] { target1, target2 }, this).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.reset:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.reset_button).setMessage(R.string.reset_text)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                chapter.reset();
                                onLoad();
                            }
                        })
                        .setNegativeButton("Cancel", null);
                builder.show();
                break;
        }
        return true;
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
