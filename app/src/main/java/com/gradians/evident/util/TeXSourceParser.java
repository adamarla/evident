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
            Pattern titlePattern = Pattern.compile("textcolor\\{blue\\}\\{(.*)\\}");
            StringBuilder title = new StringBuilder();
            while ((line = br.readLine()) != null) {
                Matcher matcher = titlePattern.matcher(line);
                if (matcher.find()) {
                    title.append(String.format("\\title{%s} \\\\\n", matcher.group(1)));
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

            Pattern imagePattern = Pattern.compile("includegraphics\\[.*\\]\\{(.*)\\}");
            boolean inCenterMode = false;
            StringBuilder studyNote = new StringBuilder();
            studyNote.append("%text\n");
            while ((line = br.readLine().trim()) != null) {
                if (line.startsWith("%text") || line.equals("%"))
                    continue;

                Matcher matcher = imagePattern.matcher(line);
                if (matcher.find()) {
                    line = line.replace(matcher.group(1), skill.getPath() + "/" + matcher.group(1));
                    line = line.replace("0.33", "1.0");
                    studyNote.append("\n").append(line);
                    continue;
                }

                if (line.trim().startsWith("\\begin")) {
                    if (line.startsWith("\\begin{itemize}")) {
                        inCenterMode = false;
                        continue;
                    } else if (line.trim().startsWith("\\begin{center}")) {
                        inCenterMode = true;
                        continue;
                    }

                    if (line.trim().startsWith("\\begin{align}")) {
                        studyNote.append("\n%\n"); // end text-mode
                    } else if (inCenterMode) {
                        studyNote.append("\n%\n"); // end text-mode
                        studyNote.append("\\begin{align}\n");
                    }
                    studyNote.append(line).append("\n");
                } else if (line.trim().startsWith("\\end")) {
                    if (line.trim().startsWith("\\end{itemize}"))
                        continue;

                    if (line.trim().startsWith("\\end{center}")) {
                        if (inCenterMode) {
                            line = line.replace("\\end{center}", "\\end{align}");
                            inCenterMode = false;
                        } else {
                            continue;
                        }
                    }

                    if (line.trim().startsWith("\\end{align}")) {
                        studyNote.append("\n").append(line).append("\n"); // end math-mode
                        studyNote.append("%text\n"); // resume text-mode
                    } else if (line.trim().startsWith("\\end{skill}")) {
                        studyNote.append("\n%\n");
                        break;
                    } else {
                        studyNote.append("\n").append(line);
                    }
                } else if (line.trim().startsWith("\\[")) {
                    studyNote.append("\n").append(line).append("\n");
                } else if (!line.trim().isEmpty()) {
                    studyNote.append("\n").append(line);
                }
            }
            skill.studyNote = toPureTeX(studyNote.toString());
        } catch (Exception e) {
            Log.d("EvidentApp", skill.studyNote);
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

    @Override
    protected String toPureTeX(String tex) {
        tex = tex.replaceAll("\\\\item\\{(.*)\\}", " - $1");
        tex = tex.replaceAll("\\\\smallmath", "");
        tex = tex.replaceAll("\\\\underline", "\\\\underline\\\\text");
        tex = tex.replaceAll("\\\\newline", "");
        tex = tex.replaceAll("\\\\toprule", "\\\\hline");
        tex = tex.replaceAll("midrule", "hline");
        tex = tex.replaceAll("\\\\bottomrule", "\\\\hline");
        return super.toPureTeX(tex);
    }

    BufferedReader br;
}
