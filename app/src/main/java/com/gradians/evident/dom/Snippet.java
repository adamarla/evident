package com.gradians.evident.dom;


import android.os.Parcel;

import com.gradians.evident.gui.ICard;
import com.gradians.evident.tex.SourceParser;

/**
 * Created by adamarla on 3/19/17.
 */

public class Snippet extends Asset {

    public Snippet(int id, String path) {
        super(id, path);
    }

    @Override
    public ICard getCard() {
        return step;
    }

    @Override
    protected void extract(SourceParser parser) throws Exception {
        parser.populateSnippet(this);
    }

    public Step step;

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
