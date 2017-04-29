package com.gradians.evident.tex;

import android.util.Log;

import com.gradians.evident.dom.Question;
import com.gradians.evident.dom.Skill;
import com.gradians.evident.dom.Snippet;
import com.gradians.evident.dom.Step;

import java.io.BufferedReader;
import java.io.IOException;
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
        Log.d("EvidentApp", "Populating Skill " + skill.getId() + " from " + skill.getPath());
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

            skill.studyNote = extractTeX(skill.getPath(), "\\end{skill}");
        } catch (Exception e) {
            Log.d("EvidentApp", skill.studyNote);
            Log.e("EvidentApp", "Error populating Skill " + e.getMessage());
        }
    }

    @Override
    public void populateSnippet(Snippet snippet) {
        Log.d("EvidentApp", "Populating Snippet " + snippet.getId() + " from " + snippet.getPath());
        String correct = null, incorrect = null, reason, line;
        boolean isCorrect = false;
        try {
            StringBuilder newcommands = new StringBuilder();
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("\\newcommand")) {
                    newcommands.append(line).append("\n");
                } else if (line.equals("\\incorrect") || line.equals("\\correct")) {
                    isCorrect = line.equals("\\correct");
                    break;
                }
            }
            Log.d("EvidentApp", newcommands.toString());

            String context = extractTeX(snippet.getPath(), "\\reason");
            if (isCorrect) correct = newcommands.append(context).toString();
            else incorrect = newcommands.append(context).toString();

            reason = newcommands.append(extractTeX(snippet.getPath(), "\\end{snippet}")).toString();
            snippet.step = new Step(correct, incorrect, reason);
        } catch (Exception e) {
            Log.d("EvidentApp", snippet.step.toString());
            Log.e("EvidentApp", "Error populating Snippet " + e.getMessage());
        }
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

    private String extractTeX(String path, String exitCommand) throws IOException {
        StringBuilder tex = new StringBuilder();
        String line;
        Pattern imagePattern = Pattern.compile("includegraphics\\[scale=(.*)\\]\\{(.*)\\}");
        boolean switchToMathMode = false;
        tex.append("%text\n");
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("%text") || line.equals("%"))
                continue;
            else if (line.startsWith(exitCommand)) {
                tex.append("\n%\n");
                break;
            }

            Matcher matcher = imagePattern.matcher(line);
            if (matcher.find()) {
                tex.append("\n%\n"); // end text-mode
                if (switchToMathMode)
                    tex.append("\\begin{align}\n");
                line = line.replace(matcher.group(1), String.valueOf(Float.parseFloat(matcher.group(1))*3));
                line = line.replace(matcher.group(2), path + "/" + matcher.group(2));
                tex.append(line);
                if (switchToMathMode) {
                    tex.append("\n\\end{align}\n");
                    switchToMathMode = false;
                } else
                    tex.append("\\\\\n");
                continue;
            }

            if (line.startsWith("\\begin")) {
                if (line.startsWith("\\begin{itemize}")) {
                    switchToMathMode = false;
                    continue;
                } else if (line.startsWith("\\begin{center}")) {
                    switchToMathMode = true;
                    continue;
                }

                if (line.startsWith("\\begin{align}")) {
                    tex.append("\n%\n"); // end text-mode
                } else if (switchToMathMode) {
                    tex.append("\n%\n"); // end text-mode
                    tex.append("\\begin{align}\n");
                }
                tex.append(line).append("\n");
            } else if (line.startsWith("\\end")) {
                if (line.startsWith("\\end{itemize}"))
                    continue;

                if (line.startsWith("\\end{center}")) {
                    if (switchToMathMode) {
                        line = line.replace("\\end{center}", "\\end{align}");
                        switchToMathMode = false;
                    } else {
                        continue;
                    }
                }

                if (line.startsWith("\\end{align}")) {
                    tex.append("\n").append(line).append("\n"); // end math-mode
                    tex.append("%text\n"); // resume text-mode
                } else {
                    tex.append("\n").append(line);
                }
            } else if (line.startsWith("\\[")) {
                tex.append("\n").append(line).append("\n");
            } else if (!line.isEmpty()) {
                tex.append("\n").append(line);
            }
        }
        return toPureTeX(tex.toString());
    }

    private BufferedReader br;
}
