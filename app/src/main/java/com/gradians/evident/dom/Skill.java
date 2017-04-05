package com.gradians.evident.dom;

import android.content.Context;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.os.Parcel;
import android.util.Log;
import android.util.Xml;

import com.gradians.evident.gui.ICard;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by adamarla on 3/19/17.
 */

public class Skill extends Asset {

    public Skill(int id, String path) {
        super(id, path);
    }

    @Override
    public String getFront() {
        return title;
    }

    @Override
    public String getBack() {
        return studyNote;
    }

    @Override
    public boolean isARiddle() {
        return false;
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
                        parser.next();
                        String text = parser.getText();
                        if (onFrontFace) {
                            title = toPureTeX(text);
                        } else {
                            studyNote = toPureTeX(text);
                        }
                    }
                }
                parser.next() ;
            }
        } catch (Exception e ) {
            Log.d("EvidentApp", e.getMessage());
        }
    }

    private String title, studyNote;

    public static Creator<Skill> CREATOR = new Creator<Skill>() {
        @Override
        public Skill createFromParcel(Parcel parcel) {
            return new Skill(parcel);
        }

        @Override
        public Skill[] newArray(int i) {
            return new Skill[i];
        }
    };

    private Skill(Parcel in) {
        super();
        id = in.readInt();
        path = in.readString();
    }
}
