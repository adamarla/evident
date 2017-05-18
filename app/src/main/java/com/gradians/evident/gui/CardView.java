package com.gradians.evident.gui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gradians.evident.R;
import com.himamis.retex.renderer.android.LaTeXView;

/**
 * Created by adamarla on 4/12/17.
 */

public class CardView extends RelativeLayout {

    public CardView(Context context) {
        super(context);
        initialize();
    }

    public CardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        front = (LaTeXView)findViewById(R.id.front);
        rear = (LaTeXView)findViewById(R.id.rear);
        trueIndicator = (ImageView)findViewById(R.id.true_indicator);
        rightIndicator = (ImageView)findViewById(R.id.right_indicator);
        expansionIndicator = (ImageView)findViewById(R.id.expansion_indicator);
    }

    public void hideIndicators() {
        expansionIndicator.setVisibility(View.INVISIBLE);
        trueIndicator.setVisibility(View.INVISIBLE);
        rightIndicator.setImageResource(View.INVISIBLE);
    }

    public void setCard(ICard card) {
        this.card = card;

        front.setLatexText(card.getFront());
        rear.setLatexText(card.getBack());

        trueIndicator.setVisibility(View.INVISIBLE);
        rightIndicator.setVisibility(View.INVISIBLE);
        expansionIndicator.setVisibility(View.INVISIBLE);
        int minHeight;

        if (card.isAnswerable()) {
            minHeight = (int)getResources().getDimension(R.dimen.snippet_min_height);
            if (card.hasFurtherSteps()) {
                enableExpansionIndicator(R.mipmap.ic_launch_new);
                if (card.wasAttempted()) {
                    enableAttemptedIndicators();
                }
            } else {
                enableTrueIndicator();
                if (card.wasAttempted()) {
                    enableAttemptedIndicators();
                    enableExpansionIndicator(R.mipmap.ic_expand_more);
                }
            }
        } else {
            minHeight = (int)getResources().getDimension(R.dimen.skill_min_height);
            enableExpansionIndicator(R.mipmap.ic_expand_more);
        }

        this.setMinimumHeight(minHeight);
    }

    public ICard getCard() {
        return card;
    }

    public void select() {
        // The list 'selector' bg_selected_card.xml
        // needs a transparent drawable background,
        // otherwise it covers the text of the
        // selected item. That's why explicitly
        // specifying background as White
        setBackgroundResource(R.color.white);
        if (card.wasAttempted()) flip();
    }

    public void unselect() {
        setBackgroundResource(R.drawable.bg_unselected_card);
        if (!rightSideUp) flip();
    }

    public void answer(boolean iSayCorrect) {
        card.setAttempt(iSayCorrect);
        enableAttemptedIndicators();
    }

    public void flip() {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)front.getLayoutParams();
        if (rightSideUp) {
            lp.addRule(RelativeLayout.CENTER_VERTICAL, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            rear.setVisibility(View.VISIBLE);
            if (card.isAnswerable() && card.wasAttempted()) {
                trueIndicator.setVisibility(View.VISIBLE);
                rightIndicator.setVisibility(View.INVISIBLE);
            }
            expansionIndicator.setImageResource(R.mipmap.ic_expand_less);
        } else {
            lp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            lp.addRule(RelativeLayout.CENTER_VERTICAL);
            rear.setVisibility(View.GONE);
            trueIndicator.setVisibility(View.INVISIBLE);
            rightIndicator.setVisibility(View.VISIBLE);
            expansionIndicator.setImageResource(R.mipmap.ic_expand_more);
        }
        front.setLayoutParams(lp);
        rightSideUp = !rightSideUp;
    }

    private void enableExpansionIndicator(int resId) {
        expansionIndicator.setImageResource(resId);
        expansionIndicator.setVisibility(View.VISIBLE);
    }

    private void enableAttemptedIndicators() {
        rightIndicator.setImageResource(card.isCorrect() == card.getAttempt() ?
                R.mipmap.ic_thumbs_up : R.mipmap.ic_thumbs_down);
        rightIndicator.setVisibility(View.VISIBLE);
        expansionIndicator.setImageResource(R.mipmap.ic_expand_more);
        expansionIndicator.setVisibility(View.VISIBLE);
    }

    private void enableTrueIndicator() {
        if (card.isCorrect()) {
            trueIndicator.setImageResource(R.mipmap.white_tick);
            trueIndicator.setBackgroundResource(R.drawable.bg_circle_correct);
        } else {
            trueIndicator.setImageResource(R.mipmap.white_cross);
            trueIndicator.setBackgroundResource(R.drawable.bg_circle_incorrect);
        }
    }

    private void initialize() {
        this.setPadding(10, 0, 0, 10);
        this.setBackgroundResource(R.drawable.bg_unselected_card);
        this.rightSideUp = true;
    }

    private ICard card;
    private LaTeXView front, rear;
    private ImageView trueIndicator, rightIndicator, expansionIndicator;
    public boolean rightSideUp;

}
