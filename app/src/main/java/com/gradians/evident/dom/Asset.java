package com.gradians.evident.dom;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.Xml;

import com.gradians.evident.gui.ICard;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by adamarla on 3/19/17.
 */

public abstract class Asset implements ICard, Parcelable {

    public Asset(int id, String path) {
        this.id = id;
        this.path = path;
        loaded = false;
    }

    @Override
    public boolean hasBeenAttempted() {
        return true;
    }

    @Override
    public void setAttempt(boolean isTrue) {
        // default implementation
    }

    @Override
    public boolean getAttempt() {
        // default implementation
        return true;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean isAnswerable() {
        return false;
    }

    @Override
    public boolean isCorrect() {
        return true;
    }

    @Override
    public boolean hasSteps() {
        return false;
    }

    public void load(Context context) {
        if (loaded) return;
        InputStream is;
        File source = new File(context.getExternalFilesDir(null), path + "/source.xml");
        try {
            is = new FileInputStream(source);
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);
            extract(parser);
            is.close();
            loaded = true;
        } catch (Exception e) {
            Log.e("EvidentApp", e.getMessage());
        }
    }

    protected abstract void extract(XmlPullParser parser) throws Exception;

    protected int id;
    protected String path;
    protected boolean loaded;

    protected Asset(Parcel in) {
        id = in.readInt();
        path = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(path);
        parcel.writeInt(loaded ? 1 : 0);
    }

    protected String toPureTeX(String tex) {
        String[] lines = tex.split("\n");
        StringBuilder sb = new StringBuilder();

        boolean textMode = false;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.startsWith("%text")) {
                textMode = true;
                continue;
            } else if (line.startsWith("%")) {
                textMode = false;
                continue;
            }

            if (textMode)
                sb.append(String.format("\\text{%s} \\\\\n", line));
            else
                sb.append(String.format("%s\n", line));
        }
        return sb.toString();
    }

}
