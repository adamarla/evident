package com.gradians.evident.dom;

import android.content.Context;
import android.os.Parcel;

import com.gradians.evident.gui.ICard;

/**
 * Created by adamarla on 3/30/17.
 */
public class Step implements ICard {

    public Step() { }

    @Override
    public String getFront() {
        return faceShownIsCorrect ? correct : incorrect;
    }

    @Override
    public String getBack() {
        return reason;
    }

    @Override
    public void attempt(boolean correctly) {
        attempted = true;
    }

    @Override
    public boolean hasBeenAttempted() {
        return attempted;
    }

    @Override
    public boolean isARiddle() {
        return true;
    }

    @Override
    public boolean isCorrect() {
        return faceShownIsCorrect;
    }

    @Override
    public void load(Context context) {
        if (correct != null && incorrect != null) {
            faceShownIsCorrect = ((int)Math.random()*10)%2 == 0;
        } else {
            faceShownIsCorrect = correct != null;
        }
    }

    public String correct, incorrect, reason;
    public boolean faceShownIsCorrect, attempted;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(correct);
        parcel.writeString(incorrect);
        parcel.writeString(reason);
    }

    public static Creator<Step> CREATOR = new Creator<Step>() {
        @Override
        public Step createFromParcel(Parcel parcel) {
            return new Step(parcel);
        }

        @Override
        public Step[] newArray(int i) {
            return new Step[i];
        }
    };

    private Step(Parcel in) {
        correct = in.readString();
        incorrect = in.readString();
        reason = in.readString();
    }

}
