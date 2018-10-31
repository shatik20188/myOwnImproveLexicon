package com.example.shati.myownimprovelexicon;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class VocabularyFragment extends Fragment {

    public static final int REQUEST_FILTER = 1;
    public static final int REQUEST_RESET = 2;

    private static final int CM_DELETE_ID = 1;
    private static final int CM_EDIT_ID = 2;

    ExpandableListView vocListView;
    View viewFragm;
    Context context;
    AdapterHelperForListView adapterHelper;
    DBHelper dbHelper;

    public VocabularyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        viewFragm = inflater.inflate(R.layout.fragment_vocabulary, container, false);
        context = container.getContext();
        adapterHelper = new AdapterHelperForListView(context);
        dbHelper = adapterHelper.dbHelper;
        dbHelper.open(true);

        return viewFragm;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        vocListView = (ExpandableListView) viewFragm.findViewById(R.id.vocListView);
        vocListView.setAdapter( adapterHelper.getAdapter() );
        registerForContextMenu(vocListView);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dbHelper.close();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, CM_DELETE_ID, Menu.NONE, "Delete word");
        menu.add(Menu.NONE, CM_EDIT_ID, Menu.NONE, "Edit word");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        ExpandableListView.ExpandableListContextMenuInfo acmi =
                (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();
        String word = ((TextView)acmi.targetView.findViewById(R.id.itemLV_Word)).getText().toString();

        if(item.getItemId() == CM_DELETE_ID) {

            dbHelper.delWordByWord(word);
            adapterHelper.notifyChanged();

        } else if (item.getItemId() == CM_EDIT_ID) {

            Intent intent = new Intent(context, EditWordActivity.class);
            intent.putExtra("word", word);
            startActivity(intent);

        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapterHelper.notifyChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_FILTER:
                    String str = data.getStringExtra("theme");
                    adapterHelper.setFilter(dbHelper.getThemeIdByName(str));
                    vocListView.setAdapter(adapterHelper.resetAdapter());
                    break;
                case REQUEST_RESET:
                    adapterHelper.resetFilter();
                    vocListView.setAdapter(adapterHelper.resetAdapter());
                    break;
            }
        }
    }
}
