package com.gradians.evident.gui;

import android.content.Context;
import android.os.Parcelable;

/**
 * Created by adamarla on 3/30/17.
 */

public interface ICard extends Parcelable {

    String getFront();

    String getBack();

    void setAttempt(boolean sezTrue);

    boolean getAttempt();

    boolean hasBeenAttempted();

    boolean isARiddle();

    boolean isCorrect();

    boolean hasSteps();

}
