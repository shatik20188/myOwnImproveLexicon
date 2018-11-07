package com.example.shati.myownimprovelexicon;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

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
    int countWords;

    public final static int MODE_WORD_TRANSLATE = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewFragm = inflater.inflate(R.layout.fragment_presettinglearn, container, false);
        context = container.getContext();
        adapterHelper = new AdapterHelperForListView(context);
        dbHelper = adapterHelper.dbHelper;
        dbHelper.open();

        return viewFragm;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        spinMode = viewFragm.findViewById(R.id.preset_SpinMode);
        btnStart = viewFragm.findViewById(R.id.preset_BtnStart);
        btnStart.setOnClickListener(this);
        countOfWords = viewFragm.findViewById(R.id.preset_CountOfWords);
        linLayDegree = viewFragm.findViewById(R.id.preset_LinLayDegree);
        linLayTheme = viewFragm.findViewById(R.id.preset_LinLayTheme);

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

        countWords = dbHelper.getCountWordsByFilter(degreeStrArray, themesStrArray);
        String strWords = "Words: "
                + String.valueOf( countWords );
        countOfWords.setText(strWords);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.preset_BtnStart:

                if (countWords == 0) {
                    Toast.makeText(context, R.string.no_words, Toast.LENGTH_SHORT).show();
                } else {

                    modeLearn = spinMode.getSelectedItemPosition();

                    Intent intent = new Intent(context, LearnActivity.class);
                    intent.putExtra("themes", themesList);
                    intent.putExtra("degrees", degreeList);
                    intent.putExtra("mode", modeLearn);
                    startActivity(intent);
                }
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

        countWords = dbHelper.getCountWordsByFilter(degreeStrArray, themesStrArray);
        String strWords = "Words: "
                + String.valueOf( countWords );
        countOfWords.setText(strWords);

    }
}
