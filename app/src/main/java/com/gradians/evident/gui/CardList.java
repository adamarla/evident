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
import com.gradians.evident.activity.InChapter;
import com.gradians.evident.dom.Chapter;
import com.gradians.evident.dom.Question;

/**
 * Created by adamarla on 3/26/17.
 */

public class CardList extends Fragment {

    public static CardList newInstance(ICard[] items, ICard header) {
        CardList cl = header == null ? new CardList(): new CardListRelated();
        Bundle bundle = new Bundle();
        bundle.putParcelable("header", header);
        bundle.putParcelableArray("items", items);
        cl.setArguments(bundle);
        return cl;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cards_list, container, false);

        Bundle args = getArguments();
        cards = (ICard[])args.getParcelableArray("items");

        list = (ListView)view.findViewById(R.id.items);
        setAdapter(cards);

        CardListListener listener = new CardListListener(this);
        list.setOnItemClickListener(listener);

        answerButtonBar = (AnswerButtonBar)view.findViewById(R.id.answer_button_bar);
        answerButtonBar.setOnClickListener(listener);

        if (cards.length > 0)
            if (cards[0].isAnswerable() && !cards[0].hasSteps()) {
                list.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                list.setSelector(R.drawable.bg_selected_card);
                list.setDrawSelectorOnTop(true);
                answerButtonBar.enable(false);
            } else {
                answerButtonBar.setVisibility(View.GONE);
            }

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int position = data.getIntExtra("position", -1);
        Question question = data.getParcelableExtra("question");
        cards[position] = question.getCard();
        adapter.notifyDataSetChanged();
    }

    void setAdapter(ICard[] cards) {
        adapter = new CardListAdapter(getContext(), cards);
        list.setAdapter(adapter);
    }

    void enableButtonBar(boolean enable) {
        answerButtonBar.enable(enable);
    }

    void postClickAction() {
        enableButtonBar(false);
    }

    Question getQuestionAt(int position) {
        return ((InChapter)getActivity()).getQuestion(position);
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
        ICard card = ((CardView)view).getCard();
        if (card.hasSteps()) {
            Intent intent = new Intent(cardList.getActivity(), DoQuestion.class);
            intent.putExtra("question", cardList.getQuestionAt(position));
            intent.putExtra("position", position);
            cardList.startActivityForResult(intent, 10);
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
