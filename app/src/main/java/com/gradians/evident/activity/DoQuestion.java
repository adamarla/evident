package com.gradians.evident.activity;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gradians.evident.R;
import com.gradians.evident.dom.Question;
import com.gradians.evident.gui.CardList;
import com.gradians.evident.gui.CardListRelated;
import com.gradians.evident.gui.ICard;

public class DoQuestion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_question);

        position = getIntent().getIntExtra("position", 0);
        question = getIntent().getParcelableExtra("question");
        ICard[] cards = question.getSteps();
        CardListRelated cardList = (CardListRelated)CardList.newInstance(cards, question.getCard());

        initiate(cardList);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("position", position);
        intent.putExtra("question", question);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    private Question question;
    private int position;

    private void initiate(CardList cl) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.activity_do_question, cl);
        fragmentTransaction.commit();
    }
}
