package com.gradians.evident.tex;

import android.util.Log;
import android.util.Xml;

import com.gradians.evident.dom.Question;
import com.gradians.evident.dom.Skill;
import com.gradians.evident.dom.Snippet;
import com.gradians.evident.dom.Step;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

/**
 * Created by adamarla on 4/20/17.
 */

public class XMLSourceParser extends SourceParser {

    public XMLSourceParser(File source) {
        parser = Xml.newPullParser();
        try {
            fis = new FileInputStream(source);
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(fis, null);
        } catch (Exception e) {
            Log.e("EvidentApp", "XMLSourceParser Error");
        }
    }

    @Override
    public void populate(Skill skill) {
        boolean onFrontFace = true;
        try {
            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                int type = parser.getEventType();
                if (type == XmlPullParser.START_TAG) {
                    String node = parser.getName();
                    if (node.equals("reason")) {
                        onFrontFace = false;
                    } else if (node.equals("tex") || node.equals("image")) {
                        parser.next();
                        String text = parser.getText();
                        if (onFrontFace) {
                            skill.title = toPureTeX(text);
                        } else {
                            skill.studyNote = toPureTeX(text);
                        }
                    }
                }
                parser.next() ;
            }
        } catch (Exception e ) {
            Log.d("EvidentApp", "Error populating Skill " + e.getMessage());
        }
        closeStreams();
    }

    @Override
    public void populate(Snippet snippet) {
        String correct = null, incorrect = null, reason;
        try {
            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                int type = parser.getEventType();
                if (type == XmlPullParser.START_TAG) {
                    String node = parser.getName();
                    if (node.equals("tex") || node.equals("image")) {
                        String isCorrect = parser.getAttributeValue(null, "correct");
                        parser.next();
                        String text = parser.getText();
                        if (correct == null && incorrect == null) {
                            if (isCorrect == null || isCorrect.equals("true"))
                                correct = toPureTeX(text);
                            else
                                incorrect = toPureTeX(text);
                        } else {
                            reason = toPureTeX(text);
                            snippet.step = new Step(correct, incorrect, reason);
                            break;
                        }
                    }
                }
                parser.next() ;
            }
        } catch (Exception e ) {
            Log.d("EvidentApp", "Error populating Snippet " + e.getMessage());
        }
        closeStreams();
    }

    @Override
    public void populate(Question question) {
        boolean inStep = false, outStep = false;
        ArrayList<Step> _steps = new ArrayList<>();
        String correct = null, incorrect = null, reason;
        try {
            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                int type = parser.getEventType();

                String name = parser.getName();
                if (type != XmlPullParser.START_TAG || name == null) {
                    parser.next() ;
                    continue ;
                }

                if (name.equals("step")) {
                    inStep = true;
                    correct = null; incorrect = null;
                } else if (name.equals("reason")) {
                    inStep = false;
                    outStep = true;
                } else if (name.equals("tex") || name.equals("image")) {
                    String isCorrect = parser.getAttributeValue(null, "correct");
                    parser.next();
                    String text = parser.getText();
                    if (question.statement == null) {
                        question.statement = new Step(toPureTeX(text), null, null);
                    } else if (inStep) {
                        if (isCorrect == null || isCorrect.equals("true")) {
                            correct = toPureTeX(text);
                        } else {
                            incorrect = toPureTeX(text);
                        }
                    } else if (outStep) {
                        reason = toPureTeX(text);
                        _steps.add(new Step(correct, incorrect, reason));
                        outStep = false;
                    }
                }
                parser.next();
            }
            question.steps = _steps.toArray(new Step[_steps.size()]);
        } catch (Exception e ) {
            Log.d("EvidentApp", "Error populating Question " + e.getMessage());
        }
        closeStreams();
    }

    @Override
    protected void closeStreams() {
        try {
            fis.close();
        } catch (Exception e) {
            Log.d("EvidentApp", "Error closing stream " + e.getMessage());
        }
    }

    private FileInputStream fis;
    private XmlPullParser parser;
}
