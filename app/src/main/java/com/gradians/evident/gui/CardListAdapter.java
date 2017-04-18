package com.gradians.evident.gui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.gradians.evident.R;

import java.util.ArrayList;

/**
 * Created by adamarla on 3/19/17.
 */

public class CardListAdapter extends ArrayAdapter<ICard> {

    public CardListAdapter(Context ctx, ICard[] cards) {
        super(ctx, R.layout.card, cards);
    }

    public CardListAdapter(Context ctx, ArrayList<ICard> cards) {
        super(ctx, R.layout.card, cards);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ICard card = this.getItem(position);

        CardView view = (CardView)convertView;
        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            view = (CardView)layoutInflater.inflate(R.layout.card, parent, false);
        }

        view.setCard(card);
        return view;
    }

}
