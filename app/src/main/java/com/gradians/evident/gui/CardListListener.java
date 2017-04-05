package com.gradians.evident.gui;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.gradians.evident.R;
import com.gradians.evident.activity.DoQuestion;
import com.gradians.evident.dom.Asset;
import com.gradians.evident.dom.Question;

/**
 * Created by adamarla on 4/4/17.
 */
class CardListListener implements AdapterView.OnItemClickListener, View.OnClickListener {

    public CardListListener(CardList cardList) {
        this.cardList = cardList;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        selectedCard = cardList.getCards()[i];
        if (selectedCard instanceof Question) {
            Intent intent = new Intent(cardList.getActivity(), DoQuestion.class);
            intent.putExtra("id", ((Asset)selectedCard).getId());
            cardList.getActivity().startActivity(intent);
        } else {
            if (selectedCard.hasBeenAttempted()) {
                flip(view);
            } else {
                cardList.enableButtonBar(true);
            }
            if (selectedView != null && selectedView != view) {
                selectedView.setBackgroundResource(R.drawable.bg_grey_card);
            }
            selectedView = view;
            selectedView.setBackgroundResource(R.color.white);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_is_true) {
            selectedCard.attempt(selectedCard.isCorrect());
        } else {
            selectedCard.attempt(!selectedCard.isCorrect());
        }
        cardList.refresh();
    }

    CardList cardList;
    View selectedView;
    ICard selectedCard;

    private void flip(View view) {
        if (view.findViewById(R.id.front).getVisibility() == View.GONE) {
            view.findViewById(R.id.front).setVisibility(View.VISIBLE);
            view.findViewById(R.id.rear).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.rear).setVisibility(View.VISIBLE);
            view.findViewById(R.id.front).setVisibility(View.GONE);
        }
    }
}
