package com.gradians.evident.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gradians.evident.R;
import com.gradians.evident.activity.DoQuestion;
import com.himamis.retex.renderer.android.LaTeXView;

/**
 * Created by adamarla on 3/26/17.
 */

public class CardList extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.cards_list, container, false);

        Bundle args = getArguments();
        chapterId = args.getInt("chapterId");
        cards = (ICard[])args.getParcelableArray("items");

        if (getActivity().getClass().toString().endsWith("DoQuestion")) {
            ICard questionCard = args.getParcelable("header");
            LaTeXView header = (LaTeXView)view.findViewById(R.id.header);
            header.setLatexText(questionCard.getFront());
            header.setVisibility(View.VISIBLE);
            cardsAreLinked = true;
            adapter = new CardListAdapter(getContext(), new ICard[] { cards[0] });
        } else {
            adapter = new CardListAdapter(getContext(), cards);
        }

        list = (ListView)view.findViewById(R.id.items);
        list.setAdapter(adapter);

        CardListListener listener = new CardListListener(this);
        list.setOnItemClickListener(listener);

        answerButtonBar = (AnswerButtonBar)view.findViewById(R.id.answer_button_bar);
        answerButtonBar.setOnClickListener(listener);

        if (cards[0].isAnswerable()) {
            list.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            list.setSelector(R.drawable.bg_selected_card);
            list.setDrawSelectorOnTop(true);
            enableButtonBar(false);
        } else {
            answerButtonBar.setVisibility(View.GONE);
        }

        return view;
    }

    public void revealNextCard() {
        if (cards.length == adapter.getCount()) return;
        adapter.add(cards[adapter.getCount()]);
        adapter.notifyDataSetChanged();
    }

    public void smoothScrollTo(int position) {
        list.smoothScrollToPosition(position);
        list.setSelection(position);
    }

    public void enableButtonBar(boolean enable) {
        answerButtonBar.enable(enable);
    }

    public static CardList newInstance(ICard header, ICard[] items, int chapterId) {
        CardList cl = new CardList();
        Bundle bundle = new Bundle();
        bundle.putParcelable("header", header);
        bundle.putParcelableArray("items", items);
        bundle.putInt("chapterId", chapterId);
        cl.setArguments(bundle);
        return cl;
    }

    CardListAdapter adapter;
    ICard[] cards;
    boolean cardsAreLinked = false;
    int chapterId;
    AnswerButtonBar answerButtonBar;
    ListView list;
}

class CardListListener implements AdapterView.OnItemClickListener, View.OnClickListener {

    public CardListListener(CardList cardList) {
        this.cardList = cardList;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        ICard card = ((CardView)view).getCard();
        if (card.hasSteps()) {
            Intent intent = new Intent(cardList.getActivity(), DoQuestion.class);
            intent.putExtra("chapterId", cardList.chapterId);
            intent.putExtra("position", position);
            cardList.getActivity().startActivity(intent);
        } else {
            if (selectedView != null && selectedView != view) {
                selectedView.unselect();
            }

            selectedView = (CardView)view;
            selectedView.select();

            if (card.hasBeenAttempted()) {
                /* move the flipped card to top of listView */
                if (selectedView.rightSideUp) cardList.smoothScrollTo(position);
                cardList.enableButtonBar(false);
            } else {
                cardList.enableButtonBar(true);
            }
        }
    }

    @Override
    public void onClick(View view) {
        selectedView.answer(view.getId() == R.id.btn_is_true);
        if (cardList.cardsAreLinked) cardList.revealNextCard();
    }

    CardList cardList;
    CardView selectedView;

}
