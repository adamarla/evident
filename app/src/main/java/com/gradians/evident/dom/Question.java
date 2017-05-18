package com.gradians.evident.dom;

import android.os.Parcel;

import com.gradians.evident.gui.ICard;
import com.gradians.evident.tex.SourceParser;


/**
 * Created by adamarla on 3/19/17.
 */

public class Question extends Asset {

    public Question(int id, String path) {
        super(id, path);
    }

    @Override
    public ICard getCard() {
        return statement;
    }

    public Step[] getSteps() {
        return steps;
    }

    @Override
    protected void extract(SourceParser parser) throws Exception {
        parser.populate(this);
    }

    public Step statement;
    public Step[] steps;

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeParcelable(statement, 0);
        parcel.writeTypedArray(steps, 0);
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
        statement = in.readParcelable(this.getClass().getClassLoader());
        steps = in.createTypedArray(Step.CREATOR);
    }

}

