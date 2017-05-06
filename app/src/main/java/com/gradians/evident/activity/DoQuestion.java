package com.gradians.evident.activity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gradians.evident.EvidentApp;
import com.gradians.evident.R;
import com.gradians.evident.dom.Chapter;
import com.gradians.evident.dom.Question;
import com.gradians.evident.gui.CardList;
import com.gradians.evident.gui.CardListRelated;
import com.gradians.evident.gui.ICard;

public class DoQuestion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_question);

        int chapterId = getIntent().getIntExtra("chapterId", 0);
        int position = getIntent().getIntExtra("position", 0);

        Chapter chapter = EvidentApp.app.chapterList.getChapter(chapterId);
        Question question = chapter.questions.get(position);

        ICard[] cards = question.getSteps();
        CardListRelated cardList = (CardListRelated)CardList.newInstance(question.getCard(), cards, 0);

        initiate(cardList);
    }

    private void initiate(CardList cl) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.activity_do_question, cl);
        fragmentTransaction.commit();
    }
}
