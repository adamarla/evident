package com.gradians.evident.gui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.gradians.evident.R;
import com.gradians.evident.dom.Asset;

import com.himamis.retex.renderer.android.LaTeXView;

import java.io.StringWriter;

/**
 * Created by adamarla on 3/19/17.
 */

public class AssetsListAdapter extends ArrayAdapter<Asset> {

    public AssetsListAdapter(Context ctx, Asset[] assets) {
        super(ctx, R.layout.display_asset, assets);
        mList = assets;
        this.ctx = ctx;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Asset asset = mList[position];
        asset.load(ctx);

        View view = convertView;
        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(R.layout.display_asset, parent, false);
            view.setPadding(10, 0, 0, 10) ;
            view.setBackgroundResource(R.color.white);
        }

        LaTeXView front = (LaTeXView) view.findViewById(R.id.front);
        front.setVisibility(View.VISIBLE);
        front.setLatexText(asset.getFront());

        LaTeXView rear = (LaTeXView) view.findViewById(R.id.rear );
        rear.setLatexText(asset.getBack());

        return view;
    }

    @Override
    public Asset getItem(int position) {
        return mList[position];
    }

    @Override
    public int getCount() {
        return mList.length ;
    }

    private Asset[] mList;
    private Context ctx;

}
