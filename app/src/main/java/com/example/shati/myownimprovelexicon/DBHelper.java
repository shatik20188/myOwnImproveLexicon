package com.example.shati.myownimprovelexicon;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

public class DBHelper {
    final static public String DATABASE_NAME = "db_of_words";
    final static public int DATABASE_VERSION = 1;

    final static public String THEMES_TABLE_NAME = "themes";
    final static public String WORDS_TABLE_NAME = "words";
    final static public String DEGREE_TABLE_NAME = "degree";

    final static public String THEMES_COL_ID = "_id";
    final static public String THEMES_COL_NAME = "name";

    final static public String DEGREE_COL_ID = "_id";
    final static public String DEGREE_COL_NAME = "name";

    final static public String WORDS_COL_ID = "_id";
    final static public String WORDS_COL_WORD = "word";
    final static public String WORDS_COL_TRANSLATE = "translate";
    final static public String WORDS_COL_ID_DEGREE = "id_degree";
    final static public String WORDS_COL_ID_THEME = "id_theme";

    private Context ctx;
    private DBCreate dbCreate;
    private SQLiteDatabase sqLiteDB;

    public DBHelper(Context _ctx) {
        ctx = _ctx;
    }

    public void open(boolean isWrite) {
        dbCreate = new DBCreate();
        if (isWrite) {
            sqLiteDB = dbCreate.getWritableDatabase();
        } else {
            sqLiteDB = dbCreate.getReadableDatabase();
        }
    }

    public void close() {
        if (dbCreate != null) {
            dbCreate.close();
        }
    }

    public SQLiteDatabase getSQLiteDatabase() {
        return sqLiteDB;
    }

    public Cursor getDegreeData() {
        return sqLiteDB.query(DEGREE_TABLE_NAME, null, null, null,
                null, null, null);
    }

    public Cursor getWordsFromDegree(long degreeID) {

        String table = WORDS_TABLE_NAME + " inner join " + THEMES_TABLE_NAME + " on "
                + WORDS_TABLE_NAME + "." + WORDS_COL_ID_THEME + " = "
                + THEMES_TABLE_NAME + "." + THEMES_COL_ID;

        return sqLiteDB.query(table, null,
                WORDS_COL_ID_DEGREE + " = " + degreeID, null, null,
                null, null);
    }

    public Cursor getThemesData() {
        return sqLiteDB.query(THEMES_TABLE_NAME, null, null, null,
                null, null, null);
    }

    public Cursor getWordByWord(String word) {
        return sqLiteDB.query(WORDS_TABLE_NAME, null,
                WORDS_COL_WORD + " = \'" + word + "\'", null, null,
                null, null);
    }

    public void delWordByWord(String word) {
        sqLiteDB.delete(WORDS_TABLE_NAME, WORDS_COL_WORD + " = \'" + word + "\'", null);
    }

    public void delThemeByTheme(String theme) {
        sqLiteDB.delete(THEMES_TABLE_NAME, THEMES_COL_NAME + " = \'" + theme + "\'", null);
    }

    public int getThemeIdByName(String name) {
        Cursor cursor = sqLiteDB.query(THEMES_TABLE_NAME, null,
                THEMES_COL_NAME + " = \'" + name + "\'", null, null,
                null, null);
        cursor.moveToFirst();
        return cursor.getInt(cursor.getColumnIndex(THEMES_COL_ID));
    }

    public int getDegreeIdByName(String name) {
        Cursor cursor = sqLiteDB.query(DEGREE_TABLE_NAME, null,
                DEGREE_COL_NAME + " = \'" + name + "\'", null, null,
                null, null);
        cursor.moveToFirst();
        return cursor.getInt(cursor.getColumnIndex(DEGREE_COL_ID));
    }

    public String getNameById(String TABLE_NAME, long id) {

        String COL_ID, COL_NAME;
        if (TABLE_NAME.equals(THEMES_TABLE_NAME)) {
            COL_ID = THEMES_COL_ID;
            COL_NAME = THEMES_COL_NAME;
        } else {
            COL_ID = DEGREE_COL_ID;
            COL_NAME = DEGREE_COL_NAME;
        }

        Cursor cursor = sqLiteDB.query(TABLE_NAME, null,
                COL_ID + " = \'" + id + "\'", null, null,
                null, null);
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(COL_NAME));
    }

    private class DBCreate extends SQLiteOpenHelper {

        public DBCreate() {
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
