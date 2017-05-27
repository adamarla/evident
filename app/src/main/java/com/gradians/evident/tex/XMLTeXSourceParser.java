package com.gradians.evident.tex;

import android.util.Log;
import android.util.Xml;

import com.gradians.evident.dom.Question;
import com.gradians.evident.dom.Step;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by adamarla on 4/29/17.
 */

public class XMLTeXSourceParser extends TeXSourceParser {

    public XMLTeXSourceParser(File texSource, File xmlSource) {
        super(texSource);
        try {
            fis = new FileInputStream(xmlSource);
            parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(fis, null);
        } catch (Exception e) {
            Log.e("EvidentApp", "TeXSourceParser Error");
        }
    }

    @Override
    public void populate(Question question) {
        cards = new HashMap<>();
        try {
            String newCommands = extractNewCommands();

            int counter = 2;
            while (br.readLine() != null) {
                String tex;
                if (question.statement == null) {
                    tex = newCommands + extractTeX("\\newcard");
                    question.statement = new Step(tex, null, null);
                } else {
                    tex = newCommands + extractTeX("\\newcard");
                    cards.put(String.format("tex-%s.svg", counter), tex);
                    counter++;
                }
            }
            question.steps = parseXmlSource();
        } catch (Exception e) {
            Log.e("EvidentApp", "Error populating Question " + e.getMessage());
        }
        closeStreams();
    }

    private HashMap<String, String> cards;

    private Step[] parseXmlSource() throws Exception {
        boolean inStatement = true, inStep = false, outStep = false;
        ArrayList<Step> _steps = new ArrayList<>();
        String correct = null, incorrect = null, reason;
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
                if (inStatement) {
                    inStatement = false;
                } else if (inStep) {
                    if (isCorrect == null || isCorrect.equals("true")) {
                        correct = toPureTeX(cards.get(text));
                    } else {
                        incorrect = toPureTeX(cards.get(text));
                    }
                } else if (outStep) {
                    reason = toPureTeX(cards.get(text));
                    _steps.add(new Step(correct, incorrect, reason));
                    outStep = false;
                }
            }
            parser.next();
        }
        return _steps.toArray(new Step[_steps.size()]);
    }

    @Override
    protected void closeStreams() {
        super.closeStreams();
        try {
            fis.close();
        } catch (IOException e) {
            Log.e("EvidentApp", "Error closing stream " + e.getMessage());
        }
    }

    private FileInputStream fis;
    private XmlPullParser parser;

}
