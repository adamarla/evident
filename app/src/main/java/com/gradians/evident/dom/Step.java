package com.gradians.evident.dom;

import android.os.Parcel;

import com.gradians.evident.gui.ICard;

/**
 * Created by adamarla on 3/30/17.
 */
public class Step implements ICard {

    public Step(String correct, String incorrect, String reason) {
        if (correct != null && incorrect != null) {
            faceShownIsCorrect = ((int)Math.random()*10)%2 == 0;
        } else {
            faceShownIsCorrect = correct != null;
        }
        this.correct = correct;
        this.incorrect = incorrect;
        this.reason = reason;
        steps = false;
        answerable = true;
    }

    @Override
    public String getFront() {
        return faceShownIsCorrect ? correct : incorrect;
    }

    @Override
    public String getBack() {
        return reason;
    }

    @Override
    public void setAttempt(boolean isTrue) {
        attempted = true;
        attempt = isTrue;
    }

    @Override
    public boolean getAttempt() {
        return attempt;
    }

    @Override
    public boolean hasBeenAttempted() {
        return attempted;
    }

    @Override
    public boolean isAnswerable() {
        return answerable;
    }

    @Override
    public boolean isCorrect() {
        return faceShownIsCorrect;
    }

    @Override
    public boolean hasSteps() {
        return steps;
    }

    public String correct, incorrect, reason;
    public boolean faceShownIsCorrect, attempted, attempt, steps, answerable;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(correct);
        parcel.writeString(incorrect);
        parcel.writeString(reason);
        parcel.writeInt(faceShownIsCorrect ? 1 : 0);
        parcel.writeInt(attempted ? 1 : 0);
        parcel.writeInt(attempt ? 1 : 0);
        parcel.writeInt(steps ? 1 : 0);
        parcel.writeInt(answerable ? 1: 0);
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
        faceShownIsCorrect = in.readInt() == 1;
        attempted = in.readInt() == 1;
        attempt = in.readInt() == 1;
        steps = in.readInt() == 1;
        answerable = in.readInt() == 1;
    }

}
