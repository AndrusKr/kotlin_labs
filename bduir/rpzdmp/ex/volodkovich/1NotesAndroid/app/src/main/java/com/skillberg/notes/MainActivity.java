package com.skillberg.notes;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.skillberg.notes.db.NotesContract;
import com.skillberg.notes.ui.NotesAdapter;

import android.content.ContentUris;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private NotesAdapter notesAdapter;
    private RecyclerView recyclerView;
    EditText texactivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.notes_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        notesAdapter = new NotesAdapter(null, onNoteClickListener, onNoteDeleteClickListener);
        recyclerView.setAdapter(notesAdapter);


        getLoaderManager().initLoader(
                0, // Идентификатор загрузчика
                null, // Аргументы
                this // Callback для событий загрузчика
        );

        findViewById(R.id.create_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateNoteActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,  // Контекст
                NotesContract.Notes.URI, // URI
                NotesContract.Notes.LIST_PROJECTION, // Столбцы
                null, // Параметры выборки
                null, // Аргументы выборки
                null // Сортировка по умолчанию
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.i("Test", "Load finished: " + cursor.getCount());

        cursor.setNotificationUri(getContentResolver(), NotesContract.Notes.URI);

        notesAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.id_search:
                showSearchDialog();
                return true;

            case R.id.id_about:
                showAboutDialog();;
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSearchDialog() {

        LayoutInflater layoutInflater = LayoutInflater.from(this);

        final View search_view = layoutInflater.inflate(R.layout.search, null);

        String[] colorStrings = { "Белый", "Желтый", "Синий", "Зеленный" };
        Spinner sp = (Spinner)search_view.findViewById(R.id.id_spinner_search);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, colorStrings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Поиск")
                .setView(search_view)
                .create();

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        RadioGroup radioGroup = (RadioGroup)search_view.findViewById(R.id.id_search_group);
                        int checkedId = radioGroup.getCheckedRadioButtonId();
                        String selection = "";
                        switch (checkedId)
                        {
                            case -1:
                                Toast.makeText(getApplicationContext(), "Выберите тип поиска!", Toast.LENGTH_SHORT).show();
                                return;

                            case R.id.id_radio_data:
                                selection = NotesContract.Notes.COLUMN_NOTE;
                                break;
                            case R.id.id_radio_date:
                                selection = NotesContract.Notes.COLUMN_UPDATED_TS;
                                break;

                            case R.id.id_radio_color:
                                selection = NotesContract.Notes.COLUMN_COLOR;
                                break;
                        }

                        Cursor cur;

                        if(checkedId == R.id.id_radio_color)
                        {
                            Spinner sp = (Spinner)search_view.findViewById(R.id.id_spinner_search);
                            int colid = sp.getSelectedItemPosition();
                            cur = getContentResolver().query(NotesContract.Notes.URI, null, selection + " = ?", new String[] { "" + colid }, null, null);
                        }
                        else
                        {
                            EditText editText = (EditText)search_view.findViewById(R.id.search_input);
                            String find_string = editText.getText().toString();
                            cur = getContentResolver().query(NotesContract.Notes.URI, null, selection + " LIKE ?", new String[] { "%" + find_string + "%"}, null, null);
                        }

                        notesAdapter.swapCursor(cur);
                        notesAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Отмена",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }
        );

        if(!isFinishing()) {
            alertDialog.show();
        }
    }

    private void showAboutDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View about_view = layoutInflater.inflate(R.layout.about, null);

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("О программе")
                .setView(about_view)
                .create();

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        if(!isFinishing()) {
            alertDialog.show();
        }
    }

    /**
     * Listener для клика по заметке
     */
    private final NotesAdapter.OnNoteClickListener onNoteClickListener = new NotesAdapter.OnNoteClickListener() {
        @Override
        public void onNoteClick(long noteId) {
            Intent intent = new Intent(MainActivity.this, NoteActivity.class);
            intent.putExtra(NoteActivity.EXTRA_NOTE_ID, noteId);

            startActivity(intent);
        }
    };

    private final NotesAdapter.OnNoteDeleteClickListener onNoteDeleteClickListener = new NotesAdapter.OnNoteDeleteClickListener() {
        @Override
        public void onNoteDeleteClick(long noteId) {
            System.out.println("DELETE " + noteId);

            getContentResolver().delete(ContentUris.withAppendedId(NotesContract.Notes.URI, noteId), null, null);
        }
    };
}
