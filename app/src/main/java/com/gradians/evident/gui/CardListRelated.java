package com.gradians.evident.gui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gradians.evident.R;

import java.util.ArrayList;

/**
 * Created by adamarla on 5/5/17.
 */

public class CardListRelated extends CardList {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        Bundle args = getArguments();
        ICard questionCard = args.getParcelable("header");
        CardView header = (CardView)inflater.inflate(R.layout.card, null);
        header.setCard(questionCard);
        header.disableExpansion();
        list.addHeaderView(header, questionCard, false);

        openStack();
        return view;
    }

    @Override
    protected void setAdapter(ICard[] cards) {
        ArrayList<ICard> stack = new ArrayList<>();
        stack.add(cards[0]);
        adapter = new CardListAdapter(getContext(), stack);
        list.setAdapter(adapter);
    }

    @Override
    void postClickAction() {
        super.postClickAction();
        revealNextCard();
    }

    private boolean revealNextCard() {
        int position = adapter.getCount();
        if (cards.length == position) return false;
        adapter.add(cards[position]);
        adapter.notifyDataSetChanged();
        return true;
    }

    private void openStack() {
        int i = 0;
        while (cards[i].hasBeenAttempted()) {
            if (!revealNextCard()) break;
            else i++;
        }
    }

}
