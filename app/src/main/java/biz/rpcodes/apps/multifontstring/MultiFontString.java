package biz.rpcodes.apps.multifontstring;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Stores information about a technique that
 * will draw multiple font bitmap
 * Created by page on 1/16/16.
 */
public class MultiFontString {
    final String mOriginalString;
    ArrayList<MultiFontChar> mList;
    ArrayList<FontPaint> mFonts;

    HashMap<Character, Short> mMap;
    private Context mContext;

    MultiFontString(String o, String path, Context context){
        mOriginalString = o;

        mContext = context;

        AssetManager assets = context.getAssets();
        loadFontsFromPath(assets, path);
        buildSegments();
    }

    private void loadFontsFromPath(AssetManager assets, String path) {
        mFonts = new ArrayList<>();

        Typeface myTypeface = Typeface.createFromAsset(assets, "fonts/5.ttf");

        Typeface ttt = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);

        FontPaint fp = new FontPaint(ttt, Color.BLACK);
        FontPaint fp2 = new FontPaint(myTypeface, Color.BLUE);

        mFonts.add(fp); mFonts.add(fp2);
    }

    private void buildSegments() {

        mMap = new HashMap<>(26);
        mList = new ArrayList<>();

        char [] list = mOriginalString.toCharArray();
        Short font_number = 0;
        for ( char  c  :  list ){
            // increment font number for character
            font_number = mMap.get(c);
            if ( font_number != null) {
                font_number++;
                if ( font_number >= mFonts.size()){
                    font_number = 0;
                }
            }
            else {
                font_number = 0;
            }
            mMap.put(c, font_number);
            FontPaint fp = mFonts.get(font_number);
            MultiFontChar mfc = new MultiFontChar(c, fp.getPaint());
            mList.add(mfc);
        }
    }

    /**
     * Convert text to a bitmap
     * @param h
     * @param w
     * @return
     */
    public Bitmap toBitmap(int w, int h) {
        Bitmap b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

//
//        c.drawText("TEST", 10f, 10f,
//                ((MultiFontChar) mList.get(0)).getPaint());
//
//        // TODO: need to use multiple fonts in here:
//        c.drawColor(Color.YELLOW);
//        DrawMultilineText.drawMultilineText("Test 1234565677", 0, 0,
//                (mList.get(0)).getPaint(), c, 72
//        , new Rect(0,0,w,h) );

        b = DrawMultilineText.drawMultilineTextToBitmap(mContext,
                b, mOriginalString);
        return b;

    }

    /**
     * Should print 1 2 1 2 1 2 for "ssssss" and font ring of only 2
     */
    public void showMeTheFontIndexes(){
        MultiFontChar mfc;
        String s = "";
    }
}
