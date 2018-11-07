package com.example.shati.myownimprovelexicon;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
                    + WORDS_COL_ID_THEME + " integer" + ")" );

            ContentValues contentValues = new ContentValues();
            String[] degree_val = ctx.getResources().getStringArray(R.array.degree_remembered);
            contentValues.clear();

            for (String val : degree_val) {
                contentValues.put(DEGREE_COL_NAME, val);
                sqLiteDatabase.insert(DEGREE_TABLE_NAME, null, contentValues);
            }

            contentValues.clear();
            /////test block
            contentValues.put(WORDS_COL_WORD, "apple");
            contentValues.put(WORDS_COL_TRANSLATE, "яблоко");
            contentValues.put(WORDS_COL_ID_DEGREE, 1);
            contentValues.put(WORDS_COL_ID_THEME, 1);
            sqLiteDatabase.insert(WORDS_TABLE_NAME, null, contentValues);
            contentValues.put(WORDS_COL_WORD, "car");
            contentValues.put(WORDS_COL_TRANSLATE, "тачка");
            contentValues.put(WORDS_COL_ID_DEGREE, 2);
            contentValues.put(WORDS_COL_ID_THEME, 1);
            sqLiteDatabase.insert(WORDS_TABLE_NAME, null, contentValues);
            contentValues.put(WORDS_COL_WORD, "man");
            contentValues.put(WORDS_COL_TRANSLATE, "мужчина");
            contentValues.put(WORDS_COL_ID_DEGREE, 3);
            contentValues.put(WORDS_COL_ID_THEME, 1);
            sqLiteDatabase.insert(WORDS_TABLE_NAME, null, contentValues);
            contentValues.put(WORDS_COL_WORD, "woman");
            contentValues.put(WORDS_COL_TRANSLATE, "женщина");
            contentValues.put(WORDS_COL_ID_DEGREE, 3);
            contentValues.put(WORDS_COL_ID_THEME, 1);
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
