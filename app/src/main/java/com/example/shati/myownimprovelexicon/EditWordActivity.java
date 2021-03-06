package com.example.shati.myownimprovelexicon;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class EditWordActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnEdit;
    Spinner spinDegree, spinTheme;
    EditText editTextWord, editTextTranslate;
    DBHelper dbHelper;
    SimpleCursorAdapter spinAdapter;
    String defaultWord;
    int initialyDegreeSelectedItemPos;
    long idWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_word);

        btnEdit = findViewById(R.id.editW_EditWord);
        btnEdit.setOnClickListener(this);
        spinDegree = findViewById(R.id.editW_SpinDegree);
        spinTheme = findViewById(R.id.editW_SpinTheme);
        editTextTranslate = findViewById(R.id.editW_Translate);
        editTextWord = findViewById(R.id.editW_Word);
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

        defaultWord = getIntent().getStringExtra("word");
        cursor = dbHelper.getWordByWord(defaultWord);
        cursor.moveToFirst();

        idWord = cursor.getLong(cursor.getColumnIndex(DBHelper.WORDS_COL_ID));
        long idDegree = cursor.getLong(cursor.getColumnIndex(DBHelper.WORDS_COL_ID_DEGREE));
        long idTheme = cursor.getLong(cursor.getColumnIndex(DBHelper.WORDS_COL_ID_THEME));

        idWord = cursor.getLong(cursor.getColumnIndex(DBHelper.WORDS_COL_ID));
        editTextWord.setText(cursor.getString(cursor.getColumnIndex(DBHelper.WORDS_COL_WORD)));
        editTextTranslate.setText(cursor.getString(cursor.getColumnIndex(DBHelper.WORDS_COL_TRANSLATE)));
        setSpinnerSelectionById(spinDegree, idDegree);
        setSpinnerSelectionById(spinTheme, idTheme);

        initialyDegreeSelectedItemPos = spinDegree.getSelectedItemPosition();

    }

    private void setSpinnerSelectionById(Spinner spin, long id) {
        for (int i = 0; i < spin.getCount(); i++) {
            if (spin.getItemIdAtPosition(i) == id) {
                spin.setSelection(i);
                break;
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.editW_EditWord:

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
                if (!enteredWord.equals(defaultWord)
                        & !dbHelper.checkIsNewRecord(DBHelper.WORDS_TABLE_NAME, DBHelper.WORDS_COL_WORD, enteredWord)) {

                    Toast.makeText(this, R.string.the_word_is_already_there, Toast.LENGTH_SHORT).show();
                    break;
                }

                String chosenTheme = themesData[spinTheme.getSelectedItemPosition()];
                String chosenDegree = degreeData[spinDegree.getSelectedItemPosition()];

                int themeId = dbHelper.getThemeIdByName(chosenTheme);
                int degreeId = dbHelper.getDegreeIdByName(chosenDegree);

                if(initialyDegreeSelectedItemPos != spinDegree.getSelectedItemPosition())
                    dbHelper.setAmountRightInSuccZeroById(idWord);

                ContentValues contentValues = new ContentValues();
                contentValues.put(DBHelper.WORDS_COL_WORD, enteredWord);
                contentValues.put(DBHelper.WORDS_COL_TRANSLATE, enteredTranslate);
                contentValues.put(DBHelper.WORDS_COL_ID_DEGREE, degreeId);
                contentValues.put(DBHelper.WORDS_COL_ID_THEME, themeId);

                dbHelper.getSQLiteDatabase().update(DBHelper.WORDS_TABLE_NAME,
                        contentValues, DBHelper.WORDS_COL_ID + " = ?",
                        new String[] { String.valueOf(idWord) } );

                finish();
                break;

        }
    }
}
