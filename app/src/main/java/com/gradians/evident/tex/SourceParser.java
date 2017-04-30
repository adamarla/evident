package com.gradians.evident.tex;

import com.gradians.evident.dom.Question;
import com.gradians.evident.dom.Skill;
import com.gradians.evident.dom.Snippet;

import java.io.InputStream;

/**
 * Created by adamarla on 4/20/17.
 */

public abstract class SourceParser {

    public abstract void populate(Skill skill);
    public abstract void populate(Snippet snippet);
    public abstract void populate(Question question);
    protected abstract void closeStreams();

    protected String toPureTeX(String tex) {
        String[] lines = tex.split("\n");
        StringBuilder sb = new StringBuilder();

        boolean textMode = false;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.startsWith("%text")) {
                textMode = true;
                continue;
            } else if (line.startsWith("%")) {
                textMode = false;
                continue;
            }

            if (textMode)
                sb.append(String.format("\\text{%s} \\\\\n", line));
            else
                sb.append(String.format("%s\n", line));
        }
        return sb.toString();
    }
}
