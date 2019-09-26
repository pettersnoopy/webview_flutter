package io.flutter.plugins.webviewflutter;

import android.os.Build;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ScrollView;

/**
 * @author luopeng
 * Created at 2019-09-26 17:37
 */
public class Utils {
    public static boolean canChildScrollUp(View view) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (view instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) view;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return view.getScrollY() > 0;
            }
        } else {
            return view.canScrollVertically(-1);
        }
    }

    public static boolean canChildScrollDown(View view) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (view instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) view;
                return absListView.getChildCount() > 0
                        && (absListView.getLastVisiblePosition() < absListView.getChildCount() - 1
                        || absListView.getChildAt(absListView.getChildCount() - 1).getBottom() > absListView.getPaddingBottom());
            } else if (view instanceof ScrollView) {
                ScrollView scrollView = (ScrollView) view;
                if (scrollView.getChildCount() == 0) {
                    return false;
                } else {
                    return scrollView.getScrollY() < scrollView.getChildAt(0).getHeight() - scrollView.getHeight();
                }
            } else {
                return false;
            }
        } else {
            return view.canScrollVertically(1);
        }
    }
}
