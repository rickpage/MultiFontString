package biz.rpcodes.apps.multifontstring;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

/**
 * Associate font and paint
 * Created by page on 1/16/16.
 */
public class FontPaint {
    final Typeface mT;
    final Paint mP;
    FontPaint(Typeface t, int color){
        mT = t;
        Paint p = new Paint();
        p.setColor(color);
        p.setTypeface(mT);
        mP = p;
    }

    public Paint getPaint() {
        return mP;
    }
}
