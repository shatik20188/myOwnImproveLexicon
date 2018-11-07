package com.example.shati.myownimprovelexicon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;


public class DialogFilterFragment extends DialogFragment implements View.OnClickListener {

    Context context;
    DBHelper dbHelper;
    Spinner spinFilter;
    View viewFragm;
    SimpleCursorAdapter spinAdapter;
    Button btnOK, btnReset;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.filter_Ok:
                Cursor cursor = dbHelper.getThemesData();
                cursor.moveToFirst();
                String[] themesData = new String[cursor.getCount()];
                for (int i=0; i < themesData.length; i++, cursor.moveToNext()) {
                    themesData[i] = cursor.getString(cursor.getColumnIndex(DBHelper.THEMES_COL_NAME));
                }

                String chosenTheme = themesData[spinFilter.getSelectedItemPosition()];

                Intent intent = new Intent();
                intent.putExtra("theme", chosenTheme);
                assert getTargetFragment() != null;
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                dismiss();
                break;
            case R.id.filter_Reset:
                assert getTargetFragment() != null;
                getTargetFragment().onActivityResult(2, Activity.RESULT_OK, null);
                dismiss();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle(R.string.filter);
        viewFragm = inflater.inflate(R.layout.fragment_dialog_filter, container, false);
        context = getContext();
        dbHelper = new DBHelper(context);
        dbHelper.open();

        return viewFragm;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btnOK = viewFragm.findViewById(R.id.filter_Ok);
        btnOK.setOnClickListener(this);
        btnReset = viewFragm.findViewById(R.id.filter_Reset);
        btnReset.setOnClickListener(this);
        spinFilter = viewFragm.findViewById(R.id.filter_Spinner);

        String[] themesFrom = { DBHelper.THEMES_COL_NAME };
        int[] themesTo = { android.R.id.text1 };

        Cursor cursor = dbHelper.getThemesData();
        spinAdapter = new SimpleCursorAdapter(context, android.R.layout.simple_spinner_item,
                cursor, themesFrom, themesTo, 0);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinFilter.setAdapter(spinAdapter);
    }


}
