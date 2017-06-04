package com.gradians.evident.gui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;

import com.gradians.evident.R;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.shape.RectangleShape;
import uk.co.deanwild.materialshowcaseview.target.Target;

/**
 * Created by adamarla on 6/4/17.
 */

public class HelpOverlay {

    public HelpOverlay(View target, Activity activity, int title, int text) {
        build(target, activity, title, text);
    }

    public void show() {
        overlay.show(activity);
    }

    private void build(View target, Activity activity, int title, int text) {
        this.activity = activity;
        int delayInMillis = 500;
        String SHOWCASE_ID = title + "-" + text;
        // single example
        overlay = new MaterialShowcaseView.Builder(activity)
                .setTarget(target)
                .setDismissText(R.string.ack_text)
                .setTitleText(title)
                .setContentText(text)
                .setShape(new RectangleShape(target.getMeasuredWidth(), target.getMeasuredHeight()))
                .setShapePadding(-5)
                .singleUse(SHOWCASE_ID) // provide a unique ID used to ensure it is only shown once
                .setDelay(delayInMillis).build();
    }

    private Activity activity;
    private MaterialShowcaseView overlay;

}
