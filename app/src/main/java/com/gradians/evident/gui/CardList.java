package com.gradians.evident.gui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.gradians.evident.R;
import com.gradians.evident.dom.Snippet;
import com.gradians.evident.dom.Step;

/**
 * Created by adamarla on 3/26/17.
 */

public class CardList extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.cards_list, container, false);
        ListView list = (ListView)view.findViewById(R.id.cards);

        Bundle args = getArguments();
        cards = (ICard[])args.getParcelableArray("cards");

        adapter = new CardListAdapter(getContext(), cards);
        answerButtonBar = (AnswerButtonBar)view.findViewById(R.id.answer_button_bar);
        list.setAdapter(adapter);

        CardListListener listener = new CardListListener(this);
        list.setOnItemClickListener(listener);
        answerButtonBar.setOnClickListener(listener);

        if (cards[cards.length-1].isARiddle()) {
            list.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            list.setSelector(R.drawable.bg_selected_card);
            list.setDrawSelectorOnTop(true);
            enableButtonBar(false);
        } else {
            hideButtonBar(true);
        }

        return view;
    }

    public ICard[] getCards() {
        return cards;
    }

    public void hideButtonBar(boolean hide) {
        answerButtonBar.setVisibility(hide ? View.GONE : View.VISIBLE);
    }

    public void enableButtonBar(boolean enable) {
        answerButtonBar.enable(enable);
    }

    public void refresh() {
        adapter.notifyDataSetChanged();
    }

    public static CardList newInstance(ICard[] cards) {
        CardList list = new CardList();
        Bundle bundle = new Bundle();
        bundle.putParcelableArray("cards", cards);
        list.setArguments(bundle);
        return list;
    }

    CardListAdapter adapter;
    ICard[] cards;
    AnswerButtonBar answerButtonBar;
}

