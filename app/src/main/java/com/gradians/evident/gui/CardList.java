package com.gradians.evident.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gradians.evident.R;
import com.gradians.evident.activity.DoQuestion;

/**
 * Created by adamarla on 3/26/17.
 */

public class CardList extends Fragment {

    int chapterId;

    public static CardList newInstance(ICard header, ICard[] items, int chapterId) {
        CardList cl = header == null ? new CardList(): new CardListRelated();
        Bundle bundle = new Bundle();
        bundle.putParcelable("header", header);
        bundle.putParcelableArray("items", items);
        bundle.putInt("chapterId", chapterId);
        cl.setArguments(bundle);
        return cl;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cards_list, container, false);

        Bundle args = getArguments();
        chapterId = args.getInt("chapterId");
        cards = (ICard[])args.getParcelableArray("items");

        list = (ListView)view.findViewById(R.id.items);
        setAdapter(cards);

        CardListListener listener = new CardListListener(this);
        list.setOnItemClickListener(listener);

        answerButtonBar = (AnswerButtonBar)view.findViewById(R.id.answer_button_bar);
        answerButtonBar.setOnClickListener(listener);

        if (cards.length > 0)
            if (cards[0].isAnswerable()) {
                list.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                list.setSelector(R.drawable.bg_selected_card);
                list.setDrawSelectorOnTop(true);
                list.setSelectionAfterHeaderView();
                answerButtonBar.enable(false);
            } else {
                answerButtonBar.setVisibility(View.GONE);
            }

        return view;
    }

    protected void setAdapter(ICard[] cards) {
        adapter = new CardListAdapter(getContext(), cards);
        list.setAdapter(adapter);
    }

    void enableButtonBar(boolean enable) {
        answerButtonBar.enable(enable);
    }

    void postClickAction() {
        enableButtonBar(false);
    }

    ICard[] cards;
    ListView list;
    CardListAdapter adapter;

    private AnswerButtonBar answerButtonBar;

}

class CardListListener implements AdapterView.OnItemClickListener, View.OnClickListener {

    CardListListener(CardList cardList) {
        this.cardList = cardList;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Log.d("EvidentApp", "Item Click # "+ position + " id: " + id + " view.toString: " + view.toString());
        ICard card = ((CardView)view).getCard();
        if (card.hasSteps()) {
            Intent intent = new Intent(cardList.getActivity(), DoQuestion.class);
            intent.putExtra("chapterId", cardList.chapterId);
            intent.putExtra("position", position);
            cardList.startActivity(intent);
        } else {
            if (selectedView != null && selectedView != view) {
                selectedView.unselect();
            }

            selectedView = (CardView)view;
            selectedView.select();

            if (card.hasBeenAttempted()) cardList.enableButtonBar(false);
            else cardList.enableButtonBar(true);
        }
    }

    @Override
    public void onClick(View view) {
        selectedView.answer(view.getId() == R.id.btn_is_true);
        cardList.postClickAction();
    }

    private CardList cardList;
    private CardView selectedView;

}
