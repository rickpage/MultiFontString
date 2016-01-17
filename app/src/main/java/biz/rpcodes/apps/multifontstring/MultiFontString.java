package biz.rpcodes.apps.multifontstring;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
    private int mPaintingX;
    private int mPaintingY;

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
        Canvas canvas = new Canvas(b);

        // loop characters, grouping until a change in font
        // write the bitmap, and track where we left off

        Paint p = null;
        Paint oldPaint = mList.get(0).getPaint();
        String substring = "";

        // track position of next character
        mPaintingX = 0;
        mPaintingY = 0;

        for ( MultiFontChar c : mList){
            p = c.getPaint();
            if ( oldPaint != p){
                paintSubstring(substring, canvas, p);
                // store this paint so we change only when font changes
                oldPaint = p;
                substring = String.valueOf(c.getChar());
            } else {
                substring += c.getChar();
            }
        }
        // paint last substring
        if ( !substring.isEmpty()){
            paintSubstring(substring, canvas, p);
            substring = "";
        }



        // We need to find out how many lines, then get font size
        // based on fitting to lines. one word = one line,
        // two words, two lines, three or more split at spaces
        // until substrings are all close to equal (how?)

        // Not quite:
//        b = DrawMultilineText.drawMultilineTextToBitmap(mContext,
//                b, mOriginalString);
        return b;

    }

    /**
     * Uses mPaintingX,Y to paint the substring
     * onto the canvas
     * @param substring
     */
    private void paintSubstring(String substring, Canvas c, Paint p) {
        int canvasW = c.getWidth();

        float scale = mContext.getResources().getDisplayMetrics().density;
        float widthDelta = p.measureText(substring) * scale;

        p.setTextSize(128);
        mPaintingY = c.getHeight() / 2 ;
        c.drawText(substring, mPaintingX, mPaintingY, p);
        mPaintingX += (int) widthDelta;


    }

    /**
     * Should print 1 2 1 2 1 2 for "ssssss" and font ring of only 2
     */
    public void showMeTheFontIndexes(){
        MultiFontChar mfc;
        String s = "";
    }
}
