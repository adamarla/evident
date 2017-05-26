package com.gradians.evident.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gradians.evident.R;
import com.gradians.evident.ops.SourceControl;
import com.gradians.evident.gui.ChapterList;
import com.gradians.evident.gui.ChapterListAdapter;

public class SelectChapter extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_chapter);

        // Set the adapter on the list
        final ChapterList chapterList = new ChapterList();
        chapterList.loadCatalog("chapters", getAssets());
        ChapterListAdapter adapter = new ChapterListAdapter(this, chapterList.getChapters());

        // Set onItemClickListener on the list
        final Activity parent = this;
        ListView list = (ListView)findViewById(R.id.chapter_list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(parent, InChapter.class);
                intent.putExtra("chapter", chapterList.getChapters()[i]);
                parent.startActivity(intent);
            }
        });

        chapterList.download(this, getProgressDialog());
        new SourceControl(this).sync();
    }

    private ProgressDialog getProgressDialog() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        dialog.setProgressNumberFormat(null);
        dialog.setProgressPercentFormat(null);
        dialog.setTitle("Syncing...");
        dialog.setMessage("Please wait...");
        dialog.setIndeterminate(true);
        dialog.show();
        return dialog;
    }

}
