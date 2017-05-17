package com.gradians.evident.gui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gradians.evident.dom.Chapter;
import com.gradians.evident.dom.Question;
import com.gradians.evident.dom.Skill;
import com.gradians.evident.dom.Snippet;

/**
 * Created by adamarla on 3/26/17.
 */

public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(Chapter chapter, FragmentManager fm) {
        super(fm);
        this.chapter = chapter;
    }

    @Override
    public Fragment getItem(int position) {
        ICard[] cards;
        int i = 0;
        switch (position) {
            case 0:
                cards = new ICard[chapter.skills.size()];
                for (Skill s: chapter.skills) {
                    cards[i] = s; i++;
                }
                break;
            case 1:
                cards = new ICard[chapter.snippets.size()];
                for (Snippet s: chapter.snippets) {
                    cards[i] = s.getCard(); i++;
                }
                break;
            default:
                cards = new ICard[chapter.questions.size()];
                for (Question q: chapter.questions) {
                    cards[i] = q.getCard(); i++;
                }
        }
        return CardList.newInstance(cards, null);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return position == 0 ? "Review" : (position == 1 ? "Practise" : "Solve");
    }

    Chapter chapter;
}
