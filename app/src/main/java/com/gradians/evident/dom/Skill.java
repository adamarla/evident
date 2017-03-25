package com.gradians.evident.dom;

import android.content.Context;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by adamarla on 3/19/17.
 */

public class Skill extends Asset {

    public Skill(int id) {
        this.id = id;
    }

    @Override
    public void load(Context context) {
        InputStream is;
        File source = new File(context.getExternalFilesDir(null),
                "skills/" + id + "/source.xml");
        try {
            is = new FileInputStream(source);
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);
            extract(parser);
            is.close();
        } catch (Exception e) {
            Log.e("EvidentApp", e.getMessage());
        }
    }

    private void extract(XmlPullParser parser) throws Exception {
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
                            front = text;
                        } else {
                            back = text;
                        }
                    }
                }
                parser.next() ;
            }
        } catch (Exception e ) {
            Log.d("EvidentApp", e.getMessage());
        }
    }
}
