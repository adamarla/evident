package com.gradians.evident.dom;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.gradians.evident.gui.ICard;
import com.gradians.evident.tex.SourceParser;
import com.gradians.evident.tex.TeXSourceParser;
import com.gradians.evident.tex.XMLSourceParser;
import com.gradians.evident.tex.XMLTeXSourceParser;

import java.io.File;

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

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public abstract ICard getCard();

    boolean load(Context context) {
        File dir = new File(context.getExternalFilesDir(null), "vault/" + path);
        File texSource = new File(dir, "source.tex");
        File xmlSource = new File(dir, "source.xml");
        if (!texSource.exists() && !xmlSource.exists()) return false;

        try {
            SourceParser parser;
            if (!texSource.exists() && xmlSource.exists()) {
                parser = new XMLSourceParser(xmlSource);
            } else if (texSource.exists() && !xmlSource.exists()) {
                parser = new TeXSourceParser(texSource);
            } else {
                parser = new XMLTeXSourceParser(texSource, xmlSource);
            }
            extract(parser);
            return true;
        } catch (Exception e) {
            Log.e("EvidentApp", "Error loading " + e.getMessage());
        }
        return false;
    }

    protected abstract void extract(SourceParser parser) throws Exception;

    private int id, chapterId;
    private String path;

    Asset(Parcel in) {
        id = in.readInt();
        chapterId = in.readInt();
        path = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(chapterId);
        parcel.writeString(path);
    }

    @Override
    public String toString() {
        return id + " " + path;
    }
}
