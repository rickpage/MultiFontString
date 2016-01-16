package biz.rpcodes.apps.multifontstring;

import android.graphics.Paint;
import android.graphics.Typeface;

import java.lang.ref.WeakReference;

/**
 * Created by page on 1/16/16.
 */
public class MultiFontChar {
    final char mC;
    final WeakReference<Paint> mPaintRef;

    MultiFontChar(char c, Paint p){
        mPaintRef = new WeakReference<Paint>(p);
        mC = c;
    }

    public Paint getPaint() {
        if ( mPaintRef != null)
            return mPaintRef.get();
        else
            return null;
    }
}
