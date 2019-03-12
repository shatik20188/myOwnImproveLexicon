package com.example.shati.myownimprovelexicon;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class DialogThemeDelFragment extends DialogFragment implements View.OnClickListener {

    Context context;
    DBHelper dbHelper;
    View viewFragm;
    Button btnDelete, btnSave;
    String theme;

    interface NotifyChangedLW {
        void notifyChanged();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle(R.string.filter);
        viewFragm = inflater.inflate(R.layout.fragment_dialog_theme_del, container, false);
        context = getContext();
        dbHelper = new DBHelper(context);
        dbHelper.open();

        theme = getArguments().getString("theme", "");

        return viewFragm;
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delTheme_Save:
                dbHelper.replaceAllWordsToNoSubjByTheme(theme);
                Toast.makeText(context, R.string.saved, Toast.LENGTH_SHORT).show();
                break;
            case R.id.delTheme_Delete:
                dbHelper.delAllWordsOfThemeByTheme(theme);
                Toast.makeText(context, R.string.deleted, Toast.LENGTH_SHORT).show();
                break;
        }
        dbHelper.delThemeByTheme(theme);
        ((NotifyChangedLW)getActivity()).notifyChanged();
        dismiss();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btnDelete = viewFragm.findViewById(R.id.delTheme_Delete);
        btnDelete.setOnClickListener(this);
        btnSave = viewFragm.findViewById(R.id.delTheme_Save);
        btnSave.setOnClickListener(this);
    }
}
