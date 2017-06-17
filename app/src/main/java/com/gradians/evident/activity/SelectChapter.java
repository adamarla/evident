package com.gradians.evident.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gradians.evident.R;
import com.gradians.evident.dom.Chapter;
import com.gradians.evident.gui.HelpOverlay;
import com.gradians.evident.gui.HelpTarget;
import com.gradians.evident.ops.SourceControl;
import com.gradians.evident.gui.ChapterList;
import com.gradians.evident.gui.ChapterListAdapter;

import java.util.Comparator;

public class SelectChapter extends AppCompatActivity implements DialogInterface.OnDismissListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_chapter);

        // Set the adapter on the list
        final ChapterList chapterList = new ChapterList();
        chapterList.loadCatalog("chapters", getAssets());
        final ChapterListAdapter adapter = new ChapterListAdapter(this, chapterList.getChapters());
        adapter.sort(new Comparator<Chapter>() {
            @Override
            public int compare(Chapter c1, Chapter c2) {
                return c1.name.compareTo(c2.name);
            }
        });

        // Set onItemClickListener on the list
        final Activity parent = this;
        list = (ListView)findViewById(R.id.chapter_list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(parent, InChapter.class);
                intent.putExtra("chapter", adapter.getItem(i));
                parent.startActivity(intent);
            }
        });

        chapterList.download(this, getProgressDialog());
        new SourceControl(this).sync();
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        HelpTarget target = new HelpTarget(list.getChildAt(0),
                R.string.select_chapter_title, R.string.select_chapter_message);
        new HelpOverlay(target, this).show();
    }

    private ProgressDialog getProgressDialog() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        dialog.setProgressNumberFormat(null);
        dialog.setProgressPercentFormat(null);
        dialog.setTitle("Synchronizing...");
        dialog.setIndeterminate(true);
        dialog.show();
        dialog.setOnDismissListener(this);
        return dialog;
    }

    private ListView list;

}
