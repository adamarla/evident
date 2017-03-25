package com.gradians.evident.gui;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gradians.evident.EvidentApp;
import com.gradians.evident.R;
import com.gradians.evident.dom.Chapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by abhinavc on 2/6/15.
 */
public class ChapterList {

    public ChapterList(Context context) {
        // Set the adapter on the list
        Collection<Chapter> values = EvidentApp.app.chapters.values();
        chapters = values.toArray(new Chapter[values.size()]);
    }

    public Chapter[] getChapters() {
        return chapters;
    }

    Chapter[] chapters;
}
