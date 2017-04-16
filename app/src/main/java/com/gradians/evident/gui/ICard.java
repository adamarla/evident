package com.gradians.evident.gui;

import android.content.Context;
import android.os.Parcelable;

/**
 * Created by adamarla on 3/30/17.
 */

public interface ICard extends Parcelable {

    String getFront();

    String getBack();

    void setAttempt(boolean isTrue);

    boolean getAttempt();

    boolean hasBeenAttempted();

    boolean isAnswerable();

    boolean isCorrect();

    boolean hasSteps();

}
