package com.gradians.evident.gui;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.gradians.evident.R;

/**
 * Created by adamarla on 4/2/17.
 */

public class AnswerButtonBar extends LinearLayout {
    public AnswerButtonBar(Context context) {
        super(context);
    }

    public AnswerButtonBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void enable(boolean enable) {
        int ids[] = { R.id.btn_is_false, R.id.btn_is_true };
        for (int j: ids) {
            FloatingActionButton btn = (FloatingActionButton)findViewById(j);
            btn.setEnabled(enable);
            btn.setAlpha(enable ? 1.0f : 0.5f);
        }
        enableReferenceButton(!enable);
    }

    public void enableReferenceButton(boolean enable) {
        FloatingActionButton btn = (FloatingActionButton)findViewById(R.id.btn_see_skill);
        btn.setEnabled(enable);
        btn.setAlpha(enable ? 1.0f : 0.5f);
    }

    @Override
    public void setOnClickListener(OnClickListener listener) {
        int ids[] = { R.id.btn_is_false, R.id.btn_is_true, R.id.btn_see_skill };
        for (int j: ids) {
            FloatingActionButton btn = (FloatingActionButton)findViewById(j);
            btn.setOnClickListener(listener);
        }
    }

}
