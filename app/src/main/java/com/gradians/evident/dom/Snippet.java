package com.gradians.evident.dom;


import android.os.Parcel;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;

/**
 * Created by adamarla on 3/19/17.
 */

public class Snippet extends Asset {

    public Snippet(int id, String path) {
        super(id, path);
    }

    @Override
    public String getFront() {
        return step.getFront();
    }

    @Override
    public String getBack() {
        return step.getBack();
    }

    @Override
    public boolean isAnswerable() {
        return true;
    }

    @Override
    public boolean hasBeenAttempted() {
        return step.hasBeenAttempted();
    }

    @Override
    public void setAttempt(boolean isTrue) {
        step.setAttempt(isTrue);
    }

    @Override
    public boolean getAttempt() {
        return step.getAttempt();
    }

    @Override
    public boolean isCorrect() {
        return step.isCorrect();
    }

    @Override
    protected void extract(XmlPullParser parser) throws Exception {
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
                            step = new Step(correct, incorrect, reason);
                            break;
                        }
                    }
                }
                parser.next() ;
            }
        } catch (Exception e ) {
            Log.d("EvidentApp", e.getMessage());
        }
    }

    Step step;

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeParcelable(step, 0);
    }

    public static Creator<Snippet> CREATOR = new Creator<Snippet>() {
        @Override
        public Snippet createFromParcel(Parcel parcel) {
            return new Snippet(parcel);
        }

        @Override
        public Snippet[] newArray(int i) {
            return new Snippet[i];
        }
    };

    protected Snippet(Parcel in) {
        super(in);
        step = in.readParcelable(this.getClass().getClassLoader());
    }

}
