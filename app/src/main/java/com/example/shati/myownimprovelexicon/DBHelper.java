package com.example.shati.myownimprovelexicon;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DBHelper {
    private final static String DATABASE_NAME = "db_of_words";
    private final static int DATABASE_VERSION = 1;

    final static String THEMES_TABLE_NAME = "themes";
    final static String WORDS_TABLE_NAME = "words";
    private final static String DEGREE_TABLE_NAME = "degree";

    private final static String THEMES_COL_ID = "_id";
    final static String THEMES_COL_NAME = "name";

    final static String DEGREE_COL_ID = "_id";
    final static String DEGREE_COL_NAME = "name";

    final static String WORDS_COL_ID = "_id";
    final static String WORDS_COL_WORD = "word";
    final static String WORDS_COL_TRANSLATE = "translate";
    final static String WORDS_COL_ID_DEGREE = "id_degree";
    final static String WORDS_COL_ID_THEME = "id_theme";
    final static String WORDS_COL_AMOUNT_RIGHT = "amountRight";
    final static String WORDS_COL_AMOUNT_WRONG = "amountWrong";
    final static String WORDS_COL_RIGHT_IN_SUCCESSION = "amountRightInSuccession";

    private Context ctx;
    private DBCreate dbCreate;
    private SQLiteDatabase sqLiteDB;

    DBHelper(Context _ctx) {
        ctx = _ctx;
    }

    void open() {
        dbCreate = new DBCreate();
        sqLiteDB = dbCreate.getWritableDatabase();
    }

    public void close() {
        if (dbCreate != null) {
            dbCreate.close();
        }
    }

    SQLiteDatabase getSQLiteDatabase() {
        return sqLiteDB;
    }

    Cursor getDegreeData() {
        return sqLiteDB.query(DEGREE_TABLE_NAME, null, null, null,
                null, null, null);
    }

    Cursor getWordsFromDegree(long degreeID, long themeId) {

        String sel;
        if (themeId == -10) {
            sel = "";
        } else {
            sel = " AND (" + WORDS_COL_ID_THEME + " = " + themeId + ")";
        }

        String table = WORDS_TABLE_NAME + " inner join " + THEMES_TABLE_NAME + " on "
                + WORDS_TABLE_NAME + "." + WORDS_COL_ID_THEME + " = "
                + THEMES_TABLE_NAME + "." + THEMES_COL_ID;

        return sqLiteDB.query(table, null, "(" +
                WORDS_COL_ID_DEGREE + " = " + degreeID + ")" + sel, null, null,
                null, WORDS_COL_WORD);
    }

    Cursor getThemesData() {
        return sqLiteDB.query(THEMES_TABLE_NAME, null, null, null,
                null, null, null);
    }

    Cursor getWordByWord(String word) {
        return sqLiteDB.query(WORDS_TABLE_NAME, null,
                WORDS_COL_WORD + " = \'" + word + "\'", null, null,
                null, null);
    }

    Cursor getWordsByFilter(String[] degrees, String[] themes) {
        String degreesIdSelect = "";
        for (String degree : degrees) {
            degreesIdSelect = degreesIdSelect.concat( String.valueOf(getDegreeIdByName(degree)) + "," );
        }

        String themesIdSelect = "";
        for (String theme : themes) {
            themesIdSelect = themesIdSelect.concat( String.valueOf(getThemeIdByName(theme)) + "," );
        }

        if (degreesIdSelect.equals("") | themesIdSelect.equals("")) return null;

        degreesIdSelect = degreesIdSelect.substring(0, degreesIdSelect.length()-1);
        themesIdSelect = themesIdSelect.substring(0, themesIdSelect.length()-1);

        String selection = "(" + WORDS_COL_ID_DEGREE + " IN (" + degreesIdSelect + ")) AND ("
                + WORDS_COL_ID_THEME + " IN (" + themesIdSelect + "))";

        return sqLiteDB.query(WORDS_TABLE_NAME, null,
                selection, null, null,
                null, null);
    }

    void delWordByWord(String word) {
        sqLiteDB.delete(WORDS_TABLE_NAME, WORDS_COL_WORD + " = \'" + word + "\'", null);
    }

    void delThemeByTheme(String theme) {
        sqLiteDB.delete(THEMES_TABLE_NAME, THEMES_COL_NAME + " = \'" + theme + "\'", null);
    }

    void delAllWordsOfThemeByTheme(String theme) {
        int themeId = getThemeIdByName(theme);
        sqLiteDB.delete(WORDS_TABLE_NAME, WORDS_COL_ID_THEME + " = \'" + themeId + "\'", null);
    }

    int getThemeIdByName(String name) {
        @SuppressLint("Recycle") Cursor cursor = sqLiteDB.query(THEMES_TABLE_NAME, null,
                THEMES_COL_NAME + " = \'" + name + "\'", null, null,
                null, null);
        cursor.moveToFirst();
        return cursor.getInt(cursor.getColumnIndex(THEMES_COL_ID));
    }

    int getDegreeIdByName(String name) {
        @SuppressLint("Recycle") Cursor cursor = sqLiteDB.query(DEGREE_TABLE_NAME, null,
                DEGREE_COL_NAME + " = \'" + name + "\'", null, null,
                null, null);
        cursor.moveToFirst();
        return cursor.getInt(cursor.getColumnIndex(DEGREE_COL_ID));
    }

    int getCountWordsByFilter(String[] degrees, String[] themes) {

        String degreesIdSelect = "";
        for (String degree : degrees) {
            degreesIdSelect = degreesIdSelect.concat( String.valueOf(getDegreeIdByName(degree)) + "," );
        }

        String themesIdSelect = "";
        for (String theme : themes) {
            themesIdSelect = themesIdSelect.concat( String.valueOf(getThemeIdByName(theme)) + "," );
        }

        if (degreesIdSelect.equals("") | themesIdSelect.equals("")) return 0;

        degreesIdSelect = degreesIdSelect.substring(0, degreesIdSelect.length()-1);
        themesIdSelect = themesIdSelect.substring(0, themesIdSelect.length()-1);

        String selection = "(" + WORDS_COL_ID_DEGREE + " IN (" + degreesIdSelect + ")) AND ("
                + WORDS_COL_ID_THEME + " IN (" + themesIdSelect + "))";

        @SuppressLint("Recycle") Cursor cursor = sqLiteDB.query(WORDS_TABLE_NAME, null,
                selection, null, null,
                null, null);

        return cursor.getCount();
    }

    int getWordIdByWord(String word) {
        @SuppressLint("Recycle") Cursor cursor = sqLiteDB.query(WORDS_TABLE_NAME, null,
                WORDS_COL_WORD + " = \'" + word + "\'", null, null,
                null, null);
        cursor.moveToFirst();
        return cursor.getInt(cursor.getColumnIndex(WORDS_COL_ID));
    }

    String getThemeNameById(int id) {
        @SuppressLint("Recycle") Cursor cursor = sqLiteDB.query(THEMES_TABLE_NAME, null,
                THEMES_COL_ID + " = " + String.valueOf(id), null, null,
                null, null);
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(THEMES_COL_NAME));
    }

    String getDegreeNameById(int id) {
        @SuppressLint("Recycle") Cursor cursor = sqLiteDB.query(DEGREE_TABLE_NAME, null,
                DEGREE_COL_ID + " = " + String.valueOf(id), null, null,
                null, null);
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(DEGREE_COL_NAME));
    }

    int getAmountTranslById(int id, boolean isRight) {
        @SuppressLint("Recycle") Cursor cursor = sqLiteDB.query(WORDS_TABLE_NAME, null,
                WORDS_COL_ID + " = " + String.valueOf(id), null, null,
                null, null);
        cursor.moveToFirst();
        return cursor.getInt( cursor.getColumnIndex(
                isRight ? WORDS_COL_AMOUNT_RIGHT : WORDS_COL_AMOUNT_WRONG ) );
    }

    int getAmountRightInSuccById(int id) {
        @SuppressLint("Recycle") Cursor cursor = sqLiteDB.query(WORDS_TABLE_NAME, null,
                WORDS_COL_ID + " = " + String.valueOf(id), null, null,
                null, null);
        cursor.moveToFirst();
        return cursor.getInt( cursor.getColumnIndex(WORDS_COL_RIGHT_IN_SUCCESSION) );
    }

    void incAmountTranslById(int id, boolean isRight) {

        ContentValues cv = new ContentValues();
        cv.put(isRight ? WORDS_COL_AMOUNT_RIGHT : WORDS_COL_AMOUNT_WRONG, getAmountTranslById(id, isRight) + 1);

        getSQLiteDatabase().update(DBHelper.WORDS_TABLE_NAME,
                cv, WORDS_COL_ID + " = ?",
                new String[] { String.valueOf(id) } );

    }

    void incAmountRightInSuccById(int id, boolean isRight, int maxAmount, boolean isActiveMax) {

        int val = getAmountRightInSuccById(id);
        ContentValues cv = new ContentValues();
        if (isRight) {
            val++;
            Cursor cursor = sqLiteDB.query(WORDS_TABLE_NAME, null,
                    WORDS_COL_ID + " = " + String.valueOf(id), null, null,
                    null, null);
            cursor.moveToFirst();
            int idDegreeOfWord = cursor.getInt( cursor.getColumnIndex(WORDS_COL_ID_DEGREE));
            if(val >= maxAmount && idDegreeOfWord != 1 && isActiveMax) {
                val = 0;
                cv.put(WORDS_COL_ID_DEGREE, idDegreeOfWord - 1);
            }
        } else {
            val = 0;
        }
        cv.put(WORDS_COL_RIGHT_IN_SUCCESSION, val);
        getSQLiteDatabase().update(DBHelper.WORDS_TABLE_NAME,
                cv, WORDS_COL_ID + " = ?",
                new String[] { String.valueOf(id) } );

    }

    void setAmountRightInSuccZeroById(long id) {
        ContentValues cv = new ContentValues();
        cv.put(WORDS_COL_RIGHT_IN_SUCCESSION, 0);
        getSQLiteDatabase().update(DBHelper.WORDS_TABLE_NAME,
                cv, WORDS_COL_ID + " = ?",
                new String[] { String.valueOf(id) } );
    }

    void replaceAllWordsToNoSubjByTheme(String _theme) {

        ContentValues cv = new ContentValues();
        cv.put(WORDS_COL_ID_THEME, 1);
        getSQLiteDatabase().update(WORDS_TABLE_NAME, cv, WORDS_COL_ID_THEME + " = ?",
                new String[] { String.valueOf( getThemeIdByName(_theme) ) } );

    }

    boolean checkIsNewRecord(String table, String col, String word) {
        @SuppressLint("Recycle") Cursor cursor = sqLiteDB.query(table, null, col + " = \'" + word + "\'",
                null, null,
                null, null);
        return !cursor.moveToFirst();
    }

    private class DBCreate extends SQLiteOpenHelper {

        DBCreate() {
            super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {

            sqLiteDatabase.execSQL( "create table " + DEGREE_TABLE_NAME + "("
                    + DEGREE_COL_ID + " integer primary key autoincrement, "
                    + DEGREE_COL_NAME + " text" + ")" );

            sqLiteDatabase.execSQL( "create table " + THEMES_TABLE_NAME + "("
                    + THEMES_COL_ID + " integer primary key autoincrement, "
                    + THEMES_COL_NAME + " text" + ")" );

            sqLiteDatabase.execSQL( "create table " + WORDS_TABLE_NAME + "("
                    + WORDS_COL_ID + " integer primary key autoincrement, "
                    + WORDS_COL_WORD + " text, "
                    + WORDS_COL_TRANSLATE + " text, "
                    + WORDS_COL_ID_DEGREE + " integer,"
                    + WORDS_COL_ID_THEME + " integer,"
                    + WORDS_COL_AMOUNT_RIGHT + " integer,"
                    + WORDS_COL_AMOUNT_WRONG + " integer,"
                    + WORDS_COL_RIGHT_IN_SUCCESSION + " integer" + ")" );

            ContentValues contentValues = new ContentValues();
            String[] degree_val = ctx.getResources().getStringArray(R.array.degree_remembered);
            contentValues.clear();

            for (String val : degree_val) {
                contentValues.put(DEGREE_COL_NAME, val);
                sqLiteDatabase.insert(DEGREE_TABLE_NAME, null, contentValues);
            }

            contentValues.clear();
            /////test block
            contentValues.put(WORDS_COL_WORD, "test word 1");
            contentValues.put(WORDS_COL_TRANSLATE, "тестовое слово 1");
            contentValues.put(WORDS_COL_ID_DEGREE, 1);
            contentValues.put(WORDS_COL_ID_THEME, 1);
            contentValues.put(WORDS_COL_AMOUNT_RIGHT, 0);
            contentValues.put(WORDS_COL_AMOUNT_WRONG, 0);
            contentValues.put(WORDS_COL_RIGHT_IN_SUCCESSION, 0);
            sqLiteDatabase.insert(WORDS_TABLE_NAME, null, contentValues);
            contentValues.put(WORDS_COL_WORD, "test word 2");
            contentValues.put(WORDS_COL_TRANSLATE, "тестовое слово 2");
            contentValues.put(WORDS_COL_ID_DEGREE, 2);
            contentValues.put(WORDS_COL_ID_THEME, 1);
            contentValues.put(WORDS_COL_AMOUNT_RIGHT, 0);
            contentValues.put(WORDS_COL_AMOUNT_WRONG, 0);
            contentValues.put(WORDS_COL_RIGHT_IN_SUCCESSION, 0);
            sqLiteDatabase.insert(WORDS_TABLE_NAME, null, contentValues);
            contentValues.put(WORDS_COL_WORD, "test word 3");
            contentValues.put(WORDS_COL_TRANSLATE, "тестовое слово 3");
            contentValues.put(WORDS_COL_ID_DEGREE, 3);
            contentValues.put(WORDS_COL_ID_THEME, 1);
            contentValues.put(WORDS_COL_AMOUNT_RIGHT, 0);
            contentValues.put(WORDS_COL_AMOUNT_WRONG, 0);
            contentValues.put(WORDS_COL_RIGHT_IN_SUCCESSION, 0);
            sqLiteDatabase.insert(WORDS_TABLE_NAME, null, contentValues);
            //////test block

            contentValues.clear();

            contentValues.put(THEMES_COL_NAME, ctx.getResources().getString(R.string.no_subject));
            sqLiteDatabase.insert(THEMES_TABLE_NAME, null, contentValues);
            contentValues.clear();
        }


        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }
}
