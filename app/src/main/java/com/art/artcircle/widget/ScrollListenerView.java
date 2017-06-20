/**
 * @copyright（C）：2006-2013 Xinmei All rights reserved
 * @author： huyongsheng
 * @date：2013-12-27
 */

package com.art.artcircle.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;
public class ScrollListenerView extends ScrollView {

    private OnScrollVerticalChangedListener mListener;

    public ScrollListenerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ScrollListenerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollListenerView(Context context) {
        super(context);
    }

    public void setOnScrollVerticalChangedListener(OnScrollVerticalChangedListener listener) {
        mListener = listener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (t != oldt) {
            mListener.onScrollVerticalChanged(t);
        }
    }

    public interface OnScrollVerticalChangedListener {
        void onScrollVerticalChanged(int currentVertical);
    }

}
