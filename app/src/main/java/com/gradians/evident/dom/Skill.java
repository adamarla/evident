package com.gradians.evident.dom;

import android.os.Parcel;

import com.gradians.evident.gui.ICard;
import com.gradians.evident.tex.SourceParser;


/**
 * Created by adamarla on 3/19/17.
 */

public class Skill extends Asset implements ICard {

    public Skill(int id, String path) {
        super(id, path);
    }

    @Override
    public ICard getCard() {
        return this;
    }

    @Override
    public String getFront() {
        return title;
    }

    @Override
    public String getBack() {
        return studyNote;
    }

    // Irrelevant methods begin ==>
    @Override
    public boolean wasAttempted() {
        return true;
    }

    @Override
    public void setAttempt(boolean isTrue) { }

    @Override
    public void reset() { }

    @Override
    public boolean getAttempt() {
        return true;
    }
    // Irrelevant methods end <==

    @Override
    public boolean isAnswerable() {
        return false;
    }

    @Override
    public boolean isCorrect() {
        return true;
    }

    @Override
    public boolean hasFurtherSteps() {
        return false;
    }

    @Override
    protected void extract(SourceParser parser) throws Exception {
        parser.populate(this);
    }

    public String title, studyNote;

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(title);
        parcel.writeString(studyNote);
    }

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
        super(in);
        title = in.readString();
        studyNote = in.readString();
    }
}
