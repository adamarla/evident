package com.gradians.evident.activity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.gradians.evident.EvidentApp;
import com.gradians.evident.R;
import com.gradians.evident.dom.Chapter;
import com.gradians.evident.dom.Question;
import com.gradians.evident.gui.CardList;
import com.gradians.evident.gui.CardListAdapter;
import com.gradians.evident.gui.ICard;

public class DoQuestion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_question);

        int questionId = getIntent().getIntExtra("id", 0);
        Question question = EvidentApp.app.questionById.get(questionId);
        cards = question.getSteps();
        currentStepIdx = 1;

        ICard[] toShow = new ICard[2];
        System.arraycopy(cards, 0, toShow, 0, 2);
        cardList = CardList.newInstance(toShow);
        initiate(cardList);
    }


    CardList cardList;
    int currentStepIdx;
    ICard[] cards;

    private void initiate(CardList cl) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.activity_do_question, cl);
        fragmentTransaction.commit();
    }
}
