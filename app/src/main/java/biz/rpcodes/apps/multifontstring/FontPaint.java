package biz.rpcodes.apps.multifontstring;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

/**
 * Associate font and paint
 * MFCs will point to these paints
 * Each row may need its own set of paints
 * Created by page on 1/16/16.
 */
public class FontPaint {
    final Typeface mT;
    final Paint mP;
    int fontSize = 14;

    FontPaint(Typeface t, int color){
        mT = t;
        Paint p = new Paint();
        p.setColor(color);
        p.setTypeface(mT);
        mP = p;
    }

    public int getFontSize(){
        return fontSize;
    }

    public void setFontSize(int f){
        fontSize = f;
        mP.setTextSize((float) fontSize);
    }

    public Paint getPaint() {
        return mP;
    }
}
