package com.example.shati.myownimprovelexicon;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class AddNewWordActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnBack, btnAddWord;
    Spinner spinDegree, spinTheme;
    EditText editTextWord, editTextTranslate;
    DBHelper dbHelper;
    SimpleCursorAdapter spinAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_word);

        btnBack = findViewById(R.id.addNW_Back);
        btnBack.setOnClickListener(this);
        btnAddWord =  findViewById(R.id.addNW_addWord);
        btnAddWord.setOnClickListener(this);

        spinDegree = findViewById(R.id.addNW_SpinDegree);
        spinTheme = findViewById(R.id.addNW_SpinTheme);

        editTextTranslate = findViewById(R.id.addNW_Translate);
        editTextWord = findViewById(R.id.addNW_Word);

        dbHelper = new DBHelper(this);
        dbHelper.open();

        String[] degreeFrom = { DBHelper.DEGREE_COL_NAME };
        int[] degreeTo = { android.R.id.text1 };

        String[] themesFrom = { DBHelper.THEMES_COL_NAME };
        int[] themesTo = { android.R.id.text1 };

        Cursor cursor = dbHelper.getDegreeData();
        spinAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item,
                cursor, degreeFrom, degreeTo, 2);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinDegree.setAdapter(spinAdapter);

        cursor = dbHelper.getThemesData();
        spinAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item,
                cursor, themesFrom, themesTo, 0);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinTheme.setAdapter(spinAdapter);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.addNW_Back:
                Log.d("logm","back");
                finish();
                break;

            case R.id.addNW_addWord:
                Cursor cursor = dbHelper.getDegreeData();
                cursor.moveToFirst();
                String[] degreeData = new String[cursor.getCount()];
                for (int i=0; i < degreeData.length; i++, cursor.moveToNext()) {
                    degreeData[i] = cursor.getString(cursor.getColumnIndex(DBHelper.DEGREE_COL_NAME));
                }

                cursor = dbHelper.getThemesData();
                cursor.moveToFirst();
                String[] themesData = new String[cursor.getCount()];
                for (int i=0; i < themesData.length; i++, cursor.moveToNext()) {
                    themesData[i] = cursor.getString(cursor.getColumnIndex(DBHelper.THEMES_COL_NAME));
                }

                if (MainActivity.isEmptyEditText(editTextWord) |
                        MainActivity.isEmptyEditText(editTextTranslate)) {
                    Toast.makeText(this, R.string.not_all_fields_are_filled, Toast.LENGTH_SHORT).show();
                    break;
                }
                MainActivity.delSpacesEditText(editTextWord);
                MainActivity.delSpacesEditText(editTextTranslate);
                String enteredWord = editTextWord.getText().toString();
                String enteredTranslate = editTextTranslate.getText().toString();
                if (!dbHelper.checkIsNewRecord(DBHelper.WORDS_TABLE_NAME, DBHelper.WORDS_COL_WORD, enteredWord)) {
                    Toast.makeText(this, R.string.the_word_is_already_there, Toast.LENGTH_SHORT).show();
                    break;
                }

                String chosenTheme = themesData[spinTheme.getSelectedItemPosition()];
                String chosenDegree = degreeData[spinDegree.getSelectedItemPosition()];

                int themeId = dbHelper.getThemeIdByName(chosenTheme);
                int degreeId = dbHelper.getDegreeIdByName(chosenDegree);

                ContentValues contentValues = new ContentValues();
                contentValues.put(DBHelper.WORDS_COL_WORD, enteredWord);
                contentValues.put(DBHelper.WORDS_COL_TRANSLATE, enteredTranslate);
                contentValues.put(DBHelper.WORDS_COL_ID_DEGREE, degreeId);
                contentValues.put(DBHelper.WORDS_COL_ID_THEME, themeId);

                dbHelper.getSQLiteDatabase().insert(DBHelper.WORDS_TABLE_NAME,
                        null, contentValues);

                finish();
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}
