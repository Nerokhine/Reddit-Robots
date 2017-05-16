package com.erokhine.nikita.redditrobots;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.mycompany.myfirstapp.MESSAGE";
    public final static String EXTRA_MESSAGE2 = "com.mycompany.myfirstapp.MESSAGE2";
    public final static String EXTRA_MESSAGE3 = "com.mycompany.myfirstapp.MESSAGE3";
    public final static String EXTRA_MESSAGE4 = "com.mycompany.myfirstapp.MESSAGE4";
    public final static String EXTRA_MESSAGE5 = "com.mycompany.myfirstapp.MESSAGE5";
    private boolean regexOn = false;
    private boolean replyOn = false;
    private String userName = "";
    private String password = "";

    /** Called when the user clicks the Send button */
    public void addAction(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_match);
        EditText editText2 = (EditText) findViewById(R.id.edit_reply);
        String match = editText.getText().toString();
        String reply = editText2.getText().toString();
        if(replyOn){
            intent.putExtra(EXTRA_MESSAGE3, "true");
        }else{
            intent.putExtra(EXTRA_MESSAGE3, "false");
        }
        if(regexOn == false && !match.equals("")){
            match = ".*" + match + ".*";
        }
        intent.putExtra(EXTRA_MESSAGE, match);
        intent.putExtra(EXTRA_MESSAGE2, reply);
        intent.putExtra(EXTRA_MESSAGE4, this.userName);
        intent.putExtra(EXTRA_MESSAGE5, this.password);
        startActivity(intent);
    }

    public void activateRegex(View view) {
        regexOn = ((CheckBox) findViewById(R.id.regex_check_box)).isChecked();
        EditText editText = (EditText) findViewById(R.id.edit_match);
        if(regexOn){
            editText.setHint(getString(R.string.advanced_matching_hint));
        }else{
            editText.setHint(getString(R.string.basic_matching_hint));
        }
    }

    public void activateReply(View view) {
        replyOn = ((CheckBox) findViewById(R.id.reply_check_box)).isChecked();
        EditText editText = (EditText) findViewById(R.id.edit_reply);
        TextView textView = (TextView) findViewById(R.id.reply_text);
        if(replyOn){
            textView.setTextColor(getResources().getColor(R.color.primary_material_light));
            editText.setEnabled(true);
            //textView.setTextAppearance(R.style.normalText);
            //textView.setTypeface(Typeface.DEFAULT_BOLD);
        }else{
            textView.setTextColor(getResources().getColor(R.color.primary_dark_material_light));
            //textView.setTypeface(Typeface.DEFAULT);
            editText.setEnabled(false);
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    protected void login(View view){

        EditText editTextPassword = (EditText) findViewById(R.id.edit_password);
        this.password = editTextPassword.getText().toString();

        EditText editTextUsername = (EditText) findViewById(R.id.edit_username);
        this.userName = editTextUsername.getText().toString();

        setContentView(R.layout.activity_main);


        //to make keyboard dissapear upon clicking elsewhere
        EditText editText1 = (EditText) findViewById(R.id.edit_match);
        editText1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        EditText editText2 = (EditText) findViewById(R.id.edit_reply);
        editText2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        final EditText editText3 = (EditText) findViewById(R.id.edit_subreddit);
        editText3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        editText3.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                DisplayMessageActivity.subreddit = s.toString();
                //System.out.println(s.toString());
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText editText1 = (EditText) findViewById(R.id.edit_username);
        editText1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        final EditText editText2 = (EditText) findViewById(R.id.edit_password);
        editText2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    public void onBackPressed() {
        finish();
    }
}
