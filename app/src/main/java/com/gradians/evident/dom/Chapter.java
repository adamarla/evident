package com.gradians.evident.dom;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by adamarla on 3/19/17.
 */

public class Chapter implements Comparable<Chapter> {

    public Chapter(int id) {
        this.id = id;
        skills = new ArrayList<Skill>();
        snippets = new ArrayList<Snippet>();
        questions = new ArrayList<Question>();
        name = "Chapter #" + id;
    }

    public void addSkill(int id) {
        skills.add(new Skill(id));
    }
    public void addSnippet(int id) {
        snippets.add(new Snippet(id));
    }
    public void addProblem(int id) {
        questions.add(new Question(id));
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
