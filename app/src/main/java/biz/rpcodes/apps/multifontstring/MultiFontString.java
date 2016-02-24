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
    public static final int MIN_FONT_SIZE = 9;
    final String mOriginalString;
    ArrayList<MultiFontChar> mList;
    ArrayList<FontPaint> mFonts;

    HashMap<Character, Short> mMap;
    private Context mContext;
    private int mPaintingX;
    private int mPaintingY;
    private ArrayList<String> mDisplayRows;

    MultiFontString(String o, String path, Context context){
        mOriginalString = o;

        mContext = context;

        AssetManager assets = context.getAssets();
        try {
            loadFontsFromPath(assets, path);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        // buildSegments();
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


    /** Take word and change into 1 to 3 MFChar Rows
     *
     */
    public Bitmap rowBasedBitmap(int bmpW, int bmpH){
        Bitmap b = Bitmap.createBitmap(bmpW, bmpH, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(b);
        canvas.drawColor(Color.WHITE);

        // get width
        int bitmapWidth = bmpW;

        // get rows and calc row size
        int rowHeight = 0;
        // first get row info
        storeWordInformation();
        // then calc
        rowHeight = bmpH / mDisplayRows.size();
        // for each row, make MFC Row
        // Use the map technique
        float rowX = 0;
        float rowY = rowHeight;
        int dRow = 0;
        for ( String s : mDisplayRows){
            dRow++;
            l("Row #" + dRow + " == " + s);
            // make a new row
            // copies fonts and chars and
            // represents
            MultiFontCharRow mfcRow = new MultiFontCharRow(s
                    , mFonts, bitmapWidth, rowHeight);
            rowX = (int) mfcRow.getXOffset();
            // loop characters
            for ( MultiFontChar mfc : mfcRow.getList()){
                canvas.drawText(String.valueOf(mfc.getChar())
                        , rowX, rowY, mfc.getPaint());
                rowX += mfc.getWidth();
            }
            l("Row X ends at " + rowX);
            rowY += rowHeight;
            rowX = 0;
        }

        return b;
    }


    void l(String s){
        Log.i(TAG, s);
    }
    private void storeWordInformation(){

        // length of whole thing
        int originalStringLength = mOriginalString.length();
        l("total string length " + originalStringLength);

        if (originalStringLength == 0){
            throw new IllegalStateException("Cannot parse 0 length original string");
        }
        // split into words
        String strs[] = mOriginalString.split("\\s+");
        ArrayList<Integer> lens = new ArrayList<>();

        // how many words is that
        int mNumberOfWords = strs.length;

        int longestWordLength = 0;
        int longestWordIndex = 0;
        for (String s : strs){
            l("Parsing: "  + s);
            lens.add(s.length());
            if ( s.length() > longestWordLength){
                longestWordLength = s.length();
                longestWordIndex = lens.size() - 1;
            }
        }
        l("longest word " + strs[longestWordIndex] + " : " + longestWordLength + " chars");
        int rows = 1;

        // if more than 3 words, break up somewhat evenly
        if (mNumberOfWords > 3){
            rows = 3;
        }
        // if 3 words, or less, one on each row.
        else {
            rows = strs.length;
        }
        l("rows " + rows);
        // TODO: figure out the font size
        // do this by figuring out how many characters in each row
        // We would need to know how much width we have here
        // also, would make sense to have smaller/larger for realism

        int averageCharactersPerRow = Math.max(originalStringLength / rows, 1);
        l("Avg chars " + averageCharactersPerRow);

        int maxCharPerRow = averageCharactersPerRow;
        // TODO: evaluate
        if ( longestWordLength > averageCharactersPerRow){
            maxCharPerRow = longestWordLength;
        }
        l("max chars per row: " + maxCharPerRow);
        // Did we just decrease our row count?
        // We will need to check actual rows after

        ArrayList<String> displayRowSubstrings = new ArrayList<>(rows);
        String temp = strs[0];

        int rowCount = 0;
        // add a word to the row until we are over max characters
        for ( int index = 1; index < mNumberOfWords; index++){

            // +1 below is for the " " we want to add after word(s) in the row
            if ( displayRowSubstrings.size() != (rows - 1) &&
                    (temp.length() + strs[index].length() + 1) >= maxCharPerRow){
                displayRowSubstrings.add(temp);
                temp = strs[index];
                l("Row #" + (displayRowSubstrings.size())
                        + ": " + displayRowSubstrings.get(displayRowSubstrings.size()-1));
            } else {
                temp += " " + strs[index];
            }
        }
        // add the last row. if one row, we never added a single row yet
        if (temp != ""){
//            String s = displayRowSubstrings.remove(rows - 1);
//            s += temp;
            displayRowSubstrings.add(temp);
            l("Row #" + (displayRowSubstrings.size())
                    + ": " + displayRowSubstrings.get(displayRowSubstrings.size()-1));
        }

        // populate members
        mDisplayRows = displayRowSubstrings;
    }

    /**
     * Build a list of MFC, changing the font when a letter is used.
     * Also split up the words "evenly" in the case of three or
     * more words
     * TODO: Set a length about equal to the L/3, then fill until should break instead
     */
    private void buildSegments() {

        mMap = new HashMap<>(26);
        mList = new ArrayList<>();

        // First, collect word information
        storeWordInformation();

        // TODO: do this for each word, keeping map from last word
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
        // wgit gui &
        // e the bitmap, and track where we left off


        MultiFontChar c = mList.get(0);
        Paint p = c.getPaint(); // will be overwritten if we loop
        Paint oldPaint = c.getPaint();
        String substring = String.valueOf(c.getChar());

        // track position of next character
        mPaintingX = 0;
        mPaintingY = canvas.getHeight() / 2 ;

//        int rowIndex = -1;
//        // add to this
//        int rowLength = 0; mDisplayRows.get(rowIndex).length();

        // we added first one as oldPaint, so continue at 1
        for ( int i = 1; i < mList.size(); i++){
//            // TODO See if we are at the end of the row
//            // TODO if so, recalculate font size
//            if ( i >= rowLength -1 ){
//                l("row length " + rowLength + " : i " + i);
//                rowLength += mDisplayRows.get(rowIndex++).length();
//                l(" new row index " + rowIndex + " , new chars for row check " + rowLength);
//
//            }
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
}
