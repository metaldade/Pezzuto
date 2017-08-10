package com.pezzuto.pezzuto.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.pezzuto.pezzuto.R;

/**
 * Created by dade on 09/05/17.
 */

public class GraphicUtils {
    public static void setListViewHeightBasedOnChildren(ListView listView, int type) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;

        view = listAdapter.getView(0, view, listView);
        view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, RelativeLayout.LayoutParams.WRAP_CONTENT));

        view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
        totalHeight = view.getMeasuredHeight()*(listAdapter.getCount()+type);

        /*for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, RelativeLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }*/
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
    public static void setVisibleWithFading(Context context, final View view, boolean isVisible) {
        Animation animationFadeIn = AnimationUtils.loadAnimation(context, R.anim.fadein);
        Animation animationFadeOut = AnimationUtils.loadAnimation(context, R.anim.fadeout);
        animationFadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        if (isVisible) {
            view.setVisibility(View.VISIBLE);
            view.startAnimation(animationFadeIn);
        }
        else {
            view.startAnimation(animationFadeOut);
        }
    }
}
