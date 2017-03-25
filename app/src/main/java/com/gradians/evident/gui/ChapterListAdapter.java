package com.gradians.evident.gui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gradians.evident.R;
import com.gradians.evident.dom.Chapter;

/**
 * Created by adamarla on 3/19/17.
 */
public class ChapterListAdapter extends ArrayAdapter<Chapter> {

    public ChapterListAdapter(Context ctx, Chapter[] chapters) {
        super(ctx, R.layout.display_line, chapters) ;
        mCtx = ctx ;
        mList = chapters;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Chapter child = mList[position];

        View view = convertView;
        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(R.layout.display_line, parent, false);
            view.setPadding(10, 0, 0, 10) ;
            view.setBackgroundResource(R.color.white);
        }

        TextView label = (TextView) view.findViewById(R.id.label) ;
        label.setVisibility(View.VISIBLE);
        label.setText(child.name);

        return view;
    }

    @Override
    public Chapter getItem(int position) {
        return mList[position];
    }

    @Override
    public int getCount() {
        return mList.length ;
    }

    private Context mCtx;
    private Chapter[] mList;

}
