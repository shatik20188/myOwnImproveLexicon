package com.example.shati.myownimprovelexicon;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.chip.ChipGroup;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PresettingLearnFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    View viewFragm;
    Context context;
    AdapterHelperForListView adapterHelper;
    DBHelper dbHelper;
    Spinner spinMode;
    TextView countOfWords;
    Button btnStart;
    LinearLayout linLayDegree, linLayTheme;

    ArrayList<String> degreeList;
    ArrayList<String> themesList;
    int modeLearn;

    public final static int MODE_WORD_TRANSLATE = 0;
    public final static int MODE_TRANSLATE_WORD = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewFragm = inflater.inflate(R.layout.fragment_presettinglearn, container, false);
        context = container.getContext();
        adapterHelper = new AdapterHelperForListView(context);
        dbHelper = adapterHelper.dbHelper;
        dbHelper.open(true);

        return viewFragm;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        spinMode = (Spinner) viewFragm.findViewById(R.id.preset_SpinMode);
        btnStart = (Button) viewFragm.findViewById(R.id.preset_BtnStart);
        btnStart.setOnClickListener(this);
        countOfWords = (TextView) viewFragm.findViewById(R.id.preset_CountOfWords);
        linLayDegree = (LinearLayout) viewFragm.findViewById(R.id.preset_LinLayDegree);
        linLayTheme = (LinearLayout) viewFragm.findViewById(R.id.preset_LinLayTheme);

        degreeList = new ArrayList<>();
        themesList = new ArrayList<>();

        Cursor cursor = dbHelper.getDegreeData();
        String[] degreeStrArray = new String[cursor.getCount()];
        if (cursor.moveToFirst()) {
            do {
                CheckBox checkBox = new CheckBox(context);
                String text = cursor.getString( cursor.getColumnIndex(DBHelper.DEGREE_COL_NAME) );

                checkBox.setText(text);
                degreeList.add(text);
                degreeStrArray[cursor.getPosition()] = text;

                checkBox.setChecked(true);
                linLayDegree.addView(checkBox);
                checkBox.setOnCheckedChangeListener(this);
            } while (cursor.moveToNext());
            degreeList.trimToSize();
        }

        cursor = dbHelper.getThemesData();
        String[] themesStrArray = new String[cursor.getCount()];
        if (cursor.moveToFirst()) {
            do {
                CheckBox checkBox = new CheckBox(context);
                String text = cursor.getString( cursor.getColumnIndex(DBHelper.THEMES_COL_NAME) );

                checkBox.setText(text);
                themesList.add(text);
                themesStrArray[cursor.getPosition()] = text;

                checkBox.setChecked(true);
                linLayTheme.addView(checkBox);
                checkBox.setOnCheckedChangeListener(this);
            } while (cursor.moveToNext());
            themesList.trimToSize();
        }

        ArrayAdapter<CharSequence> spinAdapter = ArrayAdapter.createFromResource(context,
                R.array.learn_mode, android.R.layout.simple_spinner_item);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinMode.setAdapter(spinAdapter);

        String strWords = "Words: "
                + String.valueOf( dbHelper.getCountWordsByFilter(degreeStrArray, themesStrArray) );
        countOfWords.setText(strWords);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.preset_BtnStart:
                modeLearn = spinMode.getSelectedItemPosition();

                Intent intent = new Intent(context, LearnActivity.class);
                intent.putExtra("themes", themesList);
                intent.putExtra("degrees", degreeList);
                intent.putExtra("mode", modeLearn);
                startActivity(intent);
                break;

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        CheckBox checkBox = (CheckBox) buttonView;

        if ( ((LinearLayout) checkBox.getParent()).getId() == R.id.preset_LinLayDegree ) {
            if (isChecked) {
                degreeList.add( checkBox.getText().toString() );
            } else {
                degreeList.remove( checkBox.getText().toString() );
            }
            degreeList.trimToSize();
        } else {
            if (isChecked) {
                themesList.add( checkBox.getText().toString() );
            } else {
                themesList.remove( checkBox.getText().toString() );
            }
            themesList.trimToSize();
        }

        String[] degreeStrArray = new String[degreeList.size()];
        for (int i = 0; i < degreeList.size(); i++) {
            degreeStrArray[i] = degreeList.get(i);
        }

        String[] themesStrArray = new String[themesList.size()];
        for (int i = 0; i < themesList.size(); i++) {
            themesStrArray[i] = themesList.get(i);
        }

        String strWords = "Words: "
                + String.valueOf( dbHelper.getCountWordsByFilter(degreeStrArray, themesStrArray) );
        countOfWords.setText(strWords);

    }
}
