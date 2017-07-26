package com.sf.ALuaGuide;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by user on 2017/7/21.
 */

public class ScrollingFABBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {
    private int toolBarHeight = 0;

    public ScrollingFABBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton fab, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton fab, View dependency) {
        if (toolBarHeight == 0) {
            Toolbar toolbar = (Toolbar) parent.findViewById(R.id.toolbar);
            toolBarHeight = toolbar.getHeight();
        }

        if (dependency instanceof AppBarLayout) {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
            int fabBottomMargin = lp.bottomMargin;
            int distanceToScroll = fab.getHeight() + fabBottomMargin;
            float ty = dependency.getY();
            float ratio = ty / (float) toolBarHeight;
            fab.setTranslationY(-distanceToScroll * ratio);
        }
        return true;
    }

}