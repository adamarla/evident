package com.gradians.evident.gui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gradians.evident.EvidentApp;
import com.gradians.evident.dom.Chapter;
import com.gradians.evident.dom.Question;

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
        switch (position) {
            case 0:
                cards = chapter.skills.toArray(new ICard[chapter.skills.size()]);
                break;
            case 1:
                cards = chapter.snippets.toArray(new ICard[chapter.snippets.size()]);
                break;
            default:
                cards = chapter.questions.toArray(new ICard[chapter.questions.size()]);
        }
        return CardList.newInstance(cards);
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
