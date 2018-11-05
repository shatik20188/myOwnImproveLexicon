package com.example.shati.myownimprovelexicon;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LearnActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnNext, btnEdit, btnShowTranslate;
    TextView textCount, textWord, textTranslate, textDegree, textTheme;
    DBHelper dbHelper;
    AdapterHelperForListView adapterHelper;

    private int indexRandomWord;
    private int mode;

    private final static String KEY_WORD = "keyWord";
    private final static String KEY_TRANSLATE = "keyTranslate";
    private final static String KEY_DEGREE = "keyDegree";
    private final static String KEY_THEME = "keyTheme";

    private final static int INDEX_WORD = 0;
    private final static int INDEX_TRANSLATE = 1;
    private final static int INDEX_DEGREE = 2;
    private final static int INDEX_THEME = 3;

    private ArrayList<ArrayList<Map<String, String>>> wordAllInfo;

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
        dbHelper.open(true);

        btnEdit = (Button) findViewById(R.id.learn_BtnEditWord);
        btnEdit.setOnClickListener(this);
        btnNext = (Button) findViewById(R.id.learn_BtnNextWord);
        btnNext.setOnClickListener(this);
        btnShowTranslate = (Button) findViewById(R.id.learn_BtnShowTranslate);
        btnShowTranslate.setOnClickListener(this);
        textCount = (TextView) findViewById(R.id.learn_CountWords);
        textTheme = (TextView) findViewById(R.id.learn_Theme);
        textDegree = (TextView) findViewById(R.id.learn_Degree);
        textTranslate = (TextView) findViewById(R.id.learn_Translate);
        textWord = (TextView) findViewById(R.id.learn_Word);

        wordAllInfo = new ArrayList<>();
        ArrayList<Map<String, String>> wordOneInfo = new ArrayList<>();
        Map<String, String> wordWordInfo = new HashMap<>();
        Map<String, String> wordTranslateInfo = new HashMap<>();
        Map<String, String> wordThemeInfo = new HashMap<>();
        Map<String, String> wordDegreeInfo = new HashMap<>();

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

            wordWordInfo.put(KEY_WORD, cursor.getString(cursor.getColumnIndex(DBHelper.WORDS_COL_WORD)));
            wordTranslateInfo.put(KEY_TRANSLATE, cursor.getString(cursor.getColumnIndex(DBHelper.WORDS_COL_TRANSLATE)));
            String degree = dbHelper.getDegreeNameById(cursor.getInt(cursor.getColumnIndex(DBHelper.WORDS_COL_ID_DEGREE)));
            wordDegreeInfo.put(KEY_DEGREE, degree);
            String theme = dbHelper.getThemeNameById(cursor.getInt(cursor.getColumnIndex(DBHelper.WORDS_COL_ID_THEME)));
            wordThemeInfo.put(KEY_THEME, theme);

            wordOneInfo = new ArrayList<>();
            wordOneInfo.add(INDEX_WORD, wordWordInfo);
            wordOneInfo.add(INDEX_TRANSLATE, wordTranslateInfo);
            wordOneInfo.add(INDEX_DEGREE, wordDegreeInfo);
            wordOneInfo.add(INDEX_THEME, wordThemeInfo);

            wordAllInfo.add(wordOneInfo);

        } while(cursor.moveToNext());

        nextWord();
    }

    private void nextWord() {
        textTranslate.setVisibility(TextView.INVISIBLE);
        textTheme.setVisibility(TextView.INVISIBLE);
        textDegree.setVisibility(TextView.INVISIBLE);
        btnShowTranslate.setClickable(true);
        btnNext.setClickable(false);
        btnEdit.setClickable(false);

        wordAllInfo.trimToSize();
        textCount.setText(String.valueOf(wordAllInfo.size()));

        indexRandomWord = (int) (Math.random() * wordAllInfo.size());

        if (mode == PresettingLearnFragment.MODE_WORD_TRANSLATE) {
            textWord.setText( wordAllInfo.get(indexRandomWord).get(INDEX_WORD).get(KEY_WORD) );
            textTranslate.setText( wordAllInfo.get(indexRandomWord).get(INDEX_TRANSLATE).get(KEY_TRANSLATE) );
        } else {
            textWord.setText( wordAllInfo.get(indexRandomWord).get(INDEX_TRANSLATE).get(KEY_TRANSLATE) );
            textTranslate.setText( wordAllInfo.get(indexRandomWord).get(INDEX_WORD).get(KEY_WORD) );
        }
        textDegree.setText( wordAllInfo.get(indexRandomWord).get(INDEX_DEGREE).get(KEY_DEGREE) );
        textTheme.setText( wordAllInfo.get(indexRandomWord).get(INDEX_THEME).get(KEY_THEME) );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.learn_BtnShowTranslate:
                btnShowTranslate.setClickable(false);
                btnEdit.setClickable(true);
                btnNext.setClickable(true);
                textTranslate.setVisibility(TextView.VISIBLE);
                textTheme.setVisibility(TextView.VISIBLE);
                textDegree.setVisibility(TextView.VISIBLE);
                break;
            case R.id.learn_BtnEditWord:
                Intent intent = new Intent(this, EditWordActivity.class);
                intent.putExtra("word",
                        (mode == PresettingLearnFragment.MODE_WORD_TRANSLATE) ? textWord.getText().toString()
                                : textTranslate.getText().toString());
                startActivity(intent);
                Log.d("logm", "resume");
            case R.id.learn_BtnNextWord:
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
