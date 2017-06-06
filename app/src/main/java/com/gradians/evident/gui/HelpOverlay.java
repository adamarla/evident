package com.gradians.evident.gui;

import android.app.Activity;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

/**
 * Created by adamarla on 6/4/17.
 */

public class HelpOverlay {

    public HelpOverlay(HelpTarget target, Activity activity) {
        overlay = build(target, activity);
    }

    public HelpOverlay(HelpTarget[] targets, Activity activity) {
        sequence = build(targets, activity);
    }

    public void show() {
        if (overlay != null) overlay.show(activity);
        else sequence.start();
    }

    private MaterialShowcaseView build(HelpTarget target, Activity activity) {
        this.activity = activity;
        int delayInMillis = 1000;
        String SHOWCASE_ID = target.titleId + "-" + target.textId;
        // single example
        MaterialShowcaseView.Builder builder = new MaterialShowcaseView.Builder(activity)
                .setTarget(target.view)
                .setTitleText(target.titleId)
                .setContentText(target.textId)
                .withRectangleShape()
                .setShapePadding(-5)
                .singleUse(SHOWCASE_ID) // provide a unique ID used to ensure it is only shown once
                .setFadeDuration(delayInMillis);
        if (target.dismissId > 0) {
            builder = builder.setDismissText(target.dismissId);
        } else {
            builder = builder.setDismissOnTargetTouch(true).setTargetTouchable(true);
        }
        return builder.build();
    }

    private MaterialShowcaseSequence build(HelpTarget[] targets, Activity activity) {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setFadeDuration(1000);
        config.setDelay(1000); // between each showcase view
        config.setShapePadding(-5);

        sequence = new MaterialShowcaseSequence(activity);
        sequence.setConfig(config);

        for (HelpTarget target: targets) {
            MaterialShowcaseView item = build(target, activity);
            sequence.addSequenceItem(item);
        }
        return sequence;
    }

    private Activity activity;
    private MaterialShowcaseView overlay;
    private MaterialShowcaseSequence sequence;

}
