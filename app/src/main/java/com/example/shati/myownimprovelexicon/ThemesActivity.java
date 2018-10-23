package com.example.shati.myownimprovelexicon;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class ThemesActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int CM_DELETE_ID = 1;

    EditText themeName;
    Button addTheme, back;
    ListView themesListView;
    SimpleCursorAdapter adapter;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_themes);

        themeName = (EditText) findViewById(R.id.themes_EditTheme);

        addTheme = (Button) findViewById(R.id.themes_AddThemes);
        addTheme.setOnClickListener(this);
        back = (Button) findViewById(R.id.themes_Back);
        back.setOnClickListener(this);

        dbHelper = new DBHelper(this);
        dbHelper.open(true);

        Cursor cursor = dbHelper.getThemesData();
        String[] itemsFrom = { DBHelper.THEMES_COL_NAME };
        int[] itemsTo = { android.R.id.text1 };

        themesListView = (ListView) findViewById(R.id.themes_ListView);
        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor,
                itemsFrom, itemsTo, 0);
        themesListView.setAdapter(adapter);

        registerForContextMenu(themesListView);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.themes_AddThemes) {
            if(themeName.getText().toString().isEmpty()) {
                Toast.makeText(this, "Empty field", Toast.LENGTH_LONG).show();
            } else {
                ContentValues cv = new ContentValues();
                cv.put(DBHelper.THEMES_COL_NAME, themeName.getText().toString());
                dbHelper.getSQLiteDatabase().insert(DBHelper.THEMES_TABLE_NAME, null, cv);
                adapter.notifyDataSetChanged();
                ////
                Cursor cursor = dbHelper.getThemesData();
                String[] itemsFrom = { DBHelper.THEMES_COL_NAME };
                int[] itemsTo = { android.R.id.text1 };

                themesListView = (ListView) findViewById(R.id.themes_ListView);
                adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor,
                        itemsFrom, itemsTo, 0);
                themesListView.setAdapter(adapter);
                /////
                themeName.setText("");
            }
        } else if (view.getId() == R.id.themes_Back) {
            finish();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, CM_DELETE_ID, Menu.NONE, "Delete theme");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE_ID) {
            AdapterView.AdapterContextMenuInfo acmi =
                    (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            String theme = ((AppCompatTextView)acmi.targetView.findViewById(android.R.id.text1)).getText().toString();
            dbHelper.delThemeByTheme(theme);

            adapter.notifyDataSetChanged();
            ////
            Cursor cursor = dbHelper.getThemesData();
            String[] itemsFrom = { DBHelper.THEMES_COL_NAME };
            int[] itemsTo = { android.R.id.text1 };

            themesListView = (ListView) findViewById(R.id.themes_ListView);
            adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor,
                    itemsFrom, itemsTo, 0);
            themesListView.setAdapter(adapter);
            /////


        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }

}
