package com.gradians.evident.dom;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;

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
