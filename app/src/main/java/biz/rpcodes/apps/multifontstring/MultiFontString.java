package biz.rpcodes.apps.multifontstring;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Draw a string using multiple fonts from a list.
 * Each letter is cycled i.e. 11323 will have the second 1 and second 3 drawn in the second font in the list.
 *
 * TODO: Measure each word using its own font, then
     * see if the word length + current word length is too
     * large to fit. If so, reduce the font of this word and
     * the previous. This assumes we account for multi line text
     * by chopping up the
     * words / characters to fit close to evenly
     * (i.e. the cat \n a dog \n airplane)
 *
 * Created by page on 1/16/16.
 */
public class MultiFontString {
    private static final String TAG = "MultiFontString";
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
        try {
            loadFontsFromPath(assets, path);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        buildSegments();
    }

    private void loadFontsFromPath(AssetManager assets, String path) throws IOException {
        mFonts = new ArrayList<>();
        String[] list;
        try {
            list =assets.list(path);
        } catch (IOException e) {
            throw new IOException("Cannot load " + path);
        }


        Typeface myTypeface = null;

        FontPaint fp = null;

        int colors[] = { Color.BLUE, Color.RED, Color.GRAY, Color.GREEN, Color.CYAN};
        for (int i = list.length - 1; i >= 0; i--){
            String s = list[i];
            int color = colors[i];
            myTypeface = Typeface.createFromAsset(assets, path + '/' + s);
            fp = new FontPaint(myTypeface, color);
            mFonts.add(fp);
        }
//
//
//        Typeface ttt = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
//
//        FontPaint fp = new FontPaint(ttt, Color.BLACK);
//        FontPaint fp2 = new FontPaint(myTypeface, Color.BLUE);
//
//        mFonts.add(fp); mFonts.add(fp2);
    }

    private void buildSegments() {

        mMap = new HashMap<>(26);
        mList = new ArrayList<>();

        char [] list = mOriginalString.toCharArray();
        Short font_number = 0;
        for ( char  c  :  list ){
            // increment font number for character
            font_number = mMap.get(c);
            // if exists
            if ( font_number != null){
                // increment
                font_number++;
                // if too high
               if ( font_number == mFonts.size()) {
                   // reset to 0
                   font_number = 0;
               }
            } else {
                // start at 0 (TODO: SETTING start at random font)
                font_number = 0;
            }
            mMap.put(c, font_number);
            FontPaint fp = mFonts.get(font_number);
            MultiFontChar mfc = new MultiFontChar(c, fp.getPaint());
            mList.add(mfc);
            Log.v(TAG, "ADDED " + c + " : " + font_number);
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
        canvas.drawColor(Color.WHITE);

        // loop characters, grouping until a change in font
        // write the bitmap, and track where we left off


        MultiFontChar c = mList.get(0);
        Paint p = c.getPaint(); // will be overwritten if we loop
        Paint oldPaint = c.getPaint();
        String substring = String.valueOf(c.getChar());

        // track position of next character
        mPaintingX = 0;
        mPaintingY = canvas.getHeight() / 2 ;

        // we added first one as oldPaint, so continue at 1
        for ( int i = 1; i < mList.size(); i++){
             c = mList.get(i);
            // get paint
            p = c.getPaint();
            // if different paint (we use weak ref in MFC), print string
            if ( oldPaint != p){
                // Use oldpaint ecause p is now different
                paintSubstring(substring, canvas, oldPaint);
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

        // float scale = mContext.getResources().getDisplayMetrics().density;

        p.setTextSize(128);

        float widthDelta = p.measureText(substring);

        Log.v(TAG, substring + " is " + widthDelta);

        if ( mPaintingX + widthDelta > canvasW ){
            mPaintingY += (int) p.getTextSize() ;
            mPaintingX = 0;
            Log.v(TAG, "Next line.");
        }
        // TODO: Use measurements to determine when to draw (rows)
        c.drawText(substring, mPaintingX, mPaintingY, p);
        mPaintingX += (int) widthDelta;
        Log.v(TAG, "x painting is " + mPaintingX);


    }

    /**
     * Should print 1 2 1 2 1 2 for "ssssss" and font ring of only 2
     */
    public void showMeTheFontIndexes(){
        MultiFontChar mfc;
        String s = "";
    }
}
