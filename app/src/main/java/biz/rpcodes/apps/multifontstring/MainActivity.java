package biz.rpcodes.apps.multifontstring;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityMFS";
    EditText edittext;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addKeyListener();
        if (null == edittext){
            Log.e(TAG, "Edittext not init");
            throw new IllegalStateException("Cannot initialize 1");
        }
        process("TT");

        Button b = (Button) findViewById(R.id.buttonGo);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = edittext.getText().toString();
                Log.v(TAG, "Processing " + s);
                process(s);
            }
        });
    }

    public void addKeyListener() {

        // get edittext component
         edittext = (EditText) findViewById(R.id.editText);

        // add a keylistener to keep track user input
        edittext.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //if ( event.getAction() != KeyEvent.ACTION_DOWN) {
                    String s = ((EditText) v).getText().toString();
                    process(s);
                //}
                return false;
            }
        });
    }

    public void process(String text){
        if ( text.isEmpty() ){
            return;
        }
        MultiFontString mfs = new MultiFontString(text
                , "fonts", getApplicationContext());
        // Bitmap b = mfs.toBitmap(1600,1200);
        Bitmap b = mfs.rowBasedBitmap(1600,1200);
        ImageView i = (ImageView) findViewById(R.id.iv1);
        i.setImageBitmap(b);
    }
}
