package io.flutter.plugins.webviewflutter;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * @author luopeng
 * Created at 2019-09-26 18:04
 */
public class ScrollWebView extends WebView {
    public OnScrollChangeListener listener;

    public ScrollWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ScrollWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollWebView(Context context) {
        super(context);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        float webcontent = getContentHeight() * getScale();
        float webnow = getHeight() + getScrollY();
        if (Math.abs(webcontent - webnow) < 10) {
            listener.onPageEnd(l, t, oldl, oldt);
        } else if (getScrollY() == 0) {
            listener.onPageTop(l, t, oldl, oldt);
        } else {
            listener.onScrollChanged(l, t, oldl, oldt);
        }
    }

    public void setOnScrollChangeListener(OnScrollChangeListener listener) {
        this.listener = listener;
    }

    public interface OnScrollChangeListener {
        public void onPageEnd(int l, int t, int oldl, int oldt);
        public void onPageTop(int l, int t, int oldl, int oldt);
        public void onScrollChanged(int l, int t, int oldl, int oldt);
    }
}
