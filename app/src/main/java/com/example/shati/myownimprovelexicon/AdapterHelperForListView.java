package com.example.shati.myownimprovelexicon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorTreeAdapter;



public class AdapterHelperForListView {

    DBHelper dbHelper;
    private AdapterForMainListView adapter;
    private Context ctx;
    private long filterIdTheme;

    AdapterHelperForListView(Context _ctx) {
        ctx = _ctx;
        dbHelper = new DBHelper(ctx);
        filterIdTheme = -10;
    }

    AdapterForMainListView getAdapter() {

        Cursor cursor = dbHelper.getDegreeData();

        cursor.moveToFirst();
        do {
            Log.d("logm", cursor.getInt(cursor.getColumnIndex(DBHelper.DEGREE_COL_ID)) + " " + cursor.getString(cursor.getColumnIndex(DBHelper.DEGREE_COL_NAME)));
        } while (cursor.moveToNext());

        String[] groupFrom = { DBHelper.DEGREE_COL_NAME };
        int[] groupTo = { R.id.groupLV_Degree };

        String[] childFrom = { DBHelper.WORDS_COL_WORD, DBHelper.WORDS_COL_TRANSLATE, DBHelper.THEMES_COL_NAME,
                DBHelper.WORDS_COL_AMOUNT_RIGHT, DBHelper.WORDS_COL_AMOUNT_WRONG, DBHelper.WORDS_COL_RIGHT_IN_SUCCESSION};
        int[] childTo = { R.id.itemLV_Word, R.id.itemLV_Translate, R.id.itemLV_Theme,
                R.id.itemLV_RightTr, R.id.itemLV_WrongTr, R.id.itemLV_InSuccTr };

        adapter = new AdapterForMainListView(ctx, cursor, R.layout.listview_group, groupFrom,
                groupTo, R.layout.listview_item, childFrom, childTo);

        return adapter;
    }

    void notifyChanged() {
        adapter.notifyDataSetChanged();
    }

    AdapterForMainListView resetAdapter() {
        adapter = getAdapter();
        adapter.notifyDataSetChanged();
        return adapter;
    }


    AdapterForMainListView retAdapter() {
        return adapter;
    }

    public void setFilter(long _filterIdTheme) {
        filterIdTheme = _filterIdTheme;
    }

    void resetFilter() {
        filterIdTheme = -10;
    }

    class AdapterForMainListView extends SimpleCursorTreeAdapter {

        AdapterForMainListView(Context context, Cursor cursor, int groupLayout,
                                      String[] groupFrom, int[] groupTo, int childLayout,
                                      String[] childFrom, int[] childTo) {
            super(context, cursor, groupLayout, groupFrom, groupTo, childLayout, childFrom, childTo);
        }

        @Override
        protected Cursor getChildrenCursor(Cursor cursor) {
            int idColDegree = cursor.getColumnIndex(DBHelper.DEGREE_COL_ID);
            return dbHelper.getWordsFromDegree(cursor.getInt(idColDegree), filterIdTheme);
        }

        @SuppressLint("InflateParams")
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            convertView = LayoutInflater.from(ctx).inflate(R.layout.listview_group, null);

            LinearLayout liner = convertView.findViewById(R.id.groupLV_LinLay);
            ImageView image = convertView.findViewById(R.id.groupLV_Img);
            switch (groupPosition) {
                case 0:
                    liner.setBackgroundColor(ctx.getResources().getColor(R.color.colorWellRem));
                    image.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_well));
                    break;
                case 1:
                    liner.setBackgroundColor(ctx.getResources().getColor(R.color.colorSosoRem));
                    image.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_soso));
                    break;
                case 2:
                    liner.setBackgroundColor(ctx.getResources().getColor(R.color.colorPoorlyRem));
                    image.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_poorly));
                    break;
            }

            return super.getGroupView(groupPosition, isExpanded, convertView, parent);
        }

        @SuppressLint("InflateParams")
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            convertView = LayoutInflater.from(ctx).inflate(R.layout.listview_item, null);

            LinearLayout liner = convertView.findViewById(R.id.itemLV_LinLay);
            switch (childPosition % 2) {
                case 0:
                    liner.setBackgroundColor(ctx.getResources().getColor(R.color.colorItem1));
                    break;
                case 1:
                    liner.setBackgroundColor(ctx.getResources().getColor(R.color.colorItem2));
                    break;
            }

            return super.getChildView(groupPosition, childPosition, isLastChild, convertView, parent);
        }

    }
}
