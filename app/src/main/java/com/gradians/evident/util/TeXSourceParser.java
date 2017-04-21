package com.gradians.evident.util;

import android.util.Log;

import com.gradians.evident.dom.Question;
import com.gradians.evident.dom.Skill;
import com.gradians.evident.dom.Snippet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by adamarla on 4/20/17.
 */

public class TeXSourceParser extends SourceParser {

    public TeXSourceParser(InputStream is) {
        super(is);
        br = new BufferedReader(new InputStreamReader(is));
    }

    @Override
    public void populateSkill(Skill skill) {
        boolean inTitle = false, inStudyNote = false;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = br.readLine()) != null) {
            }
        } catch (IOException e) {
            Log.d("EvidentApp", "Error populating Skill" + e.getMessage());
        }

    }

    @Override
    public void populateSnippet(Snippet snippet) {

    }

    @Override
    public void populateQuestion(Question question) {

    }

    BufferedReader br;
}
