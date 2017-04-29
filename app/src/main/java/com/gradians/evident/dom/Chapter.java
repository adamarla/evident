package com.gradians.evident.dom;

import android.content.Context;
import android.support.v4.content.res.TypedArrayUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by adamarla on 3/19/17.
 */

public class Chapter implements Comparable<Chapter> {

    public Chapter(int id, String name) {
        this.id = id;
        this.name = name;
        skills = new ArrayList<>();
        snippets = new ArrayList<>();
        questions = new ArrayList<>();
    }

    public void load(Context context) {
        ArrayList[] assets = { skills, snippets, questions};
        for (ArrayList list : assets) {
            Stack<Asset> blanks = new Stack<>();
            for (Object asset: list)
                if (!((Asset)asset).load(context))
                    blanks.push((Asset)asset);
            // don't want nothing to do with assets that
            // could not get loaded for whatever reason
            while (!blanks.empty())
                list.remove(blanks.pop());
        }
    }

    public Asset[] getAllAssets() {
        ArrayList<Asset> assets = new ArrayList<>();
        assets.addAll(skills);
        assets.addAll(snippets);
        assets.addAll(questions);
        return assets.toArray(new Asset[assets.size()]);
    }

    public void addSkill(Skill skill) {
        skills.add(skill);
    }
    public void addSnippet(Snippet snippet) {
        snippets.add(snippet);
    }
    public void addQuestion(Question question) {
        questions.add(question);
    }

    public ArrayList<Skill> skills;
    public ArrayList<Snippet> snippets;
    public ArrayList<Question> questions;

    public int id;
    public String name;

    @Override
    public String toString() {
        return name + ": " + skills.size() + " skills; " + snippets.size() + " snippets; "
                + questions.size() + " questions";
    }

    @Override
    public int compareTo(Chapter chapter) {
        return name.compareTo(chapter.name);
    }
}
