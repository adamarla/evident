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
import com.gradians.evident.dom.Asset;

/**
 * Created by adamarla on 3/26/17.
 */

public class CardList extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.cards_list, container, false);
        list = (ListView)view.findViewById(R.id.cards);

        Bundle args = getArguments();
        ICard[] cards = (ICard[])args.getParcelableArray("cards");

        CardListAdapter adapter = new CardListAdapter(getContext(), cards);
        answerButtonBar = (AnswerButtonBar)view.findViewById(R.id.answer_button_bar);
        list.setAdapter(adapter);

        CardListListener listener = new CardListListener(this);
        list.setOnItemClickListener(listener);
        answerButtonBar.setOnClickListener(listener);

        if (cards[cards.length-1].isAnswerable()) {
            list.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            list.setSelector(R.drawable.bg_selected_card);
            list.setDrawSelectorOnTop(true);
            enableButtonBar(false);
        } else {
            hideButtonBar(true);
        }

        return view;
    }

    public void smoothScrollTo(int position) {
        list.smoothScrollToPosition(position);
        list.setSelection(position);
    }

    public void hideButtonBar(boolean hide) {
        answerButtonBar.setVisibility(hide ? View.GONE : View.VISIBLE);
    }

    public void enableButtonBar(boolean enable) {
        answerButtonBar.enable(enable);
    }

    public static CardList newInstance(ICard[] cards) {
        CardList list = new CardList();
        Bundle bundle = new Bundle();
        bundle.putParcelableArray("cards", cards);
        list.setArguments(bundle);
        return list;
    }

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
            intent.putExtra("id", ((Asset)card).getId());
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
    }

    CardList cardList;
    CardView selectedView;

}
