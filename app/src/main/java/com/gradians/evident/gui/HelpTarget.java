package com.gradians.evident.gui;

import android.view.View;

/**
 * Created by adamarla on 6/5/17.
 */

public class HelpTarget {

    public HelpTarget(View view, int titleId, int textId) {
        this.view = view;
        this.textId = textId;
        this.titleId = titleId;
    }

    public HelpTarget(View view, int titleId, int textId, int dismissId) {
        this(view, titleId, textId);
        this.dismissId = dismissId;
    }

    View view;
    int textId, titleId, dismissId;

}
