package com.gradians.evident.tex;

import android.util.Log;

import com.gradians.evident.dom.Question;
import com.gradians.evident.dom.Skill;
import com.gradians.evident.dom.SkillMap;
import com.gradians.evident.dom.Snippet;
import com.gradians.evident.dom.Step;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

    public void setSkillMap(SkillMap skillMap) {
        this.skillMap = skillMap;
    }

    @Override
    public void populate(Skill skill) {
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
        this.path = snippet.getPath();
        String correct = null, incorrect = null, reason, line;
        boolean isCorrect = false;
        try {
            String newCommands = extractNewCommands("\\begin{document}");

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
            int[] id = skillMap.getSkillId(snippet.getPath());
            if (id != null)
                snippet.step.skillId = id[0];
        } catch (Exception e) {
            Log.d("EvidentApp", snippet.step.toString());
            Log.e("EvidentApp", "Error populating Snippet " + e.getMessage());
        }
        closeStreams();
    }

    @Override
    public void populate(Question question) {
        this.path = question.getPath();
        try {
            String newCommands = extractNewCommands("\\begin{document}");

            jumpTo("\\statement");
            String statementTex = newCommands + extractTeX("\\begin{step}");
            question.statement = new Step(statementTex, null, null);

            ArrayList<Step> _steps = new ArrayList<>();
            while (true) {
                if (jumpTo("\\begin{options}") == null) break;
                // get both correct and incorrect options, if any
                String tex = extractTeX("\\end{options}");

                // separate out correct and incorrect options... if any
                StringBuilder option = new StringBuilder();
                boolean correctOptionOnly = false;
                String correct = null, incorrect = null;
                for (String s: tex.split("\n")) {
                    if (s.trim().contains("\\correct")) {
                        correctOptionOnly = true;
                    } else if (s.trim().contains("\\incorrect")) {
                        if (correctOptionOnly) {
                            correct = newCommands + option.toString();
                            option = new StringBuilder();
                        }
                        correctOptionOnly = false;
                    } else {
                        option.append(s).append('\n');
                    }
                }
                if (option.length() > 0) {
                    if (correctOptionOnly)
                        correct = newCommands + option.toString();
                    else
                        incorrect = newCommands + option.toString();
                }

                jumpTo("\\reason");
                String reason = newCommands + extractTeX("\\end{step}");
                _steps.add(new Step(correct, incorrect, reason));
            }
            question.steps = _steps.toArray(new Step[_steps.size()]);
            int[] ids = skillMap.getSkillId(question.getPath());
            if (ids != null)
                for (int i = 0; i < question.steps.length; i++)
                    question.steps[i].skillId = ids[i];
        } catch (Exception e) {
            Log.e("EvidentApp", "Error populating Question " + question.getPath() + "\n" + e.getMessage());
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
        tex = tex.replaceAll("\\\\midrule", "\\\\hline");
        tex = tex.replaceAll("\\\\bottomrule", "\\\\hline");
        return super.toPureTeX(tex);
    }

    private SkillMap skillMap;
    protected String path;
    BufferedReader br;

    private String jumpTo(String locator) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            if (line.trim().startsWith(locator))
                break;
        }
        return line;
    }

    String extractNewCommands(String exitCondition) throws IOException {
        StringBuilder newcommands = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("\\newcommand")) {
                newcommands.append(line).append("\n");
            } else if (line.startsWith(exitCondition))
                break;
        }
        return newcommands.toString();
    }

    String extractTeX(String exitCommand) throws IOException {
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
            } else if (line.startsWith("\\correct") || line.startsWith("\\incorrect")) {
                autoLineBreak = false;
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
