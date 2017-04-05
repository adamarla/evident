package com.gradians.evident.gui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.gradians.evident.R;

import com.gradians.evident.dom.Skill;
import com.gradians.evident.dom.Snippet;
import com.himamis.retex.renderer.android.LaTeXView;

/**
 * Created by adamarla on 3/19/17.
 */

public class CardListAdapter extends ArrayAdapter<ICard> {

    public CardListAdapter(Context ctx, ICard[] cards) {
        super(ctx, R.layout.card, cards);
        mList = cards;
        this.ctx = ctx;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ICard card = mList[position];
        card.load(ctx);

        View view = convertView;
        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(R.layout.card, parent, false);
            view.setPadding(10, 0, 0, 10);
            view.setBackgroundResource(R.drawable.bg_grey_card);
            int minHeight = card.isARiddle() ?
                (int)ctx.getResources().getDimension(R.dimen.snippet_min_height):
                (int)ctx.getResources().getDimension(R.dimen.skill_min_height);
            view.setMinimumHeight(minHeight);

        }

        LaTeXView front = (LaTeXView) view.findViewById(R.id.front);
        front.setVisibility(View.VISIBLE);
        front.setLatexText(card.getFront());

        LaTeXView rear = (LaTeXView) view.findViewById(R.id.rear);
        rear.setLatexText(card.getBack());

        if (card.isARiddle() && card.hasBeenAttempted()) {
            ImageView rightWrong = (ImageView)view.findViewById(R.id.right_wrong_indicator);
            rightWrong.setBackgroundResource(card.isCorrect() ?
                    R.drawable.bg_circle_correct : R.drawable.bg_circle_incorrect);
            rightWrong.setImageResource(card.isCorrect() ?
                    R.mipmap.white_tick : R.mipmap.white_cross);
            rightWrong.setVisibility(View.VISIBLE);
        }

        if (card instanceof Snippet)
            Log.d("EvidentApp", "Card at #" + position + " is correct? " + card.isCorrect());
        return view;
    }

    @Override
    public ICard getItem(int position) {
        return mList[position];
    }

    @Override
    public int getCount() {
        return mList.length ;
    }

    private ICard[] mList;
    private Context ctx;

}
