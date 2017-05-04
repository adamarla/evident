package com.gradians.evident.tex;

import android.util.Log;

import com.gradians.evident.dom.Question;
import com.gradians.evident.dom.Skill;
import com.gradians.evident.dom.Snippet;
import com.gradians.evident.dom.Step;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by adamarla on 4/20/17.
 */

public class TeXSourceParser extends SourceParser {

    public TeXSourceParser(File source) {
        try {
            FileInputStream fis = new FileInputStream(source);
            br = new BufferedReader(new InputStreamReader(fis));
        } catch (FileNotFoundException e) {
            Log.e("EvidentApp", "TeXSourceParser Error");
        }
    }

    @Override
    public void populate(Skill skill) {
        Log.d("EvidentApp", "Populating Skill " + skill.getId() + " from " + skill.getPath());
        this.path = skill.getPath();
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

            jumpTo("\\reason");
            skill.studyNote = extractTeX("\\end{skill}");
        } catch (Exception e) {
            Log.d("EvidentApp", skill.studyNote);
            Log.e("EvidentApp", "Error populating Skill " + e.getMessage());
        }
        closeStreams();
    }

    @Override
    public void populate(Snippet snippet) {
        Log.d("EvidentApp", "Populating Snippet " + snippet.getId() + " from " + snippet.getPath());
        this.path = snippet.getPath();
        String correct = null, incorrect = null, reason, line;
        boolean isCorrect = false;
        try {
            String newCommands = extractNewCommands();

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.equals("\\incorrect") || line.equals("\\correct")) {
                    isCorrect = line.equals("\\correct");
                    break;
                }
            }

            String context = extractTeX("\\reason");
            if (isCorrect) correct = newCommands + context;
            else incorrect = newCommands + context;

            reason = newCommands + extractTeX("\\end{snippet}");
            snippet.step = new Step(correct, incorrect, reason);
        } catch (Exception e) {
            Log.d("EvidentApp", snippet.step.toString());
            Log.e("EvidentApp", "Error populating Snippet " + e.getMessage());
        }
        closeStreams();
    }

    @Override
    public void populate(Question question) {
        Log.d("EvidentApp", "Populating Question " + question.getId() + " from " + question.getPath());
        this.path = question.getPath();
        try {
            String newCommands = extractNewCommands();

            jumpTo("\\statement");
            String statementTex = newCommands + extractTeX("\\begin{step}");
            Step statement = new Step(statementTex, null, null);
            statement.steps = true;
            statement.answerable = false;
            question.statement = statement;

            ArrayList<Step> _steps = new ArrayList<>();
            while (br.readLine() != null) {
                if (jumpTo("\\begin{options}") == null) break;
                // get both correct and incorrect options, if any
                String tex = extractTeX("\\end{options}");

                // separate out correct and incorrect options... if any
                StringBuilder option = new StringBuilder();
                boolean correctOption = false;
                String correct = null, incorrect = null;
                for (String s: tex.split("\n")) {
                    if (s.trim().contains("\\correct")) {
                        correctOption = true;
                    } else if (s.trim().contains("\\incorrect")) {
                        if (option.length() > 0) {
                            correct = option.toString();
                            option = new StringBuilder();
                        }
                        correctOption = false;
                    } else {
                        option.append(s);
                    }
                }
                if (option.length() > 0) {
                    if (correctOption)
                        correct = newCommands + option;
                    else
                        incorrect = newCommands + option;
                }

                jumpTo("\\reason");
                String reason = newCommands + extractTeX("\\end{step}");
                _steps.add(new Step(correct, incorrect, reason));
            }
            question.steps = _steps.toArray(new Step[_steps.size()]);
        } catch (Exception e) {
            Log.e("EvidentApp", "Error populating Question " + e.getMessage());
        }
        closeStreams();
    }

    @Override
    protected void closeStreams() {
        try {
            br.close();
        } catch (IOException e) {
            Log.e("EvidentApp", "Error closing stream " + e.getMessage());
        }
    }

    @Override
    protected String toPureTeX(String tex) {
        tex = tex.replaceAll("\\\\item *\\{(.*)\\}", " - $1");
        tex = tex.replaceAll("\\\\textbf\\{(.*)\\}", "\\\\textbf\\{$1 \\}");
        tex = tex.replaceAll("\\\\smallmath", "");
        tex = tex.replaceAll("\\\\underline", "\\\\underline\\\\text");
        tex = tex.replaceAll("\\\\newline", "\n");
        tex = tex.replaceAll("\\\\toprule", "\\\\hline");
        tex = tex.replaceAll("midrule", "hline");
        tex = tex.replaceAll("\\\\bottomrule", "\\\\hline");
        return super.toPureTeX(tex);
    }

    private String path;
    protected BufferedReader br;

    protected String jumpTo(String locator) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            if (line.trim().startsWith(locator))
                break;
        }
        return line;
    }

    protected String extractNewCommands() throws IOException {
        StringBuilder newcommands = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("\\newcommand")) {
                newcommands.append(line).append("\n");
            } else if (line.equals("\\begin{document}")) {
                break;
            }
        }
        return newcommands.toString();
    }

    protected String extractTeX(String exitCommand) throws IOException {
        StringBuilder tex = new StringBuilder();
        String line;
        Pattern imagePattern = Pattern.compile("includegraphics\\[scale=(.*)\\]\\{(.*)\\}");
        boolean autoLineBreak = true;
        tex.append("%text\n"); // start text-mode
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("%text") || line.equals("%"))
                continue;
            else if (line.startsWith(exitCommand)) {
                tex.append("\n%\n"); // end text-mode
                break;
            }

            Matcher matcher = imagePattern.matcher(line);
            if (matcher.find()) {
                line = line.replace(matcher.group(1),
                        String.valueOf(Float.parseFloat(matcher.group(1))*3));
                line = line.replace(matcher.group(2), path + "/" + matcher.group(2));
            }

            if (line.contains("\\begin")) {
                if (line.startsWith("\\begin{itemize}")) {
                    autoLineBreak = false;
                    continue;
                } else if (line.startsWith("\\begin{center}")) {
                    line = line.replace("\\begin{center}", "\\begin{align}");
                }

                if (line.startsWith("\\begin{align}") ||
                    line.contains("\\begin{cases}")) {
                    tex.append("\n%\n"); // end text-mode
                    autoLineBreak = false;
                }
                tex.append(line).append("\n");
            } else if (line.contains("\\end")) {
                if (line.startsWith("\\end{itemize}")) {
                    autoLineBreak = true;
                    continue;
                }

                if (line.startsWith("\\end{center}")) {
                    line = line.replace("\\end{center}", "\\end{align}");
                }

                if (line.startsWith("\\end{align}") ||
                    line.contains("\\end{cases}")) {
                    tex.append("\n").append(line).append("\n"); // end math-mode
                    tex.append("%text\n"); // resume text-mode
                    autoLineBreak = true;
                } else if (line.startsWith("\\end{document}")) {
                    tex.append("\n%\n"); // end text-mode
                    break;
                } else {
                    tex.append("\n").append(line);
                }
            } else if (line.startsWith("\\[")) {
                tex.append("\n").append(line).append("\n");
            } else if (!line.isEmpty()) {
                tex.append(autoLineBreak ? " " : "\n").append(line);
            }
        }
        return toPureTeX(tex.toString());
    }

}
