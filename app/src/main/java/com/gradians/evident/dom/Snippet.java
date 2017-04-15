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
        return statement;
    }

    @Override
    public String getBack() {
        return reason;
    }

    @Override
    public boolean isARiddle() {
        return true;
    }

    @Override
    public boolean hasBeenAttempted() {
        return attempted;
    }

    @Override
    public void setAttempt(boolean iSayCorrect) {
        attempt = iSayCorrect;
        attempted = true;
    }

    @Override
    public boolean getAttempt() {
        return attempt;
    }

    @Override
    public boolean isCorrect() {
        return faceShownIsCorrect;
    }

    @Override
    protected void extract(XmlPullParser parser) throws Exception {
        boolean onFrontFace = true;
        try {
            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                int type = parser.getEventType();
                if (type == XmlPullParser.START_TAG) {
                    String node = parser.getName();
                    if (node.equals("reason")) {
                        onFrontFace = false;
                    } else if (node.equals("tex") || node.equals("image")) {
                        String isCorrect = parser.getAttributeValue(null, "correct");
                        parser.next();
                        String text = parser.getText();
                        if (onFrontFace) {
                            statement = toPureTeX(text);
                            faceShownIsCorrect = isCorrect == null || isCorrect.equals("true");
                        } else {
                            reason = toPureTeX(text);
                        }
                    }
                }
                parser.next() ;
            }
        } catch (Exception e ) {
            Log.d("EvidentApp", e.getMessage());
        }
    }

    String statement, reason;
    boolean faceShownIsCorrect, attempted, attempt;

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(statement);
        parcel.writeString(reason);
        parcel.writeInt(faceShownIsCorrect ? 1 : 0);
        parcel.writeInt(attempted ? 1 : 0);
        parcel.writeInt(attempt ? 1 : 0);
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
        statement = in.readString();
        reason = in.readString();
        faceShownIsCorrect = in.readInt() == 1;
        attempted = in.readInt() == 1;
        attempt = in.readInt() == 1;
    }

}
