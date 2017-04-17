package com.gradians.evident.dom;

import android.os.Parcel;
import android.util.Log;

import com.gradians.evident.gui.ICard;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;

/**
 * Created by adamarla on 3/19/17.
 */

public class Question extends Asset {

    public Question(int id, String path) {
        super(id, "bank/vault/" + path);
    }

    @Override
    public ICard getCard() {
        return statement;
    }

    public Step[] getSteps() {
        return steps;
    }

    @Override
    protected void extract(XmlPullParser parser) throws Exception {
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
                    if (statement == null) {
                        statement = new Step(toPureTeX(text), null, null);
                        statement.steps = true;
                        statement.answerable = false;
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
            steps = _steps.toArray(new Step[_steps.size()]);
        } catch (Exception e ) {
            Log.d("EvidentApp", e.getMessage());
        }
    }

    Step statement;
    Step[] steps;

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeParcelable(statement, 0);
        parcel.writeParcelableArray(steps, 0);
    }

    public static Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel parcel) {
            return new Question(parcel);
        }

        @Override
        public Question[] newArray(int i) {
            return new Question[i];
        }
    };

    private Question(Parcel in) {
        super(in);
        ClassLoader cl = this.getClass().getClassLoader();
        statement = in.readParcelable(cl);
        steps = (Step[])in.readParcelableArray(cl);
    }

}

