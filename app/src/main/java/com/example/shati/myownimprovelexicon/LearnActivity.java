package com.example.shati.myownimprovelexicon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LearnActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnShowTranslate, btnEdit, btnNextWordTrue, btnNextWordFalse;
    TextView textCount, textWord, textTranslate, textDegree, textTheme, textAmountRightTransl
            ,textAmountWrongTransl, textRightInSuccesion;
    DBHelper dbHelper;
    AdapterHelperForListView adapterHelper;
    LinearLayout linlayNext;

    SharedPreferences sharedPref;
    int maxAmountRightInSucc;
    boolean isActiveMax;

    private int indexRandomWord;
    private int mode;

    final static String KEY_WORD = "keyWord";
    final static String KEY_TRANSLATE = "keyTranslate";
    final static String KEY_DEGREE = "keyDegree";
    final static String KEY_THEME = "keyTheme";
    final static String KEY_RIGHT_TR = "keyRightTransl";
    final static String KEY_WRONG_TR = "keyWrongTransl";
    final static String KEY_IN_SUCC = "keyInSucc";

    final static int INDEX_WORD = 0;
    final static int INDEX_TRANSLATE = 1;
    final static int INDEX_DEGREE = 2;
    final static int INDEX_THEME = 3;
    final static int INDEX_RIGHT_TR = 4;
    final static int INDEX_WRONG_TR = 5;
    final static int INDEX_IN_SUCC = 6;

    private ArrayList<ArrayList<Map<String, String>>> wordAllInfo;
    private int idOfTranslateWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        Intent intent = getIntent();
        ArrayList<String> degreeList = intent.getStringArrayListExtra("degrees");
        ArrayList<String> themesList = intent.getStringArrayListExtra("themes");
        mode = intent.getIntExtra("mode", 0);

        adapterHelper = new AdapterHelperForListView(this);
        dbHelper = adapterHelper.dbHelper;
        dbHelper.open();

        linlayNext = findViewById(R.id.learn_LinLayBtn);
        btnEdit = findViewById(R.id.learn_BtnEditWord);
        btnEdit.setOnClickListener(this);
        btnShowTranslate = findViewById(R.id.learn_BtnShowTranslate);
        btnShowTranslate.setOnClickListener(this);
        btnNextWordTrue = findViewById(R.id.learn_BtnNextWordTrue);
        btnNextWordTrue.setOnClickListener(this);
        btnNextWordFalse = findViewById(R.id.learn_BtnNextWordFalse);
        btnNextWordFalse.setOnClickListener(this);
        textCount = findViewById(R.id.learn_CountWords);
        textTheme = findViewById(R.id.learn_Theme);
        textDegree = findViewById(R.id.learn_Degree);
        textTranslate = findViewById(R.id.learn_Translate);
        textWord = findViewById(R.id.learn_Word);
        textAmountRightTransl = findViewById(R.id.learn_AmountRightTransl);
        textAmountWrongTransl = findViewById(R.id.learn_AmountWrongTransl);
        textRightInSuccesion = findViewById(R.id.learn_RightInSuccession);

        wordAllInfo = new ArrayList<>();
        ArrayList<Map<String, String>> wordOneInfo;
        Map<String, String> wordWordInfo;
        Map<String, String> wordTranslateInfo;
        Map<String, String> wordThemeInfo;
        Map<String, String> wordDegreeInfo;
        Map<String, String> wordRightTrInfo;
        Map<String, String> wordWrongTrInfo;
        Map<String, String> wordInSuccInfo;

        String[] degreeStrArray = new String[degreeList.size()];
        for (int i = 0; i < degreeList.size(); i++) {
            degreeStrArray[i] = degreeList.get(i);
        }

        String[] themesStrArray = new String[themesList.size()];
        for (int i = 0; i < themesList.size(); i++) {
            themesStrArray[i] = themesList.get(i);
        }

        Cursor cursor = dbHelper.getWordsByFilter(degreeStrArray, themesStrArray);
        cursor.moveToFirst();

        do {
            wordWordInfo = new HashMap<>();
            wordTranslateInfo = new HashMap<>();
            wordThemeInfo = new HashMap<>();
            wordDegreeInfo = new HashMap<>();
            wordRightTrInfo = new HashMap<>();
            wordWrongTrInfo = new HashMap<>();
            wordInSuccInfo = new HashMap<>();

            wordWordInfo.put(KEY_WORD, cursor.getString(cursor.getColumnIndex(DBHelper.WORDS_COL_WORD)));
            wordTranslateInfo.put(KEY_TRANSLATE, cursor.getString(cursor.getColumnIndex(DBHelper.WORDS_COL_TRANSLATE)));
            String degree = dbHelper.getDegreeNameById(cursor.getInt(cursor.getColumnIndex(DBHelper.WORDS_COL_ID_DEGREE)));
            wordDegreeInfo.put(KEY_DEGREE, degree);
            String theme = dbHelper.getThemeNameById(cursor.getInt(cursor.getColumnIndex(DBHelper.WORDS_COL_ID_THEME)));
            wordThemeInfo.put(KEY_THEME, theme);
            idOfTranslateWord = cursor.getInt(cursor.getColumnIndex(DBHelper.WORDS_COL_ID));
            String rightTr = String.valueOf( dbHelper.getAmountTranslById(idOfTranslateWord, true) );
            wordRightTrInfo.put(KEY_RIGHT_TR, rightTr);
            String wrongTr = String.valueOf( dbHelper.getAmountTranslById(idOfTranslateWord, false)) ;
            wordWrongTrInfo.put(KEY_WRONG_TR, wrongTr);
            String rightInSucc = String.valueOf( dbHelper.getAmountRightInSuccById(cursor.getInt(cursor.getColumnIndex(DBHelper.WORDS_COL_ID))) );
            wordInSuccInfo.put(KEY_IN_SUCC, rightInSucc);

            wordOneInfo = new ArrayList<>();
            wordOneInfo.add(INDEX_WORD, wordWordInfo);
            wordOneInfo.add(INDEX_TRANSLATE, wordTranslateInfo);
            wordOneInfo.add(INDEX_DEGREE, wordDegreeInfo);
            wordOneInfo.add(INDEX_THEME, wordThemeInfo);
            wordOneInfo.add(INDEX_RIGHT_TR, wordRightTrInfo);
            wordOneInfo.add(INDEX_WRONG_TR, wordWrongTrInfo);
            wordOneInfo.add(INDEX_IN_SUCC, wordInSuccInfo);

            wordAllInfo.add(wordOneInfo);

        } while(cursor.moveToNext());

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActiveMax = sharedPref.getBoolean("isActiveMaxInSucc", false);
        maxAmountRightInSucc = sharedPref.getInt("maxAmountInSucc", 3);
        nextWord();
    }

    private void nextWord() {
        textTranslate.setVisibility(TextView.INVISIBLE);
        textTheme.setVisibility(TextView.INVISIBLE);
        textDegree.setVisibility(TextView.INVISIBLE);
        textAmountRightTransl.setVisibility(TextView.INVISIBLE);
        textAmountWrongTransl.setVisibility(TextView.INVISIBLE);
        textRightInSuccesion.setVisibility(TextView.INVISIBLE);
        btnEdit.setVisibility(Button.GONE);
        btnNextWordTrue.setVisibility(Button.GONE);
        btnNextWordFalse.setVisibility(Button.GONE);
        btnShowTranslate.setVisibility(Button.VISIBLE);

        wordAllInfo.trimToSize();
        textCount.setText(String.valueOf(wordAllInfo.size()));

        indexRandomWord = (int) (Math.random() * wordAllInfo.size());

        if (mode == PresettingLearnFragment.MODE_WORD_TRANSLATE) {
            textWord.setText( wordAllInfo.get(indexRandomWord).get(INDEX_WORD).get(KEY_WORD) );
            textTranslate.setText( wordAllInfo.get(indexRandomWord).get(INDEX_TRANSLATE).get(KEY_TRANSLATE) );
            idOfTranslateWord = dbHelper.getWordIdByWord(textWord.getText().toString());
        } else {
            textWord.setText( wordAllInfo.get(indexRandomWord).get(INDEX_TRANSLATE).get(KEY_TRANSLATE) );
            textTranslate.setText( wordAllInfo.get(indexRandomWord).get(INDEX_WORD).get(KEY_WORD) );
            idOfTranslateWord = dbHelper.getWordIdByWord(textTranslate.getText().toString());
        }
        textDegree.setText( wordAllInfo.get(indexRandomWord).get(INDEX_DEGREE).get(KEY_DEGREE) );
        textTheme.setText( wordAllInfo.get(indexRandomWord).get(INDEX_THEME).get(KEY_THEME) );

        String strForOutput = "";
        strForOutput = getResources().getString(R.string.right_translate) + wordAllInfo.get(indexRandomWord).get(INDEX_RIGHT_TR).get(KEY_RIGHT_TR);
        textAmountRightTransl.setText( strForOutput );
        strForOutput = getResources().getString(R.string.wrong_translate) + wordAllInfo.get(indexRandomWord).get(INDEX_WRONG_TR).get(KEY_WRONG_TR);
        textAmountWrongTransl.setText( strForOutput );
        strForOutput = getResources().getString(R.string.right_in_succ) + wordAllInfo.get(indexRandomWord).get(INDEX_IN_SUCC).get(KEY_IN_SUCC) +
                (isActiveMax ? ("/" + maxAmountRightInSucc) : "");
        textRightInSuccesion.setText( strForOutput );

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.learn_BtnShowTranslate:
                textTranslate.setVisibility(TextView.VISIBLE);
                textTheme.setVisibility(TextView.VISIBLE);
                textDegree.setVisibility(TextView.VISIBLE);
                textAmountRightTransl.setVisibility(TextView.VISIBLE);
                textAmountWrongTransl.setVisibility(TextView.VISIBLE);
                textRightInSuccesion.setVisibility(TextView.VISIBLE);
                linlayNext.setVisibility(LinearLayout.VISIBLE);
                btnNextWordTrue.setVisibility(Button.VISIBLE);
                btnNextWordFalse.setVisibility(Button.VISIBLE);
                btnShowTranslate.setVisibility(Button.GONE);
                btnEdit.setVisibility(Button.VISIBLE);
                break;
            case R.id.learn_BtnEditWord:
                Intent intent = new Intent(this, EditWordActivity.class);
                intent.putExtra("word",
                        (mode == PresettingLearnFragment.MODE_WORD_TRANSLATE) ? textWord.getText().toString()
                                : textTranslate.getText().toString());
                startActivity(intent);
                break;
            case R.id.learn_BtnNextWordTrue:
                dbHelper.incAmountTranslById(idOfTranslateWord, true);
                dbHelper.incAmountRightInSuccById(idOfTranslateWord, true, maxAmountRightInSucc, isActiveMax);

                if (wordAllInfo.size() == 1) {
                    finish();
                } else {
                    wordAllInfo.remove(indexRandomWord);
                    nextWord();
                }
                break;
            case R.id.learn_BtnNextWordFalse:
                dbHelper.incAmountTranslById(idOfTranslateWord, false);
                dbHelper.incAmountRightInSuccById(idOfTranslateWord, false, maxAmountRightInSucc, isActiveMax);

                if (wordAllInfo.size() == 1) {
                    finish();
                } else {
                    wordAllInfo.remove(indexRandomWord);
                    nextWord();
                }
                break;
        }
    }

}
