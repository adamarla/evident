package com.gradians.evident.dom;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.Xml;

import com.gradians.evident.gui.ICard;
import com.gradians.evident.util.SourceParser;
import com.gradians.evident.util.TeXSourceParser;
import com.gradians.evident.util.XMLSourceParser;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;

/**
 * Created by adamarla on 3/19/17.
 */

public abstract class Asset implements Parcelable {

    public Asset(int id, String path) {
        this.id = id;
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public abstract ICard getCard();

    public boolean load(Context context) {
        File dir = new File(context.getExternalFilesDir(null), "vault/" + path);
        File source = new File(dir, "source.tex");
        if (!source.exists()) source = new File(dir, "source.xml");
        if (!source.exists()) return false;

        try {
            InputStream is = new FileInputStream(source);
            SourceParser parser;
            if (source.getName().endsWith("xml")) {
                parser = new XMLSourceParser(is);
            } else {
                parser = new TeXSourceParser(is);
            }
            extract(parser);
            is.close();
            return true;
        } catch (Exception e) {
            Log.e("EvidentApp", "Error loading " + e.getMessage());
        }
        return false;
    }

    protected abstract void extract(SourceParser parser) throws Exception;

    protected int id;
    protected String path;

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
    }

    @Override
    public String toString() {
        return id + " " + path;
    }
}
