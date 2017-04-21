package com.gradians.evident.util;

import com.gradians.evident.dom.Question;
import com.gradians.evident.dom.Skill;
import com.gradians.evident.dom.Snippet;

import java.io.InputStream;

/**
 * Created by adamarla on 4/20/17.
 */

public abstract class SourceParser {

    public SourceParser(InputStream is) {
        this.inputStream = is;
    }

    public abstract void populateSkill(Skill skill);
    public abstract void populateSnippet(Snippet snippet);
    public abstract void populateQuestion(Question question);

    InputStream inputStream;
}
