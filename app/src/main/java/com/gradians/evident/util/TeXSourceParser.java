package com.gradians.evident.util;

import android.util.Log;

import com.gradians.evident.dom.Question;
import com.gradians.evident.dom.Skill;
import com.gradians.evident.dom.Snippet;
import com.gradians.evident.dom.Step;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        Log.d("EvidentApp", "Populating Skill " + skill.getId());
        String line;
        try {
            Pattern titlePattern = Pattern.compile("(textcolor\\{blue\\}\\{)(.*)(\\})");
            StringBuilder title = new StringBuilder();
            while ((line = br.readLine()) != null) {
                Matcher matcher = titlePattern.matcher(line);
                if (matcher.find()) {
                    title.append(String.format("\\title{%s} \\\\\n", matcher.group(2)));
                    title.append("%text\n");
                    break;
                }
            }

            while (!(line = br.readLine().trim()).startsWith("\\end{narrow}")) {
                if (!line.isEmpty()) {
                    line = line.replace("\\underline", "\\underline\\text");
                    title.append(line);
                }
            }
            title.append("%\n");
            skill.title = toPureTeX(title.toString());

            while (!br.readLine().trim().startsWith("\\reason")) {}

            StringBuilder studyNote = new StringBuilder();
            studyNote.append("%text\n");
            while ((line = br.readLine()) != null) {
                if (line.startsWith("%text") || line.equals("%"))
                    continue;

                if (line.trim().startsWith("\\begin")) {
                    studyNote.append("\n%\n"); // end text-mode
                    studyNote.append(line).append("\n");
                } else if (line.trim().startsWith("\\end") && !line.trim().startsWith("\\end{skill}")) {
                    studyNote.append(line).append("\n"); // end math-mode
                    studyNote.append("%text\n"); // resume text-mode
                } else if (line.trim().startsWith("\\end{skill}")) {
                    studyNote.append("\n%");
                    break;
                } else if (line.trim().startsWith("\\[")) {
                    studyNote.append("\n").append(line).append("\n");
                } else if (!line.trim().isEmpty()) {
                    line = line.replace("\\newline", "");
                    line = line.replace("\\underline", "\\underline\\text");
                    studyNote.append("\n").append(line);
                }
            }
            skill.studyNote = toPureTeX(studyNote.toString());
        } catch (Exception e) {
            Log.e("EvidentApp", "Error populating Skill " + e.getMessage());
        }
    }

    @Override
    public void populateSnippet(Snippet snippet) {
        snippet.step = new Step("\\text{Correct}", null, "\\text{Reason}");
    }

    @Override
    public void populateQuestion(Question question) {
        question.statement = new Step("\\text{Statement is thus}", null, null);
        question.steps = new Step[1];
        question.steps[0] = new Step("\\text{Correct}", null, "\text{Reason}");
    }

    BufferedReader br;
}
